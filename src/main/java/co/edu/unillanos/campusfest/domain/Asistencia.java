package co.edu.unillanos.campusfest.domain;

import java.time.LocalDateTime;

public class Asistencia {

    private final LocalDateTime hora;

    public Asistencia(LocalDateTime hora) {
        if (hora == null) {
            throw new IllegalArgumentException("La hora de asistencia es obligatoria.");
        }
        this.hora = hora;
    }

    public LocalDateTime getHora() {
        return hora;
    }
}
