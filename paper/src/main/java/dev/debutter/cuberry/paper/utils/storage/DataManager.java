package dev.debutter.cuberry.paper.utils.storage;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;

public class DataManager {

    private Plugin plugin;
    private static HashMap<String, DataStorage> dataFiles = new HashMap<>();

    public DataManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void saveAll() {
        for (String key : dataFiles.keySet()) {
            dataFiles.get(key).save();
        }
    }

    public DataStorage getStorage(String filepath) {
        if (!dataFiles.containsKey(filepath)) {
            dataFiles.put(filepath, new DataStorage(this.plugin, filepath));
        }
        return dataFiles.get(filepath);
    }

    public boolean doesStorageExist(String filepath) {
        if (dataFiles.containsKey(filepath)) return true;

        File file = getStorageFile(plugin, filepath);
        return file.exists();
    }

    public void deleteStorage(String filepath) {
        if (!doesStorageExist(filepath)) return;

        // Try and delete the file
        File file = getStorageFile(plugin, filepath);
        boolean isDeleted = false;
        try {
            isDeleted = file.delete();
        } catch (SecurityException ignored) {}

        // Erase all the data if the file could not be deleted
        if (!isDeleted) {
            DataStorage storage = getStorage(filepath);
            for (String key : storage.getKeys()) {
                storage.remove(key);
            }
        }

        dataFiles.remove(filepath);
    }

    public static File getStorageFile(Plugin plugin, String filepath) {
        return new File(plugin.getDataFolder() + File.separator + filepath);
    }
}
