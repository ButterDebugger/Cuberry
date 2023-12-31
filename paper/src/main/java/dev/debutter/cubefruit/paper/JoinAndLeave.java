package dev.debutter.cubefruit.paper;

import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.Caboodle;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class JoinAndLeave implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = Paper.plugin().config();

		if (config.getBoolean("join-leave-message.enabled")) {
			if (!player.hasPlayedBefore()) {
				List<String> messages = config.getStringList("join-leave-message.newbie");
				
				if (messages.size() > 0) {
					String message = AwesomeText.prettifyMessage(Caboodle.randomListItem(messages), player);
					event.setJoinMessage(message);
				}
			} else {
				List<String> messages = config.getStringList("join-leave-message.join");
				
				if (messages.size() > 0) {
					String message = AwesomeText.prettifyMessage(Caboodle.randomListItem(messages), player);
					event.setJoinMessage(message);
				}
			}
		}

		if (config.getBoolean("join-motd.enabled")) {
			for (String msg : config.getStringList("join-motd.message")) {
				player.sendMessage(AwesomeText.prettifyMessage(msg, player));
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = Paper.plugin().config();

		if (config.getBoolean("join-leave-message.enabled")) {
			List<String> messages = config.getStringList("join-leave-message.leave");
			
			if (messages.size() > 0) {
				String message = AwesomeText.prettifyMessage(Caboodle.randomListItem(messages), player);
				event.setQuitMessage(message);
			}
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		Result result = event.getResult();
		FileConfiguration config = Paper.plugin().config();

		if (config.getBoolean("join-leave-message.enabled") && config.getBoolean("join-leave-message.join-attempt.enabled")) {
			if (result.equals(Result.KICK_BANNED)) {
				List<String> messages = config.getStringList("join-leave-message.join-attempt.banned");
				
				if (messages.size() > 0) {
					String message = AwesomeText.prettifyMessage(Caboodle.randomListItem(messages), player);
					
					if (config.getBoolean("join-leave-message.join-attempt.visible-to-everyone")) {
						Caboodle.broadcast(message);
					} else {
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (Caboodle.hasPermission(p, "alerts.joinattempt")) {
								p.sendMessage(message);
							}
						}
					}
				}
			} else if (result.equals(Result.KICK_WHITELIST)) {
				List<String> messages = config.getStringList("join-leave-message.join-attempt.whitelist");
				
				if (messages.size() > 0) {
					String message = AwesomeText.prettifyMessage(Caboodle.randomListItem(messages), player);
					
					if (config.getBoolean("join-leave-message.join-attempt.visible-to-everyone")) {
						Caboodle.broadcast(message);
					} else {
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (Caboodle.hasPermission(p, "alerts.joinattempt")) {
								p.sendMessage(message);
							}
						}
					}
				}
			}
		}
	}
}
