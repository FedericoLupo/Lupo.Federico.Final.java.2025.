package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import service.GestionVehiculos;
import model.Vehiculo;

public class App extends Application {
    public static GestionVehiculos<Vehiculo> gestor = new GestionVehiculos<>();

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/main_view.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Gestión Vehículos - Final");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}