package com.butterycode.cubefruit;

import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.idlePlayers;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class playerNametag implements Listener {

	static FileConfiguration config = Main.plugin.config();

	public static void start() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			updateNametag(player);
		}

		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					updateNametag(player);
				}
			}
		}, 0, 5);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		updateNametag(player);
	}

	private static void updateNametag(Player player) {
		if (!config.getBoolean("player-nametag.enabled")) return;

		UUID uuid = player.getUniqueId();
		String format = "";

		if (config.getBoolean("player-nametag.afk") && idlePlayers.isAFK(uuid)) {
			format = config.getString("player-nametag.afk-format") + "&r";
		} else {
			format = config.getString("player-nametag.format") + "&r";
		}

		format = awesomeText.prettifyMessage(format, player);

		if (config.getBoolean("player-nametag.change-displayname")) {
			player.setDisplayName(format);
		}

		if (config.getBoolean("player-nametag.change-playerlist")) {
			player.setPlayerListName(format);
		}
	}
}
