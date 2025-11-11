package br.com.fiap.motivagig.infrastructure.web.resource;


public class CheckInInput {
    

    private String humorRegistrado; 
    private String gatilho; 
    private Integer trabalhadorId; 

    
    
    public String getHumorRegistrado() {
        return humorRegistrado;
    }

    public void setHumorRegistrado(String humorRegistrado) {
        this.humorRegistrado = humorRegistrado;
    }

    public String getGatilho() {
        return gatilho;
    }

    public void setGatilho(String gatilho) {
        this.gatilho = gatilho;
    }

    public Integer getTrabalhadorId() {
        return trabalhadorId;
    }

    public void setTrabalhadorId(Integer trabalhadorId) {
        this.trabalhadorId = trabalhadorId;
    }
}