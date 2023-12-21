package arc.hullmod;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public interface IHullmodPart<T> {

    void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, T customData);

    boolean hasData();

    boolean makesNewData();

    T makeNew();
    String makeKey();

}
