package br.com.fiap.motivagig.domain.service;


public interface ApiKeyValidator {


    boolean isValid(String apiKey);


    boolean isPresent(String apiKey);
}