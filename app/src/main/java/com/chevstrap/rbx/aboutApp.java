package com.chevstrap.rbx;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;

public class aboutApp {

    private final Context context;

    public aboutApp(Context context) {
        this.context = context;
    }

    public String getAppVersion() {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            return null;
        }
    }

    public String getLatestVersion() {
        try {
            URL url = new URL("https://api.github.com/repos/frossky/chevstrap/releases/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "no";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            JSONObject json = new JSONObject(response.toString());

            if (!json.has("tag_name")) {
                return "no";
            }

            return json.getString("tag_name").replaceFirst("^v", "");

        } catch (Exception e) {
            //e.printStackTrace(); // Helpful for debugging
            return "no";
            //return "Error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
        }
    }

    public boolean isInternetWorking() {
        try {
            URL url = new URL("https://www.google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();

            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (IOException e) {
            return false;
        }
    }
}
