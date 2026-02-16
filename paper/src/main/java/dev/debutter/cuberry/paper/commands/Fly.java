package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
					return true;
				}

                player.setAllowFlight(!player.getAllowFlight());

				if (player.getAllowFlight()) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.fly.enable_self", sender)));
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.fly.disable_self", sender)));
				}
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				if (player == null) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
					return true;
				}

				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("on")) {
						player.setAllowFlight(true);

						sender.sendMessage(AwesomeText.beautifyMessage(
							PaperCuberry.locale().getMessage("commands.fly.enable_other", sender),
							Placeholder.component("other_name", player.name())
						));
						return true;
					} else if (args[1].equalsIgnoreCase("disable") || args[1].equalsIgnoreCase("off")) {
						player.setAllowFlight(false);

						sender.sendMessage(AwesomeText.beautifyMessage(
							PaperCuberry.locale().getMessage("commands.fly.disable_other", sender),
							Placeholder.component("other_name", player.name())
						));
						return true;
					} else {
						sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.invalid_arguments", sender)));
						return true;
					}
				} else {
					player.setAllowFlight(!player.getAllowFlight());

					if (player.getAllowFlight()) {
						sender.sendMessage(AwesomeText.beautifyMessage(
							PaperCuberry.locale().getMessage("commands.fly.enable_other", sender),
							Placeholder.component("other_name", player.name())
						));
					} else {
						sender.sendMessage(AwesomeText.beautifyMessage(
							PaperCuberry.locale().getMessage("commands.fly.disable_other", sender),
							Placeholder.component("other_name", player.name())
						));
					}
					return true;
				}
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
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
