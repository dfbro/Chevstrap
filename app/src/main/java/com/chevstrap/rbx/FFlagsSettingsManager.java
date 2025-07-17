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

		// Create directory if needed
		FileToolAlt.createDirectoryWithPermissions(targetDir);

		if (!FileToolAlt.pathExists(targetDir)) {
			throw new IOException("Target directory inaccessible or blocked by SELinux: " + targetDir);
		}

		String copyCommand = String.format("cp -f \"%s\" \"%s\"", sourcePath, targetPath);

		// If root is available, use `su`
		if (FileToolAlt.isRootAvailable()) {
			execShell("su", copyCommand);
		} else {
			// Try using sh (only works if the target is writable)
			File targetTest = new File(targetPath);
			if (!targetTest.canWrite()) {
				throw new IOException("No root and cannot write to target path: " + targetPath);
			}
			execShell("sh", copyCommand);
		}
	}

	private static void execShell(String shell, String command) throws IOException {
		Process process = Runtime.getRuntime().exec(new String[]{shell, "-c", command});
		try {
			int result = process.waitFor();
			if (result != 0) {
				throw new IOException(shell + " command failed with exit code " + result);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException(shell + " command interrupted", e);
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
