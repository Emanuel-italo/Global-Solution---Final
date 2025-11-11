package br.com.fiap.motivagig.infrastructure.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Trabalhador;
import br.com.fiap.motivagig.domain.repository.TrabalhadorRepository; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped

public class JdbcTrabalhadorRepository implements TrabalhadorRepository {

    private final DatabaseConnection conexaoBD;

    @Inject 
    public JdbcTrabalhadorRepository(DatabaseConnection conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

    
    private Trabalhador mapearResultSetParaTrabalhador(ResultSet rs) throws SQLException {
        Trabalhador trabalhador = new Trabalhador();
        
      
        trabalhador.setId(rs.getInt("id"));
        trabalhador.setNome(rs.getString("nome"));
        trabalhador.setContato(rs.getString("telefone")); 
        
        
        trabalhador.setEmail(rs.getString("email"));
        trabalhador.setCpf(rs.getString("cpf"));
        trabalhador.setTipoVeiculo(rs.getString("tipo_veiculo"));
        trabalhador.setPontos(rs.getInt("pontos"));
        trabalhador.setNivel(rs.getInt("nivel"));
        trabalhador.setAtivo(rs.getInt("ativo") == 1);
        
        
        
        return trabalhador;
    }


    @Override
 
    public Trabalhador salvar(Trabalhador trabalhador) {

        
        String sql = "INSERT INTO T_TRABALHADOR "
                + "(nome, email, cpf, telefone, tipo_veiculo, pontos, nivel, ativo, created_at, last_update) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String[] generatedColumns = { "ID" };

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql, generatedColumns);

            stmt.setString(1, trabalhador.getNome());
            stmt.setString(2, trabalhador.getEmail());
            stmt.setString(3, trabalhador.getCpf());
            stmt.setString(4, trabalhador.getContato()); 
            stmt.setString(5, trabalhador.getTipoVeiculo());
            stmt.setInt(6, trabalhador.getPontos());
            stmt.setInt(7, trabalhador.getNivel());
            stmt.setInt(8, 1); 

            Timestamp agora = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(9, agora); 
            stmt.setTimestamp(10, agora); 

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                System.err.println("Falha ao salvar trabalhador, nenhuma linha afetada.");
                return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                trabalhador.setId(generatedKeys.getInt(1)); 
                return trabalhador;
            } else {
                System.err.println("Falha ao obter ID gerado para o trabalhador.");
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao salvar trabalhador: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public Trabalhador buscarPorId(int id) throws EntidadeNaoLocalizada {


        String sql = "SELECT id, nome, email, cpf, telefone, tipo_veiculo, pontos, nivel, ativo "
                + "FROM T_TRABALHADOR WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearResultSetParaTrabalhador(rs); 
            } else {
                throw new EntidadeNaoLocalizada("Trabalhador não encontrado com ID: " + id); 
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar trabalhador por ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar trabalhador.", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public Trabalhador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada {

       
        String sql = "SELECT id, nome, email, cpf, telefone, tipo_veiculo, pontos, nivel, ativo "
                + "FROM T_TRABALHADOR WHERE cpf = ? AND ativo = 1";
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
                return mapearResultSetParaTrabalhador(rs); 
            } else {
                throw new EntidadeNaoLocalizada("Trabalhador não encontrado com CPF: " + cpf); 
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar trabalhador por CPF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar trabalhador por CPF.", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }


    @Override
    public List<Trabalhador> buscarTodos() {
       
        String sql = "SELECT id, nome, email, cpf, telefone, tipo_veiculo, pontos, nivel, ativo "
                + "FROM T_TRABALHADOR WHERE ativo = 1 ORDER BY nome";
        List<Trabalhador> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSetParaTrabalhador(rs)); 
            }
            return lista;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao listar trabalhadores: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao listar trabalhadores.", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
   
    public boolean editar(Trabalhador trabalhador) { 

        
        String sql = "UPDATE T_TRABALHADOR SET nome = ?, email = ?, telefone = ?, tipo_veiculo = ?, "
                + "pontos = ?, nivel = ?, last_update = ? "
                + "WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);

            Timestamp agora = new Timestamp(System.currentTimeMillis());

            stmt.setString(1, trabalhador.getNome());
            stmt.setString(2, trabalhador.getEmail());
            stmt.setString(3, trabalhador.getContato());
            stmt.setString(4, trabalhador.getTipoVeiculo());
            stmt.setInt(5, trabalhador.getPontos());
            stmt.setInt(6, trabalhador.getNivel());
            stmt.setTimestamp(7, agora); 
            stmt.setInt(8, trabalhador.getId()); 

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao editar trabalhador: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public boolean desativar(int id) {

       
        String sql = "UPDATE T_TRABALHADOR SET ativo = 0, last_update = ? "
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
            System.err.println("Erro SQL ao desativar trabalhador: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }
}