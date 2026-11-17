package co.edu.unillanos.campusfest.application.usecase;

import co.edu.unillanos.campusfest.application.port.Reloj;
import co.edu.unillanos.campusfest.application.port.RepositorioEventos;
import co.edu.unillanos.campusfest.domain.Evento;

public class ConsultarCupos {

    private final RepositorioEventos repositorio;
    private final Reloj reloj;

    public ConsultarCupos(RepositorioEventos repositorio, Reloj reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    public int consultar(String eventoId) {
        Evento evento = repositorio.buscar(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("No existe un evento con el identificador: " + eventoId);
        }
        return evento.cuposDisponibles(reloj.ahora());
    }
}
