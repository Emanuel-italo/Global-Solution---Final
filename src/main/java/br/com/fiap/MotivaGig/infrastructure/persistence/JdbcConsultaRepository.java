package br.com.fiap.motivagig.infrastructure.persistence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Consulta;
import br.com.fiap.motivagig.domain.model.Medico;
import br.com.fiap.motivagig.domain.model.Paciente;
import br.com.fiap.motivagig.domain.repository.ConsultaRepository;
import br.com.fiap.motivagig.domain.repository.PacienteRepository; 

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcConsultaRepository implements ConsultaRepository {

    private final DatabaseConnection conexaoBD;
    private final PacienteRepository pacienteRepository; 



    @Inject
    public JdbcConsultaRepository(DatabaseConnection conexaoBD, PacienteRepository pacienteRepository) {
        this.conexaoBD = conexaoBD;
        this.pacienteRepository = pacienteRepository;
    }


    private Consulta mapearResultSetParaConsulta(ResultSet rs) throws SQLException, EntidadeNaoLocalizada {
        int consultaId      = rs.getInt("id");
        LocalDate data      = rs.getDate("data_consulta").toLocalDate();
        Timestamp tsHora    = rs.getTimestamp("hora_consulta"); 
        LocalTime hora      = (tsHora != null) ? tsHora.toLocalDateTime().toLocalTime() : null;
        String status       = rs.getString("status");
        int pacienteId      = rs.getInt("paciente_id");
        int medicoId        = rs.getInt("medico_id");
        boolean ativo       = rs.getInt("ativo") == 1;


        Paciente paciente = pacienteRepository.buscarPorId(pacienteId);


        Medico medico = new Medico(medicoId, "Médico " + medicoId, "", "CRM" + medicoId, "Especialidade");



        Consulta consulta = new Consulta(consultaId, data, hora, status, paciente, medico);
        consulta.setAtivo(ativo);
        return consulta;
    }


    @Override
    public Consulta salvar(Consulta consulta) {
        
        String sql = "INSERT INTO CONSULTA "
                + "(data_consulta, hora_consulta, status, paciente_id, medico_id, ativo, created_at, last_update) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String[] generatedColumns = { "ID" };

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql, generatedColumns);

            stmt.setDate(1, Date.valueOf(consulta.getData()));
            stmt.setTimestamp(2, Timestamp.valueOf(consulta.getData().atTime(consulta.getHora()))); // Combina data/hora
            stmt.setString(3, consulta.getStatus());
            stmt.setInt(4, consulta.getPaciente().getId());
            stmt.setInt(5, consulta.getMedico().getId());
            stmt.setInt(6, 1); 

            Timestamp agora = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(7, agora); 
            stmt.setTimestamp(8, agora); 

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Falha ao salvar consulta."); return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                consulta.setId(generatedKeys.getInt(1));
                return consulta;
            } else {
                System.err.println("Falha ao obter ID da consulta."); return null;
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao salvar consulta: " + e.getMessage());
            e.printStackTrace();

            return null;
        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public Consulta buscarPorId(int id) throws EntidadeNaoLocalizada {
        
        String sql = "SELECT id, data_consulta, hora_consulta, status, paciente_id, medico_id, ativo "
                + "FROM CONSULTA WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {

                return mapearResultSetParaConsulta(rs);
            } else {
                throw new EntidadeNaoLocalizada("Consulta não encontrada com ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar consulta por ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar consulta.", e);
        } catch (EntidadeNaoLocalizada e) {

            System.err.println("Erro ao buscar consulta: " + e.getMessage());
            throw new EntidadeNaoLocalizada("Consulta encontrada, mas paciente associado não existe ou está inativo (ID Consulta: " + id + ")");
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public List<Consulta> buscarTodos() {
        String sql = "SELECT id, data_consulta, hora_consulta, status, paciente_id, medico_id, ativo "
                + "FROM CONSULTA WHERE ativo = 1 ORDER BY data_consulta, hora_consulta";
        List<Consulta> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                try {
                    lista.add(mapearResultSetParaConsulta(rs));
                } catch (EntidadeNaoLocalizada e) {

                    System.err.println("Aviso: Paciente da consulta ID " + rs.getInt("id") + " não encontrado/inativo. Consulta ignorada na lista.");
                }
            }
            return lista;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao listar consultas: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao listar consultas.", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public List<Consulta> buscarPorPacienteId(int pacienteId) {
        String sql = "SELECT id, data_consulta, hora_consulta, status, paciente_id, medico_id, ativo "
                + "FROM CONSULTA WHERE paciente_id = ? AND ativo = 1 ORDER BY data_consulta, hora_consulta";
        List<Consulta> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, pacienteId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                try {
                    lista.add(mapearResultSetParaConsulta(rs));
                } catch (EntidadeNaoLocalizada e) {
                    System.err.println("Aviso: Paciente da consulta ID " + rs.getInt("id") + " não encontrado/inativo. Consulta ignorada na lista.");
                }
            }
            return lista;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao listar consultas do paciente: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao listar consultas do paciente.", e);
        } finally {

            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }


    @Override
    public boolean editar(Consulta consulta) {

        String sql = "UPDATE CONSULTA SET data_consulta = ?, hora_consulta = ?, status = ?, last_update = ? "
                + "WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);

            Timestamp agora = new Timestamp(System.currentTimeMillis());

            stmt.setDate(1, Date.valueOf(consulta.getData()));
            stmt.setTimestamp(2, Timestamp.valueOf(consulta.getData().atTime(consulta.getHora())));
            stmt.setString(3, consulta.getStatus());
            stmt.setTimestamp(4, agora); 
            stmt.setInt(5, consulta.getId()); 

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao editar consulta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public boolean cancelar(int id) {

        String sql = "UPDATE CONSULTA SET status = 'CANCELADA', ativo = 0, last_update = ? "
                + "WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);

            Timestamp agora = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(1, agora);
            stmt.setInt(2, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao cancelar consulta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }
}