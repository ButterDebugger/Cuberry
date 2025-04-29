package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.storage.DataStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Report extends CommandWrapper {

	// TODO: make a report history command

	public Report() {
		CommandRegistry reportCmd = new CommandRegistry(this, "report");
		reportCmd.setDescription("Report another player to staff");

		addRegistries(reportCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		FileConfiguration config = Paper.plugin().getConfig();
		DataStorage reportData = Paper.data().getStorage("reports.yml");

		if (label.equalsIgnoreCase("report")) {
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "report")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/report <player> <reason>"));
				return true;
			}
			if (args.length == 1) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.report.no_reason", sender)));
				return true;
			}

			OfflinePlayer reportedPlayer = Caboodle.getOfflinePlayer(args[0]);

			if (!reportedPlayer.hasPlayedBefore()) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_never_joined", sender)));
				return true;
			}

			String reason = String.join(" ", Caboodle.splice(args, 0, 1));

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
					String reasons = AwesomeText.commaOrSeparatedList(
						config.getStringList("commands.report.reasons")
							.stream()
							.map(str -> "<white>" + str + "</white>")
							.collect(Collectors.toList()
						)
					);

					sender.sendMessage(AwesomeText.beautifyMessage(
						Paper.locale().getMessage("commands.report.explicit_reasons", sender),
						Placeholder.parsed("reasons", reasons)
					));
					return true;
				}
			}

			Component message = AwesomeText.beautifyMessage(
				config.getString("commands.report.report-message"),
				Placeholder.unparsed("subject", Objects.requireNonNull(reportedPlayer.getName())),
				Placeholder.unparsed("reporter", sender.getName()),
				Placeholder.unparsed("reason", reason)
			);

			// Save report to the list of player report
			HashMap<String, Object> report = new HashMap<>();
			report.put("reason", reason);
			report.put("timestamp", System.currentTimeMillis());
			report.put("reporter", player.getUniqueId().toString());

			reportData.addToList(reportedPlayer.getUniqueId().toString(), report);

			// Notify players about the report
			sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.report.sent", sender)));

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (Caboodle.hasPermission(p, "alerts.report")) {
					p.sendMessage(message);
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		FileConfiguration config = Paper.plugin().getConfig();

		if (label.equalsIgnoreCase("report") && Caboodle.hasPermission(sender, "report")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}
			if (args.length == 2) {
				return config.getStringList("commands.report.reasons");
			}

			return Collections.emptyList();
		}

		return null;
	}
}
