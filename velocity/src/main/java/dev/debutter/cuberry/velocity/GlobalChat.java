package dev.debutter.cuberry.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class GlobalChat {

    @Subscribe
    public void onChat(PlayerChatEvent event) {
//        for (RegisteredServer server : Velocity.getPlugin().getProxy().getAllServers()) {
//            MiniMessage mm = MiniMessage.miniMessage();
//            server.sendMessage(mm.deserialize("<green>" + event.getMessage()));
//        }
    }

}
