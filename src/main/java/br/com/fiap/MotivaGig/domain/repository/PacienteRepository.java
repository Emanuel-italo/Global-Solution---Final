package br.com.fiap.motivagig.domain.exceptions;

import java.util.List;
import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Paciente;

public interface PacienteRepository {

    Paciente salvar(Paciente paciente);

    Paciente buscarPorId(int id) throws EntidadeNaoLocalizada;

    Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;

    List<Paciente> buscarTodos();

    boolean editar(Paciente paciente);

    boolean desativar(int id);


}