package dev.debutter.cuberry.paper.idle;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class PlayerIdleEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerIdleEvent(@NotNull final Player player) {
        super(player);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NonNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
