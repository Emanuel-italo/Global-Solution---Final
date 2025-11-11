package br.com.fiap.saudetodos.domain.service;

import br.com.fiap.saudetodos.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.saudetodos.domain.model.Paciente;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface PacienteService {

    Paciente criar(Paciente paciente);

    Paciente atualizar(Paciente paciente)
            throws EntidadeNaoLocalizada;

    Paciente buscarPorId(Long id)
            throws EntidadeNaoLocalizada;

    List<Paciente> buscarTodos();

    void desativar(Long id, Long version)
            throws EntidadeNaoLocalizada;

    void reativar(Long id, Long version)
            throws EntidadeNaoLocalizada;

    List<Paciente> buscarPorCpf(String cpf);

    boolean isIdoso(Long id)
            throws EntidadeNaoLocalizada;

    boolean podeAgendarConsulta(Long id,
                                LocalDate data,
                                LocalTime hora)
            throws EntidadeNaoLocalizada;
}
