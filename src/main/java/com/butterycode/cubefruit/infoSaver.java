package com.butterycode.cubefruit;

import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class infoSaver implements Listener {

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin(), new Runnable() {
			@Override
			public void run() {
				DataStorage doubleData = Main.plugin().getData("data.yml");

				for (World world : Bukkit.getWorlds()) {
					doubleData.set("worlds." + world.getName() + ".environment", world.getEnvironment().toString());
				}
			}
		}, 0, 1);
	}

	public static void end() {}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		DataStorage playerData = Main.plugin().getData("players.yml");

		String username = player.getDisplayName().toString();
		if (playerData.exists(player.getUniqueId() + ".username")) {
			String oldUsername = playerData.getString(player.getUniqueId() + ".username");

			if (!oldUsername.equals(username)) {
				playerData.addToList(player.getUniqueId() + ".name-history", oldUsername, true);
			}
		}
		playerData.set(player.getUniqueId() + ".username", username);

		playerData.set(player.getUniqueId() + ".ipaddress", player.getAddress().getAddress().getHostAddress().toString());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		DataStorage playerData = Main.plugin().getData("players.yml");

		playerData.set(player.getUniqueId() + ".logoutlocation", caboodle.stringifyLocation(player));
	}
}
