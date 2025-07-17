package com.chevstrap.rbx;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.chevstrap.rbx.Utility.FileTool;
import com.chevstrap.rbx.Utility.FileToolAlt;
import com.chevstrap.rbx.Utility.INeedPath;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FFlagsSettingsManager {
    private final Context context;

    public FFlagsSettingsManager(Context context) {
        this.context = context;
    }

    public static String getPackageTarget(Context context) {
        String preferredApp = getSetting1(context, "PreferredRobloxApp");

        if (Objects.equals(preferredApp, "Roblox VN")) {
            return "com.roblox.client.vnggames";
        } else if (Objects.equals(preferredApp, "Roblox")) {
            return "com.roblox.client";
        }

        String[] robloxPackages = {
                "com.roblox.client.vnggames", // preferred VN variant
                "com.roblox.client"           // global fallback
        };

        for (String pkg : robloxPackages) {
            try {
                context.getPackageManager().getPackageInfo(pkg, 0);
                return pkg;
            } catch (PackageManager.NameNotFoundException ignored) {}
        }
        return null; // or default to one if needed

    }

    public static void applyFastFlag(Context context) throws IOException {
        String rbxpathh = INeedPath.getRBXPathDir(context, getPackageTarget(context));

        // Log rbxpathh to logcat and path.txt
        Log.d("RBXPathLogger", "RBXPath: " + rbxpathh);
        FileTool.write(new File(context.getFilesDir(), "path.txt"), rbxpathh);

        // Check user setting
        if (isExistSettingKey1(context, "UseFastFlagManager")) {
            boolean bo3 = Boolean.parseBoolean(getSetting1(context, "UseFastFlagManager"));
            if (bo3) {
                throw new IllegalStateException("No permission to apply fast flags");
            }
        }

        // Source: internal JSON file
        File clientSettingsDir = new File(context.getFilesDir(), "Modifications/ClientSettings");
        File outFile1 = new File(clientSettingsDir, "ClientAppSettings.json");

        if (!outFile1.exists()) {
            throw new IOException("Source file does not exist: " + outFile1.getAbsolutePath());
        }

        // Destination
        String targetDir = rbxpathh + "appData/ClientSettings";
        String targetFile = targetDir + "/IxpSettings.json";

        // Ensure the directory exists
        FileToolAlt.createDirectoryWithPermissions(targetDir);

        try {
            // Use shell cp -f to copy
            String cmd = "mkdir -p \"" + targetDir + "\" && cp -f \"" +
                         outFile1.getAbsolutePath() + "\" \"" + targetFile + "\"";
            runCommand(cmd);
            return;
        } catch (Exception e) {
            Log.e("FFlagApply", "Shell copy failed, using Java fallback", e);
        }

        // Fallback if shell command fails
        File fallbackFile = new File(targetFile);
        String content = FileTool.read(outFile1);
        FileTool.write(fallbackFile, content);
    }

    private static void runCommand(String command) throws IOException {
        String[] cmd;
        if (FileToolAlt.isRootAvailable()) {
            cmd = new String[]{"su", "-c", command};
        } else {
            cmd = new String[]{"sh", "-c", command};
        }

        Process process = Runtime.getRuntime().exec(cmd);

        // Read stdout
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder stdout = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stdout.append(line).append("\n");
        }

        // Read stderr
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder stderr = new StringBuilder();
        while ((line = errorReader.readLine()) != null) {
            stderr.append(line).append("\n");
        }

        try {
            int result = process.waitFor();
            Log.d("FFlags", "Command exit: " + result);
            if (!stdout.toString().isEmpty()) {
                Log.d("FFlags", "stdout:\n" + stdout);
            }
            if (!stderr.toString().isEmpty()) {
                Log.e("FFlags", "stderr:\n" + stderr);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Log.e("FFlags", "Command interrupted", e);
        }
    }

    public static boolean isExistSettingKey1(Context context, String keyName) {
        File filePath = new File(context.getFilesDir(), "AppSettings.json");

        if (!filePath.exists()) return false;

        try {
            JSONObject jsonObject = new JSONObject(FileTool.read(filePath));
            return jsonObject.has(keyName);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static String getSetting1(Context context, String flagName) {
        File filePath = new File(context.getFilesDir(), "LastAppSettings.json");

        if (!filePath.exists()) return null;

        try {
            String content = FileTool.read(filePath);
            JSONObject jsonObject = new JSONObject(content);
            return jsonObject.optString(flagName, null);
        } catch (Exception e) {
            return null;
        }
    }

    public String getPreset(String flagName) {
        File clientSettingsDir = new File(context.getFilesDir(), "Modifications/ClientSettings");
        File filePath = new File(clientSettingsDir, "LastClientAppSettings.json");

        if (!filePath.exists()) return null;

        try {
            String content = FileTool.read(filePath);
            JSONObject jsonObject = new JSONObject(content);
            return jsonObject.optString(flagName, null);
        } catch (Exception e) {
            return null;
        }
    }

    public String getSetting(String flagName) {
        File filePath = new File(context.getFilesDir(), "AppSettings.json");

        if (!filePath.exists()) return null;

        try {
            String content = FileTool.read(filePath);
            JSONObject jsonObject = new JSONObject(content);
            return jsonObject.optString(flagName, null);
        } catch (Exception e) {
            return null;
        }
    }
}
