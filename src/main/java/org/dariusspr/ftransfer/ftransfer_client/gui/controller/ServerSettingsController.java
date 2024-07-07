package org.dariusspr.ftransfer.ftransfer_client.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.dariusspr.ftransfer.ftransfer_common.ServerInfo;

import java.net.URL;
import java.util.ResourceBundle;

import static org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication.getSecondaryStage;
import static org.dariusspr.ftransfer.ftransfer_client.gui.utils.AlertUtils.createErrorAlert;
import static org.dariusspr.ftransfer.ftransfer_client.gui.utils.StageUtils.makeDraggable;

public class ServerSettingsController implements Initializable {

    @FXML
    private AnchorPane bar;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnSave;
    @FXML
    private TextField tfIp;
    @FXML
    private TextField tfPort;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeDraggable(bar, getSecondaryStage());
        btnClose.setOnMouseClicked(e -> getSecondaryStage().close());
        btnSave.setOnMouseClicked(this::saveSettings);
    }

    private void saveSettings(MouseEvent mouseEvent) {
        String ip = tfIp.getText();
        String portText = tfPort.getText();

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ignored) {
            createErrorAlert("Invalid info", "Port has to be a number");
            return;
        }

        boolean isValid = ServerInfo.isValid(ip, port);
        if (!isValid) {
            createErrorAlert("Invalid info", "Invalid server details");
            return;
        }

        ServerInfo.setIp(ip);
        ServerInfo.setPort(port);
        getSecondaryStage().close();
    }

}
