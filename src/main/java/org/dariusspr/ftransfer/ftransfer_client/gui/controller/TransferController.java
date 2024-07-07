package org.dariusspr.ftransfer.ftransfer_client.gui.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.dariusspr.ftransfer.ftransfer_client.data.FileTransfer;

import static org.dariusspr.ftransfer.ftransfer_client.data.FileTransfer.*;


public class TransferController {

    @FXML
    private HBox container;
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

    private FileTransfer fileTransfer;

    public void setFileField(boolean isFile) {
        txtFileDir.setText(isFile ? "F" : "D");
    }

    public void setFromToField(String fromTo) {
        String shortName = fromTo;
        if (fromTo.length() > 20) {
            shortName = shortName.substring(0, 20) + "...";
        }
        txtFromTo.setText(shortName);
    }

    public void setNameField(String name) {
        String shortName = name;
        if (name.length() > 16) {
            shortName = shortName.substring(0, 16) + "...";
        }
        txtName.setText(shortName);
    }

    public void bindProgressField(SimpleDoubleProperty progress) {
        txtProgress.textProperty().bind(progress.asString());
    }

    public void setSizeField(double size) {
        txtSize.setText(String.valueOf(size));
    }

    public void bindStateField(SimpleObjectProperty<TransferState> state) {
        txtState.textProperty().bind(state.asString());
    }

    public void setUnits(String units) {
        txtUnits.setText(units);
    }

    public void init(FileTransfer fileTransfer) {
        this.fileTransfer = fileTransfer;
        initFields();

        ContextMenu componentContextMenu = initComponentContextMenu();
        addContextMenuToContainer(componentContextMenu);
    }

    private void addContextMenuToContainer(ContextMenu componentContextMenu) {
        container.setOnContextMenuRequested((ContextMenuEvent event) -> {
            if (componentContextMenu.getItems().size() != 1) {
                MenuItem pauseResumeItem = componentContextMenu.getItems().getFirst();
                MenuItem cancelItem = componentContextMenu.getItems().get(1);
                MenuItem deleteItem = componentContextMenu.getItems().getLast();

                switch (fileTransfer.getState()) {
                    case PENDING -> {
                        cancelItem.setDisable(true);
                        pauseResumeItem.setDisable(true);
                        deleteItem.setDisable(true);
                    }
                    case PAUSED -> {
                        pauseResumeItem.setDisable(false);
                        cancelItem.setDisable(false);
                        deleteItem.setDisable(false);
                        pauseResumeItem.setText("Resume");
                    }
                    case SENDING, RECEIVING -> {
                        pauseResumeItem.setDisable(false);
                        cancelItem.setDisable(false);
                        deleteItem.setDisable(false);
                        pauseResumeItem.setText("Pause");
                    }
                    case SENT, RECEIVED, ERROR, CANCELLED -> {
                        componentContextMenu.getItems().remove(pauseResumeItem);
                        componentContextMenu.getItems().remove(cancelItem);
                        deleteItem.setDisable(false);
                    }
                }
            }
            componentContextMenu.show(container, event.getScreenX(), event.getScreenY());
        });
    }

    private void initFields() {
        setFileField(fileTransfer.isFile());
        setNameField(fileTransfer.getName());
        setSizeField(fileTransfer.getSize());
        setFromToField(fileTransfer.getFromTo());
        setUnits(fileTransfer.getUnit());

        SimpleDoubleProperty progressProperty = fileTransfer.progressProperty();
        bindProgressField(progressProperty);
        SimpleObjectProperty<FileTransfer.TransferState> stateProperty = fileTransfer.stateProperty();
        bindStateField(stateProperty);
    }

    private ContextMenu initComponentContextMenu() {
        ContextMenu cm = new ContextMenu();

        MenuItem item1 = new MenuItem("Pause");
        item1.setOnAction(eventHandler -> {
            System.out.println("Pause");
            fileTransfer.getManager().setPaused(item1.getText().equals("Pause"));
        });

        MenuItem item2 = new MenuItem("Cancel");
        item2.setOnAction(eventHandler -> {
            fileTransfer.getManager().cancel();
        });

        MenuItem item3 = new MenuItem("Delete");
        item3.setOnAction(eventHandler -> {
            fileTransfer.getManager().delete();
        });

        cm.getItems().addAll(item1, item2, item3);
        return cm;
    }
}
