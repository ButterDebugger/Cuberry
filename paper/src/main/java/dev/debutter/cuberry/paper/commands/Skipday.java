package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
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
			if (!(sender instanceof Player player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
				return true;
			}

            if (!Caboodle.hasPermission(sender, "skipday")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.skipday.success", sender)));

			new BukkitRunnable() {
				@Override
				public void run() {
					player.getWorld().setTime(player.getWorld().getTime() + 25);
					if (player.getWorld().getTime() <= 50) {
						cancel();
					}
				}
			}.runTaskTimer(PaperCuberry.plugin(), 1L, 1L);
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
