package com.bmstechpro;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FindingFiles {
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Usage: [root folder path][filter1] [filter2]...");
            System.out.println("Example: [\"c:/favorite photos\"] [jpg] [pdf]");
            return;
        }

        Path path = Paths.get(args[0]);
        boolean exists = Files.exists(path);
        if (!exists) {
            System.out.println("Unable to find root directory");
            return;
        }
        System.out.println("Searching for files: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:s")));

        List<String> filterList = Arrays.stream(args).skip(1).collect(Collectors.toList());

        try (Stream<Path> find = Files.find(path, 7, (p, attr) -> {
            for (String filter : filterList) {
                if (p.toString().endsWith(filter)) return true;
            }
            return false;
        })) {
            List<Path> filesList = find.collect(Collectors.toList());
            System.out.println("count = " + filesList.size());
            System.out.println("File search completed: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:s")));
            System.out.println("Writing search results to file...");
            createCSVFile(filesList, "output.csv");


        } catch (IOException e) {
            System.out.println("An error occurred...");
        }

    }

    public static void createCSVFile(List<Path> fileList, String file) throws IOException {

        FileWriter out = new FileWriter(file);
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT
                .withHeader("File Name", "Last Modified Time","Path"))) {
            fileList.forEach((f) -> {

                try {
                    printer.printRecord(f.toFile().getName(),
                            Files.getLastModifiedTime(f),
                            f.toFile().getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
