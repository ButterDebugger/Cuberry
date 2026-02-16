package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public class Rules extends CommandWrapper {

	public Rules() {
		CommandRegistry rulesCmd = new CommandRegistry(this, "rules");
		rulesCmd.setDescription("View the server rules");

		addRegistries(rulesCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = PaperCuberry.plugin().getConfig();

		if (label.equalsIgnoreCase("rules")) {
			if (!Caboodle.hasPermission(sender, "rules")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			List<String> rules = config.getStringList("commands.rules.message");

			sender.sendMessage(AwesomeText.beautifyMessage(String.join("\n", rules)));
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!Caboodle.hasPermission(sender, "rules")) return null;

		return Collections.emptyList();
	}
}
