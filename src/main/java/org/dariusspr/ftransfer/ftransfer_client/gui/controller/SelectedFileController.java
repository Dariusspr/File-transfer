package org.dariusspr.ftransfer.ftransfer_client.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class SelectedFileController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Text txtName;

    private File file;
    private String shortName;

    public void setFile(File file) {
        this.file = file;
        String name = file.getName();

        int baseNameIndex = name.lastIndexOf('/');
        shortName = baseNameIndex == -1 ? name : name.substring(baseNameIndex);
        if (shortName.length() > 15) {
            shortName = shortName.substring(0, 10) + "...";
        }
        txtName.setText(shortName);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnDelete.setOnMouseClicked(e -> {
            ClientLocalData.getData().getSelectedFiles().remove(file);
        });

        txtName.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                txtName.setText(file.getAbsolutePath());
            } else {
                txtName.setText(shortName);
            }
        });

    }
}
