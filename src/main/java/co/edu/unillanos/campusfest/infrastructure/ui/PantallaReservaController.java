package co.edu.unillanos.campusfest.infrastructure.ui;

import co.edu.unillanos.campusfest.application.usecase.ReservarEntrada;
import co.edu.unillanos.campusfest.domain.Reserva;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PantallaReservaController {

    private final ReservarEntrada reservarEntrada;

    @FXML
    private TextField campoEventoId;

    @FXML
    private TextField campoEstudianteId;

    @FXML
    private TextField campoCodigoReserva;

    @FXML
    private Label etiquetaMensaje;

    public PantallaReservaController(ReservarEntrada reservarEntrada) {
        this.reservarEntrada = reservarEntrada;
    }

    @FXML
    private void solicitarReserva() {
        try {
            Reserva reserva = reservarEntrada.solicitarReserva(
                    campoEventoId.getText(),
                    campoEstudianteId.getText());
            campoCodigoReserva.setText(reserva.getCodigo());
            etiquetaMensaje.setText("Reserva pendiente creada. Código QR: " + reserva.getCodigo());
        } catch (Exception e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void confirmarReserva() {
        try {
            Reserva reserva = reservarEntrada.confirmarReserva(
                    campoEventoId.getText(),
                    campoCodigoReserva.getText());
            etiquetaMensaje.setText("Reserva confirmada para estudiante: " + reserva.getEstudianteId());
        } catch (Exception e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
