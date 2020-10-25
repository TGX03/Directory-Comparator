package de.tgx03;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Comparator {

    /**
     * Asks for the first and second directory.
     * Every file that exists in the first one, but not in the second one, will either be
     * printed on the console or written to a file, depending whether one was provided
     *
     * @param args When an argument is given, it is interpreted as a path to the output file. When the path is invalid,
     *             the tool probably crashes or something
     * @throws IOException Might happen when the output file cannot be created
     */
    public static void main(String[] args) throws IOException {
        File output;
        if (args.length == 0) {
            output = null;
        } else {
            output = new File(args[0]);
        }
        List<File> result = Collections.synchronizedList(new ArrayList<>());
        System.out.println("First directory");
        List<File> first = getAllFilesFromDirectory(getNewFolder());
        System.out.println("Second directory");
        List<File> second = getAllFilesFromDirectory(getNewFolder());
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
                result.add(file);
            }
        });
        result.sort(java.util.Comparator.naturalOrder());
        if (output == null) {
            for (File toPrint : result) {
                System.out.println(toPrint.getName());
            }
        } else {
            if (!output.createNewFile()) {
                throw new IOException("Couldn't create the output file");
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));
            for (File toWrite : result) {
                writer.append(toWrite.getAbsolutePath());
                writer.newLine();
            }
            writer.close();
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

    /**
     * Lists all the files in a directory recursively, however hidden directories and files are ignored
     *
     * @param directory The directory to find the files in
     * @return All the files in that directory recursively
     */
    private static List<File> getAllFilesFromDirectory(File directory) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory");
        }
        return new ArrayList<>(FileUtils.listFiles(directory, HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE));
    }
}
