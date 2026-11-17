package co.edu.unillanos.campusfest.infrastructure.persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import co.edu.unillanos.campusfest.application.port.RepositorioEventos;
import co.edu.unillanos.campusfest.domain.Asistencia;
import co.edu.unillanos.campusfest.domain.EstadoReserva;
import co.edu.unillanos.campusfest.domain.Evento;
import co.edu.unillanos.campusfest.domain.Reserva;

public class RepositorioEventosH2 implements RepositorioEventos {

    private final Connection conexion;

    public RepositorioEventosH2(Connection conexion) {
        this.conexion = conexion;
        inicializarEsquema();
    }

    private void inicializarEsquema() {
        String script = leerScriptSchema();
        String[] sentencias = script.split(";");
        try (Statement stmt = conexion.createStatement()) {
            for (String sentencia : sentencias) {
                String limpia = sentencia.trim();
                if (!limpia.isEmpty()) {
                    stmt.execute(limpia);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al inicializar el esquema en H2: " + e.getMessage(), e);
        }
    }

    private String leerScriptSchema() {
        try (InputStream is = getClass().getResourceAsStream("/db/schema.sql")) {
            if (is == null) {
                throw new IllegalStateException("No se encontró el recurso /db/schema.sql");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error al leer /db/schema.sql: " + e.getMessage(), e);
        }
    }

    @Override
    public Evento buscar(String eventoId) {
        String sqlEvento = "SELECT id, nombre, fecha, lugar, aforo, bloqueados FROM EVENTO WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sqlEvento)) {
            ps.setString(1, eventoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                String id = rs.getString("id");
                String nombre = rs.getString("nombre");
                LocalDateTime fecha = rs.getTimestamp("fecha").toLocalDateTime();
                String lugar = rs.getString("lugar");
                int aforo = rs.getInt("aforo");
                int bloqueados = rs.getInt("bloqueados");

                List<Reserva> reservas = cargarReservas(id);
                return new Evento(id, nombre, fecha, lugar, aforo, bloqueados, reservas);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar el evento en H2: " + e.getMessage(), e);
        }
    }

    private List<Reserva> cargarReservas(String eventoId) throws SQLException {
        String sqlReservas =
                "SELECT r.codigo, r.estudiante_id, r.vence_en, r.estado, a.hora AS hora_asistencia " +
                "FROM RESERVA r " +
                "LEFT JOIN ASISTENCIA a ON r.codigo = a.reserva_codigo " +
                "WHERE r.evento_id = ?";
        List<Reserva> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sqlReservas)) {
            ps.setString(1, eventoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String codigo = rs.getString("codigo");
                    String estudianteId = rs.getString("estudiante_id");
                    LocalDateTime venceEn = rs.getTimestamp("vence_en").toLocalDateTime();
                    EstadoReserva estado = EstadoReserva.valueOf(rs.getString("estado"));
                    Timestamp tsAsistencia = rs.getTimestamp("hora_asistencia");
                    Asistencia asistencia = (tsAsistencia != null)
                            ? new Asistencia(tsAsistencia.toLocalDateTime())
                            : null;
                    lista.add(Reserva.reconstruir(codigo, estudianteId, venceEn, estado, asistencia));
                }
            }
        }
        return lista;
    }

    @Override
    public void guardar(Evento evento) {
        String mergeEvento =
                "MERGE INTO EVENTO (id, nombre, fecha, lugar, aforo, bloqueados) " +
                "KEY (id) VALUES (?, ?, ?, ?, ?, ?)";
        String mergeReserva =
                "MERGE INTO RESERVA (codigo, evento_id, estudiante_id, vence_en, estado) " +
                "KEY (codigo) VALUES (?, ?, ?, ?, ?)";
        String mergeAsistencia =
                "MERGE INTO ASISTENCIA (reserva_codigo, hora) " +
                "KEY (reserva_codigo) VALUES (?, ?)";

        try {
            boolean autoCommitOriginal = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement psEvento = conexion.prepareStatement(mergeEvento)) {
                    psEvento.setString(1, evento.getId());
                    psEvento.setString(2, evento.getNombre());
                    psEvento.setTimestamp(3, Timestamp.valueOf(evento.getFecha()));
                    psEvento.setString(4, evento.getLugar());
                    psEvento.setInt(5, evento.getAforo());
                    psEvento.setInt(6, evento.getBloqueados());
                    psEvento.executeUpdate();
                }

                try (PreparedStatement psReserva = conexion.prepareStatement(mergeReserva);
                     PreparedStatement psAsistencia = conexion.prepareStatement(mergeAsistencia)) {
                    for (Reserva r : evento.getReservas()) {
                        psReserva.setString(1, r.getCodigo());
                        psReserva.setString(2, evento.getId());
                        psReserva.setString(3, r.getEstudianteId());
                        psReserva.setTimestamp(4, Timestamp.valueOf(r.getVenceEn()));
                        psReserva.setString(5, r.getEstado().name());
                        psReserva.executeUpdate();

                        if (r.getAsistencia() != null) {
                            psAsistencia.setString(1, r.getCodigo());
                            psAsistencia.setTimestamp(2, Timestamp.valueOf(r.getAsistencia().getHora()));
                            psAsistencia.executeUpdate();
                        }
                    }
                }

                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(autoCommitOriginal);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al guardar el evento en H2: " + e.getMessage(), e);
        }
    }
}
