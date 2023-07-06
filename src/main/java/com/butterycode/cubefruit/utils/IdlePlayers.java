package com.butterycode.cubefruit.utils;

import com.butterycode.cubefruit.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IdlePlayers implements Listener {

	private static Map<UUID, Integer> idleTimer = new HashMap<>();

	public static void start() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			idleTimer.put(player.getUniqueId(), 0);
		}

		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin(), new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					idleTimer.putIfAbsent(player.getUniqueId(), 0);
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
		return getIdleDuration(uuid) / 20 >= Main.plugin().config().getInt("idle.afk-threshold");
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
