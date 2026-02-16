package dev.debutter.cuberry.velocity.chat;

import dev.debutter.cuberry.common.chat.GlobalChat;
import dev.debutter.cuberry.velocity.VelocityCuberry;
import me.mrnavastar.protoweaver.api.ProtoConnectionHandler;
import me.mrnavastar.protoweaver.api.netty.ProtoConnection;

import java.util.HashSet;

public class BroadcastHandler implements ProtoConnectionHandler {

    private static HashSet<ProtoConnection> hooks = new HashSet<>();

    public static void register() {
        GlobalChat.PROTOCOL.setClientHandler(BroadcastHandler.class).load();
    }

    @Override
    public void onReady(ProtoConnection connection) {
        VelocityCuberry.getPlugin().getLogger().info("Established global chat hook with server {}", connection.getRemoteAddress());
        hooks.add(connection);
    }

    @Override
    public void onDisconnect(ProtoConnection connection) {
        hooks.remove(connection);
        VelocityCuberry.getPlugin().getLogger().info("The global chat hook with server {} has disconnected", connection.getRemoteAddress());
    }

    @Override
    public void handlePacket(ProtoConnection connection, Object packet) {
        for (ProtoConnection hook : hooks) {
            if (hook.equals(connection)) continue;

            hook.send(packet);
        }
    }

}
