package br.com.fiap.MotivaGig.domain.exceptions;


public interface ApiKeyValidator {


    boolean isValid(String apiKey);


    boolean isPresent(String apiKey);
}