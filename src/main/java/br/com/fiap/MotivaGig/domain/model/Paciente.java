package br.com.fiap.motivagig.domain.exceptions;



public class Paciente extends Pessoa {
    private int idade;
    private String tipoDeficiencia;
    private String telefone;
    private String cpf;
    private String email;
    private boolean ativo;


    public Paciente(int id, String nome, int idade, String tipoDeficiencia, String telefone, String cpf, String email) {
        super(id, nome, telefone);
        setIdade(idade);
        setTipoDeficiencia(tipoDeficiencia);
        setTelefone(telefone);
        setCpf(cpf);
        setEmail(email);
        this.ativo = true;
    }


    public Paciente() {
        super(0, "", "");
        this.ativo = true;
    }


    public int getIdade() { return idade; }
    public String getTipoDeficiencia() { return tipoDeficiencia; }
    public String getTelefone() { return telefone; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public boolean isAtivo() { return ativo; }


    public void setIdade(int idade) {
        if (idade <= 0 || idade > 120) {
            System.err.println("Idade inválida (" + idade + "). Deve ser entre 1 e 120.");
            return;
        }
        if (idade >= 65) {
            System.out.println("Paciente idoso (>= 65 anos).");
        }
        this.idade = idade;
    }

    public void setTipoDeficiencia(String tipoDeficiencia) {
        this.tipoDeficiencia = tipoDeficiencia;
    }

    public void setTelefone(String telefone) {

        String regex = "^[\\d\\s()+-]+$";
        if (telefone == null || !telefone.matches(regex)) {
            System.err.println("Telefone parece inválido: " + telefone);

            return;
        }
        this.telefone = telefone;

        super.setContato(telefone);
    }

    public void setCpf(String cpf) {
        if (cpf == null) {
            System.err.println("CPF não pode ser nulo."); return;
        }
        String cpfNumerico = cpf.replaceAll("\\D", "");
        if (cpfNumerico.length() != 11) {
            System.err.println("CPF inválido (não tem 11 dígitos): " + cpf);

            return;
        }
        if (cpfNumerico.matches("(\\d)\\1{10}")) {
            System.err.println("CPF inválido (dígitos repetidos): " + cpf);

            return;
        }

        this.cpf = cpfNumerico;
    }

    public void setEmail(String email) {

        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (email == null || !email.matches(regex)) {
            System.err.println("Email parece inválido: " + email);

            return;
        }
        this.email = email;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }


    public boolean isIdoso() {
        return this.idade >= 65;
    }

    @Override
    public String toString() {
        return "Paciente [" + super.toString()
                + ", idade=" + idade
                + ", tipoDeficiencia=" + tipoDeficiencia
                + ", telefone=" + telefone
                + ", cpf=" + cpf
                + ", email=" + email
                + ", ativo=" + ativo + "]";
    }
}