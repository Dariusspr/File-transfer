package org.dariusspr.ftransfer.ftransfer.gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dariusspr.ftransfer.ftransfer.Launcher;

import java.io.IOException;
import java.util.Objects;

public class ClientApplication extends Application {
    private static Stage stage;



    @Override
    public void start(Stage aStage) {
        stage = aStage;
        stage.initStyle(StageStyle.TRANSPARENT);

        setScene(SceneType.LAUNCHER);
        stage.show();
    }

    public static void start() {
        launch();
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
            stage.setScene((scene));

        } catch (IOException e) {
            // TODO: improve exception handling
            e.printStackTrace();
        }
    }



    public static void close() {
        stage.close(); // TODO: proper way
    }

    public static Stage getStage() {
        return stage;
    }


    private static Scene getLauncherScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("launch-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        return  scene;
    }
    private static Scene getMainScene() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setFill(Color.TRANSPARENT);
        return  scene;
    }
}