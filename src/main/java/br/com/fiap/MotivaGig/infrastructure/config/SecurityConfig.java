package br.com.fiap.motivagig.infrastructure.config;

import br.com.fiap.motivagig.domain.service.ApiKeyValidator;
import br.com.fiap.motivagig.infrastructure.security.ApiKeyValidatorImpl;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@ApplicationScoped
public class SecurityConfig {


    @ConfigProperty(name = "motivagig.api.key")
    String chaveSecretaConfigurada;


    @Produces
    @ApplicationScoped
    public ApiKeyValidator apiKeyValidator() {

        return new ApiKeyValidatorImpl(chaveSecretaConfigurada);
    }
}