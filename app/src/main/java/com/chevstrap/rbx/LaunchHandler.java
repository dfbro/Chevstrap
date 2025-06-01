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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

        String rbxPath;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            rbxPath = info.dataDir + "/files/";
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        String rbxCachesPaths;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            rbxCachesPaths = info.dataDir + "/cache/";
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        File clientSettingsDir = new File(getDataStorage(), "Modifications/ClientSettings");
        if (!clientSettingsDir.exists() && !clientSettingsDir.mkdirs()) return;

        File outFile = new File(clientSettingsDir, "ClientAppSettings.json");
        String sourceSettingsPath = outFile.getAbsolutePath();
        String clientSettingsPath = rbxPath + "exe/ClientSettings/ClientAppSettings.json";

        LoadingFragment fragment = createLoadingDialog();

        new Handler(Looper.getMainLooper()).post(() -> {
            fragment.setMessageText("Preparing...");
            fragment.setMessageStatus("0%");
            try {
                fragment.show(fragmentManager, "Messagebox");
            } catch (IllegalStateException ignored) {}
        });

        boolean sourceExists = new File(sourceSettingsPath).exists();
        boolean clientExists = new File(clientSettingsPath).exists() || FileToolAlt.isExists(clientSettingsPath);
        boolean bothExist = sourceExists && clientExists;
        boolean libExists = rbxIsLibFolderExisted();

        String resultChangesFlags = FileTool.read(new File(clientSettingsPath));
        if (resultChangesFlags.isEmpty()) {
            resultChangesFlags = FileToolAlt.readFile(sourceSettingsPath);
        }

        allowedToLaunch = false;

        if (bothExist && !libExists) {
            String finalResultChangesFlags = resultChangesFlags;
            new Handler(Looper.getMainLooper()).post(() -> {
                String currentFlags = FileTool.read(new File(sourceSettingsPath));
                if (!currentFlags.equals(finalResultChangesFlags)) {
                    fragment.setMessageText("Applying Roblox fast flags");

                    if (context instanceof SettingsActivity) {
                        ((SettingsActivity) context).applyFastFlag();
                    } else {
                        Log.w("FastFlag", "Context is not an instance of SettingsActivity");
                        Toast.makeText(context, "Not a SettingsActivity", Toast.LENGTH_SHORT).show();
                    }
                }

                new Thread(() -> {
                    if (isCancelled) return;

                    if (getStateSettingKey("ClearCacheEveryRBXLaunch")) {
                        File cacheRBXDir = new File(rbxCachesPaths);
                        File[] files = null;

                        // Ensure the directory exists
                        if (!cacheRBXDir.exists()) {
                            try {
                                if (FileToolAlt.isExists(cacheRBXDir.getAbsolutePath())) {
                                    files = FileToolAlt.listFiles(cacheRBXDir.getAbsolutePath());
                                } else {
                                    // Directory does not exist, nothing to delete
                                    return;
                                }
                            } catch (IOException e) {
                                Log.e("RBX Cache", "Error accessing cache directory", e);
                                return;
                            }
                        } else {
                            files = cacheRBXDir.listFiles();
                        }

                        // Proceed with file deletion
                        if (files != null) {
                            for (File file : files) {
                                if (file.isFile()) {
                                    if (file.delete()) {
                                        Log.d("RBX Cache", "Deleted: " + file.getName());
                                    } else {
                                        Log.w("RBX Cache", "Failed to delete: " + file.getName());
                                    }
                                }
                            }
                        }
                    }
                }).start();

                animateProgress(fragment, 0, 50, () -> {
                    if (isCancelled) return;

                    new Thread(() -> {
                        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(clientSettingsPath)))) {
                            writer.write(readFile(outFile));
                        } catch (IOException ignored) {}

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
                                        if (context instanceof Activity && context instanceof SettingsActivity) {
                                            ((SettingsActivity) context).LaunchWatcher();
                                        }
                                        if (context instanceof Activity && context instanceof MainActivity) {
                                            ((MainActivity) context).LaunchWatcher();
                                        }
                                        context.startActivity(launchIntent);
                                        if (fragment.isAdded()) {
                                            fragment.dismissAllowingStateLoss();
                                        }
                                    } else {
                                        fragment.setMessageText("Failed Launching Roblox");
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
                if (!bothExist) {
                    fragment.setMessageText("Your fast flags files are missing. Please go back to settings and save them again");
                } else {
                    fragment.setMessageText("Your Roblox may have been modified. Please download Roblox only on the official site");
                }
                fragment.setMessageStatus("-");
            });
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
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try (ZipFile zipFile = new ZipFile(ai.sourceDir)) {
                    for (ZipEntry entry : Collections.list(zipFile.entries())) {
                        if (entry.getName().startsWith("lib/")) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
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
            if (jsonObject.has(key)) {
                return Boolean.parseBoolean(jsonObject.getString(key));
            }
        } catch (Exception ignored) {}

        return false;
    }

    private String readFile(File file) {
        StringBuilder sb = new StringBuilder();
        if (!file.exists()) return "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException ignored) {}
        return sb.toString().trim();
    }
}

