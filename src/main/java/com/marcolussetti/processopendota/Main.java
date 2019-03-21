package com.marcolussetti.processopendota;

import com.jsoniter.JsonIterator;
import com.jsoniter.annotation.JsonObject;
import com.jsoniter.any.Any;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.zip.GZIPInputStream;


public class Main {

    public static void main(String[] args) {

        String pathString = "D://datasets//opendota//matches.gz";
        Path path = Paths.get(pathString);

        CsvParserSettings parserSettings = new CsvParserSettings();
        RowListProcessor rowProcessor = new RowListProcessor();

        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setMaxCharsPerColumn(100000);
        CsvParser parser = new CsvParser(parserSettings);

        InputStream fileIs = null;
        BufferedInputStream bufferedIs = null;
        GZIPInputStream gzipIs = null;
        try {
            fileIs = Files.newInputStream(path);
            bufferedIs = new BufferedInputStream(fileIs, 65535);
            gzipIs = new GZIPInputStream(bufferedIs);
        } catch (IOException e) {
            closeSafely(gzipIs);
            closeSafely(bufferedIs);
            closeSafely(fileIs);
            throw new UncheckedIOException(e);
        }

        THashMap<Long, THashMap<Integer, Integer>> map = new THashMap<>();

        THashSet<Long> allDates = new THashSet<>();

        int recordCounter = 0;


        for(Record record: parser.iterateRecords(gzipIs)) {
            long start_time = record.getLong("start_time");
//            System.out.println(record.getValues()[record.getValues().length - 1]);
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(start_time),
                    ZoneId.of("UTC")
            );
            Long date = dateTime.toLocalDate().toEpochDay();
            String pgroup = record.getString(26);
            JsonIterator iterator = JsonIterator.parse(pgroup);

            ArrayList<Integer> heroes = new ArrayList<>();

            try {
                Any whatever = iterator.read(Any.class);
                Map<String, Any> whateverMap = whatever.asMap();
                whateverMap.forEach((index , object) -> {
                    int heroId = object.get("hero_id").toInt();
                    heroes.add(heroId);
                });

            } catch (IOException e) {
                e.printStackTrace();
            }

            THashMap<Integer, Integer> todayMap = map.getOrDefault(date, new THashMap<Integer, Integer>());
//
            heroes.forEach(hero -> {
                Integer count = todayMap.getOrDefault(hero, 0) + 1;
                todayMap.put(hero, count);
            });

            map.put(date, todayMap);

            allDates.add(date);

            //System.out.println(map.size());
            recordCounter++;
            if (recordCounter % 10000 == 0) {
                System.out.println("" + (recordCounter / 1000) + "K" + " - " + allDates.size() + " days");
            }

        }

    }

        private static void closeSafely(Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }

}
