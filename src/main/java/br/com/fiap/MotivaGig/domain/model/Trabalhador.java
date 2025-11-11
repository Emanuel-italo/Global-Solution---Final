package br.com.fiap.motivagig.domain.model;


public class Trabalhador extends Pessoa {
    
   
    private String email;
    private String cpf; 
    private String senha; 
    private String tipoVeiculo; 
    private boolean ativo;

  
    private int pontos;
    private int nivel;

   
    
    public Trabalhador() {
        super();
        this.ativo = true;
        this.pontos = 0;
        this.nivel = 1;
    }

   

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; } 

    public String getTipoVeiculo() { return tipoVeiculo; }
    public void setTipoVeiculo(String tipoVeiculo) { this.tipoVeiculo = tipoVeiculo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public int getPontos() { return pontos; }
    public void setPontos(int pontos) { this.pontos = pontos; }

    public int getNivel() { return nivel; }
    public void setNivel(int nivel) { this.nivel = nivel; }
}