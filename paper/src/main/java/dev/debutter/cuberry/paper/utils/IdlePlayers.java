package dev.debutter.cuberry.paper.utils;

import dev.debutter.cuberry.paper.Paper;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IdlePlayers implements Listener {

	private static final Map<UUID, Long> idleTimestamp = new HashMap<>();

	public static void start() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Paper.plugin(), () -> {
            double autoKickThreshold = Paper.plugin().getConfig().getDouble("idle.auto-kick-threshold");
            if (autoKickThreshold < 0) return;

            for (Player player : Bukkit.getOnlinePlayers()) {
				if (Caboodle.hasPermission(player, "idle-kick.bypass")) continue;

                if (getIdleDuration(player.getUniqueId()) / 1000d >= autoKickThreshold) {
                    player.kick(AwesomeText.beautifyMessage(Paper.locale().getMessage("idle.auto_kick_message", player)), PlayerKickEvent.Cause.IDLING);
                }
            }
	    }, 0, 5);
	}

	/**
	 * @param uuid The uuid of a player
	 * @return Time since the player was last active in milliseconds
	 */
	public static double getIdleDuration(UUID uuid) {
		return System.currentTimeMillis() - idleTimestamp.getOrDefault(uuid, System.currentTimeMillis());
	}

	/**
	 * @param uuid The uuid of a player
	 * @return Whether the player is considered to be AFK
	 */
	public static boolean isAFK(UUID uuid) {
		return getIdleDuration(uuid) / 1000d >= Paper.plugin().getConfig().getDouble("idle.afk-threshold");
	}

	/*
	 * Custom event handlers for afk players
	 */

	@EventHandler(ignoreCancelled = true)
	public void onEntityPickupItem(EntityPickupItemEvent event) {
		Entity entity = event.getEntity();

		if (!(entity instanceof Player)) return; // Make sure the entity is a player

		Player player = (Player) entity;

		if (!isAFK(player.getUniqueId())) return; // If the player is not AFK, stop the rest of the code
		if (!Paper.plugin().getConfig().getBoolean("idle.disable-item-pickup-while-afk")) return;

		event.setCancelled(true);
	}

	/*
	 * Anti-AFK triggers
	 */

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		idleTimestamp.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		idleTimestamp.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		Player player = event.getPlayer();

		idleTimestamp.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();

		idleTimestamp.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		idleTimestamp.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		idleTimestamp.put(player.getUniqueId(), System.currentTimeMillis());
	}
}
