package com.bmstechpro;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FindingFiles {
    private static final String OUTPUT_FILE = "output.csv";

    public static void main(String[] args) {

        if (args.length !=2) {
            System.out.println("Usage: [root folder path][filter]");
            System.out.println("Example: [\"c:/favorite photos\"] [*.pdf]");
            return;
        }

        Path path = Paths.get(args[0]);
        boolean exists = Files.exists(path);
        if (!exists) {
            System.out.println("Unable to find root directory");
            return;
        }

        System.out.println(path.getFileName());
        System.out.println("Searching for files: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:s")));

        List<Path> matchesList = new ArrayList<>();
        String pattern = args[1];


        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);

        FileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Path name = file.getFileName();
                if (matcher.matches(name)) {
                    matchesList.add(name);
                }
                return FileVisitResult.CONTINUE;

            }
        };
        try {
            Files.walkFileTree(path, fileVisitor);
            System.out.println("File search completed: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:s")));
            System.out.println("Found: " + matchesList.size() + " files");
            System.out.println("Writing files to file");

            createCSVFile(matchesList, OUTPUT_FILE);
            System.out.println(OUTPUT_FILE + ": " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:s")));


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void createCSVFile(List<Path> fileList, String file) throws IOException {

        FileWriter out = new FileWriter(file);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader("File Name", "Path"))) {
            fileList.forEach((f) -> {

                try {
                    printer.printRecord(f.getFileName(),
                            f.toAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
