package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dataStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Collections;
import java.util.List;

public class Home extends CommandWrapper implements Listener {

	public Home() {
		CommandRegistry homeCmd = new CommandRegistry(this, "home");
		homeCmd.setDescription("Teleport to your home");

		CommandRegistry sethomeCmd = new CommandRegistry(this, "sethome");
		sethomeCmd.setDescription("Set your home to your current location");

		CommandRegistry delhomeCmd = new CommandRegistry(this, "delhome");
		delhomeCmd.setDescription("Remove your home");

		addRegistries(homeCmd, sethomeCmd, delhomeCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		dataStorage playerData = Main.plugin().getData("players.yml");

		if (label.equalsIgnoreCase("home")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player)sender;

			if (!caboodle.hasPermission(sender, "home")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length != 0) {
				sender.sendMessage(awesomeText.colorize("&cUsage: &7/home"));
				return true;
			}

			if (playerData.existsNot(player.getUniqueId() + ".home")) {
				sender.sendMessage(awesomeText.colorize("&cError: &7You do not have a home set."));
				return true;
			}

			Location homeLoc = caboodle.parseLocation(playerData.getString(player.getUniqueId() + ".home"));

			sender.sendMessage(awesomeText.colorize("&a&l» &7You have been teleported home."));
			player.teleport(homeLoc);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1f);
			return true;
		} else if (label.equalsIgnoreCase("sethome")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!caboodle.hasPermission(sender, "sethome")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length != 0) {
				sender.sendMessage(awesomeText.colorize("&cUsage: &7/sethome"));
				return true;
			}

			sender.sendMessage(awesomeText.colorize("&a&l» &7Your home has been set."));
			playerData.set(player.getUniqueId() + ".home", caboodle.stringifyLocation(player));
			return true;
		} else if (label.equalsIgnoreCase("delhome")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player)sender;

			if (!caboodle.hasPermission(sender, "delhome")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length != 0) {
				sender.sendMessage(awesomeText.colorize("&cUsage: &7/delhome"));
				return true;
			}

			if (playerData.get(player.getUniqueId() + ".home") == null) {
				sender.sendMessage(awesomeText.colorize("&cError: &7You don't already have a home."));
				return true;
			} else {
				playerData.remove(player.getUniqueId() + ".home");
				sender.sendMessage(awesomeText.colorize("&a&l» &7Your home has been deleted."));
				return true;
			}
		}
		return false;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		dataStorage playerData = Main.plugin().getData("players.yml");

		if (Main.plugin().config().getBoolean("commands.spawn.spawn-on-death")) {
			if (playerData.existsNot(player.getUniqueId() + ".home") || (player.getBedLocation() != null)) return;

			Location homeLoc = caboodle.parseLocation(playerData.getString(player.getUniqueId() + ".home"));

			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin(), () -> {
				player.teleport(homeLoc);
			}, 2L);
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("home") && caboodle.hasPermission(sender, "home")) {
			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("sethome") && caboodle.hasPermission(sender, "sethome")) {
			return Collections.emptyList();
		}
		if (label.equalsIgnoreCase("delhome") && caboodle.hasPermission(sender, "delhome")) {
			return Collections.emptyList();
		}

		return null;
	}
}
