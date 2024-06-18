package dev.debutter.cuberry.common.chat;

import java.util.UUID;

public class ChatPacket {

    public UUID uuid;
    public String message;

    public ChatPacket(UUID playerUuid, String serializedMessage) {
        this.uuid = playerUuid;
        this.message = serializedMessage;
    }

}
