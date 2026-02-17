package dev.debutter.cuberry.paper.resourcepack;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.kyori.adventure.resource.ResourcePackRequest.resourcePackRequest;

public class ResourcePacks implements Listener {

	private static final Set<ResourcePackInfo> GLOBAL_RESOURCE_PACKS = ConcurrentHashMap.newKeySet();
	private static final Map<String, Set<ResourcePackInfo>> WORLD_RESOURCE_PACKS = new ConcurrentHashMap<>();
	private static final Map<UUID, Set<UUID>> PLAYER_LOADED_PACKS = new ConcurrentHashMap<>();

	/** Called internally when initializing */
	public static void hook() {
		FileConfiguration config = PaperCuberry.plugin().getConfig();

		// Get the list of global resource packs
		List<Map<?, ?>> globalPacks = config.getMapList("resource-packs.global");

		for (Map<?, ?> map : globalPacks) {
			ResourcePackInfo packInfo = parseResourcePack(map);
			if (packInfo == null) continue;

			// Register the global pack
			registerResourcePack(packInfo);
		}

		// Get the list of per world resource packs
		ConfigurationSection worldSection = config.getConfigurationSection("resource-packs.per-world");

		if (worldSection != null) {
			Set<String> worldKeys = worldSection.getKeys(false);

			for (String worldName : worldKeys) {
				for (Map<?, ?> map : worldSection.getMapList(worldName)) {
					ResourcePackInfo packInfo = parseResourcePack(map);
					if (packInfo == null) continue;

					// Register the world specific pack
					registerResourcePack(packInfo, worldName);
				}
			}
		}
	}

	private static @Nullable ResourcePackInfo parseResourcePack(@NonNull Map<?, ?> map) {
		// Return early if the map does not contain the required keys
		if (!map.containsKey("id") || !map.containsKey("url") || !map.containsKey("hash")) return null;

		// Try parsing the values
		try {
			UUID id = UUID.fromString((String) map.get("id"));
			URI url = URI.create((String) map.get("url"));
			String hash = (String) map.get("hash");

			// Return the resource pack info
			return ResourcePackInfo.resourcePackInfo(id, url, hash);
		} catch (IllegalArgumentException | NullPointerException e) {
			// Log a warning to the console
			PaperCuberry.plugin().getLogger().warning("Failed to parse resource pack id or url.");
			return null;
		}
	}

	/**
	 * Registers a resource pack that will automatically be loaded with an optional world requirement
	 * @param packInfo The resource pack info
	 * @param worldName An optional world name that the resource pack will only apply to
	 */
	public static void registerResourcePack(@NotNull ResourcePackInfo packInfo, @Nullable String worldName) {
		if (worldName == null) {
			registerResourcePack(packInfo);
		} else {
			WORLD_RESOURCE_PACKS
				.computeIfAbsent(worldName, k -> ConcurrentHashMap.newKeySet())
				.add(packInfo);
		}
	}

	/**
	 * Registers a resource pack that will automatically be loaded
	 * @param packInfo The resource pack info
	 */
	public static void registerResourcePack(@NotNull ResourcePackInfo packInfo) {
		GLOBAL_RESOURCE_PACKS.add(packInfo);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		handleResourcePacks(player, player.getWorld());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		PLAYER_LOADED_PACKS.remove(uuid);
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
		UUID playerUuid = player.getUniqueId();

		// Collect the list of requested resource packs
		Set<ResourcePackInfo> requestedPacks = ConcurrentHashMap.newKeySet();
		requestedPacks.addAll(GLOBAL_RESOURCE_PACKS);

		Set<ResourcePackInfo> worldPacks =
			WORLD_RESOURCE_PACKS.get(world.getName());

		if (worldPacks != null) {
			requestedPacks.addAll(worldPacks);
		}

		// Unload resource packs not included but still loaded by the player
		Set<UUID> loadedPackIds = PLAYER_LOADED_PACKS.getOrDefault(player.getUniqueId(), Collections.emptySet());
		Set<UUID> packsToUnload = ConcurrentHashMap.newKeySet();

		// Create a set of requested IDs before modifying it
		Set<UUID> requestedIds = requestedPacks.stream()
			.map(ResourcePackInfo::id)
			.collect(Collectors.toSet());

		// Determine what to unload
		for (UUID loadedId : loadedPackIds) {
			if (!requestedIds.contains(loadedId)) {
				packsToUnload.add(loadedId);
			}
		}

		// Remove requested packs if they are already loaded
		requestedPacks.removeIf(pack ->
			loadedPackIds.contains(pack.id())
		);

		// Unload the packs
		if (!packsToUnload.isEmpty()) {
			PLAYER_LOADED_PACKS
				.computeIfAbsent(playerUuid, k -> ConcurrentHashMap.newKeySet())
				.removeAll(packsToUnload);

			// Send request to the player to remove the packs
			player.removeResourcePacks(packsToUnload);
		}

		// Send new resource pack request to the player
		if (!requestedPacks.isEmpty()) {
			ResourcePackRequest request = resourcePackRequest()
				.prompt(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("resource_packs.prompt", player)))
				.required(true)
				.packs(requestedPacks)
				.callback((packId,status,audience) -> audience.get(Identity.UUID).ifPresent(callbackPlayerUuid -> {
                    switch (status) {
                        case SUCCESSFULLY_LOADED -> PLAYER_LOADED_PACKS
                            .computeIfAbsent(callbackPlayerUuid, k -> ConcurrentHashMap.newKeySet())
                            .add(packId);
                        case DECLINED, FAILED_DOWNLOAD, INVALID_URL, FAILED_RELOAD, DISCARDED -> player.kick(
                            Component.text("Failed to load all of the resource packs requested by the server"),
                            PlayerKickEvent.Cause.RESOURCE_PACK_REJECTION
                        );
                        default -> {}
                    }
                }))
				.build();

			player.sendResourcePacks(request);
		}
	}
}
