package com.bmstechpro;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FindingFiles {
    private static final String OUTPUT_FILE = "output.csv";

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: [root folder path][filter][maxDepth]");
            System.out.println("Example: [\"c:/favorite photos\"] [pdf] [5]");
            return;
        }

        Path path = Paths.get(args[0]);
        int maxDepth = 5;
        if (args.length == 3) {
            try {
                int param = Integer.parseInt(args[2]);
                if (param > 0 && param < Integer.MAX_VALUE) {
                    maxDepth = param;
                }
            } catch (NumberFormatException e) {
                System.out.println("invalid depth parameter");
            }
        }
        System.out.println("search depth: " + maxDepth);
        boolean exists = Files.exists(path);
        if (!exists) {
            System.out.println("Unable to find root directory");
            return;
        }

        System.out.println(path.getFileName());
        System.out.println("Searching for files: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:s")));
        List<Path> matchesList = new ArrayList<>();


        try (Stream<Path> pathStream = Files.walk(path, maxDepth)) {
            pathStream.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(args[1]))
                    .forEach(matchesList::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("File search completed: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:s")));
        System.out.println("Found: " + matchesList.size() + " files");
        if (matchesList.size() > 0) {
            try {
                createCSVFile(matchesList, OUTPUT_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private static void createCSVFile(List<Path> fileList, String file) throws IOException {

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
