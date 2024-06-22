package org.dariusspr.ftransfer.ftransfer.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


import javafx.fxml.Initializable;
import org.dariusspr.ftransfer.ftransfer.Data.ClientInfo;

import java.net.URL;
import java.util.ResourceBundle;

import static org.dariusspr.ftransfer.ftransfer.gui.ClientApplication.getStage;
import static org.dariusspr.ftransfer.ftransfer.gui.utils.AlertUtils.createErrorAlert;
import static org.dariusspr.ftransfer.ftransfer.gui.utils.StageUtils.makeDraggable;

public class LauncherController implements Initializable {


    @FXML
    private AnchorPane bar;

    @FXML
    private TextField tfIP;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfPort;

    @FXML
    private Button btnLaunch;

    private ClientInfo clientInfo;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeDraggable(bar, getStage());

        clientInfo = new ClientInfo();
        btnLaunch.setOnMouseClicked(this::launch);
    }

    private void launch(MouseEvent mouseEvent) {
        if (!readClientInfo()) {
            // TODO: improve alerts - more details
            createErrorAlert("Invalid info", "Invalid client details");
            return;
        }
        // TODO: save clientInfo
        // TODO: change view
    }

    private boolean readClientInfo() {
        String name = tfName.getText();
        String ip = tfIP.getText();
        String port = tfPort.getText();

        // TODO: add better validation
        if (name.isEmpty() || ip.isEmpty() || port.isEmpty()) {
            return false;
        }

        clientInfo.setName(name);
        clientInfo.setIp(ip);
        clientInfo.setPort(port);

        return true;
    }

}



