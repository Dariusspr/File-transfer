package org.dariusspr.ftransfer.ftransfer_client.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileOutput {
    private final static String DEFAULT_FILE_SAVE_PATH = "./saved/";
    private final static String FILE_WORKING_MARKER = "_tmp";

    private String file;
    private Path localFilePathTmp;
    private FileOutputStream fileOutputStream;

    public FileOutput() {
        try {
            createSaveDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileOutput(String file) throws IOException {
        setFile(file);
    }

    public void setFile(String file) throws IOException {
        if (fileOutputStream != null) {
            closeFile();
        }

        this.file = file;
        localFilePathTmp = getTemporaryLocalPath(file);

        fileOutputStream = new FileOutputStream(localFilePathTmp.toFile());
    }

    private static Path getTemporaryLocalPath(String file) {
        int index = file.lastIndexOf('.');

        if (index == -1) {
            return Paths.get(DEFAULT_FILE_SAVE_PATH + file + FILE_WORKING_MARKER);
        } else {
            String base = file.substring(0, index);
            String extension = file.substring(index);
            return Paths.get(DEFAULT_FILE_SAVE_PATH + base + FILE_WORKING_MARKER + extension);
        }
    }

    public String getFile() {
        return file;
    }

    public void append(byte[] dataMessage) throws IOException {
        if (fileOutputStream == null) {
            throw new IOException("fileOutputStream is null");
        }
        fileOutputStream.write(dataMessage);
    }

    public void closeFile() throws IOException {
        if (fileOutputStream == null) {
            return;
        }
        if (localFilePathTmp == null || file == null) {
            fileOutputStream.close();
            return;
        }
        try {
            fileOutputStream.close();
        } finally {
            renameFile();
        }
    }

    private void renameFile() {
        Path path = localFilePathTmp.resolveSibling(file);
        if (localFilePathTmp.toFile().renameTo(path.toFile())) {
            System.err.println("File '" + localFilePathTmp + "' renamed to '" + path + "'.");
        } else {
            System.err.println("Failed to rename File '" + localFilePathTmp + "' to '" + path + "'.");
        }
    }

    public static void createSaveDirectory() throws IOException {
        Path localPath = Paths.get(DEFAULT_FILE_SAVE_PATH);
        Files.createDirectories(localPath);
    }

    public static void createDirectory(String directory) throws IOException {
        Path localPath = Paths.get(DEFAULT_FILE_SAVE_PATH + directory);
        Files.createDirectories(localPath);
    }

}
