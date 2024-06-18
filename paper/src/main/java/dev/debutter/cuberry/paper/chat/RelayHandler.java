package dev.debutter.cuberry.paper.chat;

import dev.debutter.cuberry.common.chat.ChatPacket;
import dev.debutter.cuberry.common.chat.GlobalChat;
import dev.debutter.cuberry.paper.Paper;
import dev.debutter.cuberry.paper.utils.AwesomeText;
import me.mrnavastar.protoweaver.api.ProtoConnectionHandler;
import me.mrnavastar.protoweaver.api.netty.ProtoConnection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

import java.util.UUID;

public class RelayHandler implements ProtoConnectionHandler {

    private static ProtoConnection proxy;
    private static boolean enabled = false;

    public static void register() {
        GlobalChat.PROTOCOL.setServerHandler(RelayHandler.class).load();
        enabled = true;
    }

    public static void sendMessage(UUID uuid, Component message) {
        if (!enabled) return;

        ChatPacket data = new ChatPacket(uuid, AwesomeText.destylize(message));
        proxy.send(data);
    }

    @Override
    public void onReady(ProtoConnection connection) {
        proxy = connection;

        Paper.plugin().getLogger().info("Established global chat hook with server " + connection.getRemoteAddress());
    }

    @Override
    public void onDisconnect(ProtoConnection connection) {
        Paper.plugin().getLogger().info("The global chat hook with server " + connection.getRemoteAddress() + " has disconnected");
    }

    @Override
    public void handlePacket(ProtoConnection connection, Object packet) {
        if (!(packet instanceof ChatPacket chatPacket)) {
            Paper.plugin().getLogger().info("Received malformed global chat packet with server " + connection.getRemoteAddress());
            return;
        }

//        Paper.plugin().getLogger().info("wow! got: " + chatPacket.message + " from: " + chatPacket.uuid);
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(chatPacket.message));
    }

}
