package br.com.fiap.MotivaGig.domain.exceptions;

import java.util.List;
import br.com.fiap.MotivaGig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.MotivaGig.domain.model.Consulta;

public interface ConsultaRepository {

    Consulta salvar(Consulta consulta);

    Consulta buscarPorId(int id) throws EntidadeNaoLocalizada;

    List<Consulta> buscarTodos();

    List<Consulta> buscarPorPacienteId(int pacienteId);

    boolean editar(Consulta consulta);

    boolean cancelar(int id);
}