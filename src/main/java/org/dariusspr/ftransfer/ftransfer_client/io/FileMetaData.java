package org.dariusspr.ftransfer.ftransfer_client.io;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @param size in bytes
 */
public record FileMetaData(ArrayList<Path> fileTree, long size) implements Serializable {
}
