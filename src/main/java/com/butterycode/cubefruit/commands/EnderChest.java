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

public class EnderChest extends CommandWrapper {

	public EnderChest() {
		CommandRegistry enderChestCmd = new CommandRegistry(this, "enderchest");
		enderChestCmd.addAliases("echest");
		enderChestCmd.setDescription("Lets you see inside an enderchest");

		addRegistries(enderChestCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("enderchest")) {
			if (!Caboodle.hasPermission(sender, "enderchest")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (args.length == 0) {
				player.openInventory(player.getEnderChest());

				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7You are now viewing your own enderchest."));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player other = Bukkit.getPlayer(args[0]);

				player.openInventory(other.getEnderChest());

				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7You are now viewing &f" + other.getName() + "&7's enderchest."));
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
		if (command.getName().equalsIgnoreCase("enderchest") && Caboodle.hasPermission(sender, "enderchest")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
