package dev.debutter.cuberry.paper.communication;

import dev.debutter.cuberry.paper.PaperCuberry;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import dev.debutter.cuberry.paper.utils.Caboodle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Objects;

public class JoinAndLeave implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = PaperCuberry.plugin().getConfig();

		if (player.hasPlayedBefore()) {
			if (config.getBoolean("system-messages.join.enabled")) {
				// Send normal join message
				List<String> messages = config.getStringList("system-messages.join.messages");

				if (!messages.isEmpty()) {
					event.joinMessage(AwesomeText.beautifyMessage(
						Caboodle.randomListItem(messages), player,
						Placeholder.unparsed("player_name", Objects.requireNonNull(player.getName()))
					));
				}
			}
		} else {
			if (config.getBoolean("system-messages.first-join.enabled")) {
				// Send first join message
				List<String> messages = config.getStringList("system-messages.first-join.messages");

				if (!messages.isEmpty()) {
					event.joinMessage(AwesomeText.beautifyMessage(
						Caboodle.randomListItem(messages), player,
						Placeholder.unparsed("player_name", Objects.requireNonNull(player.getName()))
					));
				}
			}
		}

		// Send join motd
		if (config.getBoolean("system-messages.join-motd.enabled")) {
			player.sendMessage(AwesomeText.beautifyMessage(
				config.getString("system-messages.join-motd.message"), player,
				Placeholder.unparsed("player_name", Objects.requireNonNull(player.getName()))
			));
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = PaperCuberry.plugin().getConfig();

		if (!config.getBoolean("system-messages.leave.enabled")) return;

		List<String> messages = config.getStringList("system-messages.leave.messages");

		if (!messages.isEmpty()) {
			event.quitMessage(AwesomeText.beautifyMessage(
				Caboodle.randomListItem(messages), player,
				Placeholder.unparsed("player_name", Objects.requireNonNull(player.getName()))
			));
		}
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		FileConfiguration config = PaperCuberry.plugin().getConfig();

		switch (event.getResult()) {
			case KICK_BANNED -> {
				if (config.getBoolean("system-messages.failed-join-attempt.banned.enabled")) {
					List<String> messages = config.getStringList("system-messages.failed-join-attempt.banned.messages");

					if (!messages.isEmpty()) {
						Component message = AwesomeText.beautifyMessage(
							Caboodle.randomListItem(messages), player,
							Placeholder.unparsed("player_name", Objects.requireNonNull(player.getName()))
						);

						if (config.getBoolean("system-messages.failed-join-attempt.visible-to-everyone")) {
							Bukkit.getServer().broadcast(message);
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
			case KICK_WHITELIST -> {
				if (config.getBoolean("system-messages.failed-join-attempt.whitelist.enabled")) {
					List<String> messages = config.getStringList("system-messages.failed-join-attempt.whitelist.messages");

					if (!messages.isEmpty()) {
						Component message = AwesomeText.beautifyMessage(
							Caboodle.randomListItem(messages), player,
							Placeholder.unparsed("player_name", Objects.requireNonNull(player.getName()))
						);

						if (config.getBoolean("system-messages.failed-join-attempt.visible-to-everyone")) {
							Bukkit.getServer().broadcast(message);
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
}
