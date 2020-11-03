package de.tgx03;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Comparator {

    private static final List<File> missingFiles = Collections.synchronizedList(new ArrayList<>());
    private static FileLister firstDirectory;
    private static FileLister secondDirectory;
    private static Thread firstThread;
    private static Thread secondThread;

    /**
     * Parses 2 directories and either prints or writes to a file the
     * ones existing in the first directory but not in the second
     *
     * @param args Empty or the input and output files. When 1 string provided, treated as output file, when 2 provided, treated as input, for 3 both
     * @throws IOException When input files couldn't be read or output couldn't be written to
     * @throws InterruptedException When some weird stuff with parallel execution happens
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        switch (args.length) {
            case 0,1 -> {
                getInputDirectories();
                findMissingFiles();
                if (args.length == 0) {
                    outputToConsole();
                } else {
                    outputToFile(new File(args[0]));
                }
            }
            case 2,3 -> {
                createThreads(new File(args[0]), new File(args[1]));
                findMissingFiles();
                if (args.length == 2) {
                    outputToConsole();
                } else {
                    outputToFile(new File(args[2]));
                }
            }
            default -> throw new IllegalArgumentException("Invalid number of arguments provided");
        }
    }

    /**
     * Gets the input directories from the console
     * and starts the threads parsing them
     */
    private static void getInputDirectories() {
        System.out.println("First directory:");
        firstDirectory = new FileLister(getNewFolder(), false);
        firstThread = new Thread(firstDirectory);
        firstThread.start();
        System.out.println("Second directory:");
        secondDirectory = new FileLister(getNewFolder(), false);
        secondThread = new Thread(secondDirectory);
        secondThread.start();
    }

    /**
     * Starts the threads crawling through the directories from provided Files
     *
     * @param firstPath The path of the first directory
     * @param secondPath The path of the second directory
     */
    private static void createThreads(File firstPath, File secondPath) {
        firstDirectory = new FileLister(firstPath, false);
        firstThread = new Thread(firstDirectory);
        firstThread.start();
        secondDirectory = new FileLister(secondPath, false);
        secondThread = new Thread(secondDirectory);
        secondThread.start();
    }

    /**
     * Compares the 2 previously scanned directories
     * @throws InterruptedException When some weird stuff with the threads happens (probably when this method gets called multiple times, I dunno)
     */
    private static void findMissingFiles() throws InterruptedException {
        firstThread.join();
        secondThread.join();
        List<File> first = firstDirectory.getFiles();
        List<File> second = secondDirectory.getFiles();
        first.parallelStream().forEach(file -> {
            String fileName = file.getName();
            boolean found = false;
            for (File fileToTest : second) {
                if (fileName.equalsIgnoreCase(fileToTest.getName())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missingFiles.add(file);
            }
        });
    }

    /**
     * Prints the missing files to the console
     */
    private static void outputToConsole() {
        missingFiles.sort(java.util.Comparator.naturalOrder());
        for (File file : missingFiles) {
            System.out.println(file.getPath());
        }
    }

    /**
     * Writes the paths of the missing files to the provided output file
     * @param output The file to write the output to
     * @throws IOException When the file couldn't be written to
     */
    private static void outputToFile(File output) throws IOException {
        missingFiles.sort(java.util.Comparator.naturalOrder());
        if (output.createNewFile()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            for (File toWrite : missingFiles) {
                writer.append(toWrite.getAbsolutePath());
                writer.newLine();
            }
            writer.close();
        } else {
            throw new IOException("File already exists");
        }
    }

    /**
     * Waits for user input and parses it to create a new folder.
     * When it's not a folder, and error is thrown
     *
     * @return The new folder
     */
    private static File getNewFolder() {
        Scanner scanner = new Scanner(System.in);
        String directory = scanner.nextLine();
        File newDirectory = new File(directory);
        if (!newDirectory.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory");
        }
        return newDirectory;
    }
}
