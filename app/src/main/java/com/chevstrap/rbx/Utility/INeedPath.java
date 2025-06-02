package com.chevstrap.rbx.Utility;

import android.content.Context;

public class INeedPath {
    public static String getRBXPathDir(Context context, String packageName) {
        String getmyDataStorageAsString = String.valueOf(context.getFilesDir());
        String result = getmyDataStorageAsString.replace(
                "/" + context.getPackageName() + "/",
                "/" + packageName + "/"
        );

        // Add trailing slash if missing
        if (!result.endsWith("/")) {
            result += "/";
        }

        return result;
    }

    public static String getRBXPathCach(Context context, String packageName) {
        String getmyCacheDirStorageAsString = String.valueOf(context.getCacheDir());
        return getmyCacheDirStorageAsString.replace(
                "/" + context.getPackageName() + "/",
                "/" + packageName + "/"
        );
    }
}