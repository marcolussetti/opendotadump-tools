package com.marcolussetti.processopendota;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.JsonStream;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import org.simpleflatmapper.csv.CsvParser;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Command(description = "Process OpenDota Matches File",
        name = "processopendota",
        mixinStandardHelpOptions = true,
        version = "processopendota 0.3")
class ProcessOpenDota implements Callable<Void> {
    // ARGUMENTS
    @Option(names = { "-x", "--extract-to-json"}, description = "Extract a .ser file to a JSON file.")
    private File extractToJson = null;

    @Option(names = { "-c", "--condense"}, description = "Condense the input openDota CSV file.")
    private File condense = null;


    // CONSTANTS
    public static final int MATCHES_NO = 1191768403;
    public static final String inputPath = "D://datasets//opendota//matches"; // This is a file
    public static final String outputPath = "D://datasets//opendota//output//"; // This is a directory
    public static final int DAYS_NO = 1859;
    public static final int REPORT_THRESHOLD = 100000; // Report progress every 100K rows
    public static final int SERIALIZE_THRESHOLD = 1000000; // Serialize every million rows

    // VARIABLES
    // Keep track of progress
    private LocalDateTime startOfParsing;
    private THashSet<Long> allDates = new THashSet<>();
    private int recordCounter = 0;
    // Store the data {date: Long, {hero#: int -> picks# int}}
    private THashMap<Long, THashMap<Integer, Integer>> data = new THashMap<>();



    private void condenseInputFile(File file) {
        this.startOfParsing = LocalDateTime.now();

        // Main loop!
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
            Iterator<String[]> csvReader = CsvParser.iterator(fileReader);
            String[] headers = csvReader.next();
            // Iterate through stuff
            while (csvReader.hasNext()) {
                String[] row = csvReader.next();

                parseRow(row);

                if (recordCounter % REPORT_THRESHOLD == 0) {
                    reportProgress(this.recordCounter, this.allDates.size(), this.startOfParsing);

                    if (recordCounter % SERIALIZE_THRESHOLD == 0) {
                        String serializePath = outputPath + "intermediate_" + (recordCounter % SERIALIZE_THRESHOLD) + ".ser";
                        serializeData(data, serializePath);
                    }
                }
            }

            String serializePath = outputPath + "final_" + (recordCounter % SERIALIZE_THRESHOLD) + ".ser";
            serializeData(data, serializePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void extractToJson(File file) {
        THashMap<Long, THashMap<Integer, Integer>> hashMap = deserializeData(file);

        writeJSON(hashMap, outputPath + "output.json");
    }

    private void parseRow(String[] row) {
        // Extract relevant fields
        long startTime = Long.parseLong(row[3]);
        String pgroup = row[26];

        // Parse date
        Long date = extractDate(startTime).toEpochDay();

        // Parse picks
        ArrayList<Integer> heroesPicked = extractHeroesPicked(pgroup);

        // Update copy of local map
        THashMap<Integer, Integer> todayPicks = this.data.getOrDefault(date, new THashMap<Integer, Integer>());
        heroesPicked.forEach(hero -> {
            Integer count = todayPicks.getOrDefault(hero, 0) + 1;
            todayPicks.put(hero, count);
        });

        // Push to global map
        this.data.put(date, todayPicks);

        // Tracking progress
        allDates.add(date);
        recordCounter++;
    }

    private static LocalDate extractDate(long epochTimeInSeconds) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(epochTimeInSeconds),
                ZoneId.of("UTC")
        ).toLocalDate();
    }

    private static ArrayList<Integer> extractHeroesPicked(String jsonInput) {
        ArrayList<Integer> heroes = new ArrayList<>();

        JsonIterator iterator = JsonIterator.parse(jsonInput);
        Map<String, Any> jsonObject = null;
        try {
            jsonObject = iterator.read(Any.class).asMap();
        } catch (IOException e) {
            e.printStackTrace();
        }

        jsonObject.forEach((index , object) -> {
            int heroId = object.get("hero_id").toInt();
            heroes.add(heroId);
        });

        return heroes;

    }

    private static void reportProgress(int recordCounter, int days, LocalDateTime startOfParsing) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        System.out.printf(
                "\n%s (%s elapsed) | %,.2f million rows (%.2f)| %4d days ()",
                dtf.format(LocalDateTime.now()),
                timeDifference(startOfParsing, LocalDateTime.now()),
                recordCounter / 1000000.0,
                recordCounter / 1000000.0 / MATCHES_NO,
                days,
                days / (float) DAYS_NO
        );
    }

    private static String timeDifference(LocalDateTime start, LocalDateTime end) {
        // From https://stackoverflow.com/a/44142896/6238740
        Duration duration = Duration.between(start, end);
        long millis = duration.toMillis();

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    private static void serializeData(THashMap<Long, THashMap<Integer, Integer>> data, String path) {
        // From https://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("> Saved data to " + path);
    }

    public static THashMap<Long, THashMap<Integer, Integer>> deserializeData(File file) {
        // From https://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
        THashMap<Long, THashMap<Integer, Integer>> hashMap;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            hashMap = (THashMap<Long, THashMap<Integer, Integer>>) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe) {
            ioe.printStackTrace();
            return null;
        }catch(ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
            return null;
        }
        return hashMap;
    }

    public static void writeJSON(THashMap<Long, THashMap<Integer, Integer>> hashMap, String path) {

        String output = JsonStream.serialize(hashMap);
        try (PrintStream out = new PrintStream(new FileOutputStream("filename.txt"))) {
            out.print(output);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        CommandLine.call(new ProcessOpenDota(), args);

//        FileReader fileReader = null;
//        try {
//            fileReader = new FileReader(inputPath);
//            Iterator<String[]> csvReader = CsvParser.iterator(fileReader);
//            String[] headers = csvReader.next();
//
//            // Iterate through stuff
//            while (csvReader.hasNext()) {
//                String[] row = csvReader.next();
//
//                // Extract relevant fields
//                long start_time = Long.parseLong(row[3]);
//                String pgroup = row[26];
//
//                // Parse date
//                LocalDateTime dateTime = LocalDateTime.ofInstant(
//                        Instant.ofEpochSecond(start_time),
//                        ZoneId.of("UTC")
//                );
//                Long date = dateTime.toLocalDate().toEpochDay();
//
//                // Parse picks
//                JsonIterator iterator = JsonIterator.parse(pgroup);
//                ArrayList<Integer> heroes = new ArrayList<>();
//
//                Any whatever = iterator.read(Any.class);
//                Map<String, Any> whateverMap = whatever.asMap();
//                whateverMap.forEach((index , object) -> {
//                    int heroId = object.get("hero_id").toInt();
//                    heroes.add(heroId);
//                });
//
//                THashMap<Integer, Integer> todayMap = map.getOrDefault(date, new THashMap<Integer, Integer>());
//
//                heroes.forEach(hero -> {
//                    Integer count = todayMap.getOrDefault(hero, 0) + 1;
//                    todayMap.put(hero, count);
//                });
//
//                map.put(date, todayMap);
//
//
//                // Tracking progress
//                allDates.add(date);
//                recordCounter++;
//                if (recordCounter % 50000 == 0) {
//                    //System.gc();
//                    System.out.println("" + (recordCounter / 1000) + "K" + " - " + allDates.size() + " days");
//                }
//                if (recordCounter % 1000000 == 0) {
//                    // Serialize!
//                    FileOutputStream fos = new FileOutputStream(pathDir + "output_" + (recordCounter / 1000000) + ".ser");
//                    ObjectOutputStream oos = new ObjectOutputStream(fos);
//                    oos.writeObject(map);
//                    oos.close();
//                    fos.close();
//                    System.out.println("Serialized HashMap data is saved in hashmap.ser");
//                }
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(pathString));
//
//            String line;
//            while ((line = reader.readLine()) != null)
//            {
////                LocalDateTime dateTime = LocalDateTime.ofInstant(
////                        Instant.ofEpochSecond(start_time),
////                        ZoneId.of("UTC")
////                );
////                Long date = dateTime.toLocalDate().toEpochDay();
////
////                allDates.add(date);
//
//                //System.out.println(map.size());
//                recordCounter++;
//                if (recordCounter % 50000 == 0) {
//                    //System.gc();
//                    System.out.println("" + (recordCounter / 1000) + "K" + " - " + allDates.size() + " days");
//                }
//            }
//            reader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



//        for(Record record: parser.iterateRecords(reader)) {
//            long start_time = record.getLong("start_time");
////            System.out.println(record.getValues()[record.getValues().length - 1]);
//            LocalDateTime dateTime = LocalDateTime.ofInstant(
//                    Instant.ofEpochSecond(start_time),
//                    ZoneId.of("UTC")
//            );
//            Long date = dateTime.toLocalDate().toEpochDay();
//            String pgroup = record.getString(26);
//            JsonIterator iterator = JsonIterator.parse(pgroup);
//
//            ArrayList<Integer> heroes = new ArrayList<>();
//
//            try {
//                Any whatever = iterator.read(Any.class);
//                Map<String, Any> whateverMap = whatever.asMap();
//                whateverMap.forEach((index , object) -> {
//                    int heroId = object.get("hero_id").toInt();
//                    heroes.add(heroId);
//                });
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            THashMap<Integer, Integer> todayMap = map.getOrDefault(date, new THashMap<Integer, Integer>());
////
//            heroes.forEach(hero -> {
//                Integer count = todayMap.getOrDefault(hero, 0) + 1;
//                todayMap.put(hero, count);
//            });
//
//            map.put(date, todayMap);
//
//            allDates.add(date);
//
//            //System.out.println(map.size());
//            recordCounter++;
//            if (recordCounter % 50000 == 0) {
//                //System.gc();
//                System.out.println("" + (recordCounter / 1000) + "K" + " - " + allDates.size() + " days");
//            }
//
//        }

    }

//        private static void closeSafely(Closeable closeable) {
//            if (closeable != null) {
//                try {
//                    closeable.close();
//                } catch (IOException e) {
//                    // Ignore
//                }
//            }
//        }

    @Override
    public Void call() throws Exception {
        // BUSINESS LOGIC

        if (extractToJson != null) {

        }
        return null;
    }
}
