package org.dariusspr.ftransfer.ftransfer_client.io;


import org.dariusspr.ftransfer.ftransfer_common.FileMetaData;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class FileProcessor {
    private FileMetaData metaData;
    private final Path localParentPath;

    private long size = 0;
    private final ArrayList<Path> fileTree = new ArrayList<>();

    public FileProcessor(File file) {
        this.localParentPath = file.getParentFile().toPath();
        generateMetaData(file);
    }

    private void generateMetaData(File rootFile) {
        if (rootFile.isDirectory()) {

            Path rootPathParent = rootFile.getParentFile().toPath();
            FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    fileTree.add(rootPathParent.relativize(dir));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    fileTree.add(rootPathParent.relativize(file));
                    size += file.toFile().length();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            };

            try {
                Files.walkFileTree(rootPathParent, fileVisitor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            size = rootFile.length();
            fileTree.add(rootFile.toPath().getFileName());
        }
        metaData = new FileMetaData(fileTree, size);
    }

    


    public FileMetaData getMetaData() {
        return metaData;
    }

    public Path getLocalParentPath() {
        return localParentPath;
    }
}
