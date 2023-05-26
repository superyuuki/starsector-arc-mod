package arc.hullmod.sheath;

import cmu.misc.CombatUI;
import cmu.plugins.SubsystemCombatManager;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lwjgl.util.vector.Vector2f;

import static cmu.plugins.SubsystemCombatManager.showInfoText;

public class VariableSheath extends BaseHullMod {

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (ship.isHoldFireOneFrame()) { //if you tap X
            ship.setHoldFire(false);
        }

        Vector2f rootLoc = CombatUI.getSubsystemsRootLocation(ship, 1, 13.0F * Global.getSettings().getScreenScaleMult());

        CombatUI.drawSubsystemsTitle(Global.getCombatEngine().getPlayerShip(), showInfoText, rootLoc);

        CombatUI.drawSubsystemStatus(
                ship,
                1f,
                "system",
                "info",
                "state",
                "hotkey",
                "flavor",
                false,
                1,
                new Vector2f(rootLoc),
                rootLoc
        );
    }
}
