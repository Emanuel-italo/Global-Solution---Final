package br.com.fiap.motivagig.domain.exceptions;


public interface ApiKeyValidator {


    boolean isValid(String apiKey);


    boolean isPresent(String apiKey);
}