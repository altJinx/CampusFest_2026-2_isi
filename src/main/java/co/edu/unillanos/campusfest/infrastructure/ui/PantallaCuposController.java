package co.edu.unillanos.campusfest.infrastructure.ui;

import co.edu.unillanos.campusfest.application.usecase.ConsultarCupos;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class PantallaCuposController {

    private final ConsultarCupos consultarCupos;

    @FXML
    private TextField campoEventoId;

    @FXML
    private Label etiquetaMensaje;

    public PantallaCuposController(ConsultarCupos consultarCupos) {
        this.consultarCupos = consultarCupos;
    }

    @FXML
    private void consultar() {
        try {
            int disponibles = consultarCupos.consultar(campoEventoId.getText());
            etiquetaMensaje.setText("Cupos disponibles: " + disponibles);
        } catch (Exception e) {
            etiquetaMensaje.setText(e.getMessage());
        }
    }
}
