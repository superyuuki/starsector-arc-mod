package arc.hullmod;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class ARCBaseHullmod extends BaseHullMod {

    final IHullmodPart<Object>[] hullmodParts;

    protected ARCBaseHullmod(IHullmodPart<Object>[] hullmodParts) {
        this.hullmodParts = hullmodParts;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {

        if (!ship.isAlive()) return;
        if (ship.isPiece()) return;
        if(ship.getFluxTracker().isOverloaded()) return;

        CombatEngineAPI engine = Global.getCombatEngine();

        for (IHullmodPart<Object> part : hullmodParts) {

            String key = part.makeKey();

            Object possibleData = ship.getCustomData().get(key);
            if (possibleData == null) {
                possibleData = part.makeNew();
            }

            part.advanceSafely(engine, ship, amount, possibleData);

            ship.setCustomData(key, possibleData);
        }
    }
}
