package br.com.fiap.motivagig.domain.repository;

import java.util.List;
import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Trabalhador;

public interface TrabalhadorRepository {

    Trabalhador salvar(Trabalhador paciente);

    Trabalhador buscarPorId(int id) throws EntidadeNaoLocalizada;

    Trabalhador buscarPorCpf(String cpf) throws EntidadeNaoLocalizada;

    List<Trabalhador> buscarTodos();

    boolean editar(Trabalhador paciente);

    boolean desativar(int id);


}