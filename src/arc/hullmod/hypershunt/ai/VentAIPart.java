package arc.hullmod.hypershunt.ai;

import arc.Index;
import arc.hullmod.ARCData;
import arc.util.ARCUtils;
import arc.hullmod.laminate.ArchotechLaminate;
import arc.hullmod.IHullmodPart;
import arc.hullmod.laminate.VentingGivesArmorPart;
import cmu.CMUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;


import java.awt.*;
import java.util.List;


/**
 * Handles how the AI will decide when to use vent abilities
 */
public class VentAIPart implements IHullmodPart<ARCData> {


    final IntervalUtil ventEvery = new IntervalUtil(0.05f, 0.2f); //simulate a human's reaction time


    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI ship, float timestep, ARCData customData) {
        ventEvery.advance(timestep);
        if (!ventEvery.intervalElapsed()) return;

        if (customData.mode == ARCData.Mode.BERSERK || customData.mode == ARCData.Mode.GLIDE_DRIVE) {
            return; //Don't hyper vent while berserking or glide driving
        }

        if (ship.getSystem().isActive()) return; //Don't activate using system
        if (ship == Global.getCombatEngine().getPlayerShip() && ship.getAI() == null) return; //dont use this on the player ship


        FluxTrackerAPI fluxTracker = ship.getFluxTracker();
        ShipwideAIFlags flags = ship.getAIFlags();
        float fluxLevel = fluxTracker.getFluxLevel();
        if (fluxTracker.isOverloadedOrVenting()) return;


        VentingGivesArmorPart.Data data  = (VentingGivesArmorPart.Data) ship.getCustomData().get(VentingGivesArmorPart.VENTING_ARMOR);

        if (data == null) {
        }

        if (data == null) return;
        if (data.cooldown != 0) return; //No point in doing any of this if our armor/gun isn't ready to vent yet

        //check all these every tick
        //use armor to block shield damage fluxLevel > 0.05f && fluxLevel < 0.6f &&

        float damageMult = ARCUtils.clamp(
                ArchotechLaminate.MAX_DAMAGE_REDUCTION,
                ArchotechLaminate.MIN_DAMAGE_REDUCTION,
                ARCUtils.remap(
                        0f,
                        1,
                        ArchotechLaminate.MAX_DAMAGE_REDUCTION,
                        ArchotechLaminate.MIN_DAMAGE_REDUCTION,
                        fluxLevel
                )
        );

        for (WeaponAPI weaponAPI : ship.getUsableWeapons()) {
            if (weaponAPI.getSpec().hasTag("hyper") && weaponAPI.isFiring()) return;
            if (weaponAPI.getSpec().hasTag("STRIKE") && weaponAPI.isFiring()) return;
        }

        //todo MORE CONSERVATIVE for firgates

        float tooMuchArmorDamageThreshold = ARCUtils.decideBasedOnHullSize(
                ship,
                0.3f,
                0.4f,
                0.4f,
                0.5f
        );

        boolean tooMuchArmorDamage = ARCUtils.tooMuchArmorDamagePossible(
                ship,
                tooMuchArmorDamageThreshold,
                2500f,
                1f,
                damageMult
        );




        if (fluxLevel > 0.05f && fluxLevel < 0.4f && ARCUtils.tooMuchShieldDamageIncoming(ship, 0.07f, 1300f, 1f)) {
            //anti shield vent

            if (tooMuchArmorDamage) {
                engineAPI.addFloatingText(ship.getLocation(), "opportunistic vent, but scared!", 20f, Color.RED, ship, 2f, 2f);

                return;
            }

            engineAPI.addFloatingText(ship.getLocation(), "opportunistic vent!", 20f, Color.RED, ship, 2f, 2f);

            FluxTrackerAPI fluxTracker1 = ship.getFluxTracker();

            //as long as the time is low...

            if (ship.getMutableStats().getVentRateMult().getModifiedValue() > 3 && ship.getFluxLevel() < 0.6f) {

                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF, fluxTracker1.getTimeToVent() + 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING, fluxTracker1.getTimeToVent() + 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.MANEUVER_TARGET, fluxTracker1.getTimeToVent() + 3f);
                //close that gap, these are ANGRY ships


                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
            } else {
                //back down, but still armor vent

                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE, 2f, 600f- ship.getCollisionRadius()*0.5f);
                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
            }


            return;
        }


        if (flags.hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS) && fluxLevel < 0.4f && fluxLevel > 0.05f) { //super efficient damper) {

            if (tooMuchArmorDamage) {
                engineAPI.addFloatingText(ship.getLocation(), "shield drop vent, but scared!", 20f, Color.RED, ship, 2f, 2f);

                return;
            }

            engineAPI.addFloatingText(ship.getLocation(), "shield drop vent!", 20f, Color.RED, ship, 2f, 2f);

            FluxTrackerAPI fluxTracker1 = ship.getFluxTracker();

            //as long as the time is low...

            if (ship.getMutableStats().getVentRateMult().getModifiedValue() > 3 && ship.getFluxLevel() < 0.6f) {

                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF, fluxTracker1.getTimeToVent() + 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING, fluxTracker1.getTimeToVent() + 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.MANEUVER_TARGET, fluxTracker1.getTimeToVent() + 3f);
                //close that gap, these are ANGRY ships


                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
            } else {
                //back down, but still armor vent

                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE, 2f, 600f- ship.getCollisionRadius()*0.5f);
                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
            }


            return;



        }

        //inverse curve to prioritize low flux and penalize high flux
        float ventingNeed = ARCUtils.pow(fluxLevel + 0.9, -1.5f);

        //we never want to get down to hull, but this can remain

        float hullLevel = ship.getHullLevel();
        float hullFactor = ARCUtils.decideBasedOnHullSize(
                ship,
                ARCUtils.pow(hullLevel,2f),
                hullLevel,
                ARCUtils.pow(hullLevel,0.6f),
                ARCUtils.pow(hullLevel,0.4f)
        );

        //situational danger

        float dangerFactor=0;


        List<ShipAPI> nearbyEnemies = AIUtils.getNearbyEnemies(ship, 2000f);
        for (ShipAPI enemy : nearbyEnemies) {
            switch (enemy.getHullSize()) {
                case CAPITAL_SHIP:
                    dangerFactor+= Math.max(
                            0, 3 - MathUtils.getDistanceSquared(
                                            enemy.getLocation(), ship.getLocation()
                            ) /1000000
                    );
                    break;
                case CRUISER:
                    dangerFactor+= Math.max(0,2.25f-(MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation())/1000000));
                    break;
                case DESTROYER:
                    dangerFactor+= Math.max(0,1.5f-(MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation())/1000000));
                    break;
                case FRIGATE:
                    dangerFactor+= Math.max(0,1f-(MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation())/1000000));
                    break;
                default:
                    dangerFactor+= Math.max(0,0.5f-(MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation())/640000));
                    break;
            }
        }

        float decisionLevel = (ventingNeed * hullFactor+1) /(dangerFactor+1);

        if (fluxLevel < 0.05f) return; //silly bug

        //TODO AND no armor AND
        //TODO right now this lets shots through

        CMUtils.getGuiDebug().putText(VentAIPart.class, "a", "a");

        if (decisionLevel >=MathUtils.getRandomNumberInRange(1.1f, 1.6f)) {
            engineAPI.addFloatingText(ship.getLocation(), "vent, decision " + decisionLevel + " and damger " + dangerFactor, 20f, Color.RED, ship, 2f, 2f);


            FluxTrackerAPI fluxTracker1 = ship.getFluxTracker();

            //as long as the time is low...

            if (ship.getMutableStats().getVentRateMult().getModifiedValue() > 3 && ship.getFluxLevel() < 0.6f) {

                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF, fluxTracker1.getTimeToVent() + 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING, fluxTracker1.getTimeToVent() + 2f);
                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.MANEUVER_TARGET, fluxTracker1.getTimeToVent() + 3f);
                //close that gap, these are ANGRY ships


                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
            } else {
                //back down, but still armor vent

                ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE, 2f, 600f- ship.getCollisionRadius()*0.5f);
                ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
            }


        }
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
