package br.com.fiap.saudetodos.infrastructure.web.resource;

import java.time.LocalDate;
import java.time.LocalTime;


public class ConsultaInput {
    private LocalDate data;
    private LocalTime hora;
    private String status;
    private Integer pacienteId;
    private Integer medicoId;


    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPacienteId() { return pacienteId; }
    public void setPacienteId(Integer pacienteId) { this.pacienteId = pacienteId; }
    public Integer getMedicoId() { return medicoId; }
    public void setMedicoId(Integer medicoId) { this.medicoId = medicoId; }
}