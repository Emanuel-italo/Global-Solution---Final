package br.com.fiap.motivagig.domain.model;

public class Missao {

    
    private int id;
    private String titulo;
    private String descricao;
    private int pontosRecompensa;
    private String tipo; 
    private boolean ativo;

    // Construtores
    public Missao() {
        this.ativo = true;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getPontosRecompensa() {
        return pontosRecompensa;
    }

    public void setPontosRecompensa(int pontosRecompensa) {
        this.pontosRecompensa = pontosRecompensa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Missao [id=" + id + ", titulo=" + titulo + ", pontos=" + pontosRecompensa + ", tipo=" + tipo + ", ativo=" + ativo + "]";
    }
}