package dev.debutter.cuberry.paper.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;

public class LocaleManager {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final HashMap<String, JsonObject> localeObjects = new HashMap<>();
	private static String defaultLocale;

	public LocaleManager(Plugin plugin, Charset charset, String ...locales) {
		defaultLocale = locales[0];

		// Get every language saved in the plugin resources
		for (String locale : locales) {
			loadLanguage(plugin, charset, locale);
		}

		// Load other languages saved on the disk
		loadLanguages(plugin);
	}

	/**
	 *  Loads a language stored in the plugin resources and syncs it with the disk
	 */
	private void loadLanguage(Plugin plugin, Charset charset, String locale) {
		String filepath = "lang/" + locale + ".json";

		// Get plugin language resource
		InputStream input = plugin.getResource(filepath);
		if (input == null) return;

		String resourceContent = new BufferedReader(new InputStreamReader(input, charset))
				.lines()
				.collect(Collectors.joining("\n"));
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
			Files.writeString(langFile.toPath(), gson.toJson(jsonResource));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		localeObjects.put(locale, jsonResource);
	}

	/**
	 *  Loads additional unknown languages stored on the disk
	 */
	private void loadLanguages(Plugin plugin) {
		File langDir = new File(plugin.getDataFolder() + File.separator + "lang");
		langDir.mkdirs();

		File[] langFiles = langDir.listFiles();
		if (langFiles != null) {
			for (File file : langFiles) {
				if (!file.isFile() || !file.canRead()) continue;

				String locale = AwesomeText.removeSuffix(file.getName(), ".json");
				if (localeObjects.containsKey(locale)) continue;

				JsonObject json = null;

				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					String content = br.lines().collect(Collectors.joining("\n"));
					json = gson.fromJson(content, JsonObject.class);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}

				if (json != null) {
					localeObjects.put(locale, json);
				}
			}
		}
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
	public String getMessage(String key, CommandSender sender) {
		if (sender instanceof Player) {
			return getMessage(key, (Player) sender);
		}
		return getMessage(key);
	}
	public String getMessage(String key, String locale) {
		if (!localeObjects.containsKey(locale)) {
			if (!locale.equals(defaultLocale)) {
				return getMessage(key, defaultLocale);
			}
			return null;
		}
		JsonObject jsonLocale = localeObjects.get(locale);
		if (!jsonLocale.has(key)) {
			if (!locale.equals(defaultLocale)) {
				return getMessage(key, defaultLocale);
			}
			return null;
		}
		return jsonLocale.getAsJsonPrimitive(key).getAsString();
	}

	public Component getBeautifiedMessage(String key, TagResolver... tagResolvers) {
		return AwesomeText.beautifyMessage(getMessage(key), tagResolvers);
	}
	public Component getBeautifiedMessage(String key, Player player, TagResolver... tagResolvers) {
		return AwesomeText.beautifyMessage(getMessage(key, player), tagResolvers);
	}
	public Component getBeautifiedMessage(String key, CommandSender sender, TagResolver... tagResolvers) {
		return AwesomeText.beautifyMessage(getMessage(key, sender), tagResolvers);
	}
	public Component getBeautifiedMessage(String key, String locale, TagResolver... tagResolvers) {
		return AwesomeText.beautifyMessage(getMessage(key, locale), tagResolvers);
	}
}
