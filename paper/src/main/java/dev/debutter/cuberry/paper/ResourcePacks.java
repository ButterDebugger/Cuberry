package dev.debutter.cuberry.paper;

import dev.debutter.cuberry.paper.utils.AwesomeText;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.resource.ResourcePackInfo.resourcePackInfo;
import static net.kyori.adventure.resource.ResourcePackRequest.resourcePackRequest;

public class ResourcePacks implements Listener {

	// TODO: Prevent players from reloading already loaded resource packs

	@EventHandler
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		Player player = event.getPlayer();

		handleResourcePacks(player, event.getSpawnLocation().getWorld());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		World from = event.getFrom().getWorld();
		World to = event.getTo().getWorld();

		if (from.equals(to)) return;

		handleResourcePacks(player, to);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World from = event.getFrom();
		World to = player.getWorld();

		if (from.equals(to)) return;

		handleResourcePacks(player, to);
	}

	public void handleResourcePacks(Player player, World world) {
		FileConfiguration config = PaperCuberry.plugin().getConfig();

		// Get the list of requested resource packs
		List<Map<?, ?>> requestedPacks = config.getMapList("resource-packs.global");
		if (config.isList("resource-packs.per-world." + world.getName())) {
			requestedPacks.addAll(config.getMapList("resource-packs.per-world." + world.getName()));
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(PaperCuberry.plugin(), () -> {
			ArrayList<ResourcePackInfo> packs = new ArrayList<>();

			player.removeResourcePacks();

			for (Map<?, ?> questionableMap : requestedPacks) {
				Map<String, Object> map = (Map<String, Object>) questionableMap;

				UUID packId;
				try {
					packId = UUID.fromString((String) map.get("id"));
				} catch (IllegalArgumentException e) {
					PaperCuberry.plugin().getLogger().warning("Invalid resource pack id, please use a valid UUID.");
					continue;
				}

				URI packUrl = URI.create((String) map.get("url"));
				String packHash = (String) map.get("hash");

				packs.add(resourcePackInfo(packId, packUrl, packHash));
			}

			ResourcePackRequest request = resourcePackRequest()
				.prompt(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("resource_packs.prompt", player)))
				.required(config.getBoolean("resource-packs.forced"))
				.packs(packs)
				.build();

			player.sendResourcePacks(request);
		}, 1);
	}
}
