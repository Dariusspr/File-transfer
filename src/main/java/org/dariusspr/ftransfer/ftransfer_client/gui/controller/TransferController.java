package org.dariusspr.ftransfer.ftransfer_client.gui.controller;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import static org.dariusspr.ftransfer.ftransfer_client.data.FileTransfer.*;


public class TransferController {

    @FXML
    private Text txtFileDir;

    @FXML
    private Text txtFromTo;

    @FXML
    private Text txtName;

    @FXML
    private Text txtProgress;

    @FXML
    private Text txtSize;

    @FXML
    private Text txtState;

    @FXML
    private Text txtUnits;

    public void setFile(boolean isFile) {
        txtFileDir.setText(isFile ? "F" : "D");
    }

    public void setFromTo(String fromTo) {
        String shortName = fromTo;
        if (fromTo.length() > 20) {
            shortName = shortName.substring(0, 20) + "...";
        }
        txtFromTo.setText(shortName);
    }

    public void setName(String name) {
        String shortName = name;
        if (name.length() > 16) {
            shortName = shortName.substring(0, 16) + "...";
        }
        txtName.setText(shortName);
    }

    public void setProgressProperty(SimpleDoubleProperty progress) {
        txtProgress.textProperty().bind(progress.asString());
    }

    public void setSize(double size) {
        txtSize.setText(String.valueOf(size));
    }

    public void setStateProperty(SimpleObjectProperty<TransferState> state) {
        txtState.setText(String.valueOf(state.asString()));
    }

    public void setUnits(String units) {
        txtUnits.setText(units);
    }
}
