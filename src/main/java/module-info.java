module org.dariusspr.ftransfer.ftransfer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.dariusspr.ftransfer.ftransfer to javafx.fxml;
    exports org.dariusspr.ftransfer.ftransfer;

    opens org.dariusspr.ftransfer.ftransfer.gui to javafx.fxml;
    exports org.dariusspr.ftransfer.ftransfer.gui;

    opens org.dariusspr.ftransfer.ftransfer.gui.controller to javafx.fxml;
    exports org.dariusspr.ftransfer.ftransfer.gui.controller;
}