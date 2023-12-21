package arc.hullmod.sheath;

import arc.hullmod.ARCBaseHullmod;
import arc.hullmod.IHullmodPart;
import cmu.misc.CombatUI;
import cmu.plugins.SubsystemCombatManager;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import static cmu.plugins.SubsystemCombatManager.showInfoText;

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
