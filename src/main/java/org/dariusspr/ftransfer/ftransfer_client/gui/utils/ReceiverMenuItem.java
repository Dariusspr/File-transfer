package org.dariusspr.ftransfer.ftransfer_client.gui.utils;

import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import org.dariusspr.ftransfer.ftransfer_common.ClientInfo;

public class ReceiverMenuItem extends CustomMenuItem {
    private final CheckBox checkBox;
    public ReceiverMenuItem(ClientInfo info) {
        super(new CheckBox(info.getName()));
        checkBox = (CheckBox) getContent();

        setUserData(info);
        setHideOnClick(false);
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }
}
