package com.bmstechpro;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class FindingFiles {
    private static final String OUTPUT_FILE = "output.csv";

    public static void main(String[] args) throws IOException {

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

        String location = "/Users/konstantinstaykov/Public/sandbox";
        String filter = "pdf";


        String search = "1,2,3,4,5,6,7,8,9,0,_,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z";

        String[] mapKeys = search.split(",");

        List<Path> paths = listSourceFiles(path);

        Map<String, List<Path>> searchMap = getMap(mapKeys);

        paths.forEach(p -> {

            for (String key : mapKeys) {
                if (p.getFileName().toString().toUpperCase().startsWith(key)) {
                    searchMap.get(key).add(p);
                }
            }
        });

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (String key : searchMap.keySet()) {
            List<Path> searchGroup = searchMap.get(key);
            if (searchGroup.size() > 0) {
                executorService.execute(new Playground.SearchTask(key, searchGroup, filter));
            }
        }

        executorService.shutdown();


    }


    private static Map<String, List<Path>> getMap(String[] mapKeys) {
        Map<String, List<Path>> map = new HashMap<>();
        for (String mapKey : mapKeys) {
            map.put(mapKey, new ArrayList<>());
        }
        return map;
    }

    private static List<Path> listSourceFiles(Path dir) throws IOException {
        List<Path> result = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path entry : stream) {
                result.add(entry);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encounted during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return result;
    }

    private static synchronized void createCSVFile(List<Path> fileList, String file) {

        FileWriter out = null;
        try {
            out = new FileWriter(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("File Name", "Path"))) {
            fileList.forEach((f) -> {

                try {
                    printer.printRecord(f.getFileName(),
                            f.toAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class SearchTask implements Runnable {
        private final List<Path> searchGroup;
        private final String key;
        private final String filter;

        public SearchTask(String key, List<Path> searchGroup, String filter) {
            this.key = key;
            this.searchGroup = searchGroup;
            this.filter = filter;
        }

        @Override
        public void run() {
            List<Path> foundMatches = new ArrayList<>();
            for (Path path : searchGroup) {
                try (Stream<Path> pathStream = Files.find(path, 5, (p, a) -> p.getFileName().toString().endsWith(filter))) {
                    pathStream.forEach(foundMatches::add);
                } catch (IOException ignored) {

                }
            }
            if (foundMatches.size() > 0) createCSVFile(foundMatches, key + "_" + OUTPUT_FILE);

        }
    }
}
