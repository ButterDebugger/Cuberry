package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
			if (!Caboodle.hasPermission(sender, "heal")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
					return true;
				}

                player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
				player.setFoodLevel(20);
				player.setSaturation(5);
				player.setSaturatedRegenRate(10);

				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.heal.self", sender)));
				return true;
			}
			if (args.length > 0) {
				if (DogTags.isOnline(args[0])) {
					Player player = Bukkit.getPlayer(args[0]);

					player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
					player.setFoodLevel(20);
					player.setSaturation(5);
					player.setSaturatedRegenRate(10);

					sender.sendMessage(AwesomeText.beautifyMessage(
						Paper.locale().getMessage("commands.heal.other", sender),
						Placeholder.unparsed("player_name", player.getName())
					));
					return true;
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_not_online", sender)));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("heal") && Caboodle.hasPermission(sender, "heal")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
