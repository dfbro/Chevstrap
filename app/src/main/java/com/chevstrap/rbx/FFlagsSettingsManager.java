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
import java.io.FileWriter;


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
		String packageTarget = getPackageTarget(context);
		String rbxPath = INeedPath.getRBXPathDir(context, packageTarget);

		if (isExistSettingKey1(context, "UseFastFlagManager")) {
			boolean useFFM = Boolean.parseBoolean(getSetting1(context, "UseFastFlagManager"));
			if (useFFM) {
				throw new IllegalStateException("No permission to apply fast flags");
			}
		}

		// Prepare paths
		File sourceFile = new File(context.getFilesDir(), "Modifications/ClientSettings/ClientAppSettings.json");
		String sourcePath = sourceFile.getAbsolutePath();
		String targetDir = rbxPath + "appData/ClientSettings";
		String targetPath = targetDir + "/IxpSettings.json";

		// Compose shell command sequence
		String shellCommand =
			"mkdir -p \"" + targetDir + "\" && " +
			"touch \"" + targetPath + "\" && " +
			"cp -f \"" + sourcePath + "\" \"" + targetPath + "\"";

		// Log command to /data/data/com.chevstrap.rbx/cmd.log
		File logFile = new File(context.getApplicationInfo().dataDir, "cmd.log");
		try (FileWriter logWriter = new FileWriter(logFile, true)) {
			logWriter.write(shellCommand + "\n");
		} catch (Exception e) {
			Log.e("FFlags", "Failed to write cmd.log", e);
		}

		// Inline shell execution
		Process process;
		if (FileToolAlt.isRootAvailable()) {
			process = Runtime.getRuntime().exec(new String[]{"su", "-c", shellCommand});
		} else {
			File testTarget = new File(targetPath);
			if (!testTarget.canWrite()) {
				throw new IOException("No root and cannot write to target path: " + targetPath);
			}
			process = Runtime.getRuntime().exec(new String[]{"sh", "-c", shellCommand});
		}

		try {
			int result = process.waitFor();
			if (result != 0) {
				throw new IOException("Shell command failed with exit code " + result);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Shell command interrupted", e);
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
