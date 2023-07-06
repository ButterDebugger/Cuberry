package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.AwesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
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
		FileConfiguration config = Main.plugin().config();

		if (label.equalsIgnoreCase("rules")) {
			if (!Caboodle.hasPermission(sender, "rules")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			List<String> rules = config.getStringList("commands.rules.message");
			String msg = AwesomeText.prettifyMessage(String.join("\n", rules));

			sender.sendMessage(msg);
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
