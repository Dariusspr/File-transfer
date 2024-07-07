package org.dariusspr.ftransfer.ftransfer_client.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dariusspr.ftransfer.ftransfer_client.Launcher;
import org.dariusspr.ftransfer.ftransfer_client.service.ReceiverServer;
import org.dariusspr.ftransfer.ftransfer_client.service.SenderManager;
import org.dariusspr.ftransfer.ftransfer_client.service.ServerConnection;

import java.io.IOException;

public class ClientApplication extends Application {
    private static Stage primaryStage;
    private static Stage secondaryStage;


    public static void start() {
        launch();
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        setScene(SceneType.LAUNCHER);
        primaryStage.show();
    }

    public static void setScene(SceneType type) {
        try {
            Scene scene;
            if (type == SceneType.LAUNCHER) {
                scene = getLauncherScene();
            } else if (type == SceneType.MAIN) {
                scene = getMainScene();
            } else {
                throw new IllegalStateException("Illegal scene type");
            }
            primaryStage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Scene getLauncherScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("launch-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }

    private static Scene getMainScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        return scene;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static Stage initSecondaryStage() {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        return stage;
    }

    public static Stage getSecondaryStage() {
        return secondaryStage == null ? secondaryStage = initSecondaryStage() : secondaryStage;
    }

    public static void close() {
        ServerConnection serverConnection = ServerConnection.get();
        if (serverConnection.isRunning()) {
            serverConnection.stop();
        }
        if (ReceiverServer.get().isRunning()) {
            ReceiverServer.get().stop();
        }
        SenderManager.get().stop();
        primaryStage.close();
    }
}