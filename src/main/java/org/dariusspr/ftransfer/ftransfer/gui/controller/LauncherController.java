package org.dariusspr.ftransfer.ftransfer.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static org.dariusspr.ftransfer.ftransfer.gui.ClientApplication.getStage;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeDraggable(bar, getStage());
    }

}



