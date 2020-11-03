package de.tgx03;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that simply lists all files in a given directory, including subdirectories
 */
public class FileLister implements Runnable {

    private final boolean hidden;
    private final File path;

    private ArrayList<File> files;

    /**
     * Creates a new instance of the lister which lists all files from a given directory
     * @param path The path where all files should be listed
     * @param hidden Whether to include hidden files or directories
     */
    public FileLister(File path, boolean hidden) {
        if (!path.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory");
        }
        this.path = path;
        this.hidden = hidden;
    }

    /**
     * Starts this runnables search for files
     * Only gets executed once in this objects lifetime
     */
    @Override
    public void run() {
        if (files == null) {
            if (hidden) {
                files = new ArrayList<>(FileUtils.listFiles(path, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE));
            } else {
                files = new ArrayList<>(FileUtils.listFiles(path, HiddenFileFilter.VISIBLE, HiddenFileFilter.VISIBLE));
            }
        }
    }

    /**
     * Returns the found files after the execution finished
     * @return The found files
     */
    public List<File> getFiles() {
        return files;
    }
}
