package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import com.butterycode.cubefruit.utils.dataStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Report extends CommandWrapper {

	FileConfiguration config = Main.plugin.config();
	dataStorage reportData = Main.plugin.getData("reports.yml");

	// TODO: make a report history command

	public Report() {
		CommandRegistry reportCmd = new CommandRegistry(this, "report");
		reportCmd.setDescription("Report another player to staff");

		addRegistries(reportCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("report")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player) sender;

			if (!caboodle.hasPermission(sender, "report")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(awesomeText.prettifyMessage("&3Usage: &7/report <player> <reason>"));
				return true;
			}
			if (args.length == 1) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Please enter a reason for the report."));
				return true;
			}

			OfflinePlayer reportedPlayer = caboodle.getOfflinePlayer(args[0]);

			if (!reportedPlayer.hasPlayedBefore()) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7That player has never played on this server."));
				return true;
			}

			String reason = String.join(" ", caboodle.splice(args, 0, 1));

			if (config.getBoolean("commands.report.restrict-reasons")) {
				boolean isDisallowed = true;

				for (String allowedReason : config.getStringList("commands.report.reasons")) {
					if (allowedReason.equalsIgnoreCase(reason)) {
						isDisallowed = false;
						reason = allowedReason; // Match the reason with allowed reason
						break;
					}
				}

				if (isDisallowed) {
					String reasons = awesomeText.commaOrSeparatedList(new ArrayList<>(config.getStringList("commands.report.reasons").stream().map(str -> "&f" + str + "&7").collect(Collectors.toList())));
					player.sendMessage(awesomeText.prettifyMessage("&cError: &7You can only report another player for &f" + reasons + "&7.", player));
					return true;
				}
			}

			String message = config.getString("commands.report.report-message");
			message = awesomeText.replacePlaceholder(message, "subject", reportedPlayer.getName());
			message = awesomeText.replacePlaceholder(message, "reason", reason);
			message = awesomeText.prettifyMessage(message, player);

			// Save report to the list of player report
			HashMap<String, Object> report = new HashMap<>();
			report.put("reason", reason);
			report.put("timestamp", System.currentTimeMillis());
			report.put("reporter", player.getUniqueId().toString());

			reportData.addToList(reportedPlayer.getUniqueId().toString(), report);

			// Notify players about the report
			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Report has been sent."));

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (caboodle.hasPermission(p, "alerts.report")) {
					p.sendMessage(message);
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("report") && caboodle.hasPermission(sender, "report")) {
			if (args.length == 1) {
				return caboodle.getOnlinePlayerNames();
			}
			if (args.length == 2) {
				return config.getStringList("commands.report.reasons");
			}

			return Collections.emptyList();
		}

		return null;
	}
}
