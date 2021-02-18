package me.dkim19375.continuityboost.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerToggleEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final boolean toggled;

    public PlayerToggleEvent(@NotNull Player player, boolean toggled) {
        super(player);
        this.toggled = toggled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * @return true if the player is currently toggled
     */
    public boolean isToggled() {
        return toggled;
    }
}
