package org.dariusspr.ftransfer.ftransfer_client.io;

import org.dariusspr.ftransfer.ftransfer_client.data.ClientLocalData;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class FileProcessor {
    private FileMetaData metaData;
    private final Path localParentPath;
    private long size;
    private final ArrayList<String> fileTree = new ArrayList<>();

    public FileProcessor(File file) {
        this.localParentPath = file.getParentFile().toPath();
        generateMetaData(file);
    }

    private void generateMetaData(File rootFile) {
        if (rootFile.isDirectory()) {

            FileVisitor<Path> fileVisitor = initFileVisitor(rootFile);

            try {
                Files.walkFileTree(rootFile.toPath(), fileVisitor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            size = rootFile.length();
            fileTree.add(rootFile.toPath().getFileName().toString());
        }
        String sender = ClientLocalData.getData().getInfo().getName();

        metaData = new FileMetaData(sender, fileTree, size);
    }

    private FileVisitor<Path> initFileVisitor(File rootFile) {
        Path rootPathParent = rootFile.getParentFile().toPath();
        return new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                Path path = rootPathParent.relativize(dir);
                fileTree.add(path.toString());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                Path path = rootPathParent.relativize(file);
                fileTree.add(path.toString());
                size += file.toFile().length();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        };
    }

    public FileMetaData getMetaData() {
        return metaData;
    }

    public Path getLocalParentPath() {
        return localParentPath;
    }
}
