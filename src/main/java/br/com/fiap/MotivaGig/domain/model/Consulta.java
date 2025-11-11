package br.com.fiap.MotivaGig.domain.exceptions;

import java.time.LocalDate;
import java.time.LocalTime;

public class Consulta {
    private int id;
    private LocalDate data;
    private LocalTime hora;
    private String status;
    private Paciente paciente;
    private Medico medico;
    private boolean ativo;


    public Consulta(int id, LocalDate data, LocalTime hora, String status, Paciente paciente, Medico medico) {
        this.id = id;
        setData(data);
        setHora(hora);
        setStatus(status);
        setPaciente(paciente);
        setMedico(medico);
        this.ativo = true;
    }


    public Consulta() {
        this.ativo = true;
    }


    public int getId() { return id; }
    public LocalDate getData() { return data; }
    public LocalTime getHora() { return hora; }
    public String getStatus() { return status; }
    public Paciente getPaciente() { return paciente; }
    public Medico getMedico() { return medico; }
    public boolean isAtivo() { return ativo; }


    public void setId(int id) { this.id = id; }

    public void setData(LocalDate data) {
        if (data == null || data.isBefore(LocalDate.now())) {
            System.err.println("Data da consulta inválida: " + data);

            return;
        }
        this.data = data;
    }

    public void setHora(LocalTime hora) {
        if (hora == null) {
            System.err.println("Hora da consulta não pode ser nula."); return;
        }

        if(hora.getHour() < 8 || hora.getHour() >= 18){
            System.err.println("Hora da consulta fora do expediente (8h-18h): " + hora);

            return;
        }
        this.hora = hora;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.err.println("Status da consulta não pode ser vazio.");

            return;
        }

        this.status = status.toUpperCase();
    }

    public void setPaciente(Paciente paciente) {
        if (paciente == null) {
            System.err.println("Paciente da consulta não pode ser nulo.");

            return;
        }
        this.paciente = paciente;
    }

    public void setMedico(Medico medico) {
        if (medico == null) {
            System.err.println("Médico da consulta não pode ser nulo.");

            return;
        }
        this.medico = medico;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }


    public void cancelar() {
        if ("AGENDADA".equals(this.status) || "CONFIRMADA".equals(this.status)) {
            this.status = "CANCELADA";
            this.ativo = false;
            System.out.println("Consulta ID " + this.id + " cancelada.");
        } else {
            System.err.println("Não é possível cancelar consulta com status: " + this.status);

        }
    }


    @Override
    public String toString() {
        String nomePaciente = paciente != null ? paciente.getNome() : "null";
        String nomeMedico   = medico   != null ? medico.getNome()   : "null";
        return "Consulta [id=" + id + ", data=" + data + ", hora=" + hora
                + ", status=" + status + ", paciente=" + nomePaciente + ", medico=" + nomeMedico
                + ", ativo=" + ativo + "]";
    }
}