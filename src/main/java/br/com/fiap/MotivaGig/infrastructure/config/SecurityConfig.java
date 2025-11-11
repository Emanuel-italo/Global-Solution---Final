package br.com.fiap.saudetodos.infrastructure.config;

import br.com.fiap.saudetodos.domain.service.ApiKeyValidator;
import br.com.fiap.saudetodos.infrastructure.security.ApiKeyValidatorImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class SecurityConfig {


    @ConfigProperty(name = "saudetodos.api.key")
    String chaveSecretaConfigurada;


    @Produces
    @ApplicationScoped
    public ApiKeyValidator apiKeyValidator() {

        return new ApiKeyValidatorImpl(chaveSecretaConfigurada);
    }
}