package com.chevstrap.rbx;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.chevstrap.rbx.Utility.FileTool;
import com.chevstrap.rbx.Utility.FileToolAlt;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FFlagsSettingsManager {
    private final Context context;
    private String rbxpath;

    public FFlagsSettingsManager(Context context) {
        this.context = context;
    }

    public String getPackageTarget() {
        String preferredApp = getSetting("PreferredRobloxApp");

        if (Objects.equals(preferredApp, "Roblox VN")) {
            return "com.roblox.client.vnggames";
        } else if (Objects.equals(preferredApp, "Roblox")) {
            return "com.roblox.client";
        }

        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.roblox.client", 0);
            return "com.roblox.client";
        } catch (PackageManager.NameNotFoundException e) {
            try {
                pm.getPackageInfo("com.roblox.client.vnggames", 0);
                return "com.roblox.client.vnggames";
            } catch (PackageManager.NameNotFoundException ignored) {
                return "com.roblox.client"; // fallback
            }
        }
    }

    void applyFastFlag(Context context) {
        String rbxpathh = getRbxPath();
        boolean isAllowed = false;
        boolean getSettingHey = Boolean.parseBoolean(getSetting1("UseFastFlagManager"));

        if (!isExistSettingKey1("UseFastFlagManager")) {
            isAllowed = true;
        }

        if (isExistSettingKey1("UseFastFlagManager")) {
            isAllowed = getSettingHey;
        }

        if (!isAllowed) return;

        File clientSettingsDir = new File(_getDataStorage(), "Modifications/ClientSettings");
        File outFile1 = new File(clientSettingsDir, "ClientAppSettings.json");

        boolean root = isRootAvailable();

        if (root) {
            try {
                String targetDir = rbxpathh + "exe/ClientSettings";
                String targetFile = targetDir + "/ClientAppSettings.json";

                FileToolAlt.createDirectoryWithPermissions(targetDir);

                if (FileToolAlt.pathExists(targetDir)) {
                    FileToolAlt.writeFile(targetFile, FileTool.read(outFile1));
                    Toast.makeText(context, "FFlags saved", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(context, "Directory blocked by SELinux or does not exist", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(context, "Root write failed, using fallback", Toast.LENGTH_SHORT).show();
            }
        }

        String fallbackDirPath = rbxpathh + "exe/ClientSettings";
        File fallbackDir = new File(fallbackDirPath);
        File fallbackFile = new File(fallbackDirPath + "/ClientAppSettings.json");

        if (!fallbackDir.exists() && !fallbackDir.mkdirs()) {
            Toast.makeText(context, "Failed to create fallback ClientSettings directory", Toast.LENGTH_SHORT).show();
            Log.e("FastFlag", "Fallback directory creation failed: " + fallbackDirPath);
            return;
        }

        try {
            String content = FileTool.read(outFile1);
            if (content.isEmpty()) {
                Toast.makeText(context, "Source file is empty or unreadable", Toast.LENGTH_SHORT).show();
                Log.e("FastFlag", "Source file unreadable: " + outFile1.getAbsolutePath());
                return;
            }

            FileTool.write(fallbackFile, content);
            Toast.makeText(context, "FFlags saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Fallback write failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("FastFlag", "Fallback write failed", e);
        }
    }


    private void showMessage() {
        Toast.makeText(context, "FFlags successfully saved", Toast.LENGTH_SHORT).show();
    }

    private static boolean isRootAvailable() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            return (exitValue == 0);
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public boolean isExistSettingKey1(String keyName) {
        File clientSettingsDir = new File(String.valueOf(_getDataStorage()));
        File filePath = new File(clientSettingsDir, "AppSettings.json");

        if (!filePath.exists()) {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(FileTool.read(filePath));
            return jsonObject.has(keyName);
        } catch (Exception ignored) {
            return false;
        }
    }

    public String getPreset(String flagName) {
        File clientSettingsDir = new File(_getDataStorage(), "Modifications/ClientSettings");
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

    public String getRbxPath() {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(getPackageTarget(), 0);
            return info.dataDir + "/files/";
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "Roblox folder directory not found or it's blocked", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public String getSetting1(String flagName) {
        File filePath = new File(_getDataStorage(), "LastAppSettings.json");

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
        File filePath = new File(_getDataStorage(), "AppSettings.json");

        if (!filePath.exists()) return null;

        try {
            String content = FileTool.read(filePath);
            JSONObject jsonObject = new JSONObject(content);
            return jsonObject.optString(flagName, null);
        } catch (Exception e) {
            return null;
        }
    }

    private File _getDataStorage() {
        return context.getFilesDir();
    }
}
