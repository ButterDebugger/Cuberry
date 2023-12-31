package dev.debutter.cubefruit.paper.commands;

import dev.debutter.cubefruit.paper.Paper;
import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class Skipday extends CommandWrapper {

	public Skipday() {
		CommandRegistry skipdayCmd = new CommandRegistry(this, "skipday");
		skipdayCmd.setDescription("Gradually turn the time to day");

		addRegistries(skipdayCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("skipday")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player)sender;

			if (!Caboodle.hasPermission(sender, "skipday")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7The time is now gradually turning to day."));

			new BukkitRunnable() {
				@Override
				public void run() {
					player.getWorld().setTime(player.getWorld().getTime() + 25);
					if (player.getWorld().getTime() <= 50) {
						cancel();
					}
				}
			}.runTaskTimer(Paper.plugin(), 1L, 1L);
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!Caboodle.hasPermission(sender, "skipday")) return null;

		return Collections.emptyList();
	}
}
