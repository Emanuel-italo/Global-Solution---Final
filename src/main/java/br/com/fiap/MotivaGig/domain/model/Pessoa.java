package br.com.fiap.motivagig.domain.model;


public abstract class Pessoa {
    protected int id;
    protected String nome;
    protected String contato;


    public Pessoa(int id, String nome, String contato) {

        if (nome == null || nome.trim().isEmpty()) {
            System.err.println("ALERTA: Tentando criar Pessoa com nome vazio!");

        }
        this.id = id;
        this.nome = nome;
        this.contato = contato;
    }


    public Pessoa() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            System.err.println("ALERTA: Tentando setar nome vazio!");
            return;
        }
        this.nome = nome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }


    @Override
    public String toString() {
        return "id=" + id + ", nome='" + nome + "', contato='" + contato + "'";
    }
}