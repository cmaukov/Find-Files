package com.bmstechpro;
/* directory-tree-visitor
 * @created 09/24/2022
 * @author Konstantin Staykov
 */

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecursiveFileVisitor {

    public static void main(String args[]) {
        List<Directory> directoryList = new ArrayList<>();

//        FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<Path>() {
//
//            Directory directory = null;
//
//            @Override
//            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
//                    throws IOException {
//
//                directory = new Directory(dir.toFile().getPath());
//                System.out.println("-------------------------------------");
//                System.out.println("DIRECTORY NAME:" + dir.getFileName()
//                        + "LOCATION:" + dir.toFile().getPath());
//                System.out.println("-------------------------------------");
//
//                return FileVisitResult.CONTINUE;
////                boolean dirIsNotEmpty;
////                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
////                    dirIsNotEmpty = StreamSupport.stream(directoryStream.spliterator(), false).findFirst().isPresent();
////                }
////                if (dirIsNotEmpty) {
////
////                    return FileVisitResult.CONTINUE;
////                } else {
////                    return FileVisitResult.SKIP_SUBTREE;
////                }
//
//            }
//
//            @Override
//            public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes fileAttributes)
//                    throws IOException {
//                System.out.println("FILE NAME: " + visitedFile.getFileName());
//                if(directory!=null){
//                    directory.addFile(visitedFile.getFileName().toString());
//                }
//                return FileVisitResult.CONTINUE;
//            }
//
//            @Override
//            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                if(directory!=null){
//                    directoryList.add(directory);
//                }
//                return FileVisitResult.CONTINUE;
//            }
//        };


        FileSystem fileSystem = FileSystems.getDefault();
        Path rootPath = fileSystem.getPath("/Users/konstantinstaykov/Public/sandbox");
        CustomFileVisitor customFileVisitor = new CustomFileVisitor();
        try {

            Files.walkFileTree(rootPath, customFileVisitor);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("*****************************");
        System.out.println("preVisitDirectoryCount: " + customFileVisitor.getPreVisitDirectoryCount());
    }

    static class CustomFileVisitor implements FileVisitor<Path> {

        Map<String, List<String>> files = new HashMap<>();
        private int preVisitDirectoryCount = 0;

        public int getPreVisitDirectoryCount() {
            return preVisitDirectoryCount;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            preVisitDirectoryCount++;
            System.out.println("LOCATION:" + dir.toFile().getPath());
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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
    }


}
