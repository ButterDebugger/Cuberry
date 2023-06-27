package com.butterycode.cubefruit.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.butterycode.cubefruit.Main;

public class idlePlayers implements Listener {

	static FileConfiguration config = Main.plugin.config();

	private static Map<UUID, Integer> idleTimer = new HashMap<>();

	public static void start() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			idleTimer.put(player.getUniqueId(), 0);
		}

		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (idleTimer.get(player.getUniqueId()) == null) {
						idleTimer.put(player.getUniqueId(), 0);
					}

					idleTimer.put(player.getUniqueId(), idleTimer.get(player.getUniqueId()) + 1);
				}
			}
		}, 0, 1);
	}

	public static double getIdleDuration(UUID uuid) {
		if (idleTimer.containsKey(uuid)) {
			return idleTimer.get(uuid);
		} else {
			return 0d;
		}
	}

	public static boolean isAFK(UUID uuid) {
		return getIdleDuration(uuid) / 20 >= config.getInt("idle.afk-threshold");
	}
	
	// Event handlers:
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		idleTimer.put(player.getUniqueId(), 0);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		idleTimer.put(player.getUniqueId(), 0);
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();

		idleTimer.put(player.getUniqueId(), 0);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		idleTimer.put(player.getUniqueId(), 0);
	}
}
