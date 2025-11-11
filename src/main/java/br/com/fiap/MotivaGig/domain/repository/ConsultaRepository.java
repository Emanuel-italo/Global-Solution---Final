package br.com.fiap.motivagig.domain.repository;

import java.util.List;
import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Missao;

public interface ConsultaRepository {

    Missao salvar(Missao consulta);

    Missao buscarPorId(int id) throws EntidadeNaoLocalizada;

    List<Missao> buscarTodos();

    List<Missao> buscarPorPacienteId(int pacienteId);

    boolean editar(Missao consulta);

    boolean cancelar(int id);
}