package org.dariusspr.ftransfer.ftransfer_client.gui.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.dariusspr.ftransfer.ftransfer_client.Launcher;
import org.dariusspr.ftransfer.ftransfer_client.data.FileTransfer;
import org.dariusspr.ftransfer.ftransfer_client.service.SenderManager;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;
import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication;
import org.dariusspr.ftransfer.ftransfer_client.gui.utils.ReceiverMenuItem;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication.getPrimaryStage;
import static org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication.getSecondaryStage;
import static org.dariusspr.ftransfer.ftransfer_client.gui.utils.StageUtils.makeDraggable;

public class MainController implements Initializable {

    @FXML
    private AnchorPane bar;

    @FXML
    private Button btnAddFiles;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnSend;

    @FXML
    private VBox fileContainer;

    @FXML
    private  MenuButton btnReceivers;

    @FXML
    private FlowPane fpSelectedFiles;

    private ArrayList<ClientInfo> selectedReceivers;
    private ObservableList<File> selectedFiles;
    private final SenderManager senderManager = SenderManager.get();

    private Map<File, Node> selectedFileComponentsMap;
    private Map<FileTransfer, Node> fileTransferNodeMap;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        makeDraggable(bar, getPrimaryStage());

        btnClose.setOnMouseClicked(e -> ClientApplication.close());

        initializeComboBox();

        btnAddFiles.setOnMouseClicked(this::addFiles);

        btnSend.setOnMouseClicked(this::send);


        selectedFileComponentsMap = new HashMap<>();
        fileTransferNodeMap = new HashMap<>();

        ClientLocalData clientLocalData = ClientLocalData.getData();
        selectedReceivers = clientLocalData.getSelectedReceivers();

        selectedFiles = clientLocalData.getSelectedFiles();
        selectedFiles.addListener((ListChangeListener<File>) change -> {
            while(change.next()) {
                if (change.wasRemoved()) {
                    for (File file : change.getRemoved()) {
                        removeSelectedFileComponent(file);
                    }
                }
                else if (change.wasAdded()) {
                    for (File file : change.getAddedSubList()) {
                        createSelectedFileComponent(file);
                    }
                }
            }
        });

        ObservableList<FileTransfer> fileTransfers = clientLocalData.getAllFileTransfers();
        fileTransfers.addListener((ListChangeListener<FileTransfer>) change -> {
            while(change.next()) {
                if (change.wasAdded()) {
                    for (FileTransfer transfer : change.getAddedSubList()) {
                        createFileTransferComponent(transfer);
                    }
                } else if (change.wasRemoved()) {
                    for (FileTransfer fileTransfer : change.getRemoved()) {
                        Platform.runLater(() ->removeFileTransferComponent(fileTransfer));
                    }
                }
            }
        });
    }

    private void removeFileTransferComponent(FileTransfer fileTransfer) {
        Node component = fileTransferNodeMap.get(fileTransfer);
        fileContainer.getChildren().remove(component);
        fileTransferNodeMap.remove(fileTransfer);
    }

    private void send(MouseEvent mouseEvent) {
        selectedFiles.forEach(senderManager::addFile);
        senderManager.sendAll(selectedReceivers);
    }

    private void addFiles(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select files");
        List<File> list = fileChooser.showOpenMultipleDialog(getSecondaryStage());

        if (list == null || list.isEmpty())
            return;

        selectedFiles.addAll(list);
    }

    private void createSelectedFileComponent(File file) {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("file-component.fxml"));
        Node component;
        try {
            component = fxmlLoader.load();
            SelectedFileController controller = fxmlLoader.getController();
            controller.setFileName(file);

            fpSelectedFiles.getChildren().add(component);
            selectedFileComponentsMap.put(file, component);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeSelectedFileComponent(File file) {
        Node component = selectedFileComponentsMap.get(file);
        fpSelectedFiles.getChildren().remove(component);
        selectedFileComponentsMap.remove(file);
    }

    private void initializeComboBox() {
        ObservableList<ClientInfo> receivers = ClientLocalData.getData().getAvailableClients();
        updateReceiversMenu(receivers);

        // Update context menu items
        receivers.addListener((ListChangeListener<ClientInfo>) change -> {
            updateReceiversMenu(receivers);
        });

        // Update list of selected receivers
        btnReceivers.setOnHiding(e ->{
            selectedReceivers.clear();
            for (var item : btnReceivers.getItems()) {
                if (item instanceof ReceiverMenuItem menuItem) {
                    if (menuItem.isSelected()) {
                        ClientInfo info = (ClientInfo) menuItem.getUserData();
                        selectedReceivers.add(info);
                    }
                }
            }
        });
    }

    private void createFileTransferComponent(FileTransfer fileTransfer) {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("fileTransfer-component.fxml"));
        Node component;
        try {
            component = fxmlLoader.load();
            TransferController controller = fxmlLoader.getController();
            controller.setFile(fileTransfer.isFile());
            controller.setName(fileTransfer.getName());
            controller.setSize(fileTransfer.getSize());
            controller.setFromTo(fileTransfer.getFromTo());
            controller.setUnits(fileTransfer.getUnit());

            SimpleDoubleProperty progressProperty = fileTransfer.progressProperty();
            controller.setProgressProperty(progressProperty);
            SimpleObjectProperty<FileTransfer.TransferState> stateProperty = fileTransfer.stateProperty();
            controller.setStateProperty(stateProperty);

            Platform.runLater(() ->fileContainer.getChildren().add(component));
            fileTransferNodeMap.put(fileTransfer, component);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void updateReceiversMenu(ObservableList<ClientInfo> receivers) {
        btnReceivers.getItems().clear();
        for(ClientInfo receiver : receivers) {
            ReceiverMenuItem menuItem = new ReceiverMenuItem(receiver);
            btnReceivers.getItems().add(menuItem);
        }
    }


}
