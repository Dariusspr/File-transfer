package org.dariusspr.ftransfer.ftransfer_client.io;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @param size in bytes
 */
public record FileMetaData(ArrayList<String> fileTree, long size) implements Serializable {
}
