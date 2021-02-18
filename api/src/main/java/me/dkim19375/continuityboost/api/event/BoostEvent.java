package me.dkim19375.continuityboost.api.event;

import me.dkim19375.continuityboost.api.Booster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a boost related event
 * @see BoostStartEvent
 * @see BoostEndEvent
 */
public class BoostEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Booster booster;

    public BoostEvent(Player player, Booster booster) {
        super(false);
        this.player = player;
        this.booster = booster;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

    /**
     * @return the player involved in this event
     */
    @Nullable
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the booster involved in this event
     */
    @NotNull
    public Booster getBooster() {
        return booster;
    }
}
