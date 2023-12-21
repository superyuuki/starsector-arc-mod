package arc.hullmod.sheath;

import arc.hullmod.ARCBaseHullmod;
import arc.hullmod.IHullmodPart;
import com.fs.starfarer.api.combat.ShipAPI;

public class VariableSheath extends ARCBaseHullmod {


    @SuppressWarnings("unchecked")
    public VariableSheath() {
        super(new IHullmodPart[]{
                new VariableSheathEffectsPart()
        });
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        super.advanceInCombat(ship, amount);






    }
}
