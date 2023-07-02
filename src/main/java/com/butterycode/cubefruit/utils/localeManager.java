package com.butterycode.cubefruit.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;

public class localeManager {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static HashMap<String, JsonObject> localeObjects = new HashMap<>();
	private static String defaultLocale;

	public localeManager(Plugin plugin, String ...locales) {
		defaultLocale = locales[0];

		// Get every language saved in the plugin resources
		for (String locale : locales) {
			loadLanguage(plugin, locale);
		}

		// TODO: load other languages saved on the disk
	}

	private void loadLanguage(Plugin plugin, String locale) {
		String filepath = "lang/" + locale + ".json";

		// Get plugin language resource
		InputStream input = plugin.getResource(filepath);
		if (input == null) return;

		String resourceContent = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
		JsonObject jsonResource = gson.fromJson(resourceContent, JsonObject.class);

		// Check to merge language content found on the disk
		File langFile = new File(plugin.getDataFolder() + File.separator + filepath);
		String diskContent;
		JsonObject jsonDisk = null;

		if (langFile.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(langFile))) {
				diskContent = br.lines().collect(Collectors.joining("\n"));
				jsonDisk = gson.fromJson(diskContent, JsonObject.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Resolve language objects
			if (jsonDisk != null) {
				for (String key : jsonDisk.keySet()) {
					jsonResource.add(key, jsonDisk.get(key));
				}
			}
		}

		// Save language file to disk
		try {
			langFile.getParentFile().mkdirs();
			Files.deleteIfExists(langFile.toPath());
			Files.writeString(langFile.toPath(), gson.toJson(jsonResource)); // TODO: fix encoding issue
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		localeObjects.put(locale, jsonResource);
	}

	public void setDefaultLocale(String locale) {
		defaultLocale = locale;
	}
	public String getDefaultLocale() {
		return defaultLocale;
	}

	public String getMessage(String key) {
		return getMessage(key, defaultLocale);
	}
	public String getMessage(String key, Player player) {
		return getMessage(key, player.getLocale());
	}
	public String getMessage(String key, String locale) {
		if (!localeObjects.containsKey(locale)) {
			if (!locale.equals(defaultLocale)) {
				return getMessage(key, defaultLocale);
			}
			return null;
		}
		return localeObjects.get(locale).getAsJsonPrimitive(key).getAsString();
	}

	// TODO list:
	/* - default to en_us
	 * - store in json
	 * - always keep locale file up to date
	 *   - remove non existent keys
	 *   - add new entries found in the plugin
	 * - use "&a&l» &7" instead of "&7" for successful commands
	 */
}
