package util;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize Database
        dao.DatabaseInitializer.initialize();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/main.fxml"));

        Scene scene = new Scene(loader.load(), 1200, 700);

        // Load CSS
        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm());

        stage.setTitle("Gestion de Pharmacie - Système de Stock");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
