package com.chevstrap.rbx;

import android.util.Log;

import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Field;

public abstract class JsonManager<T> {
    protected T prop;
    protected T originalProp;
    protected boolean loaded = false;

    public abstract String getClassName();
    public abstract File getFileLocation();

    public String getLogIdentClass() {
        return "JsonManager<" + getClassName() + ">";
    }

    public boolean isLoaded() {
        return loaded;
    }

    public T getProp() {
        return prop;
    }

    public void setProp(T value) {
        this.prop = value;
    }

    public T getOriginalProp() {
        return originalProp;
    }

    protected T deserialize(JSONObject json, Class<T> clazz) throws Exception {
        T obj = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getFields()) {
            field.setAccessible(true);
            String name = field.getName();
            if (json.has(name)) {
                Object value = json.get(name);
                if (value != JSONObject.NULL) {
                    if (field.getType().isAssignableFrom(value.getClass())) {
                        field.set(obj, value);
                    } else {
                        if (field.getType() == int.class || field.getType() == Integer.class)
                            field.set(obj, json.getInt(name));
                        else if (field.getType() == boolean.class || field.getType() == Boolean.class)
                            field.set(obj, json.getBoolean(name));
                        else if (field.getType() == double.class || field.getType() == Double.class)
                            field.set(obj, json.getDouble(name));
                        else if (field.getType() == long.class || field.getType() == Long.class)
                            field.set(obj, json.getLong(name));
                        else if (field.getType() == String.class)
                            field.set(obj, json.getString(name));
                    }
                }
            }
        }
        return obj;
    }

    protected JSONObject serialize(T obj) throws Exception {
        JSONObject json = new JSONObject();
        for (Field field : obj.getClass().getFields()) {
            field.setAccessible(true);
            Object value = field.get(obj);
            json.put(field.getName(), value != null ? value : JSONObject.NULL);
        }
        return json;
    }

    public void load(boolean alertFailure) {
        final String logIdent = getLogIdentClass() + "::Load";
        File file = getFileLocation();

        Log.i(logIdent, "Loading from " + file.getAbsolutePath());

        try {
            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }

            String contents = builder.toString();
            JSONObject json = new JSONObject(contents);
            Class<T> clazz = getGenericTypeClass();

            prop = deserialize(json, clazz);
            originalProp = deserialize(json, clazz);

            loaded = true;
            Log.i(logIdent, "Loaded successfully!");

        } catch (Exception ex) {
            Log.w(logIdent, "Failed to load!", ex);

            if (alertFailure) {
//                App.showMessageBox("Failed to load " + getClassName() + ": " + ex.getMessage());

                try {
                    File backup = new File(file.getAbsolutePath() + ".bak");
                    if (file.exists()) {
                        boolean renamed = file.renameTo(backup);
                        if (!renamed) {
                            Log.w(logIdent, "Failed to rename file to backup");
                        }
                    }
                } catch (Exception copyEx) {
                    Log.w(logIdent, "Failed to create backup", copyEx);
                }
            }

            save(); // Save default object if failed
        }
    }

    public void save() {
        final String logIdent = getLogIdentClass() + "::Save";

        Log.i(logIdent, "Saving to " + getFileLocation().getAbsolutePath());

        try {
            File file = getFileLocation();
            File dir = file.getParentFile();
            if (dir != null && !dir.exists()) {
                if (dir.mkdirs()) {
                    return;
                }
            }

            JSONObject json = serialize(prop);
            String contents = json.toString(4); // pretty print

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(contents);
            }

            Log.i(logIdent, "Save complete!");

        } catch (Exception ex) {
            Log.e(logIdent, "Failed to save", ex);
//            App.showMessageBox("Failed to save " + getClassName() + ": " + ex.getMessage());
        }
    }

    protected Class<T> getGenericTypeClass() throws ClassCastException {
        throw new UnsupportedOperationException("Must override getGenericTypeClass()");
    }
}
