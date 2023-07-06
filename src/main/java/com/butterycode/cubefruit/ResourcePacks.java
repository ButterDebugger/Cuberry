package com.butterycode.cubefruit;

import com.butterycode.cubefruit.utils.AwesomeText;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.HashMap;
import java.util.UUID;

public class ResourcePacks implements Listener {

	private HashMap<UUID, String> loadedPacks = new HashMap<UUID, String>();

	@EventHandler
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();

		handleResourcePacks(player, event.getSpawnLocation().getWorld());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		World to = event.getTo().getWorld();

		if (!event.getFrom().getWorld().equals(to)) {
			handleResourcePacks(player, to);
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		loadedPacks.remove(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
		Player player = event.getPlayer();
		Status status = event.getStatus();
		FileConfiguration config = Main.plugin().config();

		if (status.equals(Status.DECLINED)) {
			if (worldHasForcedPack(player.getWorld().getName())) { // Forced by world
				player.kickPlayer(AwesomeText.prettifyMessage("&cYou have to accept this worlds resource pack in order to play."));
			} else if (config.getBoolean("resource-packs.default.force")) { // Forced by server
				player.kickPlayer(AwesomeText.prettifyMessage("&cYou have to accept this servers resource pack in order to play."));
			}
		} else if (status.equals(Status.FAILED_DOWNLOAD)) {
			if (worldHasForcedPack(player.getWorld().getName())) { // Forced by world
				player.kickPlayer(AwesomeText.prettifyMessage("&cThis worlds resource pack failed to download."));
			} else if (config.getBoolean("resource-packs.default.force")) { // Forced by server
				player.kickPlayer(AwesomeText.prettifyMessage("&cThis servers resource pack failed to download."));
			}
		}
	}

	private boolean worldHasPack(String worldName) {
		FileConfiguration config = Main.plugin().config();

		if (config.getString("resource-packs.perworld." + worldName + ".url") == null) return false;
		if (config.getString("resource-packs.perworld." + worldName + ".url").equalsIgnoreCase("")) return false;
		return true;
	}

	private boolean worldHasForcedPack(String worldName) {
		FileConfiguration config = Main.plugin().config();

		if (!worldHasPack(worldName)) return false;
		return config.getBoolean("resource-packs.perworld." + worldName + ".force");
	}

	public void handleResourcePacks(Player player, World world) {
		FileConfiguration config = Main.plugin().config();

		if (worldHasPack(world.getName())) { // Use world resource pack
			String url = config.getString("resource-packs.perworld." + world.getName() + ".url");

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin(), new Runnable() {
				@Override
				public void run() {
					if (loadedPacks.containsKey(player.getUniqueId()) && loadedPacks.get(player.getUniqueId()).equals(url)) return;
					loadedPacks.put(player.getUniqueId(), url);

					player.setResourcePack(url);
				}
			}, 20);
		} else { // Use default resource pack
			String url = config.getString("resource-packs.default.url");

			if (!url.equalsIgnoreCase("")) {
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin(), new Runnable() {
					@Override
					public void run() {
						if (loadedPacks.containsKey(player.getUniqueId()) && loadedPacks.get(player.getUniqueId()).equals(url)) return;
						loadedPacks.put(player.getUniqueId(), url);

						player.setResourcePack(url);
					}
				}, 20);
			}
		}
	}
}
