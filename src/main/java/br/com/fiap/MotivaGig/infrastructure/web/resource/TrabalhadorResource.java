package br.com.fiap.motivagig.infrastructure.web.resource;

import java.util.List;
import java.util.Map;

import br.com.fiap.motivagig.domain.exceptions.EntidadeNaoLocalizada;
import br.com.fiap.motivagig.domain.model.Trabalhador;
import br.com.fiap.motivagig.domain.repository.TrabalhadorRepository;
import br.com.fiap.motivagig.infrastructure.persistence.JdbcTrabalhadorRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/api/trabalhadores") 
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrabalhadorResource {

    
    @Inject
    TrabalhadorRepository trabalhadorRepository; 

  
    @Inject
    JdbcTrabalhadorRepository jdbcTrabalhadorRepository;

    @POST
    public Response criarTrabalhador(Trabalhador trabalhador) { 
        
        System.out.println("Recebido POST /api/trabalhadores com dados: " + trabalhador);

        if (trabalhador == null || trabalhador.getNome() == null || trabalhador.getNome().trim().isEmpty() ||
                trabalhador.getCpf() == null || trabalhador.getCpf().isEmpty() || 
                trabalhador.getEmail() == null || trabalhador.getEmail().isEmpty()) {

            System.err.println("Erro: Dados inválidos (nome, cpf ou email vazios).");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "Dados incompletos. Nome, CPF e Email são obrigatórios."))
                    .build();
        }

        try {
            
            trabalhadorRepository.buscarPorCpf(trabalhador.getCpf()); 

            System.err.println("Erro: CPF " + trabalhador.getCpf() + " já cadastrado.");
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("erro", "CPF já cadastrado."))
                    .build();

        } catch (EntidadeNaoLocalizada e) {
            
            System.out.println("CPF " + trabalhador.getCpf() + " disponível. Tentando salvar...");

            
            Trabalhador trabalhadorSalvo = trabalhadorRepository.salvar(trabalhador); 

            if (trabalhadorSalvo != null && trabalhadorSalvo.getId() > 0) {
                System.out.println("Trabalhador criado com sucesso! ID: " + trabalhadorSalvo.getId());
                return Response.status(Response.Status.CREATED).entity(trabalhadorSalvo).build();
            } else {
                System.err.println("Erro: Falha ao salvar trabalhador no banco de dados.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(Map.of("erro", "Erro interno ao salvar trabalhador."))
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

   
    @POST
    @Path("/login")
    public Response login(LoginInput loginInput) {
        System.out.println("Recebido POST /api/trabalhadores/login com CPF: " + loginInput.getCpf());

        if (loginInput.getCpf() == null || loginInput.getSenha() == null || 
            loginInput.getCpf().isEmpty() || loginInput.getSenha().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", "CPF e Senha são obrigatórios."))
                    .build();
        }

        try {
            
            Trabalhador trabalhador = jdbcTrabalhadorRepository.buscarPorCpfParaLogin(loginInput.getCpf());

            
            if (trabalhador.getSenha().equals(loginInput.getSenha())) {
                System.out.println("Login com sucesso para: " + trabalhador.getNome());
                
               
                trabalhador.setSenha(null); 

                return Response.ok(trabalhador).build();
            } else {
                System.err.println("Senha inválida para o CPF: " + loginInput.getCpf());
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(Map.of("erro", "CPF ou Senha inválidos."))
                        .build();
            }

        } catch (EntidadeNaoLocalizada e) {
            System.err.println("Erro: " + e.getMessage());
          
            return Response.status(Response.Status.UNAUTHORIZED) 
                    .entity(Map.of("erro", "CPF ou Senha inválidos."))
                    .build();
        } catch (Exception e) {
            System.err.println("Erro inesperado no login: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno no login."))
                    .build();
        }
    }


    @GET
    public Response listarTrabalhadores() { 
        
        System.out.println("Recebido GET /api/trabalhadores"); 
        try {
            List<Trabalhador> trabalhadores = trabalhadorRepository.buscarTodos(); 
            System.out.println("Encontrados " + trabalhadores.size() + " trabalhadores."); 
            return Response.ok(trabalhadores).build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar todos os trabalhadores: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao buscar trabalhadores."))
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarTrabalhadorPorId(@PathParam("id") int id) { 
     
        System.out.println("Recebido GET /api/trabalhadores/" + id); 
        try {
            Trabalhador trabalhador = trabalhadorRepository.buscarPorId(id);
            System.out.println("Trabalhador encontrado: " + trabalhador.getNome());
            return Response.ok(trabalhador).build();
        } catch (EntidadeNaoLocalizada e) {
            System.err.println("Erro: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar trabalhador por ID: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao buscar trabalhador por ID."))
                    .build();
        }
    }

    @GET
    @Path("/cpf/{cpf}")
    public Response buscarTrabalhadorPorCpf(@PathParam("cpf") String cpf) { 
       
        System.out.println("Recebido GET /api/trabalhadores/cpf/" + cpf); 
        try {
            Trabalhador trabalhador = trabalhadorRepository.buscarPorCpf(cpf);
            System.out.println("Trabalhador encontrado pelo CPF: " + trabalhador.getNome());
            return Response.ok(trabalhador).build();
        } catch (EntidadeNaoLocalizada e) {
            System.err.println("Erro: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        } catch (Exception e) {
            System.err.println("Erro ao buscar trabalhador por CPF: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("erro", "Erro interno ao buscar trabalhador por CPF."))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizarTrabalhador(@PathParam("id") int id, Trabalhador trabalhador) { 
        
        System.out.println("Recebido PUT /api/trabalhadores/" + id); 
        if (trabalhador == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", "Corpo da requisição vazio.")).build();
        }

        trabalhador.setId(id); 
        try {
            
            trabalhadorRepository.buscarPorId(id); 

            boolean sucesso = trabalhadorRepository.editar(trabalhador);
            if (sucesso) {
                System.out.println("Trabalhador ID " + id + " atualizado.");
                Trabalhador trabalhadorAtualizado = trabalhadorRepository.buscarPorId(id);
                return Response.ok(trabalhadorAtualizado).build();
            } else {
                System.err.println("Falha no update (Trabalhador ID " + id + "), verificando se ainda existe...");
             
                try {
                    trabalhadorRepository.buscarPorId(id); 
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro interno ao atualizar trabalhador.")).build();
                } catch (EntidadeNaoLocalizada naoAchou) {
                    
                    return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", "Trabalhador não encontrado ou inativo para atualização.")).build();
                }
            }
        } catch (EntidadeNaoLocalizada e) {
            System.err.println("Erro: Trabalhador ID " + id + " não encontrado para atualizar.");
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar trabalhador: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro interno ao atualizar trabalhador.")).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response desativarTrabalhador(@PathParam("id") int id) { 
       
        System.out.println("Recebido DELETE /api/trabalhadores/" + id); 
        try {
            boolean sucesso = trabalhadorRepository.desativar(id);
            if (sucesso) {
                System.out.println("Trabalhador ID " + id + " desativado.");
                return Response.noContent().build(); 
            } else {
                System.err.println("Falha ao desativar trabalhador ID " + id + ", verificando se existe...");
                try {
                    trabalhadorRepository.buscarPorId(id); 
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro inesperado ao desativar.")).build();
                } catch (EntidadeNaoLocalizada e) {

                    return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", "Trabalhador não encontrado ou já inativo.")).build();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao desativar trabalhador: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("erro", "Erro interno ao desativar trabalhador.")).build();
        }
    }
}