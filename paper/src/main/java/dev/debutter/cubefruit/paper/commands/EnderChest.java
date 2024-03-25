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
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
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
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_found", sender)));
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
