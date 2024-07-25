package dev.debutter.cuberry.paper.utils.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStorage {

	private final File file;
	private final FileConfiguration data;
	private final Plugin plugin;

	public DataStorage(Plugin plugin, String filepath) {
		this.plugin = plugin;
		this.file = DataManager.getStorageFile(plugin, filepath);

		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				plugin.getLogger().warning("Unable to create " + filepath);
				e.printStackTrace();
			}
		}

		this.data = YamlConfiguration.loadConfiguration(file);
	}

	public void save() { // Save data
		try {
			data.save(file);
		} catch (IOException e) {
			plugin.getLogger().warning("Unable to save data.");
			e.printStackTrace();
		}
	}

	public File getFile() {
		return file;
	}

	/*
	 * Simplified Functions
	 */

	public void addToList(String path, String value) {
		List<String> newList = getStringList(path);
		newList.add(value);
		set(path, newList);
	}
	public void addToList(String path, String value, boolean respect) {
		if (respect) { // Prevent duplicates
			if (!listContains(path, value)) {
				addToList(path, value);
			}
		} else {
			addToList(path, value);
		}
	}
	public void addToList(String path, HashMap<String, Object> value) {
		List<Map<String, Object>> newList = getMapList(path);
		newList.add(value);
		set(path, newList);
	}
	public void removeFromList(String path, String value) {
		List<String> newList = getStringList(path);
		newList.remove(value);
		set(path, newList);
	}
	public boolean listContains(String path, String value) {
		return getStringList(path).contains(value);
	}
	public @Nullable String getString(String path) {
		return data.getString(path);
	}
	public int getInteger(String path) {
		return data.getInt(path);
	}
	public long getLong(String path) {
		return data.getLong(path);
	}
	public double getDouble(String path) {
		return data.getDouble(path);
	}
	public boolean getBoolean(String path) {
		return data.getBoolean(path);
	}
	public @NotNull List<String> getStringList(String path) {
		return data.getStringList(path);
	}
	@SuppressWarnings("unchecked")
	public @NotNull List<Map<String, Object>> getMapList(String path) {
		List<Map<String, Object>> maplist = new ArrayList<>();
		for (Map<?, ?> map : data.getMapList(path)) {
			maplist.add((Map<String, Object>) map);
		}
		return maplist;
	}
	public @NotNull List<String> getKeys(String path) {
		List<String> keys = new ArrayList<>();

		ConfigurationSection section = data.getConfigurationSection(path);
		if (section == null) return keys;

		keys.addAll(section.getKeys(false));
		return keys;
	}
	public @NotNull List<String> getKeys() {
        return new ArrayList<>(data.getKeys(false));
	}
	public void set(String path, Object value) {
		data.set(path, value);
	}
	public void remove(String path) {
		data.set(path, null);
	}
	public boolean exists(String path) {
        return data.get(path) != null;
    }
	public @Nullable Object get(String path) {
		return data.get(path);
	}
}
