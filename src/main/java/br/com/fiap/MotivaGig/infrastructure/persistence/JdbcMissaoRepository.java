package br.com.fiap.motivagig.infrastructure.persistence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Missao;
import br.com.fiap.motivagig.domain.repository.MissaoRepository; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped

public class JdbcMissaoRepository implements MissaoRepository {

    private final DatabaseConnection conexaoBD;
    
    
    @Inject
    public JdbcMissaoRepository(DatabaseConnection conexaoBD) {
        this.conexaoBD = conexaoBD;
    }

   
    private Missao mapearResultSetParaMissao(ResultSet rs) throws SQLException {
        Missao missao = new Missao();
        missao.setId(rs.getInt("id"));
        missao.setTitulo(rs.getString("titulo"));
        missao.setDescricao(rs.getString("descricao"));
        missao.setPontosRecompensa(rs.getInt("pontos_recompensa"));
        missao.setTipo(rs.getString("tipo"));
        missao.setAtivo(rs.getInt("ativo") == 1);
        return missao;
    }

    @Override
    public Missao salvar(Missao missao) {
        
       
        String sql = "INSERT INTO T_MISSAO "
                + "(titulo, descricao, pontos_recompensa, tipo, ativo, created_at, last_update) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String[] generatedColumns = { "ID" };

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql, generatedColumns);

          
            stmt.setString(1, missao.getTitulo());
            stmt.setString(2, missao.getDescricao());
            stmt.setInt(3, missao.getPontosRecompensa());
            stmt.setString(4, missao.getTipo());
            stmt.setInt(5, 1);

            Timestamp agora = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(6, agora); 
            stmt.setTimestamp(7, agora); 

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Falha ao salvar missão."); return null;
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                missao.setId(generatedKeys.getInt(1));
                return missao;
            } else {
                System.err.println("Falha ao obter ID da missão."); return null;
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao salvar missão: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
     
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public Missao buscarPorId(int id) throws EntidadeNaoLocalizada {
        
   
        String sql = "SELECT id, titulo, descricao, pontos_recompensa, tipo, ativo "
                + "FROM T_MISSAO WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
             
                return mapearResultSetParaMissao(rs);
            } else {
                throw new EntidadeNaoLocalizada("Missão não encontrada com ID: " + id);
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL ao buscar missão por ID: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao buscar missão.", e);
        }
       
        finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
    public List<Missao> buscarTodos() {
     
        String sql = "SELECT id, titulo, descricao, pontos_recompensa, tipo, ativo "
                + "FROM T_MISSAO WHERE ativo = 1 ORDER BY tipo, pontos_recompensa";
        List<Missao> lista = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
              
                lista.add(mapearResultSetParaMissao(rs));
            }
            return lista;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao listar missões: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro de banco de dados ao listar missões.", e);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }




    @Override
    public boolean editar(Missao missao) {

       
        String sql = "UPDATE T_MISSAO SET titulo = ?, descricao = ?, pontos_recompensa = ?, tipo = ?, last_update = ? "
                + "WHERE id = ? AND ativo = 1";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = conexaoBD.getConnection();
            stmt = conn.prepareStatement(sql);

            Timestamp agora = new Timestamp(System.currentTimeMillis());

          
            stmt.setString(1, missao.getTitulo());
            stmt.setString(2, missao.getDescricao());
            stmt.setInt(3, missao.getPontosRecompensa());
            stmt.setString(4, missao.getTipo());
            stmt.setTimestamp(5, agora); 
            stmt.setInt(6, missao.getId()); 

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erro SQL ao editar missão: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    @Override
   
    public boolean desativar(int id) {

       
        String sql = "UPDATE T_MISSAO SET ativo = 0, last_update = ? "
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
            System.err.println("Erro SQL ao desativar missão: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }
}