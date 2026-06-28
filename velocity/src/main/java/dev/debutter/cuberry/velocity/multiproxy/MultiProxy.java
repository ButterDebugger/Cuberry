package dev.debutter.cuberry.velocity.multiproxy;

import dev.debutter.cuberry.common.Constants;
import dev.debutter.cuberry.velocity.VelocityCuberry;
import me.mrnavastar.protoweaver.api.ProtoConnectionHandler;
import me.mrnavastar.protoweaver.api.ProtoWeaver;
import me.mrnavastar.protoweaver.api.netty.ProtoConnection;
import me.mrnavastar.protoweaver.api.protocol.CompressionType;
import me.mrnavastar.protoweaver.api.protocol.Protocol;
import me.mrnavastar.protoweaver.client.ProtoClient;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MultiProxy implements ProtoConnectionHandler {

    private static HashSet<ProtoConnection> hooks = new HashSet<>();
    private static final int RECONNECT_DELAY_SECONDS = 5;

    public static void register() {
        Protocol protocol = Protocol.create(Constants.PLUGIN_ID, "proxy_bridge")
            .setCompression(CompressionType.SNAPPY)
            .setMaxConnections(15)
            .setClientHandler(MultiProxy.class)
            .setServerHandler(MultiProxy.class)
            .addPacket(String.class)
            .build();

        ProtoWeaver.load(protocol);

        // Get all the proxy addresses
        List<String> proxyAddresses = VelocityCuberry.getPlugin().getConfig().getStringList("multi-proxy.addresses");

        for (String proxyAddress : proxyAddresses) {
            // Get the host and port
            int colonIndex = proxyAddress.lastIndexOf(":");

            String host = proxyAddress.substring(0, colonIndex);
            int port = Integer.parseInt(proxyAddress.substring(colonIndex + 1));

            // Connect to the other proxy
            connect(protocol, host, port);
        }
    }

    private static void connect(@NotNull Protocol protocol, @NotNull String host, int port) {
        VelocityCuberry.getPlugin().getLogger().info("Connecting to {}:{} ...", host, port);

        ProtoClient proxyClient = new ProtoClient(host, port);

        proxyClient.onConnectionEstablished(event -> {
            VelocityCuberry.getPlugin().getLogger().info("Connected to {}:{}", host, port);
        });
        proxyClient.onConnectionLost(event -> {
            VelocityCuberry.getPlugin().getLogger().info("Lost connection to {}:{} because of {}", host, port, event.getDisconnecter());

            VelocityCuberry.getPlugin().getProxy().getScheduler()
                .buildTask(VelocityCuberry.getPlugin(), () -> {
                    VelocityCuberry.getPlugin().getLogger().info("Attempting to reconnect to {}:{}", host, port);

                    connect(protocol, host, port);
                })
                .delay(RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS)
                .schedule();
        });

        proxyClient.connect(protocol);
    }

    @Override
    public void onReady(ProtoConnection connection) {
        VelocityCuberry.getPlugin().getLogger().info("Established multi proxy hook with server {}", connection.getRemoteAddress());
        hooks.add(connection);
    }

    @Override
    public void onDisconnect(ProtoConnection connection) {
        hooks.remove(connection);
        VelocityCuberry.getPlugin().getLogger().info("The multi proxy hook with server {} has disconnected", connection.getRemoteAddress());
    }

    @Override
    public void handlePacket(ProtoConnection connection, Object packet) {
        VelocityCuberry.getPlugin().getLogger().info("Received packet {}", packet);
    }

}
