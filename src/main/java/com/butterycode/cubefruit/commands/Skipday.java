package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
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
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
				return true;
			}

			Player player = (Player)sender;

			if (!caboodle.hasPermission(sender, "skipday")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7The time is now gradually turning to day."));

			new BukkitRunnable() {
				@Override
				public void run() {
					player.getWorld().setTime(player.getWorld().getTime() + 25);
					if (player.getWorld().getTime() <= 50) {
						cancel();
					}
				}
			}.runTaskTimer(Main.plugin(), 1L, 1L);
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!caboodle.hasPermission(sender, "skipday")) return null;

		return Collections.emptyList();
	}
}
