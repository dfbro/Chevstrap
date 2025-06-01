package com.chevstrap.rbx;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.chevstrap.rbx.Utility.FileTool;

import org.json.JSONObject;

import java.io.File;
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
