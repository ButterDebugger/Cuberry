package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.utils.AwesomeText;
import com.butterycode.cubefruit.utils.Caboodle;
import com.butterycode.cubefruit.utils.DogTags;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Gamemode extends CommandWrapper {

	public Gamemode() {
		CommandRegistry gmCmd = new CommandRegistry(this, "gm");
		gmCmd.setDescription("Change a players gamemodes");

		CommandRegistry gmaCmd = new CommandRegistry(this, "gma");
		gmaCmd.setDescription("Switch a players gamemode to adventure mode");

		CommandRegistry gmcCmd = new CommandRegistry(this, "gmc");
		gmcCmd.setDescription("Switch a players gamemode to creative mode");

		CommandRegistry gmspCmd = new CommandRegistry(this, "gmsp");
		gmspCmd.setDescription("Switch a players gamemode to spectator mode");

		CommandRegistry gmsCmd = new CommandRegistry(this, "gms");
		gmsCmd.setDescription("Switch a players gamemode to survival mode");

		addRegistries(gmCmd, gmaCmd, gmcCmd, gmspCmd, gmsCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("gm")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.prettifyMessage("&3Usage: &7/gm <adventure|creative|spectator|survival> [<username>]"));
				return true;
			}

			Player player;
			boolean themself = true;

			if (args.length > 1) {
				if (DogTags.isOnline(args[1])) {
					player = Bukkit.getPlayer(args[1]);
					themself = false;
				} else {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7That player could not be found."));
					return true;
				}
			} else {
				if (!(sender instanceof Player)) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
					return true;
				}

				player = (Player) sender;
			}

			if (args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2")) {
				player.setGameMode(GameMode.ADVENTURE);

				if (themself) {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set own game mode to &fAdventure Mode&7."));
				} else {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set &f" + player.getName() + "&7's game mode to &fAdventure Mode&7."));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1")) {
				player.setGameMode(GameMode.CREATIVE);

				if (themself) {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set own game mode to &fCreative Mode&7."));
				} else {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set &f" + player.getName() + "&7's game mode to &fCreative Mode&7."));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("3")) {
				player.setGameMode(GameMode.SPECTATOR);

				if (themself) {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set own game mode to &fSpectator Mode&7."));
				} else {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set &f" + player.getName() + "&7's game mode to &fSpectator Mode&7."));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0")) {
				player.setGameMode(GameMode.SURVIVAL);

				if (themself) {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set own game mode to &fSurvival Mode&7."));
				} else {
					sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set &f" + player.getName() + "&7's game mode to &fSurvival Mode&7."));
				}
				return true;
			} else {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7Invalid arguments."));
				return true;
			}
		}

		if (label.equalsIgnoreCase("gma")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
					return true;
				}

				Player player = (Player) sender;

				player.setGameMode(GameMode.ADVENTURE);
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set own game mode to &fAdventure Mode&7."));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				player.setGameMode(GameMode.ADVENTURE);
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set &f" + player.getName() + "&7's game mode to &fAdventure Mode&7."));
				return true;
			} else {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}

		if (label.equalsIgnoreCase("gmc")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
					return true;
				}

				Player player = (Player) sender;

				player.setGameMode(GameMode.CREATIVE);
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set own game mode to &fCreative Mode&7."));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				player.setGameMode(GameMode.CREATIVE);
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set &f" + player.getName() + "&7's game mode to &fCreative Mode&7."));
				return true;
			} else {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}

		if (label.equalsIgnoreCase("gmsp")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
					return true;
				}

				Player player = (Player) sender;

				player.setGameMode(GameMode.SPECTATOR);
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set own game mode to &fSpectator Mode&7."));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				player.setGameMode(GameMode.SPECTATOR);
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set &f" + player.getName() + "&7's game mode to &fSpectator Mode&7."));
				return true;
			} else {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}

		if (label.equalsIgnoreCase("gms")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7You must be a player to use this."));
					return true;
				}

				Player player = (Player) sender;

				player.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set own game mode to &fSurvival Mode&7."));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				player.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage(AwesomeText.prettifyMessage("&a&l» &7Set &f" + player.getName() + "&7's game mode to &fSurvival Mode&7."));
				return true;
			} else {
				sender.sendMessage(AwesomeText.prettifyMessage("&cError: &7That player could not be found."));
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (!Caboodle.hasPermission(sender, "gamemode")) return null;

		if (label.equalsIgnoreCase("gm")) {
			if (args.length == 1) {
				return Arrays.asList("adventure", "creative", "spectator", "survival");
			}
			if (args.length == 2) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		if (label.equalsIgnoreCase("gma") || label.equalsIgnoreCase("gmc") || label.equalsIgnoreCase("gmsp") || label.equalsIgnoreCase("gms")) {
			if (args.length == 1) {
				return Caboodle.getOnlinePlayerNames();
			}

			return Collections.emptyList();
		}

		return null;
	}
}
