package arc.hullmod;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public interface HullmodPart<T> {

    void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, T customData);

}
