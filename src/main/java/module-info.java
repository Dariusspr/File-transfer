module org.dariusspr.ftransfer.ftransfer {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.dariusspr.ftransfer.ftransfer to javafx.fxml;
    exports org.dariusspr.ftransfer.ftransfer;
}