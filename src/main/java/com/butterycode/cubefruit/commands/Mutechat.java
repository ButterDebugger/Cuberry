package com.butterycode.cubefruit.commands;

import com.butterycode.cubefruit.Main;
import com.butterycode.cubefruit.utils.awesomeText;
import com.butterycode.cubefruit.utils.caboodle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Mutechat extends CommandWrapper implements Listener {

	FileConfiguration config = Main.plugin.config();

	private static boolean isChatMuted = false;

	public Mutechat() {
		CommandRegistry mutechatCmd = new CommandRegistry(this, "mutechat");
		mutechatCmd.setDescription("Mute the chat");

		addRegistries(mutechatCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("mutechat")) {
			if (!caboodle.hasPermission(sender, "mutechat")) {
				sender.sendMessage(awesomeText.prettifyMessage("&cError: &7You do not have the permission to use this."));
				return true;
			}

			if (args.length == 0) {
				isChatMuted = !isChatMuted;

				if (isChatMuted) {
					sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Chat has been muted."));
				} else {
					sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Chat has been unmuted."));
				}

				makeAnnouncement();
				return true;
			}

			if (args[0].equalsIgnoreCase("on")) {
				if (isChatMuted) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Chat is already muted."));
					return true;
				}

				isChatMuted = true;
				sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Chat has been muted."));
				makeAnnouncement();
			} else {
				if (!isChatMuted) {
					sender.sendMessage(awesomeText.prettifyMessage("&cError: &7Chat is already unmuted."));
					return true;
				}

				isChatMuted = false;
				sender.sendMessage(awesomeText.prettifyMessage("&a&l» &7Chat has been unmuted."));
				makeAnnouncement();
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("mutechat") && caboodle.hasPermission(sender, "mutechat")) {
			if (args.length == 1) {
				return Arrays.asList("on", "off");
			}

			return Collections.emptyList();
		}

		return null;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		String blockMessage = config.getString("commands.mutechat.block-message");

		if (isChatMuted && !caboodle.hasPermission(player, "mutechat.bypass")) {
			event.setCancelled(true);

			if (blockMessage.length() > 0) {
				player.sendMessage(awesomeText.prettifyMessage(blockMessage, player));
			}
		}
	}

	private void makeAnnouncement() {
		String muteAnnouncement = config.getString("commands.mutechat.mute-announcement");
		String unmuteAnnouncement = config.getString("commands.mutechat.unmute-announcement");

		if (isChatMuted) {
			if (muteAnnouncement.length() > 0) {
				caboodle.broadcast(awesomeText.prettifyMessage(muteAnnouncement));
			}
		} else {
			if (unmuteAnnouncement.length() > 0) {
				caboodle.broadcast(awesomeText.prettifyMessage(config.getString("commands.mutechat.unmute-announcement")));
			}
		}
	}
}
