package org.dariusspr.ftransfer.ftransfer_client.gui.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;
import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication;
import org.dariusspr.ftransfer.ftransfer_client.gui.utils.ReceiverMenuItem;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
        private Button btnSendTo;

        @FXML
        private VBox fileContainer;

        @FXML
        private  MenuButton btnReceivers;

        private ArrayList<ClientInfo> selectedReceivers;
        private ObservableList<File> selectedFiles;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ClientLocalData clientLocalData = ClientLocalData.getData();
        selectedReceivers = clientLocalData.getSelectedReceivers();
        makeDraggable(bar, getPrimaryStage());

        btnClose.setOnMouseClicked(e -> ClientApplication.close());

        initializeComboBox();

        selectedFiles = clientLocalData.getSelectedFiles();
        btnAddFiles.setOnMouseClicked(this::addFiles);
    }

    private void addFiles(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select files");
        List<File> list = fileChooser.showOpenMultipleDialog(getSecondaryStage());

        if (list == null || list.isEmpty())
            return;

        selectedFiles.addAll(list);
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



    private void updateReceiversMenu(ObservableList<ClientInfo> receivers) {
        btnReceivers.getItems().clear();
        for(ClientInfo receiver : receivers) {
            ReceiverMenuItem menuItem = new ReceiverMenuItem(receiver);
            btnReceivers.getItems().add(menuItem);
        }
    }


}
