package me.dkim19375.continuityboost.api.event;

import me.dkim19375.continuityboost.api.Booster;

public class BoostEndEvent extends BoostEvent {
    public BoostEndEvent(Booster booster) {
        super(null, booster);
    }
}
