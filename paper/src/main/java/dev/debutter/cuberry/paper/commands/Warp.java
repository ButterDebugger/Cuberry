package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.storage.DataStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Warp extends CommandWrapper {

	public Warp() {
		CommandRegistry warpCmd = new CommandRegistry(this, "warp");
		warpCmd.setDescription("Warp to the specified location");

		CommandRegistry delwarpCmd = new CommandRegistry(this, "delwarp");
		delwarpCmd.setDescription("Delete the specified warp");

		CommandRegistry setwarpCmd = new CommandRegistry(this, "setwarp");
		setwarpCmd.setDescription("Create a new warp");

		CommandRegistry listwarpsCmd = new CommandRegistry(this, "listwarps");
		listwarpsCmd.setDescription("List all warps");

		addRegistries(warpCmd, delwarpCmd, setwarpCmd, listwarpsCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		DataStorage warps = Paper.data().getStorage("warps.yml");

		if (label.equalsIgnoreCase("warp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player)sender;

			if (!Caboodle.hasPermission(sender, "warp")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/warp <name>"));
				return true;
			}

			String name = args[0];

			if (!warps.exists(name)) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7Warp does not exist."));
				return true;
			}

			String worldName = warps.getString(name + ".world");
			double x = warps.getDouble(name + ".x");
			double y = warps.getDouble(name + ".y");
			double z = warps.getDouble(name + ".z");
			float yaw = (float) warps.getDouble(name + ".yaw");
			float pitch = (float) warps.getDouble(name + ".pitch");

			World world = Bukkit.getWorld(worldName);

			if (world == null) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7An error has occurred with this warps world."));
				return true;
			}

			Location loc = new Location(world, x, y, z, yaw, pitch);

			player.teleport(loc);
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 1f);

			sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7You have been warped to " + name + "."));
			return true;
		} else if (label.equalsIgnoreCase("delwarp")) {
			if (!Caboodle.hasPermission(sender, "delwarp")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/delwarp <name>"));
				return true;
			}

			String name = args[0];

			if (!warps.exists(name)) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7Warp does not exist."));
				return true;
			}

			warps.remove(name);

			sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Warp has been deleted."));
			return true;
		} else if (label.equalsIgnoreCase("setwarp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.player_required", sender)));
				return true;
			}

			Player player = (Player) sender;

			if (!Caboodle.hasPermission(sender, "setwarp")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/setwarp <name>"));
				return true;
			}

			String name = args[0];
			Location loc = player.getLocation();

			warps.set(name + ".world", loc.getWorld().getName());
			warps.set(name + ".x", loc.getX());
			warps.set(name + ".y", loc.getY());
			warps.set(name + ".z", loc.getZ());
			warps.set(name + ".yaw", loc.getYaw());
			warps.set(name + ".pitch", loc.getPitch());

			sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Warp has been set."));
			return true;
		} else if (label.equalsIgnoreCase("listwarps")) {
			if (!Caboodle.hasPermission(sender, "listwarps")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			List<String> warpsList = warps.getKeys();

			if (warpsList.size() == 0) {
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7There are no warps."));
			} else {
				String warpsMsg = "&a&l» &7Warps:";

				for (String warpKey : warpsList) {
					warpsMsg += "\n&7- &f" + warpKey;
				}

				sender.sendMessage(AwesomeText.prettifyMessage(warpsMsg));
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		DataStorage warps = Paper.data().getStorage("warps.yml");

		if (command.getName().equalsIgnoreCase("warp") && Caboodle.hasPermission(sender, "warp")) {
			if (args.length == 1) {
				return warps.getKeys();
			}

			return Collections.emptyList();
		}
		if (command.getName().equalsIgnoreCase("setwarp") && Caboodle.hasPermission(sender, "setwarp")) {
			return Collections.emptyList();
		}
		if (command.getName().equalsIgnoreCase("delwarp") && Caboodle.hasPermission(sender, "delwarp")) {
			if (args.length == 1) {
				return warps.getKeys();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
