package org.dariusspr.ftransfer.ftransfer_client.gui.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;
import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication;
import org.dariusspr.ftransfer.ftransfer_client.gui.utils.ReceiverMenuItem;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.dariusspr.ftransfer.ftransfer_client.gui.ClientApplication.getStage;
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

        private ObservableList<ClientInfo> receivers;
        private ArrayList<ClientInfo> selectedReceivers;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedReceivers = new ArrayList<>();

        makeDraggable(bar, getStage());

        btnClose.setOnMouseClicked(e -> ClientApplication.close());

        initializeComboBox();
    }

    private void initializeComboBox() {

        receivers = ClientLocalData.getData().getAvailableClients();

        // Temporary data for testing purposes
        receivers.add(new ClientInfo("client 1", "localhost", 9999));
        receivers.add(new ClientInfo("client 2", "localhost", 2121));
        receivers.add(new ClientInfo("client 3", "localhost", 1111));

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
