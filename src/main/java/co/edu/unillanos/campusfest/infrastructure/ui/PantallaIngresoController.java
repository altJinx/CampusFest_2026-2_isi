package co.edu.unillanos.campusfest.infrastructure.ui;

import co.edu.unillanos.campusfest.application.usecase.RegistrarIngreso;
import co.edu.unillanos.campusfest.domain.Asistencia;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.time.format.DateTimeFormatter;

public class PantallaIngresoController {

    private final RegistrarIngreso registrarIngreso;

    @FXML
    private TextField campoEventoId;

    @FXML
    private TextField campoCodigoQr;

    @FXML
    private Label etiquetaMensaje;

    public PantallaIngresoController(RegistrarIngreso registrarIngreso) {
        this.registrarIngreso = registrarIngreso;
    }

    @FXML
    private void registrarIngreso() {
        try {
            Asistencia asistencia = registrarIngreso.registrar(
                    campoEventoId.getText(),
                    campoCodigoQr.getText());
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            etiquetaMensaje.setText("Ingreso registrado a las: " + asistencia.getHora().format(formato));
        } catch (Exception e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
