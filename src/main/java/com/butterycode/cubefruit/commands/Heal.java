package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Heal extends CommandWrapper {

	public Heal() {
		CommandRegistry healCmd = new CommandRegistry(this, "heal");
		healCmd.setDescription("Heal yourself or a given player");

		addRegistries(healCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("heal")) {
			if (!caboodle.hasPermission(sender, "heal")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
					return true;
				}

				Player player = (Player) sender;

				player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
				player.setFoodLevel(20);
				player.setSaturation(5);
				player.setSaturatedRegenRate(10);

				sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7You have been healed."));
				return true;
			}
			if (args.length > 0) {
				if (DogTags.isOnline(args[0])) {
					Player player = Bukkit.getPlayer(args[0]);

					player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
					player.setFoodLevel(20);
					player.setSaturation(5);
					player.setSaturatedRegenRate(10);

					sender.sendMessage(awesomeText.prettifyMessage("&a&l» &f" + player.getName() + "&7 has been healed."));
					return true;
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player could not be found."));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("heal") && caboodle.hasPermission(sender, "heal")) {
			if (args.length == 1) {
				return caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
