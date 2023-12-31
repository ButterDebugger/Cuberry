package dev.debutter.cubefruit.paper.commands;

import dev.debutter.cubefruit.paper.Paper;
import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
import dev.debutter.cubefruit.paper.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Sudo extends CommandWrapper {

	public Sudo() {
		CommandRegistry sudoCmd = new CommandRegistry(this, "sudo");
		sudoCmd.setDescription("Make another player perform commands or speak in chat");

		addRegistries(sudoCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("sudo")) {
			if (!Caboodle.hasPermission(sender, "sudo")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/sudo <player> <args>"));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				String str = String.join(" ", Caboodle.splice(args, 0, 1));

				Player player = Bukkit.getPlayer(args[0]);
				assert player != null;

				if (str.startsWith("/")) {
					str = str.substring(1);
					boolean result = player.performCommand(str);

					if (result) {
						sender.sendMessage(AwesomeText.colorize("&a&l» &7Command was successful."));
						return true;
					} else {
						sender.sendMessage(AwesomeText.colorize("&a&l» &7Command was not successful."));
						return true;
					}
				} else {
					player.chat(str);

					sender.sendMessage(AwesomeText.colorize("&a&l» &7Message has been sent."));
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
		if (label.equalsIgnoreCase("sudo") && Caboodle.hasPermission(sender, "sudo")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}
		}

		return null;
	}
}
