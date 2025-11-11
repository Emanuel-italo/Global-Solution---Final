package br.com.fiap.saudetodos.infrastructure.security;
import br.com.fiap.saudetodos.domain.service.ApiKeyValidator;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;


@Provider
public class ApiKeyFilter implements ContainerRequestFilter {

    @Inject
    ApiKeyValidator apiKeyValidator;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

       
        if (requestContext.getUriInfo().getPath().contains("swagger") || requestContext.getUriInfo().getPath().contains("q/swagger-ui")) {
            return;
        }

        
        if (requestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            return;
        }

        
        String apiKey = requestContext.getHeaderString("X-API-KEY");
        if (apiKey == null || !apiKeyValidator.isValid(apiKey)) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity("API Key inválida ou ausente.")
                        .build());
        }
    }
}