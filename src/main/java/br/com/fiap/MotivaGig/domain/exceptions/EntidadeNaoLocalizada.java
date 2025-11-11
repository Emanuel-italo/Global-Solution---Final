package br.com.fiap.motivagig.domain.exceptions;

public class EntidadeNaoLocalizada extends Exception{

  public EntidadeNaoLocalizada(String message) {
    super(message);
  }

  public EntidadeNaoLocalizada(String message, Throwable cause) {
    super(message, cause);
  }
}
