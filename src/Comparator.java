import java.io.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Comparator {

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

    private static File getNewFolder() {
        Scanner scanner = new Scanner(System.in);
        String directory = scanner.nextLine();
        File newDirectory = new File(directory);
        if (!newDirectory.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory");
        }
        return newDirectory;
    }

    private static List<File> getAllFilesFromDirectory(File directory) {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Provided path is not a directory");
        }
        return new ArrayList<>(FileUtils.listFiles(directory, HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE));
    }
}
