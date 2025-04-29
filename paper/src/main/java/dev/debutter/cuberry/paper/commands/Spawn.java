package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.storage.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collections;
import java.util.List;

public class Spawn extends CommandWrapper implements Listener {

	// TODO: add remove spawn

	public Spawn() {
		CommandRegistry spawnCmd = new CommandRegistry(this, "spawn");
		spawnCmd.setDescription("Teleport to the spawn");

		CommandRegistry setSpawnCmd = new CommandRegistry(this, "setspawn");
		setSpawnCmd.setDescription("Set the spawn to your current position");

		addRegistries(spawnCmd, setSpawnCmd);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = Paper.plugin().getConfig();
		DataStorage doubleData = Paper.data().getStorage("data.yml");

		if (config.getBoolean("commands.spawn.spawn-on-join")) {
			if (!doubleData.exists("spawn")) return;

			Location spawnLoc = Caboodle.parseLocation(doubleData.getString("spawn"));

			if (config.getBoolean("commands.spawn.first-join-only")) {
				if (!player.hasPlayedBefore()) {
					player.teleport(spawnLoc);
				}
			} else {
				player.teleport(spawnLoc);
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = Paper.plugin().getConfig();
		DataStorage doubleData = Paper.data().getStorage("data.yml");

		if (config.getBoolean("commands.spawn.spawn-on-death")) {
			if (!doubleData.exists("spawn") || (player.getBedLocation() != null)) return;

			Location spawnLoc = Caboodle.parseLocation(doubleData.getString("spawn"));

			Bukkit.getScheduler().scheduleSyncDelayedTask(Paper.plugin(), () -> {
				player.teleport(spawnLoc);
			}, 1L);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		DataStorage doubleData = Paper.data().getStorage("data.yml");

		if (label.equalsIgnoreCase("spawn")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "spawn")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (doubleData.get("spawn") == null) {
				sender.sendMessage(AwesomeText.colorize("&cError: &7Spawn has not been set."));
				return true;
			}

			Location spawnLoc = Caboodle.parseLocation(doubleData.getString("spawn"));

			sender.sendMessage(AwesomeText.colorize("&a&l» &7You have been teleported to spawn."));
			player.teleport(spawnLoc);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
			return true;
		}

		if (label.equalsIgnoreCase("setspawn")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "setspawn")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			sender.sendMessage(AwesomeText.colorize("&a&l» &7Spawn has been set."));
			doubleData.set("spawn", Caboodle.stringifyLocation(player));
			return true;
		}
		return false;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("spawn") && Caboodle.hasPermission(sender, "spawn")) {
			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("setspawn") && Caboodle.hasPermission(sender, "setspawn")) {
			return Collections.emptyList();
		}

		return null;
	}
}
