package dev.debutter.cuberry.paper.commands;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.commands.builder.CommandRegistry;
import dev.debutter.cuberry.paper.commands.builder.CommandWrapper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import dev.debutter.cuberry.paper.utils.DogTags;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage(AwesomeText.beautifyMessage("<dark_aqua>Usage: <gray>/gm <adventure|creative|spectator|survival> [<username>]"));
				return true;
			}

			Player player;
			boolean themself = true;

			if (args.length > 1) {
				if (DogTags.isOnline(args[1])) {
					player = Bukkit.getPlayer(args[1]);
					themself = false;
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
					return true;
				}
			} else {
				if (!(sender instanceof Player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
					return true;
				}

				player = (Player) sender;
			}

			assert player != null;

			if (args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2")) {
                player.setGameMode(GameMode.ADVENTURE);

				if (themself) {
					sender.sendMessage(beautifyGamemode(sender, GameMode.ADVENTURE));
				} else {
					sender.sendMessage(beautifyGamemode(sender, GameMode.ADVENTURE, player));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1")) {
				player.setGameMode(GameMode.CREATIVE);

				if (themself) {
					sender.sendMessage(beautifyGamemode(sender, GameMode.CREATIVE));
				} else {
					sender.sendMessage(beautifyGamemode(sender, GameMode.CREATIVE, player));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("spectator") || args[0].equalsIgnoreCase("sp") || args[0].equalsIgnoreCase("3")) {
				player.setGameMode(GameMode.SPECTATOR);

				if (themself) {
					sender.sendMessage(beautifyGamemode(sender, GameMode.SPECTATOR));
				} else {
					sender.sendMessage(beautifyGamemode(sender, GameMode.SPECTATOR, player));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0")) {
				player.setGameMode(GameMode.SURVIVAL);

				if (themself) {
					sender.sendMessage(beautifyGamemode(sender, GameMode.SURVIVAL));
				} else {
					sender.sendMessage(beautifyGamemode(sender, GameMode.SURVIVAL, player));
				}
				return true;
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.invalid_arguments", sender)));
				return true;
			}
		}

		if (label.equalsIgnoreCase("gma")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
					return true;
				}

                player.setGameMode(GameMode.ADVENTURE);
				sender.sendMessage(beautifyGamemode(sender, GameMode.ADVENTURE));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				player.setGameMode(GameMode.ADVENTURE);
				sender.sendMessage(beautifyGamemode(sender, GameMode.ADVENTURE, player));
				return true;
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
				return true;
			}
		}

		if (label.equalsIgnoreCase("gmc")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
					return true;
				}

                player.setGameMode(GameMode.CREATIVE);
				sender.sendMessage(beautifyGamemode(sender, GameMode.CREATIVE));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				player.setGameMode(GameMode.CREATIVE);
				sender.sendMessage(beautifyGamemode(sender, GameMode.CREATIVE, player));
				return true;
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
				return true;
			}
		}

		if (label.equalsIgnoreCase("gmsp")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
					return true;
				}

                player.setGameMode(GameMode.SPECTATOR);
				sender.sendMessage(beautifyGamemode(sender, GameMode.SPECTATOR));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				player.setGameMode(GameMode.SPECTATOR);
				sender.sendMessage(beautifyGamemode(sender, GameMode.SPECTATOR, player));
				return true;
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
				return true;
			}
		}

		if (label.equalsIgnoreCase("gms")) {
			if (!Caboodle.hasPermission(sender, "gamemode")) {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				if (!(sender instanceof Player player)) {
					sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_required", sender)));
					return true;
				}

                player.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage(beautifyGamemode(sender, GameMode.SURVIVAL));
				return true;
			}

			if (DogTags.isOnline(args[0])) {
				Player player = Bukkit.getPlayer(args[0]);

				player.setGameMode(GameMode.SURVIVAL);
				sender.sendMessage(beautifyGamemode(sender, GameMode.SURVIVAL, player));
				return true;
			} else {
				sender.sendMessage(AwesomeText.beautifyMessage(PaperCuberry.locale().getMessage("commands.player_not_online", sender)));
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

	/** @return A beautified set gamemode message for the player */
	public static Component beautifyGamemode(CommandSender sender, GameMode gameMode) {
		return AwesomeText.beautifyMessage(
			PaperCuberry.locale().getMessage("commands.gamemode.set_self", sender),
			Placeholder.component("gamemode", AwesomeText.createTranslatableComponent(gameMode.translationKey()))
		);
	}

	/** @return A beautified set gamemode message for another player */
	public static Component beautifyGamemode(CommandSender sender, GameMode gameMode, Player player) {
		return AwesomeText.beautifyMessage(
			PaperCuberry.locale().getMessage("commands.gamemode.set_other", sender),
			Placeholder.unparsed("player_name", player.getName()),
			Placeholder.component("gamemode", AwesomeText.createTranslatableComponent(gameMode.translationKey()))
		);
	}
}
