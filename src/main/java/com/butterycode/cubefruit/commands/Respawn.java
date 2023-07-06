package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Respawn extends CommandWrapper {

	public Respawn() {
		CommandRegistry respawnCmd = new CommandRegistry(this, "respawn");
		respawnCmd.setDescription("Respawn dead players");

		addRegistries(respawnCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("respawn")) {
			if (!caboodle.hasPermission(sender, "respawn")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(awesomeText.colorize("&3Usage: &7/respawn <username>"));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player otherPlayer = Bukkit.getPlayer(args[0]);
				if (otherPlayer.isDead()) {
					Location deathLoc = otherPlayer.getLocation();

					if (args.length < 2) {
						sender.sendMessage(awesomeText.colorize("&a&l» &f" + otherPlayer.getName() + "&7 has been respawned."));
						caboodle.respawn(otherPlayer);
						return true;
					} else if (args[1].equals("true")) {
						sender.sendMessage(awesomeText.colorize("&a&l» &f" + otherPlayer.getName() + "&7 has been respawned at their death location."));
						caboodle.respawn(otherPlayer);
						otherPlayer.teleport(deathLoc);
						return true;
					} else {
						sender.sendMessage(awesomeText.colorize("&a&l» &f" + otherPlayer.getName() + "&7 has been respawned."));
						caboodle.respawn(otherPlayer);
						return true;
					}
				} else {
					sender.sendMessage(awesomeText.colorize("&cError: &7Player isn't already dead."));
				}
				return true;
			} else {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("respawn") && caboodle.hasPermission(sender, "respawn")) {
			if (args.length == 1) {
				List<String> deadplayers = new ArrayList<>();
				List<String> players = new ArrayList<>();
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.isDead()) deadplayers.add(player.getName());
					players.add(player.getName());
				}
				if (deadplayers.size() == 0) deadplayers = players;

				return deadplayers;
			}
			if (args.length == 2) {
				return Arrays.asList("true", "false");
			}

			return Collections.emptyList();
		}

		return null;
	}
}
