package co.edu.unillanos.campusfest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;

import co.edu.unillanos.campusfest.application.port.Reloj;
import co.edu.unillanos.campusfest.application.port.RepositorioEventos;
import co.edu.unillanos.campusfest.application.usecase.ConsultarCupos;
import co.edu.unillanos.campusfest.application.usecase.EditarEvento;
import co.edu.unillanos.campusfest.application.usecase.RegistrarIngreso;
import co.edu.unillanos.campusfest.application.usecase.ReservarEntrada;
import co.edu.unillanos.campusfest.domain.Evento;
import co.edu.unillanos.campusfest.infrastructure.persistence.RepositorioEventosH2;
import co.edu.unillanos.campusfest.infrastructure.reloj.RelojDelSistema;
import co.edu.unillanos.campusfest.infrastructure.ui.PantallaCuposController;
import co.edu.unillanos.campusfest.infrastructure.ui.PantallaEventoController;
import co.edu.unillanos.campusfest.infrastructure.ui.PantallaIngresoController;
import co.edu.unillanos.campusfest.infrastructure.ui.PantallaReservaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class App extends Application {

    private static final String VISTAS_BASE = "/co/edu/unillanos/campusfest/vistas/";
    private Connection conexion;

    @Override
    public void start(Stage stage) throws Exception {
        conexion = DriverManager.getConnection("jdbc:h2:./campusfest;AUTO_SERVER=TRUE", "sa", "");
        RepositorioEventos repositorio = new RepositorioEventosH2(conexion);
        Reloj reloj = new RelojDelSistema();

        if (repositorio.buscar("EV-01") == null) {
            repositorio.guardar(new Evento(
                    "EV-01",
                    "Toque Musical - Black Heron 2026",
                    LocalDateTime.of(2026, 11, 20, 14, 0),
                    "Piscina - Sede Barcelona",
                    120,
                    5));
        }

        ConsultarCupos consultarCupos = new ConsultarCupos(repositorio, reloj);
        ReservarEntrada reservarEntrada = new ReservarEntrada(repositorio, reloj);
        EditarEvento editarEvento = new EditarEvento(repositorio, reloj);
        RegistrarIngreso registrarIngreso = new RegistrarIngreso(repositorio, reloj);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
                crearTab("CU1 · Consultar Cupos", "pantalla-cupos.fxml",
                        new PantallaCuposController(consultarCupos)),
                crearTab("CU2 · Reservar Entrada", "pantalla-reserva.fxml",
                        new PantallaReservaController(reservarEntrada)),
                crearTab("CU3 · Editar Evento", "pantalla-evento.fxml",
                        new PantallaEventoController(editarEvento)),
                crearTab("CU4 · Registrar Ingreso", "pantalla-ingreso.fxml",
                        new PantallaIngresoController(registrarIngreso))
        );

        Scene scene = new Scene(tabPane, 640, 480);
        scene.getStylesheets().add(getClass().getResource(VISTAS_BASE + "estilos.css").toExternalForm());

        stage.setTitle("CampusFest · Gestión de Eventos");
        stage.setScene(scene);
        stage.show();
    }

    private Tab crearTab(String titulo, String fxml, Object controlador) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VISTAS_BASE + fxml));
        loader.setController(controlador);
        Parent contenido = loader.load();
        Tab tab = new Tab(titulo, contenido);
        tab.setClosable(false);
        return tab;
    }

    @Override
    public void stop() throws Exception {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
