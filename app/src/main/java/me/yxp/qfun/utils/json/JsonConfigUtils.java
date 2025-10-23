package me.yxp.qfun.utils.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonConfigUtils {

    public static void putString(String absoluteDir, String configName, String key, String value) throws Exception {
        putValue(absoluteDir, configName, key, value);
    }

    public static void putInt(String absoluteDir, String configName, String key, int value) throws Exception {
        putValue(absoluteDir, configName, key, value);
    }

    public static void putBoolean(String absoluteDir, String configName, String key, boolean value) throws Exception {
        putValue(absoluteDir, configName, key, value);
    }

    public static void putLong(String absoluteDir, String configName, String key, long value) throws Exception {
        putValue(absoluteDir, configName, key, value);
    }

    public static String getString(String absoluteDir, String configName, String key, String defaultValue) throws Exception {
        JSONObject json = loadConfig(absoluteDir, configName);
        return json.optString(key, defaultValue);
    }

    public static int getInt(String absoluteDir, String configName, String key, int defaultValue) throws Exception {
        JSONObject json = loadConfig(absoluteDir, configName);
        return json.optInt(key, defaultValue);
    }

    public static boolean getBoolean(String absoluteDir, String configName, String key, boolean defaultValue) throws Exception {
        JSONObject json = loadConfig(absoluteDir, configName);
        return json.optBoolean(key, defaultValue);
    }

    public static long getLong(String absoluteDir, String configName, String key, long defaultValue) throws Exception {
        JSONObject json = loadConfig(absoluteDir, configName);
        return json.optLong(key, defaultValue);
    }

    private static void putValue(String absoluteDir, String configName, String key, Object value) throws Exception {
        JSONObject json = loadConfig(absoluteDir, configName);
        json.put(key, value);
        saveConfig(absoluteDir, configName, json);
    }

    private static JSONObject loadConfig(String absoluteDir, String configName) throws Exception {
        File configFile = getConfigFile(absoluteDir, configName);
        if (!configFile.exists()) {
            return new JSONObject();
        }

        try (FileReader fileReader = new FileReader(configFile);
             BufferedReader reader = new BufferedReader(fileReader)) {
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
            String content = contentBuilder.toString();
            return new JSONObject(new JSONTokener(content));
        } catch (IOException | JSONException e) {
            throw new Exception("Failed to load config: " + configName, e);
        }
    }

    private static void saveConfig(String absoluteDir, String configName, JSONObject json) throws Exception {
        File configFile = getConfigFile(absoluteDir, configName);
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(json.toString(4));
        } catch (IOException e) {
            throw new Exception("Failed to save config: " + configName, e);
        }
    }

    private static File getConfigFile(String absoluteDir, String configName) {
        File dir = new File(absoluteDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, configName + ".json");
    }
}