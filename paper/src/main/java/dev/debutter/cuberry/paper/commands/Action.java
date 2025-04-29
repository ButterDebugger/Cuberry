package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Action extends CommandWrapper {

	public Action() {
		CommandRegistry actionCmd = new CommandRegistry(this, "action");
		actionCmd.addAliases("me");
		actionCmd.setDescription("Broadcasts a narrative message about yourself");

		addRegistries(actionCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = Paper.plugin().getConfig();

		if (cmd.getName().equalsIgnoreCase("action")) {
			if (!Caboodle.hasPermission(sender, "action")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/action <message>"));
				return true;
			}

			String str = String.join(" ", args);
			String format = config.getString("commands.action.message");

			Bukkit.broadcast(AwesomeText.beautifyMessage(format, player, Placeholder.unparsed("message", str)));
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		return null;
	}
}
