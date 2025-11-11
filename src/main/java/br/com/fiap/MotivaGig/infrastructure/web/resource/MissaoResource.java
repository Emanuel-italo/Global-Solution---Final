package br.com.fiap.motivagig.infrastructure.web.resource;

import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Missao;
import br.com.fiap.motivagig.domain.repository.MissaoRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/api/motivagig/missoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MissaoResource {

    @Inject
    MissaoRepository missaoRepository;

    @POST
    public Response criarMissao(Missao missao) {
        if (missao == null || missao.getTitulo() == null || missao.getPontosRecompensa() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Dados inválidos. Título e pontos (>0) são obrigatórios."))
                    .build();
        }

        Missao novaMissao = missaoRepository.salvar(missao);

        if (novaMissao != null) {
            return Response.status(Response.Status.CREATED).entity(novaMissao).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao criar missão.")).build();
        }
    }

    @GET
    public Response listarMissoes() {
        try {
            List<Missao> missoes = missaoRepository.buscarTodos();
            return Response.ok(missoes).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro ao listar missões.")).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        try {
            Missao missao = missaoRepository.buscarPorId(id);
            return Response.ok(missao).build();
        } catch (EntidadeNaoLocalizada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro ao buscar missão.")).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response editarMissao(@PathParam("id") int id, Missao missaoAtualizada) {
        if (missaoAtualizada == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", "Corpo vazio.")).build();
        }
        
        try {
            Missao missaoExistente = missaoRepository.buscarPorId(id);
            missaoExistente.setTitulo(missaoAtualizada.getTitulo());
            missaoExistente.setDescricao(missaoAtualizada.getDescricao());
            missaoExistente.setPontosRecompensa(missaoAtualizada.getPontosRecompensa());
            missaoExistente.setTipo(missaoAtualizada.getTipo());

            boolean sucesso = missaoRepository.editar(missaoExistente);
            
            if (sucesso) {
                return Response.ok(missaoExistente).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Falha ao editar.")).build();
            }
        } catch (EntidadeNaoLocalizada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response desativarMissao(@PathParam("id") int id) {
        boolean sucesso = missaoRepository.desativar(id);
        if (sucesso) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND) 
                    .entity(Map.of("erro", "Missão não encontrada ou já inativa.")).build();
        }
    }
}