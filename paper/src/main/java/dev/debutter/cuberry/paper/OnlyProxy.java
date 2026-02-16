package dev.debutter.cuberry.paper;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.util.List;
import java.util.Random;

public class OnlyProxy implements Listener {

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		FileConfiguration config = PaperCuberry.plugin().getConfig();

		if (!config.getStringList("only-proxy.allowed-proxies").contains(event.getRealAddress().toString())) {
			event.setResult(Result.KICK_OTHER);

			List<String> kickmessages = config.getStringList("only-proxy.kick-messages");
			Random random = new Random();
			String kickmessage = kickmessages.get(random.nextInt(kickmessages.size()));
			event.setKickMessage(kickmessage);
		}
	}

}
