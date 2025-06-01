package com.chevstrap.rbx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.chevstrap.rbx.UI.Elements.CustomDialogs.LoadingFragment;
import com.chevstrap.rbx.Utility.FileTool;
import com.chevstrap.rbx.Utility.FileToolAlt;

import org.json.JSONObject;

import java.io.*;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LaunchHandler {
    private final Context context;
    private final FragmentManager fragmentManager;
    private String packageName = "";
    private boolean allowedToLaunch = false;
    private boolean isCancelled = false;

    public LaunchHandler(@NonNull Context context, @NonNull FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }
    public void LaunchRoblox(String packageName) throws IOException {
        this.packageName = packageName;
        isCancelled = false;

        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("LaunchHandler", "Package not found: " + packageName, e);
            return;
        }

        String rbxPath = appInfo.dataDir + "/files/";
        String rbxCachesPaths = appInfo.dataDir + "/cache/";

        File clientSettingsDir = new File(getDataStorage(), "Modifications/ClientSettings");
        if (!clientSettingsDir.exists() && !clientSettingsDir.mkdirs()) return;

        File outFile = new File(clientSettingsDir, "ClientAppSettings.json");
        String sourceSettingsPath = outFile.getAbsolutePath();
        String clientSettingsPath = rbxPath + "exe/ClientSettings/ClientAppSettings.json";

        LoadingFragment fragment = createLoadingDialog();
        fragment.setCancelable(false);

        new Handler(Looper.getMainLooper()).post(() -> {
            fragment.setMessageText("Preparing...");
            fragment.setMessageStatus("0%");
            try {
                fragment.show(fragmentManager, "Messagebox");
            } catch (IllegalStateException ignored) {
            }
        });

        boolean sourceExists;
        boolean clientExists = new File(clientSettingsPath).exists();

        if (!clientExists && isRootAvailable()) {
            clientExists = FileToolAlt.isExists(clientSettingsPath);
        }

        boolean bothExist;
        boolean libExists;

        String resultChangesFlags = FileTool.read(new File(clientSettingsPath));
        if (resultChangesFlags.isEmpty() && isRootAvailable()) {
            resultChangesFlags = FileToolAlt.readFile(sourceSettingsPath);
        }

        if (!clientExists) {
            Toast.makeText(context, "Client settings file not found", Toast.LENGTH_SHORT).show();
            FFlagsSettingsManager manager = new FFlagsSettingsManager(context);
            manager.applyFastFlag(context);
        }

        sourceExists = new File(sourceSettingsPath).exists();
        clientExists = new File(clientSettingsPath).exists();

        if (!clientExists && isRootAvailable()) {
            clientExists = FileToolAlt.isExists(clientSettingsPath);
        }

        bothExist = sourceExists && clientExists;
        libExists = rbxIsLibFolderExisted();

        allowedToLaunch = false;

        if (bothExist && !libExists) {
            String finalResultChangesFlags = resultChangesFlags;
            new Handler(Looper.getMainLooper()).post(() -> {
                String currentFlags = FileTool.read(new File(sourceSettingsPath));
                if (!currentFlags.equals(finalResultChangesFlags)) {
                    fragment.setMessageText("Applying Roblox fast flags");
                    new FFlagsSettingsManager(context).applyFastFlag(context);
                }

                new Thread(() -> {
                    if (isCancelled) return;
                    handleCacheClear(rbxCachesPaths);
                }).start();

                animateProgress(fragment, 0, 50, () -> {
                    if (isCancelled) return;

                    new Thread(() -> {
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(clientSettingsPath)))) {
                            writer.write(readFile(outFile));
                        } catch (IOException e) {
                            Log.e("LaunchHandler", "Failed to write fast flags", e);
                        }

                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (isCancelled) return;
                            fragment.setMessageText("Starting Roblox");

                            animateProgress(fragment, 50, 90, () -> {
                                if (isCancelled) return;

                                animateProgress(fragment, 90, 100, () -> {
                                    if (isCancelled) return;

                                    allowedToLaunch = true;
                                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                                    if (launchIntent != null) {
                                        if (context instanceof SettingsActivity) {
                                            ((SettingsActivity) context).LaunchWatcher();
                                        } else if (context instanceof MainActivity) {
                                            ((MainActivity) context).LaunchWatcher();
                                        }

                                        context.startActivity(launchIntent);
                                        if (fragment.isAdded()) {
                                            fragment.dismissAllowingStateLoss();
                                        }
                                    } else {
                                        fragment.setMessageText("Failed to launch Roblox");
                                        fragment.setMessageStatus("-");
                                    }
                                });
                            });
                        });
                    }).start();
                });
            });
        } else {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (bothExist) {
                    fragment.setMessageText("Your fast flags files are missing. Please go back to settings and save them again");
                } else {
                    fragment.setMessageText("Your Roblox may have been modified. Please download Roblox only from the official source.");
                }
                fragment.setMessageStatus("-");
            });
        }
    }

    private void handleCacheClear(String rbxCachesPaths) {
        if (!getStateSettingKey("ClearCacheEveryRBXLaunch")) return;

        File cacheDir = new File(rbxCachesPaths);
        File[] files = null;

        if (!cacheDir.exists() && isRootAvailable()) {
            try {
                if (FileToolAlt.isExists(rbxCachesPaths)) {
                    files = FileToolAlt.listFiles(rbxCachesPaths);
                }
            } catch (IOException e) {
                Log.e("RBX Cache", "Error accessing cache via root", e);
                return;
            }
        } else {
            files = cacheDir.listFiles();
        }

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && !file.delete()) {
                    Log.w("RBX Cache", "Failed to delete: " + file.getName());
                }
            }
        }
    }

    private void animateProgress(LoadingFragment fragment, int start, int end, Runnable onComplete) {
        Handler handler = new Handler(Looper.getMainLooper());
        int steps = 20;
        int delay = 3500 / steps;
        float increment = (float)(end - start) / steps;

        Runnable[] task = new Runnable[1];
        task[0] = new Runnable() {
            float progress = start;

            @Override
            public void run() {
                if (isCancelled) return;
                if (progress <= end) {
                    fragment.setMessageStatus(((int) progress) + "%");
                    progress += increment;
                    handler.postDelayed(this, delay);
                } else {
                    fragment.setMessageStatus(end + "%");
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            }
        };
        handler.post(task[0]);
    }

    private boolean rbxIsLibFolderExisted() {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try (ZipFile zipFile = new ZipFile(ai.sourceDir)) {
                    for (ZipEntry entry : Collections.list(zipFile.entries())) {
                        if (entry.getName().startsWith("lib/")) return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("LaunchHandler", "Error checking lib folder", e);
        }
        return false;
    }

    @NonNull
    private LoadingFragment createLoadingDialog() {
        LoadingFragment fragment = new LoadingFragment();
        fragment.setMessageboxListener(new LoadingFragment.MessageLoadingListener() {
            @Override
            public void onOkClicked() {}

            @Override
            public void onCancelClicked() {
                isCancelled = true;
                if (fragment.isAdded()) {
                    fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
            }
        });
        return fragment;
    }

    private File getDataStorage() {
        return context.getFilesDir();
    }

    private boolean getStateSettingKey(String key) {
        File file = new File(getDataStorage(), "LastAppSettings.json");
        if (!file.exists()) return false;

        try {
            JSONObject jsonObject = new JSONObject(readFile(file));
            return jsonObject.optBoolean(key, false);
        } catch (Exception e) {
            Log.e("LaunchHandler", "Error reading settings JSON", e);
        }
        return false;
    }

    public static boolean isRootAvailable() {
        return FileToolAlt.isRootAvailable();
    }

    private String readFile(File file) {
        if (!file.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e("LaunchHandler", "Error reading file", e);
        }
        return sb.toString().trim();
    }
}
