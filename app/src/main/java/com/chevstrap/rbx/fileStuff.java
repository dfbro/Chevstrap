package com.chevstrap.rbx;

import java.io.*;

public class fileStuff {

    // Method to check if a directory is accessible (exists and readable)
    public static boolean isDirectoryAccessible(String path) {
        File dir = new File(path);
        return dir.exists() && dir.canRead();
    }

    // Method to delete a directory and all its contents
    public static void deleteDirectory(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            deleteDirectoryRecursive(dir);
        }
    }

    // Recursive helper method for deleting directory contents
    private static void deleteDirectoryRecursive(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectoryRecursive(file); // Recursively delete subdirectories and files
                }
            }
        }
        dir.delete(); // Delete the file or empty directory
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

    // Helper method to copy files
    private static void copyFile(File source, File destination) {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle error
        }
    }
}
