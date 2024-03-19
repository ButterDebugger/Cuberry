package dev.debutter.cubefruit.paper;

import dev.debutter.cubefruit.paper.utils.AwesomeText;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
		World to = event.getTo().getWorld();

		if (!event.getFrom().getWorld().equals(to)) {
			handleResourcePacks(player, to);
		}
	}

	public void handleResourcePacks(Player player, World world) {
		FileConfiguration config = Paper.plugin().config();

		// Get the list of requested resource packs
		List<Map<?, ?>> requestedPacks = config.getMapList("resource-packs.global");
		if (config.isList("resource-packs.per-world." + world.getName())) {
			requestedPacks.addAll(config.getMapList("resource-packs.per-world." + world.getName()));
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(Paper.plugin(), () -> {
			ArrayList<ResourcePackInfo> packs = new ArrayList<>();

			player.removeResourcePacks();

			for (Map<?, ?> questionableMap : requestedPacks) {
				Map<String, Object> map = (Map<String, Object>) questionableMap;

				UUID packId;
				try {
					packId = UUID.fromString((String) map.get("id"));
				} catch (IllegalArgumentException e) {
					Paper.plugin().getLogger().warning("Invalid resource pack id, please use a valid UUID.");
					continue;
				}

				URI packUrl = URI.create((String) map.get("url"));
				String packHash = (String) map.get("hash");

				packs.add(resourcePackInfo(packId, packUrl, packHash));
			}

			ResourcePackRequest request = resourcePackRequest()
				.prompt(AwesomeText.beautifyMessage(Paper.locale().getMessage("resource_packs.prompt", player)))
				.required(config.getBoolean("resource-packs.forced"))
				.packs(packs)
				.build();

			player.sendResourcePacks(request);
		}, 1);
	}
}
