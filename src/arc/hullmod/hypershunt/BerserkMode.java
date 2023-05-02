package arc.hullmod.hypershunt;

import arc.hullmod.HullmodPart;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import java.awt.*;

public class BerserkMode implements HullmodPart<HypershuntData> {

    final IntervalUtil EMP = new IntervalUtil(0.33f, 0.33f);

    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, HypershuntData customData) {
        EMP.advance(timestep);
        if (customData.berserkMode && EMP.intervalElapsed()) {
            engineAPI.spawnEmpArc(shipAPI, shipAPI.getLocation(), shipAPI, shipAPI,
                    DamageType.OTHER,
                    0, // Damage
                    100, // Emp damage
                    40f, // Max range
                    "tachyon_lance_emp_impact", // Impact sound
                    40f, // Width of the arc
                    new Color(255, 100, 255, 255), // Arc color
                    new Color(255, 150, 255, 255)); // Fringe color
        }

        shipAPI.getEngineController().extendFlame(shipAPI, 6f, 0f, 0f);


    }
}
