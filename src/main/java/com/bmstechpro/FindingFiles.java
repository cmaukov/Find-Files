package com.bmstechpro;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
        boolean exists = Files.exists(path);
        if (!exists) {
            System.out.println("Unable to find root directory");
            return;
        }

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

        List<Path> directoryList = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
            directoryStream.forEach(directoryList::add);

        } catch (IOException e) {
            System.out.println("unable to collect top level directories");
            e.printStackTrace();
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            final String prefix = String.valueOf(c);
            List<Path> pathList = directoryList.parallelStream().filter(path1 -> path1.getFileName().toString().toUpperCase().startsWith(prefix))
                    .collect(Collectors.toList());
            List<Path> matches = new ArrayList<>();

            for (Path path2 : pathList) {
                try (Stream<Path> pathStream = Files.walk(path2, maxDepth)) {
                    pathStream

                            .filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(args[1]))
                            .forEach(matches::add);

                } catch (Exception ignored) {

                }
            }
            try {
                if (matches.size() > 0) createCSVFile(matches, prefix + "_" + OUTPUT_FILE);
            } catch (IOException ignored) {

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
