package dev.debutter.cubefruit.paper.utils.storage;

import dev.debutter.cubefruit.paper.utils.Caboodle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

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
		this.file = new File(plugin.getDataFolder() + File.separator + filepath);

		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				Caboodle.log(plugin, "Unable to create " + filepath, Caboodle.LogType.WARN);
				e.printStackTrace();
			}
		}

		this.data = YamlConfiguration.loadConfiguration(file);
	}

	public void save() { // Save data
		try {
			data.save(file);
		} catch (IOException e) {
			Caboodle.log(plugin, "Unable to save data.", Caboodle.LogType.WARN);
			e.printStackTrace();
		}
	}

	// Simplified Functions
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
	public String getString(String path) {
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
	public List<String> getStringList(String path) {
		return data.getStringList(path);
	}
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMapList(String path) {
		List<Map<String, Object>> maplist = new ArrayList<>();
		for (Map<?, ?> map : data.getMapList(path)) {
			maplist.add((Map<String, Object>) map);
		}
		return maplist;
	}
	public List<String> getKeys(String path) {
		List<String> keys = new ArrayList<>();
		keys.addAll(data.getConfigurationSection(path).getKeys(false));
		return keys;
	}
	public List<String> getKeys() {
		List<String> keys = new ArrayList<>();
		keys.addAll(data.getKeys(false));
		return keys;
	}
	public void set(String path, Object value) {
		data.set(path, value);
	}
	public void remove(String path) {
		data.set(path, null);
	}
	public boolean exists(String path) {
		if (data.get(path) == null) return false;
		return true;
	}
	public boolean existsNot(String path) {
		return !exists(path);
	}
	public Object get(String path) {
		return data.get(path);
	}
}
