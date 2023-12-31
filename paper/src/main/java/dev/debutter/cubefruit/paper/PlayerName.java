package dev.debutter.cubefruit.paper;

import dev.debutter.cubefruit.paper.utils.AwesomeText;
import dev.debutter.cubefruit.paper.utils.IdlePlayers;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerName implements Listener {

	public static void start() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			updateName(player);
		}

		Bukkit.getScheduler().scheduleSyncRepeatingTask(Paper.plugin(), new Runnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					updateName(player);
				}
			}
		}, 0, 5);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		updateName(player);
	}

	private static void updateName(Player player) {
		FileConfiguration config = Paper.plugin().config();

		if (!config.getBoolean("player-name.enabled")) return;

		UUID uuid = player.getUniqueId();
		String format = "";

		if (config.getBoolean("player-name.afk") && IdlePlayers.isAFK(uuid)) {
			format = config.getString("player-name.afk-format");
		} else {
			format = config.getString("player-name.format");
		}

		format = AwesomeText.prettifyMessage(format, player);

		if (config.getBoolean("player-name.change-display-name")) {
			player.displayName(AwesomeText.beautifyMessage(format, player));
		}

		if (config.getBoolean("player-name.change-player-list-name")) {
			player.playerListName(AwesomeText.beautifyMessage(format, player));
		}
	}
}
