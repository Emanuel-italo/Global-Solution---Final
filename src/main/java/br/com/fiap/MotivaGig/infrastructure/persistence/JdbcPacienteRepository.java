package br.com.fiap.MotivaGig.infrastructure.persistence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.com.fiap.MotivaGig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.MotivaGig.domain.model.Paciente;
import br.com.fiap.MotivaGig.domain.repository.PacienteRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class JdbcPacienteRepository implements PacienteRepository {

    private final DatabaseConnection conexaoBD;

    @Inject 
    public JdbcPacienteRepository(DatabaseConnection conexaoBD) {
        this.conexaoBD = conexaoBD;
    }


    private Paciente mapearResultSetParaPaciente(ResultSet rs) throws SQLException {
        return new Paciente(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getInt("idade"),
                rs.getString("tipo_deficiencia"),
                rs.getString("telefone"),
                rs.getString("cpf"),
                rs.getString("email")

        );

    }


    @Override
    public Paciente salvar(Paciente paciente) {

        String sql = "INSERT INTO PACIENTE "
                + "(nome, idade, tipo_deficiencia, telefone, cpf, email, ativo, created_at, last_update) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String[] generatedColumns = { "ID" };

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql, generatedColumns);

            stmt.setString(1, paciente.getNome());
            stmt.setInt(2, paciente.getIdade());
            stmt.setString(3, paciente.getTipoDeficiencia());
            stmt.setString(4, paciente.getTelefone());
            stmt.setString(5, paciente.getCpf());
            stmt.setString(6, paciente.getEmail());
            stmt.setInt(7, 1); 

            Timestamp agora = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(8, agora); 
            stmt.setTimestamp(9, agora); 

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("Falha ao salvar paciente, nenhuma linha afetada.");
                return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                paciente.setId(generatedKeys.getInt(1)); 
                return paciente;
            } else {
                System.err.println("Falha ao obter ID gerado para o paciente.");
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao salvar paciente: " + e.getMessage());
            e.printStackTrace();

            return null;
        } finally {

            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public Paciente buscarPorId(int id) throws EntidadeNaoLocalizada {

        String sql = "SELECT id, nome, idade, tipo_deficiencia, telefone, cpf, email, ativo "
                + "FROM PACIENTE WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSetParaPaciente(rs);
            } else {

                throw new EntidadeNaoLocalizada("Paciente não encontrado com ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar paciente por ID: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Erro de banco de dados ao buscar paciente.", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {

        String sql = "SELECT id, nome, idade, tipo_deficiencia, telefone, cpf, email, ativo "
                + "FROM PACIENTE WHERE cpf = ? AND ativo = 1";
        String cpfNumerico = cpf != null ? cpf.replaceAll("\\D", "") : ""; 

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cpfNumerico);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSetParaPaciente(rs);
            } else {
                throw new EntidadeNaoLocalizada("Paciente não encontrado com CPF: " + cpf);
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar paciente por CPF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar paciente por CPF.", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }


    @Override
    public List<Paciente> buscarTodos() {
        String sql = "SELECT id, nome, idade, tipo_deficiencia, telefone, cpf, email, ativo "
                + "FROM PACIENTE WHERE ativo = 1 ORDER BY nome";
        List<Paciente> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSetParaPaciente(rs));
            }
            return lista;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao listar pacientes: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao listar pacientes.", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public boolean editar(Paciente paciente) {

        String sql = "UPDATE PACIENTE SET nome = ?, idade = ?, tipo_deficiencia = ?, "
                + "telefone = ?, email = ?, last_update = ? "
                + "WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);

            Timestamp agora = new Timestamp(System.currentTimeMillis());

            stmt.setString(1, paciente.getNome());
            stmt.setInt(2, paciente.getIdade());
            stmt.setString(3, paciente.getTipoDeficiencia());
            stmt.setString(4, paciente.getTelefone());
            stmt.setString(5, paciente.getEmail());
            stmt.setTimestamp(6, agora); 
            stmt.setInt(7, paciente.getId()); 

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao editar paciente: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public boolean desativar(int id) {

        String sql = "UPDATE PACIENTE SET ativo = 0, last_update = ? "
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
            System.err.println("Erro SQL ao desativar paciente: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }


     }
