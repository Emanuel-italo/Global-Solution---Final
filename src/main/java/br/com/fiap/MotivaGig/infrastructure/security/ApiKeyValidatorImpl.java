package br.com.fiap.MotivaGig.infrastructure.security;


import br.com.fiap.MotivaGig.domain.service.ApiKeyValidator;


public class ApiKeyValidatorImpl implements ApiKeyValidator {

    private final String validApiKey;


    public ApiKeyValidatorImpl(String validApiKey) {
        this.validApiKey = validApiKey;
        System.out.println("Validador de API Key criado com a chave: " + validApiKey);
    }

    @Override
    public boolean isValid(String apiKey) {

        if (!isPresent(apiKey)) {
            System.err.println("API Key não está presente na requisição.");
            return false;
        }

        boolean eValida = this.validApiKey.equals(apiKey);
        if (!eValida) {
            System.err.println("API Key recebida é INVÁLIDA: " + apiKey);
        }
        return eValida;
    }

    @Override
    public boolean isPresent(String apiKey) {

        return apiKey != null && !apiKey.trim().isEmpty();
    }
}