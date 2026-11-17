package co.edu.unillanos.campusfest.infrastructure.persistence;

import java.util.HashMap;
import java.util.Map;

import co.edu.unillanos.campusfest.application.port.RepositorioEventos;
import co.edu.unillanos.campusfest.domain.Evento;

public class RepositorioEventosEnMemoria implements RepositorioEventos {

    private final Map<String, Evento> almacenamiento = new HashMap<>();

    public RepositorioEventosEnMemoria() {
    }

    @Override
    public Evento buscar(String eventoId) {
        return almacenamiento.get(eventoId);
    }

    @Override
    public void guardar(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("El evento a guardar no puede ser nulo.");
        }
        almacenamiento.put(evento.getId(), evento);
    }
}
