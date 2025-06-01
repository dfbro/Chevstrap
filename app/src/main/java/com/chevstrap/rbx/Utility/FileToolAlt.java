package com.chevstrap.rbx.Utility;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class FileToolAlt {

    // Run any command as root and get output
    public static String runRootCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec("su");
        try (
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        ) {
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            while ((line = error.readLine()) != null) {
                output.append("ERR: ").append(line).append("\n");
            }

            process.waitFor();
            return output.toString();
        } catch (Exception e) {
            throw new IOException("Root command failed", e);
        }
    }

    // Create a directory with full permissions
    public static void createDirectoryWithPermissions(String path) throws IOException {
        runRootCommand("mkdir -p " + path);
        runRootCommand("chmod -R 777 " + path);
    }

    // Check if a path exists using ls
    public static boolean pathExists(String path) {
        try {
            String result = runRootCommand("ls " + path);
            return !result.contains("No such file or directory");
        } catch (Exception e) {
            return false;
        }
    }

    // Write a text file to any root path
    public static void writeFile(String path, String content) throws IOException {
        File tempFile = File.createTempFile("temp", null);
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        String copyCommand = "cat " + tempFile.getAbsolutePath() + " > " + path;
        runRootCommand(copyCommand);
        runRootCommand("chmod 777 " + path);

        boolean deleted = tempFile.delete();
        if (!deleted) {
            throw new IOException("Failed to delete temporary file: " + tempFile.getAbsolutePath());
        }
    }

    public static boolean isDirectory(String path) throws IOException {
        String cmd = "[ -d \"" + path + "\" ] && echo dir || echo file";
        String result = runRootCommand(cmd).trim();
        return result.equals("dir");
    }

    public static boolean isExists(String path) throws IOException {
        String cmd = "[ -e \"" + path + "\" ] && echo exists || echo missing";
        String result = runRootCommand(cmd).trim();
        return result.equals("exists");
    }

    public static File[] listFiles(String directoryPath) throws IOException {
        String output = runRootCommand("ls -1 " + directoryPath);
        List<File> fileList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new StringReader(output))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("ERR:")) {
                    File f = new File(directoryPath, line.trim());
                    fileList.add(f);
                }
            }
        }

        return fileList.toArray(new File[0]);
    }


    public static String readFile(String path) throws IOException {
        return runRootCommand("cat " + path);
    }
}
