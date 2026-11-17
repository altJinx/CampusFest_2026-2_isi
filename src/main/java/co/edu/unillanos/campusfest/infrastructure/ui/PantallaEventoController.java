package co.edu.unillanos.campusfest.infrastructure.ui;

import java.time.LocalDateTime;

import co.edu.unillanos.campusfest.application.usecase.EditarEvento;
import co.edu.unillanos.campusfest.domain.Evento;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PantallaEventoController {

    private final EditarEvento editarEvento;

    @FXML
    private TextField campoEventoId;

    @FXML
    private TextField campoNombre;

    @FXML
    private TextField campoFecha;

    @FXML
    private TextField campoLugar;

    @FXML
    private TextField campoAforo;

    @FXML
    private Label etiquetaMensaje;

    public PantallaEventoController(EditarEvento editarEvento) {
        this.editarEvento = editarEvento;
    }

    @FXML
    private void obtenerEvento() {
        try {
            Evento evento = editarEvento.obtener(campoEventoId.getText());
            campoNombre.setText(evento.getNombre());
            campoFecha.setText(evento.getFecha().toString());
            campoLugar.setText(evento.getLugar());
            campoAforo.setText(String.valueOf(evento.getAforo()));
            etiquetaMensaje.setText("Evento cargado: " + evento.getNombre());
        } catch (Exception e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void guardarCambios() {
        try {
            LocalDateTime fecha = LocalDateTime.parse(campoFecha.getText().trim());
            int nuevoAforo = Integer.parseInt(campoAforo.getText().trim());
            editarEvento.editar(
                    campoEventoId.getText(),
                    campoNombre.getText(),
                    fecha,
                    campoLugar.getText(),
                    nuevoAforo);
            etiquetaMensaje.setText("Evento actualizado correctamente.");
        } catch (Exception e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
