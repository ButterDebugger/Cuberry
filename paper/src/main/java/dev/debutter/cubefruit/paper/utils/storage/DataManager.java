package dev.debutter.cubefruit.paper.utils.storage;

import org.bukkit.plugin.Plugin;

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

    public void deleteStorage() {
        // TODO: add this
    }
}
