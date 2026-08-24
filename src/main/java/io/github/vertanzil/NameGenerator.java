package io.github.vertanzil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class NameGenerator {

    private static final Logger log = Logger.getLogger(NameGenerator.class.getName());
    private static final Random random = new Random();

    public static void main(String[] args) {
        String maleFile   = "src/main/resources/male_first_names.txt";
        String femaleFile = "src/main/resources/female_first_names.txt";
        String familyFile = "src/main/resources/family_names.txt";
        String usedFile   = "src/main/resources/used_names.txt";
        String outputFile = "src/main/resources/combined_names.txt";

        int numberToGenerate = 50000;
        final int MAX_ATTEMPTS = 10_000_000;
        final int THREADS = Math.max(4, Runtime.getRuntime().availableProcessors());

        try {
            List<String> maleNames   = new ArrayList<>(new HashSet<>(readAllLines(maleFile)));
            List<String> femaleNames = new ArrayList<>(new HashSet<>(readAllLines(femaleFile)));
            List<String> familyNames = new ArrayList<>(new HashSet<>(readAllLines(familyFile)));

            Set<String> usedNames = new HashSet<>(readAllLines(usedFile));

            // Feasibility check
            int possible = (maleNames.size() + femaleNames.size()) * familyNames.size();
            int available = possible - usedNames.size();

            if (available < numberToGenerate) {
                throw new IllegalStateException(
                        "Not enough unique unused names available. Need " + numberToGenerate +
                                ", but only " + available + " possible."
                );
            }

            log.info("Generating " + numberToGenerate + " names using " + THREADS + " threads...");

            // Thread-safe set for generated names
            Set<String> generated = ConcurrentHashMap.newKeySet();

            // Progress counter
            AtomicInteger progress = new AtomicInteger(0);

            ExecutorService executor = Executors.newFixedThreadPool(THREADS);

            // Submit workers
            for (int i = 0; i < THREADS; i++) {
                executor.submit(() -> {
                    int attempts = 0;

                    while (generated.size() < numberToGenerate) {

                        if (attempts++ > MAX_ATTEMPTS) {
                            throw new IllegalStateException("Too many collisions. Not enough unique names available.");
                        }

                        String first = random.nextBoolean()
                                ? maleNames.get(random.nextInt(maleNames.size()))
                                : femaleNames.get(random.nextInt(femaleNames.size()));

                        String last = familyNames.get(random.nextInt(familyNames.size()));

                        String fullName = first + " " + last;

                        if (!usedNames.contains(fullName)) {
                            boolean added = generated.add(fullName);
                            if (added) {
                                int p = progress.incrementAndGet();
                                if (p % 100 == 0 || p == numberToGenerate) {
                                    printProgressBar(p, numberToGenerate);
                                }
                            }
                        }
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.MINUTES);

            // Write output file
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (String name : generated) {
                    writer.println(name);
                }
            }

            // Append to used names
            try (FileWriter fw = new FileWriter(usedFile, true)) {
                for (String name : generated) {
                    fw.write(name + System.lineSeparator());
                }
            }

            log.info("Finished! Output written to: " + outputFile);
            log.info("Appended " + generated.size() + " names to used_names.txt");

        } catch (IOException | InterruptedException e) {
            log.severe("Error: " + e.getMessage());
        }
    }

    // Progress bar
    private static void printProgressBar(int current, int total) {
        int width = 50;
        double ratio = (double) current / total;
        int filled = (int) (ratio * width);

        StringBuilder bar = new StringBuilder();
        bar.append("\r[");
        for (int i = 0; i < filled; i++) bar.append("█");
        for (int i = filled; i < width; i++) bar.append(" ");
        bar.append("] ");
        bar.append(String.format("%d/%d", current, total));

        System.out.print(bar.toString());

        if (current == total) {
            System.out.println("\nCompleted!");
        }
    }
    public static List<String> readAllLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line.trim());
                }
            }
        }

        if (lines.isEmpty()) {
            throw new IOException("File is empty: " + filePath);
        }
        return lines;
    }
}