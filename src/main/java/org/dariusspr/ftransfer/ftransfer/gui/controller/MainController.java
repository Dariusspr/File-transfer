package org.dariusspr.ftransfer.ftransfer.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.dariusspr.ftransfer.ftransfer.gui.ClientApplication;

import java.net.URL;
import java.util.ResourceBundle;

import static org.dariusspr.ftransfer.ftransfer.gui.ClientApplication.getStage;
import static org.dariusspr.ftransfer.ftransfer.gui.utils.StageUtils.makeDraggable;

public class MainController implements Initializable {

        @FXML
        private AnchorPane bar;

        @FXML
        private Button btnAddFiles;

        @FXML
        private Button btnClose;

        @FXML
        private Button btnSendTo;

        @FXML
        private ComboBox<?> comboClients;

        @FXML
        private VBox fileContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeDraggable(bar, getStage());

        btnClose.setOnMouseClicked(e -> ClientApplication.close());
    }
}
