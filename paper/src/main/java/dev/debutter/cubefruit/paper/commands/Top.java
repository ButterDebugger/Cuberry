package dev.debutter.cubefruit.paper.commands;

import dev.debutter.cubefruit.paper.Paper;
import dev.debutter.cubefruit.paper.commands.builder.CommandRegistry;
import dev.debutter.cubefruit.paper.commands.builder.CommandWrapper;
import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
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
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player) sender;

			Location loc = Caboodle.getHighestBlockLocation(player.getLocation()).add(0, 1, 0);
			player.teleport(loc);

			sender.sendMessage(AwesomeText.prettifyMessage("&a&l� &7You have been teleported to the highest block at your location."));
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
