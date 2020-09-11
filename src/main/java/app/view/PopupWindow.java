package app.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Popup window for displaying information.
 *
 * @author Si Tran
 *
 */
public class PopupWindow {

    public static void createPopupWindow(String title, String text) {
        Alert alert = new Alert(AlertType.INFORMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(PopupWindow.class.getClassLoader().getResourceAsStream("img/help-16.png")));
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
