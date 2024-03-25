package dev.debutter.cubefruit.paper.commands;

import dev.debutter.cubefruit.paper.Paper;
import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
import org.bukkit.Bukkit;
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

	private static boolean isChatMuted = false;

	public Mutechat() {
		CommandRegistry mutechatCmd = new CommandRegistry(this, "mutechat");
		mutechatCmd.setDescription("Mute the chat");

		addRegistries(mutechatCmd);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("mutechat")) {
			if (!Caboodle.hasPermission(sender, "mutechat")) {
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.missing_permission", sender)));
				return true;
			}

			if (args.length == 0) {
				isChatMuted = !isChatMuted;

				if (isChatMuted) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.mutechat.mute_chat", sender)));
				} else {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.mutechat.unmute_chat", sender)));
				}

				makeAnnouncement();
				return true;
			}

			if (args[0].equalsIgnoreCase("on")) {
				if (isChatMuted) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.mutechat.already_muted", sender)));
					return true;
				}

				isChatMuted = true;
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.mutechat.mute_chat", sender)));
				makeAnnouncement();
			} else {
				if (!isChatMuted) {
					sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.mutechat.already_unmuted", sender)));
					return true;
				}

				isChatMuted = false;
				sender.sendMessage(AwesomeText.beautifyMessage(Paper.locale().getMessage("commands.mutechat.unmute_chat", sender)));
				makeAnnouncement();
			}
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("mutechat") && Caboodle.hasPermission(sender, "mutechat")) {
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
		FileConfiguration config = Paper.plugin().getConfig();
		boolean sendBlockMessage = config.getBoolean("commands.mutechat.send-block-message");

		if (isChatMuted && !Caboodle.hasPermission(player, "mutechat.bypass")) {
			event.setCancelled(true);

			if (sendBlockMessage) {
				String message = Paper.locale().getMessage("commands.mutechat.block_message", player);

				player.sendMessage(AwesomeText.prettifyMessage(message, player));
			}
		}
	}

	private void makeAnnouncement() {
		FileConfiguration config = Paper.plugin().getConfig();
		boolean sendAnnouncement = config.getBoolean("commands.mutechat.send-announcement");

		if (!sendAnnouncement) return;

		for (Player player : Bukkit.getOnlinePlayers()) {
			String message;

			if (isChatMuted) {
				message = Paper.locale().getMessage("commands.mutechat.mute_alert", player);
			} else {
				message = Paper.locale().getMessage("commands.mutechat.unmute_alert", player);
			}

			player.sendMessage(AwesomeText.prettifyMessage(message));
		}
	}
}
