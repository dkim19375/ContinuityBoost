package me.dkim19375.continuityboost.api.event;

import me.dkim19375.continuityboost.api.Booster;
import org.bukkit.entity.Player;

public class BoostStartEvent extends BoostEvent {
    public BoostStartEvent(Player player, Booster booster) {
        super(player, booster);
    }
}
