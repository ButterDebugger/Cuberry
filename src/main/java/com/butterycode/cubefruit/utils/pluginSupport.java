package com.butterycode.cubefruit.utils;

import java.util.HashMap;

import com.butterycode.cubefruit.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

import me.clip.placeholderapi.PlaceholderAPI;

public class pluginSupport implements Listener {

	static FileConfiguration config = Main.plugin.getConfig();

	private static HashMap<String, Boolean> supports = new HashMap<>();

	// TODO list:
	// - add plugin checks for vanish
	// - add http request method
	// - add namemc api support

	public static void setup() {
		if (config.getBoolean("plugin.support.placeholderapi.enabled")) {
			if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) { // Check if plugin is installed
				supports.put("PlaceholderAPI", true);
			} else {
				supports.put("PlaceholderAPI", false);
			}
		} else {
			supports.put("PlaceholderAPI", false);
		}

		if (config.getBoolean("plugin.support.vanish.enabled")) {
			supports.put("Vanish", true);
		} else {
			supports.put("Vanish", false);
		}
	}

	public static boolean isSupported(String plugin) {
		return supports.containsKey(plugin) && supports.get(plugin);
	}

	// Vanish Support
	public boolean isVanished(Player player) {
		if (!supports.get("Vanish")) return false;

		for (MetadataValue meta : player.getMetadata("vanished")) {
			if (meta.asBoolean()) return true;
		}
		return false;
	}

	// PlaceholderAPI Support
	public static String getPlaceholders(OfflinePlayer player, String msg) {
		if (!supports.get("PlaceholderAPI")) return msg;

		return PlaceholderAPI.setPlaceholders(player, msg);
	}
}
