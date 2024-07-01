package org.dariusspr.ftransfer.ftransfer_client.io;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class FileProcessor {
    private FileMetaData metaData;
    private final Path localParentPath;

    private long size = 0;
    private final ArrayList<String> fileTree = new ArrayList<>();

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
                    Path path = rootPathParent.relativize(dir);
                    fileTree.add(path.toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path path = rootPathParent.relativize(file);
                    fileTree.add(path.toString());
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
                Files.walkFileTree(rootFile.toPath(), fileVisitor);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            size = rootFile.length();
            fileTree.add(rootFile.toPath().getFileName().toString());
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
