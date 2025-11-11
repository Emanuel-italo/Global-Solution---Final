package br.com.fiap.saudetodos.domain.service;


public interface ApiKeyValidator {


    boolean isValid(String apiKey);


    boolean isPresent(String apiKey);
}