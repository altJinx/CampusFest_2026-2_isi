package co.edu.unillanos.campusfest.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Reserva {

    private final String codigo;
    private final String estudianteId;
    private final LocalDateTime venceEn;
    private EstadoReserva estado;
    private Asistencia asistencia;

    private Reserva(String codigo, String estudianteId, LocalDateTime venceEn,
                    EstadoReserva estado, Asistencia asistencia) {
        this.codigo = codigo;
        this.estudianteId = estudianteId;
        this.venceEn = venceEn;
        this.estado = estado;
        this.asistencia = asistencia;
    }

    public static Reserva pendiente(String estudianteId, LocalDateTime ahora) {
        if (estudianteId == null || estudianteId.isBlank()) {
            throw new IllegalArgumentException("El identificador del estudiante no puede estar vacío.");
        }
        if (ahora == null) {
            throw new IllegalArgumentException("La hora actual no puede ser nula.");
        }
        String codigoUnico = UUID.randomUUID().toString();
        return new Reserva(codigoUnico, estudianteId, ahora.plusMinutes(10), EstadoReserva.PENDIENTE, null);
    }

    public static Reserva reconstruir(String codigo, String estudianteId, LocalDateTime venceEn,
                                      EstadoReserva estado, Asistencia asistencia) {
        return new Reserva(codigo, estudianteId, venceEn, estado, asistencia);
    }

    public boolean ocupaCupo(LocalDateTime ahora) {
        if (estado == EstadoReserva.CONFIRMADA) {
            return true;
        }
        if (estado == EstadoReserva.PENDIENTE) {
            return ahora.isBefore(venceEn);
        }
        return false;
    }

    public void confirmar(LocalDateTime ahora) {
        if (estado == EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("La reserva ya fue confirmada.");
        }
        if (estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("No se puede confirmar una reserva cancelada.");
        }
        if (estado == EstadoReserva.VENCIDA || !ahora.isBefore(venceEn)) {
            this.estado = EstadoReserva.VENCIDA;
            throw new IllegalStateException("El plazo de 10 minutos para confirmar la reserva ha vencido.");
        }
        this.estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar() {
        if (estado == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva ya se encuentra cancelada.");
        }
        if (estado == EstadoReserva.VENCIDA) {
            throw new IllegalStateException("No se puede cancelar una reserva vencida.");
        }
        this.estado = EstadoReserva.CANCELADA;
    }

    public Asistencia registrarIngreso(LocalDateTime ahora) {
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("Solo las reservas confirmadas pueden registrar ingreso.");
        }
        if (asistencia != null) {
            throw new IllegalStateException("El ingreso para esta reserva ya fue registrado.");
        }
        this.asistencia = new Asistencia(ahora);
        return this.asistencia;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getEstudianteId() {
        return estudianteId;
    }

    public LocalDateTime getVenceEn() {
        return venceEn;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public Asistencia getAsistencia() {
        return asistencia;
    }
}
