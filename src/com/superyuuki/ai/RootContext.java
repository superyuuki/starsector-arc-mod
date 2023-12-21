package com.superyuuki.ai;

import com.fs.starfarer.api.combat.ShipAPI;

public class RootContext {

    public final ShipAPI ship;

    public RootContext(ShipAPI ship) {
        this.ship = ship;
    }
}
