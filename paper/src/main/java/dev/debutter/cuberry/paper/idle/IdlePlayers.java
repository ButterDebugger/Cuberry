package dev.debutter.cuberry.paper.idle;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IdlePlayers implements Listener {

	private static final Set<UUID> IDLE_PLAYERS = ConcurrentHashMap.newKeySet();
	private static final ConcurrentHashMap<UUID, Long> LAST_ACTIVE_TIMESTAMP = new ConcurrentHashMap<>();

	/** Called internally when initializing */
	public static void hook() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(PaperCuberry.plugin(), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				UUID uuid = player.getUniqueId();

				// Skip already idle players or active players that are not afk yet
				if (IDLE_PLAYERS.contains(uuid)) continue;
				if (!isAFK(uuid)) continue;

				// Mark player as idle
				IDLE_PLAYERS.add(uuid);

				// Trigger player idle event
				PlayerIdleEvent event = new PlayerIdleEvent(player);
				event.callEvent();
			}
	    }, 0, 5);
	}

	/**
	 * @param uuid The uuid of a player
	 * @return Time since the player was last active in milliseconds
	 */
	public static double getIdleDuration(UUID uuid) {
		return System.currentTimeMillis() - LAST_ACTIVE_TIMESTAMP.getOrDefault(uuid, System.currentTimeMillis());
	}

	/**
	 * @param uuid The uuid of a player
	 * @return Whether the player is considered to be AFK
	 */
	public static boolean isAFK(UUID uuid) {
		return getIdleDuration(uuid) / 1000d >= PaperCuberry.plugin().getConfig().getDouble("idle.afk-threshold");
	}

	/*
	 * Custom event handlers for afk players
	 */

	@EventHandler(ignoreCancelled = true)
	public void onEntityPickupItem(EntityPickupItemEvent event) {
		Entity entity = event.getEntity();

		if (!(entity instanceof Player player)) return; // Make sure the entity is a player

        if (!isAFK(player.getUniqueId())) return; // If the player is not AFK, stop the rest of the code
		if (!PaperCuberry.plugin().getConfig().getBoolean("idle.disable-item-pickup-while-afk")) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onIdle(PlayerIdleEvent event) {
		Player player = event.getPlayer();

		// Auto kick players
		boolean doKick = PaperCuberry.plugin().getConfig().getBoolean("idle.kick-players");

		if (doKick && !Caboodle.hasPermission(player, "idle-kick.bypass")) {
			player.kick(
				AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("idle.auto_kick_message", player)),
				PlayerKickEvent.Cause.IDLING
			);
		}
	}

	/*
	 * Idle triggers
	 */

	private static void signalPlayerActive(Player player) {
		if (IDLE_PLAYERS.contains(player.getUniqueId())) {
			IDLE_PLAYERS.remove(player.getUniqueId());

			// Trigger player active event
			PlayerActiveEvent event = new PlayerActiveEvent(player);
			event.callEvent();
		}

		// Update timestamp
		LAST_ACTIVE_TIMESTAMP.put(player.getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		signalPlayerActive(player);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (!event.getReason().equals(PlayerQuitEvent.QuitReason.DISCONNECTED)) return;

		signalPlayerActive(player);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		signalPlayerActive(player);
	}

	@EventHandler
	public void onPlayerChat(AsyncChatEvent event) {
		Player player = event.getPlayer();

		signalPlayerActive(player);
	}

	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();

		signalPlayerActive(player);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		signalPlayerActive(player);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		signalPlayerActive(player);
	}
}
