package arc.hullmod.sheath.ai;

import arc.Index;
import arc.hullmod.ARCData;
import arc.hullmod.IHullmodPart;
import arc.util.ARCUtils;
import cmu.CMUtils;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;

import java.util.List;

public class VariableSheathAIPart implements IHullmodPart<ARCData> {
    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, ARCData customData) {

        float dangerFactor = 0;
        boolean dangerSwitch = false;

        List<ShipAPI> nearbyEnemies = AIUtils.getNearbyEnemies(shipAPI, 2000f);
        for (ShipAPI enemy : nearbyEnemies) {
            switch (enemy.getHullSize()) {
                case CAPITAL_SHIP:
                    dangerFactor+= Math.max(
                            0, 3 - MathUtils.getDistanceSquared(
                                    enemy.getLocation(), shipAPI.getLocation()
                            ) / 1200 * 1200
                    );
                    break;
                case CRUISER:
                    dangerFactor+= Math.max(0,2.25f-(MathUtils.getDistanceSquared(enemy.getLocation(), shipAPI.getLocation()) / 1000 * 1000));
                    break;
                case DESTROYER:
                    dangerFactor+= Math.max(0,1.5f-(MathUtils.getDistanceSquared(enemy.getLocation(), shipAPI.getLocation()) / 1000 * 1000));
                    break;
                case FRIGATE:
                    dangerFactor+= Math.max(0,1f-(MathUtils.getDistanceSquared(enemy.getLocation(), shipAPI.getLocation()) / 800 * 800));
                    break;
                default:
                    dangerFactor+= Math.max(0,0.5f-(MathUtils.getDistanceSquared(enemy.getLocation(), shipAPI.getLocation()) / 800 * 800));
                    break;
            }
        }

        dangerSwitch = dangerFactor < ARCUtils.decideBasedOnHullSize(
                shipAPI,
                0.7f,
                1.5f,
                2.5f,
                4f
        );




    }

    @Override
    public boolean hasData() {
        return true;
    }

    @Override
    public boolean makesNewData() {
        return true;
    }

    @Override
    public ARCData makeNew() {
        return new ARCData();
    }

    @Override
    public String makeKey() {
        return Index.ARC_DATA;
    }
}
