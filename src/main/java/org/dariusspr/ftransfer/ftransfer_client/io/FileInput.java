package org.dariusspr.ftransfer.ftransfer_client.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class FileInput implements AutoCloseable{
    private static final int CHUNK_SIZE = 65536; // 64 KB

    private Path localPath;
    private FileInputStream fileInputStream;
    private byte[] chunks = new byte[CHUNK_SIZE];;

    public FileInput() {}

    public FileInput(Path localPath) {
        this.localPath = localPath;
    }

    public void setFile(Path filePath) throws IOException {
        if (fileInputStream != null) {
            closeFile();
        }

        localPath = filePath;
        fileInputStream = new FileInputStream(localPath.toFile());
    }

    public byte[] readChunks() throws IOException {
        int bytesRead = fileInputStream.read(chunks);
        return bytesRead == -1 ? null : Arrays.copyOf(chunks, bytesRead);
    }

    @Override
    public void close() throws Exception {
        closeFile();
    }

    public void closeFile() throws IOException {
        if (fileInputStream == null) {
            return;
        }
        fileInputStream.close();
    }
}
