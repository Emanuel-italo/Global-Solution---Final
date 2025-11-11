package br.com.fiap.motivagig.infrastructure.web.resource;

import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Trabalhador;
import br.com.fiap.motivagig.domain.repository.PacienteRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;


@Path("/api/pacientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PacienteResource {

    @Inject
    PacienteRepository pacienteRepository;

    @POST
    public Response criarPaciente(Trabalhador paciente) {

        System.out.println("Recebido POST /api/pacientes com dados: " + paciente);

        if (paciente == null || paciente.getNome() == null || paciente.getNome().trim().isEmpty() ||
                paciente.getCpf() == null || paciente.getCpf().isEmpty() || 
                paciente.getEmail() == null || paciente.getEmail().isEmpty()) {

            System.err.println("Erro: Dados inválidos (nome, cpf ou email vazios).");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Dados incompletos. Nome, CPF e Email são obrigatórios."))
                    .build();
        }

        try {

            pacienteRepository.buscarPorCpf(paciente.getCpf());


            System.err.println("Erro: CPF " + paciente.getCpf() + " já cadastrado.");
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("erro", "CPF já cadastrado."))
                    .build();

        } catch (EntidadeNaoLocalizada e) {
           
            System.out.println("CPF " + paciente.getCpf() + " disponível. Tentando salvar...");


            Trabalhador pacienteSalvo = pacienteRepository.salvar(paciente);

            if (pacienteSalvo != null && pacienteSalvo.getId() > 0) {
                System.out.println("Paciente criado com sucesso! ID: " + pacienteSalvo.getId());
                return Response.status(Response.Status.CREATED).entity(pacienteSalvo).build();
            } else {
                System.err.println("Erro: Falha ao salvar paciente no banco de dados.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Map.of("erro", "Erro interno ao salvar paciente."))
                        .build();
            }
        } catch (Exception e) {

            System.err.println("Erro inesperado ao verificar CPF: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao verificar CPF."))
                    .build();
        }
    }

    @GET
    public Response listarPacientes() {
        System.out.println("Recebido GET /api/pacientes"); 
        try {
            List<Trabalhador> pacientes = pacienteRepository.buscarTodos();
            System.out.println("Encontrados " + pacientes.size() + " pacientes.");
            return Response.ok(pacientes).build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar todos os pacientes: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao buscar pacientes."))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPacientePorId(@PathParam("id") int id) {
        System.out.println("Recebido GET /api/pacientes/" + id); 
        try {
            Trabalhador paciente = pacienteRepository.buscarPorId(id);
            System.out.println("Paciente encontrado: " + paciente.getNome());
            return Response.ok(paciente).build();
        } catch (EntidadeNaoLocalizada e) {
            System.err.println("Erro: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por ID: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao buscar paciente por ID."))
                    .build();
        }
    }

    @GET
    @Path("/cpf/{cpf}")
    public Response buscarPacientePorCpf(@PathParam("cpf") String cpf) {
        System.out.println("Recebido GET /api/pacientes/cpf/" + cpf); 
        try {
            Trabalhador paciente = pacienteRepository.buscarPorCpf(cpf);
            System.out.println("Paciente encontrado pelo CPF: " + paciente.getNome());
            return Response.ok(paciente).build();
        } catch (EntidadeNaoLocalizada e) {
            System.err.println("Erro: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente por CPF: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao buscar paciente por CPF."))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizarPaciente(@PathParam("id") int id, Trabalhador paciente) {
        System.out.println("Recebido PUT /api/pacientes/" + id); 
        if (paciente == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", "Corpo da requisição vazio.")).build();
        }

        paciente.setId(id); 
        try {
            
            pacienteRepository.buscarPorId(id); 

            boolean sucesso = pacienteRepository.editar(paciente);
            if (sucesso) {
                System.out.println("Paciente ID " + id + " atualizado.");
                Trabalhador pacienteAtualizado = pacienteRepository.buscarPorId(id);
                return Response.ok(pacienteAtualizado).build();
            } else {
                System.err.println("Falha no update (paciente ID " + id + "), verificando se ainda existe...");
             
                try {
                    pacienteRepository.buscarPorId(id); 
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro interno ao atualizar paciente.")).build();
                } catch (EntidadeNaoLocalizada naoAchou) {
                    
                    return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", "Paciente não encontrado ou inativo para atualização.")).build();
                }
            }
        } catch (EntidadeNaoLocalizada e) {
            System.err.println("Erro: Paciente ID " + id + " não encontrado para atualizar.");
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar paciente: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro interno ao atualizar paciente.")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response desativarPaciente(@PathParam("id") int id) {
        System.out.println("Recebido DELETE /api/pacientes/" + id); 
        try {
            boolean sucesso = pacienteRepository.desativar(id);
            if (sucesso) {
                System.out.println("Paciente ID " + id + " desativado.");
                return Response.noContent().build(); 
            } else {
                System.err.println("Falha ao desativar paciente ID " + id + ", verificando se existe...");
                try {
                    pacienteRepository.buscarPorId(id); 
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro inesperado ao desativar.")).build();
                } catch (EntidadeNaoLocalizada e) {

                    return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", "Paciente não encontrado ou já inativo.")).build();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao desativar paciente: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro interno ao desativar paciente.")).build();
        }
    }
}