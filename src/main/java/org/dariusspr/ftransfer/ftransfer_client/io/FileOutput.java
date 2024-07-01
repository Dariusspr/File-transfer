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

    public FileOutput() {}

    public FileOutput(String file) throws IOException {
        setFile(file);
    }

    public void setFile(String file) throws IOException {
        if (fileOutputStream != null) {
            closeFile();
        }

        this.file = file;
        localFilePathTmp = Paths.get(DEFAULT_FILE_SAVE_PATH + file + FILE_WORKING_MARKER);

        fileOutputStream = new FileOutputStream(localFilePathTmp.toFile());
    }

    public void closeFile() throws IOException {
        if (fileOutputStream == null) {
            return;
        }
        if (localFilePathTmp == null || file == null) {
            fileOutputStream.close();
            return;
        }
        fileOutputStream.close();
        Files.move(localFilePathTmp, localFilePathTmp.resolveSibling(file));
    }

    public String getFile() {
        return file;
    }

    public static void createDirectory(String directory) throws IOException {
        Path localPath = Paths.get(DEFAULT_FILE_SAVE_PATH + directory);
        Files.createDirectories(localPath);
    }

    public void append(byte[] dataMessage) throws IOException {
        if (fileOutputStream == null) {
            throw new IOException("fileOutputStream is null");
        }
        fileOutputStream.write(dataMessage);
    }
}
