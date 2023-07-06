package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.DataStorage;
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
		FileConfiguration config = Main.plugin().config();
		DataStorage doubleData = Main.plugin().getData("data.yml");

		if (config.getBoolean("commands.spawn.spawn-on-join")) {
			if (!doubleData.exists("spawn")) return;

			Location spawnLoc = caboodle.parseLocation(doubleData.getString("spawn"));

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
		FileConfiguration config = Main.plugin().config();
		DataStorage doubleData = Main.plugin().getData("data.yml");

		if (config.getBoolean("commands.spawn.spawn-on-death")) {
			if (!doubleData.exists("spawn") || (player.getBedLocation() != null)) return;

			Location spawnLoc = caboodle.parseLocation(doubleData.getString("spawn"));

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin(), () -> {
				player.teleport(spawnLoc);
			}, 1L);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		DataStorage doubleData = Main.plugin().getData("data.yml");

		if (label.equalsIgnoreCase("spawn")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!caboodle.hasPermission(sender, "spawn")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (doubleData.get("spawn") == null) {
				sender.sendMessage(awesomeText.colorize("&cError: &7Spawn has not been set."));
				return true;
			}

			Location spawnLoc = caboodle.parseLocation(doubleData.getString("spawn"));

			sender.sendMessage(awesomeText.colorize("&a&l» &7You have been teleported to spawn."));
			player.teleport(spawnLoc);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
			return true;
		}

		if (label.equalsIgnoreCase("setspawn")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player)sender;

			if (!caboodle.hasPermission(sender, "setspawn")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			sender.sendMessage(awesomeText.colorize("&a&l» &7Spawn has been set."));
			doubleData.set("spawn", caboodle.stringifyLocation(player));
			return true;
		}
		return false;
	}


	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("spawn") && caboodle.hasPermission(sender, "spawn")) {
			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("setspawn") && caboodle.hasPermission(sender, "setspawn")) {
			return Collections.emptyList();
		}

		return null;
	}
}
