package br.com.fiap.saudetodos.domain.repository;

import java.util.List;
import br.com.fiap.saudetodos.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.saudetodos.domain.model.Paciente;

public interface PacienteRepository {

    Paciente salvar(Paciente paciente);

    Paciente buscarPorId(int id) throws EntidadeNaoLocalizada;

    Paciente buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;

    List<Paciente> buscarTodos();

    boolean editar(Paciente paciente);

    boolean desativar(int id);


}