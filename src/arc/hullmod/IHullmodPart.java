package arc.hullmod;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public interface IHullmodPart<T> {

    void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, T customData);

    default boolean hasData() {
        return true;
    }

    default boolean makesNewData() {
        return true;
    }

    T makeNew();
    String makeKey();

}
