module org.dariusspr.ftransfer.ftransfer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.dariusspr.ftransfer.ftransfer_client to javafx.fxml;
    exports org.dariusspr.ftransfer.ftransfer_client;

    opens org.dariusspr.ftransfer.ftransfer_client.gui to javafx.fxml;
    exports org.dariusspr.ftransfer.ftransfer_client.gui;

    opens org.dariusspr.ftransfer.ftransfer_client.gui.controller to javafx.fxml;
    exports org.dariusspr.ftransfer.ftransfer_client.gui.controller;

    opens org.dariusspr.ftransfer.ftransfer_client.data to javafx.fxml;
    exports org.dariusspr.ftransfer.ftransfer_client.data;

}