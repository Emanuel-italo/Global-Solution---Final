package br.com.fiap.motivagig.domain.repository;

import java.util.List;
import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Missao;


public interface MissaoRepository {

   
    Missao salvar(Missao missao);

    Missao buscarPorId(int id) throws EntidadeNaoLocalizada;

    List<Missao> buscarTodos();

    boolean editar(Missao missao);

   
    boolean desativar(int id);
}