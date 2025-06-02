package com.chevstrap.rbx;

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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LaunchHandler {
    private Context context;
    private FragmentManager fragmentManager;
    private volatile boolean isCancelled = false;
    private String packageName;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void LaunchRoblox(boolean isApplyFFlags) throws IOException {
        if (context == null || fragmentManager == null) {
            throw new IllegalStateException("Context or FragmentManager not set. Call setContext() and setFragmentManager() first.");
        }

        this.packageName = getPackageTarget(context);
        isCancelled = false;

        LoadingFragment fragment = createLoadingDialog();
        fragment.setCancelable(false);

        new Handler(Looper.getMainLooper()).post(() -> {
            fragment.setMessageText("Preparing...");
            fragment.setMessageStatus("0%");
            try {
                fragment.show(fragmentManager, "Messagebox");
            } catch (IllegalStateException ignored) {}
        });

        if (rbxIsLibFolderExisted()) {
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Your Roblox may have been modified", Toast.LENGTH_LONG).show()
            );
            isCancelled = true;
            return;
        }

        animateProgress(fragment, 0, 50, () -> {
            if (isCancelled) return;

            new Handler(Looper.getMainLooper()).post(() -> {
                if (isApplyFFlags) {
                    fragment.setMessageText("Apply Fast Flags");
                }
            });

            animateProgress(fragment, 50, 90, () -> {
                if (isCancelled) return;

                animateProgress(fragment, 90, 100, () -> {
                    if (isCancelled) return;

                    new Handler(Looper.getMainLooper()).post(() ->
                            fragment.setMessageText("Starting Roblox")
                    );

                    Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        if (context instanceof SettingsActivity) {
                            ((SettingsActivity) context).LaunchWatcher();
                        } else if (context instanceof MainActivity) {
                            ((MainActivity) context).LaunchWatcher();
                        }

                        context.startActivity(launchIntent);

                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (fragment.isAdded()) {
                                fragment.dismissAllowingStateLoss();
                            }
                        });
                    } else {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            fragment.setMessageText("Failed to launch Roblox");
                            fragment.setMessageStatus("-");
                        });
                    }
                });
            });
        });
    }

    private void animateProgress(LoadingFragment fragment, int start, int end, Runnable onComplete) {
        Handler handler = new Handler(Looper.getMainLooper());
        int steps = 20;
        int delay = 3500 / steps;
        float increment = (float) (end - start) / steps;

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
                    if (onComplete != null) onComplete.run();
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

    public static String getPackageTarget(Context context) {
        String preferred = getSetting(context, "PreferredRobloxApp");
        if ("Roblox VN".equals(preferred)) {
            return "com.roblox.client.vnggames";
        } else if ("Roblox".equals(preferred)) {
            return "com.roblox.client";
        }

        try {
            context.getPackageManager().getPackageInfo("com.roblox.client", 0);
            return "com.roblox.client";
        } catch (PackageManager.NameNotFoundException e) {
            try {
                context.getPackageManager().getPackageInfo("com.roblox.client.vnggames", 0);
                return "com.roblox.client.vnggames";
            } catch (PackageManager.NameNotFoundException ignored) {
                return "com.roblox.client"; // fallback
            }
        }
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

    public static boolean isRootAvailable() {
        return FileToolAlt.isRootAvailable();
    }

    public static String getSetting(Context context, String flagName) {
        File filePath = new File(context.getDataDir(), "LastAppSettings.json");

        if (!filePath.exists()) {
            return null;
        }

        try {
            String content = FileTool.read(filePath);
            JSONObject jsonObject = new JSONObject(content);
            return jsonObject.optString(flagName, null);
        } catch (Exception ignored) {
            return null;
        }
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
