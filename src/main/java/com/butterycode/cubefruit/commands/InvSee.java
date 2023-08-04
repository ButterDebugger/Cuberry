package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.AwesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class InvSee extends CommandWrapper {

	// FIXME: when you drag click on the bottom inventory is bugs out
	// TODO: expand the inventory to include the armor slots and offhand

	public InvSee() {
		CommandRegistry invSeeCmd = new CommandRegistry(this, "invsee");
		invSeeCmd.setDescription("View the inventory of other players");

		addRegistries(invSeeCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("invsee")) {
			if (!Caboodle.hasPermission(sender, "invsee")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.prettifyMessage("&3Usage: &7/invsee <player>"));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player other = Bukkit.getPlayer(args[0]);

				player.openInventory(other.getInventory());

				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7You are now viewing &f" + other.getName() + "&7's inventory."));
				return true;
			} else {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("invsee") && Caboodle.hasPermission(sender, "invsee")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
