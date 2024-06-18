package dev.debutter.cuberry.common.chat;

import dev.debutter.cuberry.common.Constants;
import me.mrnavastar.protoweaver.api.protocol.CompressionType;
import me.mrnavastar.protoweaver.api.protocol.Protocol;

public class GlobalChat {

    public static final Protocol.Builder PROTOCOL = Protocol.create(Constants.PROJECT_ID, "global_chat")
            .setCompression(CompressionType.GZIP)
            .setMaxConnections(15) // TODO: change this to unlimited
            .addPacket(ChatPacket.class);

}
