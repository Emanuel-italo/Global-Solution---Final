package br.com.fiap.motivagig.domain.service;

import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Trabalhador;

import java.util.List;


public interface TrabalhadorService {


    Trabalhador criar(Trabalhador trabalhador);

    
    Trabalhador atualizar(Trabalhador trabalhador)
            throws EntidadeNaoLocalizada;

    
    Trabalhador buscarPorId(int id)
            throws EntidadeNaoLocalizada;

    List<Trabalhador> buscarTodos();

   
    void desativar(int id)
            throws EntidadeNaoLocalizada;

    
    void reativar(int id)
            throws EntidadeNaoLocalizada;

   
    Trabalhador buscarPorCpf(String cpf)
            throws EntidadeNaoLocalizada;

  
}