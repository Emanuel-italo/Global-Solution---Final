package br.com.fiap.motivagig.domain.exceptions;

public class Medico extends Pessoa {
    private String crm;
    private String especialidade;

    public Medico(int id, String nome, String contato, String crm, String especialidade) {
        super(id, nome, contato);
        setCrm(crm);
        setEspecialidade(especialidade); 
    }


    public Medico() {
        super(0, "", "");
    }


    public String getCrm() { return crm; }
    public String getEspecialidade() { return especialidade; }


    public void setCrm(String crm) {
        if (crm == null || crm.trim().isEmpty()) {
            System.err.println("CRM não pode ser vazio.");

            return;
        }
        this.crm = crm;
    }
    public void setEspecialidade(String especialidade) {
        if (especialidade == null || especialidade.trim().isEmpty()) {
            System.err.println("Especialidade não pode ser vazia.");

            return;
        }
        this.especialidade = especialidade;
    }

    @Override
    public String toString() {
        return "Medico [" + super.toString() + ", crm=" + crm + ", especialidade=" + especialidade + "]";
    }
}