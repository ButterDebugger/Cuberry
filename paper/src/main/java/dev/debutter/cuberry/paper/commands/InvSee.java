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
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/invsee <player>"));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player other = Bukkit.getPlayer(args[0]);

				player.openInventory(other.getInventory());

				sender.sendMessage(AwesomeText.beautifyMessage(
					PaperCuberry.locale().getMessage("commands.invsee.success", sender),
					Placeholder.unparsed("player_name", player.getName())
				));
				return true;
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
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
