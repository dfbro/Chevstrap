package com.chevstrap.rbx;

import java.io.*;

public class fileStuff {

    public static boolean isExistFile(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void makeDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + path);
            }
        }
    }

    // Method to check if a directory is accessible (exists and readable)
    public static boolean isDirectoryAccessible(String path) {
        File dir = new File(path);
        return dir.exists() && dir.canRead();
    }

    // Method to delete a directory and all its contents


    static void createNewFile(String path) {
        int lastSep = path.lastIndexOf(File.separator);
        if (lastSep > 0) {
            String dirPath = path.substring(0, lastSep);
            makeDir(dirPath);
        }

        File file = new File(path);

        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    System.err.println("Failed to create file: " + path);
                }
            }
        } catch (IOException e) {
            System.err.println("IOException occurred while creating file: " + path);
        }
    }

    // Method to copy a directory and overwrite any existing files
    public static void copyDirectory(String sourcePath, String destPath) {
        File srcDir = new File(sourcePath);
        File destDir = new File(destPath);

        if (!srcDir.exists()) {
            return; // Source directory does not exist
        }

        if (!destDir.exists()) {
            destDir.mkdirs(); // Create destination directory if it doesn't exist
        }

        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                File destFile = new File(destDir, file.getName());

                if (file.isDirectory()) {
                    copyDirectory(file.getAbsolutePath(), destFile.getAbsolutePath()); // Recursively copy subdirectories
                } else {
                    copyFile(file, destFile); // Copy file and overwrite if it exists
                }
            }
        }
    }

    public static void writeFile(String path, String str) {
        createNewFile(path);

        try (FileWriter fileWriter = new FileWriter(path, false)) {
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException ignored) {

        }
    }

    // Helper method to copy files
    private static void copyFile(File source, File destination) {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException ignored) {

        }
    }
}
