package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class ViewManager {

    private static BorderPane mainContainer;

    public static void setMainContainer(BorderPane container) {
        mainContainer = container;
    }

    public static void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewManager.class.getResource(fxmlPath));
            Parent view = loader.load();
            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur",
                    "Impossible de charger la vue: " + fxmlPath);
        }
    }

    public static <T> T loadViewWithController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ViewManager.class.getResource(fxmlPath));
            Parent view = loader.load();
            mainContainer.setCenter(view);
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Erreur",
                    "Impossible de charger la vue: " + fxmlPath);
            return null;
        }
    }
}
