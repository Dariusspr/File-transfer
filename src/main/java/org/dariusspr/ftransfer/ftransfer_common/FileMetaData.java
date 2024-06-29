package org.dariusspr.ftransfer.ftransfer_common;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @param size in bytes
 */
public record FileMetaData(ArrayList<Path> fileTree, long size) implements Serializable {
}
