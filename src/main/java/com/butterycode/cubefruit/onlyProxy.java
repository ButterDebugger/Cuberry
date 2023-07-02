package com.butterycode.cubefruit;

import java.util.List;
import java.util.Random;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class onlyProxy implements Listener {

	FileConfiguration config = Main.plugin().config();

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (!config.getStringList("only-proxy.allowed-proxies").contains(event.getRealAddress().toString())) {
			event.setResult(Result.KICK_OTHER);

			List<String> kickmessages = config.getStringList("only-proxy.kick-messages");
			Random random = new Random();
			String kickmessage = kickmessages.get(random.nextInt(kickmessages.size()));
			event.setKickMessage(kickmessage);
		}
	}

}
