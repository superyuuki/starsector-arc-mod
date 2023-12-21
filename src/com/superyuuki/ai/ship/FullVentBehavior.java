package com.superyuuki.ai.ship;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.superyuuki.ai.IBehavior;
import com.superyuuki.ai.RootContext;
import com.superyuuki.ai.Status;

public class FullVentBehavior implements IBehavior {

    final RootContext ctx;

    public FullVentBehavior(RootContext ctx) {
        this.ctx = ctx;
    }

    enum InternalState {
        NOT_STARTED,
        STARTED
    }

    InternalState state = InternalState.NOT_STARTED;

    @Override
    public Status run() {
        ShipAPI ship = ctx.ship;

        if (state == InternalState.NOT_STARTED) {
            ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
        }

        if (state == InternalState.NOT_STARTED && ship.getFluxTracker().isVenting()) {
            //started venting
            state = InternalState.STARTED;
        }

        if (state == InternalState.STARTED && !ship.getFluxTracker().isVenting()) {
            //stopped venting
            return Status.SUCCESS;
        }



        return Status.CONTINUE;
    }

    @Override
    public String report() {
        return "";
    }
}
