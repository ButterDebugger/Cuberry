package dev.debutter.cuberry.paper.utils;

import dev.debutter.cuberry.paper.Paper;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

public class PluginSupport implements Listener {

	private static boolean includesPAPI = false;
	private static boolean includesProtoWeaver = false;

	// TODO list:
	//  - add http request method
	//  - add NameMC api support

	/**
	 * Automatically find plugin hooks
	 */
	public static void setup() {
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			includesPAPI = true;
			Paper.plugin().getLogger().info("Automatically hooking into PlaceholderAPI");
		}
		if (Bukkit.getPluginManager().getPlugin("ProtoWeaver") != null) {
			includesProtoWeaver = true;
			Paper.plugin().getLogger().info("Automatically hooking into ProtoWeaver");
		}
	}

	public static boolean hasPAPI() {
		return includesPAPI;
	}
	public static boolean hasProtoWeaver() {
		return includesProtoWeaver;
	}

	/**
	 * Simple Vanish check (Works with SuperVanish, PremiumVanish, VanishNoPacket and a few more vanish plugins)
	 */
	public boolean isVanished(Player player) {
		for (MetadataValue meta : player.getMetadata("vanished")) {
			if (meta.asBoolean()) return true;
		}
		return false;
	}

	/**
	 * Gets player placeholders from PlaceholderAPI
	 */
	public static String getPlaceholders(OfflinePlayer player, String msg) {
		if (!includesPAPI) return msg;

		return PlaceholderAPI.setPlaceholders(player, msg);
	}
}
