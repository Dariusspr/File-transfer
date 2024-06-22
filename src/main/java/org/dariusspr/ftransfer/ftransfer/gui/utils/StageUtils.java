package org.dariusspr.ftransfer.ftransfer.gui.utils;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.stage.Stage;

public class StageUtils {
    public static void makeDraggable(final Node node, Stage stage) {
        final DeltaPos  deltaPos = new DeltaPos();
        node.setOnMousePressed(mouseEvent -> {
            deltaPos.x = stage.getX() - mouseEvent.getScreenX();
            deltaPos.y = stage.getY() - mouseEvent.getScreenY();
            node.setCursor(Cursor.MOVE);
        });
        node.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + deltaPos.x);
            stage.setY(mouseEvent.getScreenY() + deltaPos.y);

        });
    }
    static class DeltaPos {double x, y;}
}
