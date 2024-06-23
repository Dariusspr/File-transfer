package org.dariusspr.ftransfer.ftransfer.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


import javafx.fxml.Initializable;
import org.dariusspr.ftransfer.ftransfer.Data.ClientInfo;
import org.dariusspr.ftransfer.ftransfer.Data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer.gui.ClientApplication;
import org.dariusspr.ftransfer.ftransfer.gui.SceneType;


import java.net.URL;
import java.util.ResourceBundle;

import static org.dariusspr.ftransfer.ftransfer.gui.ClientApplication.getStage;
import static org.dariusspr.ftransfer.ftransfer.gui.ClientApplication.setScene;
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

    @FXML
    private Button btnClose;

    private ClientInfo clientInfo;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeDraggable(bar, getStage());

        btnClose.setOnMouseClicked(e -> ClientApplication.close());

        clientInfo = new ClientInfo();
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
        setScene(SceneType.MAIN);
    }

    private boolean readClientInfo() {
        String name = tfName.getText();
        String ip = tfIP.getText();
        String port = tfPort.getText();

        clientInfo.setName(name);
        clientInfo.setIp(ip);
        clientInfo.setPort(port);

        return ClientInfo.isValid(clientInfo);
    }

}



