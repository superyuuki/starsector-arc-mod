package arc.hullmod.laminate;

import arc.plugin.RunnableQueuePlugin;
import arc.util.ARCUtils;
import arc.hullmod.IHullmodPart;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import data.scripts.util.MagicUI;

import java.awt.*;


public class VentingGivesArmorPart implements IHullmodPart<VentingGivesArmorPart.Data> {

    public static final String VENTING_ARMOR =  "arc_venting_armor";
    public static final float COOLDOWN = 200f;

    public static class Data {
        boolean startedVenting = false;
        public float lastMultiplier = 1.0f;

        public float cooldown = 0;

    }


    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, Data customData) {

        float fillLevel = (COOLDOWN - customData.cooldown) / COOLDOWN;

        float damageMult = ARCUtils.clamp(
                ArchotechLaminate.MAX_DAMAGE_REDUCTION,
                ArchotechLaminate.MIN_DAMAGE_REDUCTION,
                ARCUtils.remap(
                        0.1f,
                        1.3f,
                        ArchotechLaminate.MAX_DAMAGE_REDUCTION,
                        ArchotechLaminate.MIN_DAMAGE_REDUCTION,
                        shipAPI.getFluxLevel()
                )
        );



        MagicUI.drawInterfaceStatusBar(
                shipAPI,
                fillLevel,
                Color.GREEN,
                Color.WHITE,
                fillLevel,
                "CHROMA",
                100 - (int) ((damageMult) * 100)
        );




        customData.cooldown = customData.cooldown - 1;
        if (customData.cooldown < 0) customData.cooldown = 0;


        float fluxLevel = shipAPI.getFluxLevel();
        if (shipAPI.getFluxTracker().isVenting()) {



            if (!customData.startedVenting && fluxLevel > 0.05 && fluxLevel < 0.7f && customData.cooldown == 0) {
                customData.startedVenting = true;



                customData.lastMultiplier = damageMult;


                shipAPI.getMutableStats().getHullDamageTakenMult().modifyMult(VENTING_ARMOR, damageMult);
                shipAPI.getMutableStats().getArmorDamageTakenMult().modifyMult(VENTING_ARMOR, damageMult);

                Global.getSoundPlayer().playSound("system_damper", 1.1f, 0.3f, shipAPI.getLocation(), shipAPI.getVelocity());



            }

            if (customData.cooldown > 0 || !customData.startedVenting) return;

            if (customData.lastMultiplier < 0.90f) {
                shipAPI.addAfterimage(new Color(170, 254, 255, (int) (130 * (1.0 - fluxLevel))), 0f, 0f, 0f, 0f, 0f, 0f, 0.75f, 0.33f, true, true, true);
                shipAPI.addAfterimage(new Color(170, 254, 255, (int) (50 * (1.0 - fluxLevel))), 0f, 0f, 0f, 0f, 15f, 0f, 0.75f, 0.33f, true, false, false);

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
                customData.cooldown = COOLDOWN;
                customData.lastMultiplier = 1f;

                //unmodify damage absorption

                RunnableQueuePlugin.queueTask(() -> {
                    if (customData.startedVenting) return; //otherwise..

                    //iframes
                    shipAPI.getMutableStats().getHullDamageTakenMult().unmodifyMult(VENTING_ARMOR);
                    shipAPI.getMutableStats().getArmorDamageTakenMult().unmodifyMult(VENTING_ARMOR);
                }, 5);



                return;
            }
        }


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
    public Data makeNew() {
        return new Data();
    }

    @Override
    public String makeKey() {
        return VENTING_ARMOR;
    }
}
