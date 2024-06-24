package org.dariusspr.ftransfer.ftransfer_client.gui.utils;

import javafx.scene.control.Alert;


public class AlertUtils {

    public static void createErrorAlert(String title, String context) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(context);
        alert.showAndWait();
    }

}
