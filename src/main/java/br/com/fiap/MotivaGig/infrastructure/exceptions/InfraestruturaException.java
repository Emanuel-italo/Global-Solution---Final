package br.com.fiap.motivagig.infrastructure.exceptions;

public class InfraestruturaException extends  RuntimeException {

  public InfraestruturaException(String message) {
    super(message);
  }

  public InfraestruturaException(String message, Throwable cause) {
    super(message, cause);
  }
}
