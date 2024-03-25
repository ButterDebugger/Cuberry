package dev.debutter.cubefruit.paper.commands;

import dev.debutter.cubefruit.paper.Paper;
import dev.debutter.cubefruit.paper.commands.builder.CommandRegistry;
import dev.debutter.cubefruit.paper.commands.builder.CommandWrapper;
import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
import dev.debutter.cubefruit.paper.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Fly extends CommandWrapper {

	public Fly() {
		CommandRegistry flyCmd = new CommandRegistry(this, "fly");
		flyCmd.setDescription("Grant or revoke the ability to fly");

		addRegistries(flyCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("fly")) {
			if (!Caboodle.hasPermission(sender, "fly")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
					return true;
				}

				Player player = (Player) sender;

				player.setAllowFlight(!player.getAllowFlight());

				if (player.getAllowFlight()) {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Flight has been toggled on."));
				} else {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Flight has been toggled off."));
				}
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("on")) {
						player.setAllowFlight(true);

						sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Flight has been enabled for &f" + player.getName() + "&7."));
						return true;
					} else if (args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("off")) {
						player.setAllowFlight(false);

						sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Flight has been disabled for &f" + player.getName() + "&7."));
						return true;
					} else {
						sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					}
				} else {
					player.setAllowFlight(!player.getAllowFlight());

					if (player.getAllowFlight()) {
						sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Flight has been toggled on for &f" + player.getName() + "&7."));
					} else {
						sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Flight has been toggled off for &f" + player.getName() + "&7."));
					}
					return true;
				}
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("fly") && Caboodle.hasPermission(sender, "fly")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}
			if (args.length == 2) {
				return Arrays.asList("enable", "disable");
			}

			return Collections.emptyList();
		}

		return null;
	}
}
