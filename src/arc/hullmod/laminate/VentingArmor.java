package arc.hullmod.laminate;

import arc.ARCUtils;
import arc.hullmod.ArchotechLaminate;
import arc.hullmod.HullmodPart;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import java.awt.*;

import static arc.hullmod.WhitespaceCore.FLUX_LIMIT;

public class VentingArmor implements HullmodPart<VentingArmor.Data> {

    public static final String VENTING_ARMOR =  "arc_venting_armor";

    public static class Data {
        boolean startedVenting = false;
        public float lastMultiplier = 1.0f;

        public float cooldown = 0;

    }


    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, Data customData) {

        customData.cooldown = customData.cooldown - 1;

        if (customData.cooldown < 0) customData.cooldown = 0;


        float fluxLevel = shipAPI.getFluxLevel();
        if (shipAPI.getFluxTracker().isVenting()) {




            if (!customData.startedVenting && fluxLevel > 0.05 && fluxLevel < 0.7f && customData.cooldown == 0) {
                customData.startedVenting = true;


                float damageMult = ARCUtils.clamp(
                        ArchotechLaminate.MAX_DAMAGE_REDUCTION,
                        ArchotechLaminate.MIN_DAMAGE_REDUCTION,
                        ARCUtils.remap(
                                0.1f,
                                FLUX_LIMIT,
                                ArchotechLaminate.MAX_DAMAGE_REDUCTION,
                                ArchotechLaminate.MIN_DAMAGE_REDUCTION,
                                fluxLevel
                        )
                );
                customData.lastMultiplier = damageMult;


                shipAPI.getMutableStats().getHullDamageTakenMult().modifyMult(VENTING_ARMOR, damageMult);
                shipAPI.getMutableStats().getArmorDamageTakenMult().modifyMult(VENTING_ARMOR, damageMult);

                Global.getSoundPlayer().playSound("system_damper", 1.1f, 0.3f, shipAPI.getLocation(), shipAPI.getVelocity());



            }

            if (customData.cooldown > 0 || !customData.startedVenting) return;

            if (customData.lastMultiplier > 0.0f) {
                shipAPI.addAfterimage(new Color(170, 254, 255, 230), 0f, 0f, 0f, 0f, 0f, 0f, 0.75f, 0.33f, true, true, true);
                shipAPI.addAfterimage(new Color(170, 254, 255, 150), 0f, 0f, 0f, 0f, 15f, 0f, 0.75f, 0.33f, true, false, false);

            }


            if (engineAPI.getPlayerShip() == shipAPI) {
                engineAPI
                        .maintainStatusForPlayerShip(
                                VENTING_ARMOR,
                                "graphics/icons/hullsys/damper_field.png",
                                "Whitespace Field",
                                "Damage taken multiplied by " + customData.lastMultiplier + "%", false
                        );
            }

        } else {
            if (customData.startedVenting) {

                customData.startedVenting = false;
                customData.cooldown = 500f;
                customData.lastMultiplier = 1f;

                //unmodify damage absorption

                shipAPI.getMutableStats().getHullDamageTakenMult().unmodifyMult(VENTING_ARMOR);
                shipAPI.getMutableStats().getArmorDamageTakenMult().unmodifyMult(VENTING_ARMOR);

                return;
            }
        }


    }
}
