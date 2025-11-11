package br.com.fiap.motivagig.domain.service;

import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Trabalhador;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PacienteService {

    Trabalhador criar(Trabalhador paciente);

    Trabalhador atualizar(Trabalhador paciente)
            throws EntidadeNaoLocalizada;

    Trabalhador buscarPorId(Long id)
            throws EntidadeNaoLocalizada;

    List<Trabalhador> buscarTodos();

    void desativar(Long id, Long version)
            throws EntidadeNaoLocalizada;

    void reativar(Long id, Long version)
            throws EntidadeNaoLocalizada;

    List<Trabalhador> buscarPorCpf(String cpf);

    boolean isIdoso(Long id)
            throws EntidadeNaoLocalizada;

    boolean podeAgendarConsulta(Long id,
                                LocalDate data,
                                LocalTime hora)
            throws EntidadeNaoLocalizada;
}
