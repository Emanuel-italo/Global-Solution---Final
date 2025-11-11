package br.com.fiap.saudetodos.infrastructure.web.resource;

import br.com.fiap.saudetodos.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.saudetodos.domain.model.Consulta;
import br.com.fiap.saudetodos.domain.model.Medico; // Para criar objeto
import br.com.fiap.saudetodos.domain.model.Paciente; // Para criar objeto
import br.com.fiap.saudetodos.domain.repository.ConsultaRepository;
import br.com.fiap.saudetodos.domain.repository.PacienteRepository; // Para validar paciente
// Importar MedicoRepository se/quando existir

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/consultas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConsultaResource {

    @Inject
    ConsultaRepository consultaRepository;

    @Inject
    PacienteRepository pacienteRepository;

    // @Inject MedicoRepository medicoRepository;

    @POST
    public Response agendarConsulta(ConsultaInput input) {

        if (input == null || input.getPacienteId() == null || input.getMedicoId() == null ||
                input.getData() == null || input.getHora() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Dados incompletos para agendar consulta " +
                            "(pacienteId, medicoId, data, hora)."))
                    .build();
        }

        Paciente paciente;
        Medico medico;

        try {

            paciente = pacienteRepository.buscarPorId(input.getPacienteId());



            medico = new Medico(input.getMedicoId(), "Dr. Simul Fictício", "",
                    "CRM"+input.getMedicoId(), "Clínica Geral");


        } catch (EntidadeNaoLocalizada e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Paciente ou Médico não encontrado: " + e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar paciente/médico para agendar: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao validar paciente/médico."))
                    .build();
        }


        Consulta novaConsulta = new Consulta();
        try {
            novaConsulta.setPaciente(paciente);
            novaConsulta.setMedico(medico);
            novaConsulta.setData(input.getData());
            novaConsulta.setHora(input.getHora());

            novaConsulta.setStatus(input.getStatus() != null ? input.getStatus() : "AGENDADA");
        } catch (IllegalArgumentException e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Dados inválidos: " + e.getMessage()))
                    .build();
        }


        Consulta consultaSalva = consultaRepository.salvar(novaConsulta);

        if (consultaSalva != null) {
            return Response.status(Response.Status.CREATED).entity(consultaSalva).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao salvar consulta."))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarConsultaPorId(@PathParam("id") int id) {
        try {
            Consulta consulta = consultaRepository.buscarPorId(id);
            return Response.ok(consulta).build();
        } catch (EntidadeNaoLocalizada e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar consulta por ID: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao buscar consulta."))
                    .build();
        }
    }

    @GET
    public Response listarConsultas(@QueryParam("pacienteId") Integer pacienteId) {
        try {
            List<Consulta> consultas;
            if (pacienteId != null) {
                consultas = consultaRepository.buscarPorPacienteId(pacienteId);
            } else {

                consultas = consultaRepository.buscarTodos();

            }
            return Response.ok(consultas).build();
        } catch (Exception e) {
            System.err.println("Erro ao listar consultas: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao listar consultas."))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizarConsulta(@PathParam("id") int id, ConsultaInput input) {

        if (input == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro",
                    "Corpo da requisição vazio.")).build();
        }

        try {

            Consulta consultaExistente = consultaRepository.buscarPorId(id);


            boolean modificado = false;
            if (input.getData() != null) {
                consultaExistente.setData(input.getData());
                modificado = true;
            }
            if (input.getHora() != null) {
                consultaExistente.setHora(input.getHora());
                modificado = true;
            }
            if (input.getStatus() != null && !input.getStatus().isEmpty()) {

                consultaExistente.setStatus(input.getStatus());
                modificado = true;
            }

            if (!modificado) {
                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro",
                        "Nenhum campo válido para atualização fornecido (data, hora, status).")).build();
            }


            boolean sucesso = consultaRepository.editar(consultaExistente);
            if (sucesso) {

                Consulta consultaAtualizada = consultaRepository.buscarPorId(id);
                return Response.ok(consultaAtualizada).build();
            } else {

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro",
                        "Erro interno ao atualizar consulta, ou consulta não está mais ativa.")).build();
            }

        } catch (EntidadeNaoLocalizada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", "Dados inválidos:" +
                    " " + e.getMessage())).build();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar consulta: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro",
                    "Erro interno ao atualizar consulta.")).build();
        }
    }


    @DELETE
    @Path("/{id}")
    public Response cancelarConsulta(@PathParam("id") int id) {
        try {

            Consulta consulta = consultaRepository.buscarPorId(id);


            if (!"AGENDADA".equals(consulta.getStatus()) && !"CONFIRMADA".equals(consulta.getStatus())) {
                return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro",
                        "Só é possível cancelar consultas com status AGENDADA ou CONFIRMADA.")).build();
            }

            boolean sucesso = consultaRepository.cancelar(id);
            if (sucesso) {
                return Response.noContent().build(); // 204 Sucesso
            } else {

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro",
                        "Erro interno ao cancelar consulta, ou consulta já inativa.")).build();
            }
        } catch (EntidadeNaoLocalizada e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            System.err.println("Erro ao cancelar consulta: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of(
                    "erro", "Erro interno ao cancelar consulta.")).build();
        }
    }
}