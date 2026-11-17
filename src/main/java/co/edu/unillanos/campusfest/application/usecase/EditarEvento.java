package co.edu.unillanos.campusfest.application.usecase;

import java.time.LocalDateTime;

import co.edu.unillanos.campusfest.application.port.Reloj;
import co.edu.unillanos.campusfest.application.port.RepositorioEventos;
import co.edu.unillanos.campusfest.domain.Evento;

public class EditarEvento {

    private final RepositorioEventos repositorio;
    private final Reloj reloj;

    public EditarEvento(RepositorioEventos repositorio, Reloj reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    public Evento obtener(String eventoId) {
        Evento evento = repositorio.buscar(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con el identificador: " + eventoId);
        }
        return evento;
    }

    public void editar(String eventoId, String nombre, LocalDateTime fecha,
                       String lugar, int nuevoAforo) {
        Evento evento = obtener(eventoId);
        evento.cambiarAforo(nuevoAforo, reloj.ahora());
        evento.cambiarDatos(nombre, fecha, lugar);
        repositorio.guardar(evento);
    }
}
