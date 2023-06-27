package com.butterycode.cubefruit.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.butterycode.cubefruit.utils.awesomeText;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dogTags;

public class Fly extends CommandWrapper {

	public Fly() {
		CommandRegistry flyCmd = new CommandRegistry(this, "fly");
		flyCmd.setDescription("Grant or revoke the ability to fly");

		addRegistries(flyCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("fly")) {
			if (!caboodle.hasPermission(sender, "fly")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
					return true;
				}

				Player player = (Player) sender;

				player.setAllowFlight(!player.getAllowFlight());

				if (player.getAllowFlight()) {
					sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Flight has been toggled on."));
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Flight has been toggled off."));
				}
				return true;
			}

			if (dogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("on")) {
						player.setAllowFlight(true);

						sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Flight has been enabled for &f" + player.getName() + "&7."));
						return true;
					} else if (args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("off")) {
						player.setAllowFlight(false);

						sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Flight has been disabled for &f" + player.getName() + "&7."));
						return true;
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Invalid arguments."));
						return true;
					}
				} else {
					player.setAllowFlight(!player.getAllowFlight());

					if (player.getAllowFlight()) {
						sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Flight has been toggled on for &f" + player.getName() + "&7."));
					} else {
						sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Flight has been toggled off for &f" + player.getName() + "&7."));
					}
					return true;
				}
			} else {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("fly") && caboodle.hasPermission(sender, "fly")) {
			if (args.length == 1) {
				return caboodle.getOnlinePlayerNames();
			}
			if (args.length == 2) {
				return Arrays.asList("enable", "disable");
			}

			return Collections.emptyList();
		}

		return null;
	}
}
