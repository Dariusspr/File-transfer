package org.dariusspr.ftransfer.ftransfer_client.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


import javafx.fxml.Initializable;
import org.dariusspr.ftransfer.ftransfer_client.service.ReceiverServer;
import org.dariusspr.ftransfer.ftransfer_client.service.ServerConnection;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;
import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication;
import org.dariusspr.ftransfer.ftransfer_client.gui.SceneType;


import java.net.URL;
import java.util.ResourceBundle;

import static org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication.getPrimaryStage;
import static org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication.setScene;
import static org.dariusspr.ftransfer.ftransfer_client.gui.utils.AlertUtils.createErrorAlert;
import static org.dariusspr.ftransfer.ftransfer_client.gui.utils.StageUtils.makeDraggable;
import static org.dariusspr.ftransfer.ftransfer_client.service.ServerConnection.getLocalIp;

public class LauncherController implements Initializable {


    @FXML
    private AnchorPane bar;


    @FXML
    private TextField tfName;

    @FXML
    private TextField tfPort;

    @FXML
    private Button btnLaunch;

    @FXML
    private Button btnClose;

    private ClientInfo clientInfo;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeDraggable(bar, getPrimaryStage());

        btnClose.setOnMouseClicked(e -> ClientApplication.close());

        clientInfo = new ClientInfo();
        clientInfo.setIp(getLocalIp());

        btnLaunch.setOnMouseClicked(this::launch);
    }

    private void launch(MouseEvent mouseEvent) {
        if (!readClientInfo()) {
            // TODO: improve alerts - more details
            createErrorAlert("Invalid info", "Invalid client details");
            return;
        }
        ClientLocalData localData = ClientLocalData.getData();
        localData.updateClientInfo(clientInfo);

        if (!ServerConnection.get().start()) {
            createErrorAlert("Server connection failure", "Failed to connect to server");
            return;
        }
        ReceiverServer.get().start();

        setScene(SceneType.MAIN);
    }

    private boolean readClientInfo() {
        String name = tfName.getText();
        int port;
        try {
            port = Integer.parseInt(tfPort.getText());
        } catch (NumberFormatException e) {
            return false;
        }

        clientInfo.setName(name);
        clientInfo.setPort(port);
        return ClientInfo.isValid(clientInfo);
    }



}



