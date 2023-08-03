package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.AwesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Top extends CommandWrapper {

	public Top() {
		CommandRegistry topCmd = new CommandRegistry(this, "top");
		topCmd.setDescription("Teleport to the highest block at your current location");

		addRegistries(topCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("top")) {
			if (!Caboodle.hasPermission(sender, "top")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			Location loc = Caboodle.getHighestBlockLocation(player.getLocation()).add(0, 1, 0);
			player.teleport(loc);

			sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7You have been teleported to the highest block at your location."));
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("top") && Caboodle.hasPermission(sender, "top")) {
			return Collections.emptyList();
		}

		return null;
	}
}
