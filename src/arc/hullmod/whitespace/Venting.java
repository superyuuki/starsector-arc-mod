package arc.hullmod.whitespace;

import arc.ARCUtils;
import arc.hullmod.ArchotechLaminate;
import arc.hullmod.HullmodPart;
import arc.hullmod.laminate.VentingArmor;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonalityAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.util.IntervalUtil;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;


import java.awt.*;
import java.util.List;

import static arc.hullmod.WhitespaceCore.FLUX_LIMIT;


/**
 * hello, scy
 */
public class Venting implements HullmodPart<Void> {


    final IntervalUtil ventEvery = new IntervalUtil(0.1f, 0.3f); //simulate a human's reaction time




    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI ship, float timestep, Void customData) {
        ventEvery.advance(timestep);
        if (ship.getSystem().isActive()) return; //Don't activate using system
        if (ship == Global.getCombatEngine().getPlayerShip()) return; //dont use this on the player ship
        FluxTrackerAPI fluxTracker = ship.getFluxTracker();
        ShipwideAIFlags flags = ship.getAIFlags();
        float fluxLevel = fluxTracker.getFluxLevel();
        if (fluxTracker.isOverloadedOrVenting()) return;
        VentingArmor.Data data  = (VentingArmor.Data) ship.getCustomData().get(VentingArmor.VENTING_ARMOR);
        if (data  == null || data.cooldown != 0) return; //No point in doing any of this if our armor isn't ready to vent yet

        //check all these every tick
        //use armor to block shield damage fluxLevel > 0.05f && fluxLevel < 0.6f &&

        float damageMult = ARCUtils.clamp(
                ArchotechLaminate.MAX_DAMAGE_REDUCTION,
                ArchotechLaminate.MIN_DAMAGE_REDUCTION,
                ARCUtils.remap(
                        0f,
                        FLUX_LIMIT,
                        ArchotechLaminate.MAX_DAMAGE_REDUCTION,
                        ArchotechLaminate.MIN_DAMAGE_REDUCTION,
                        fluxLevel
                )
        );

        for (WeaponAPI weaponAPI : ship.getUsableWeapons()) {
            if (weaponAPI.getSpec().hasTag("hyper") && weaponAPI.isFiring()) return;
            //don't vent during sunspear
        }

        //TODO dont vent if firing in a burst
        if (fluxLevel > 0.07f && fluxLevel < 0.45f && ARCUtils.tooMuchShieldDamageIncoming(ship, 0.15f, 1000f, 1f) && !ARCUtils.tooMuchArmorDamageIncoming(ship, 0.3f, 1000f, 1f, damageMult)) {
            vent(ship);
            return;
        }


        if (flags.hasFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS) && !ARCUtils.tooMuchArmorDamageIncoming(ship, 0.3f, 1000f, 1f, damageMult) && fluxLevel < 0.45f && fluxLevel > 0.07f) { //super efficient damper) {
            vent(ship);
            return;

        }



        if (!ventEvery.intervalElapsed()) return;

        float ventingNeed = ARCUtils.decideBasedOnHullSize(
                ship,
                ARCUtils.pow(fluxLevel,2f),
                ARCUtils.pow(fluxLevel,3f),
                1.5f * ARCUtils.pow(fluxLevel,4f),
                2 * ARCUtils.pow(fluxLevel,5f)
        );

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
            //reset often with timid or cautious personalities
            FleetSide side = FleetSide.PLAYER;
            if (ship.getOriginalOwner()>0){
                side = FleetSide.ENEMY;
            }
            if(Global.getCombatEngine().getFleetManager(side).getDeployedFleetMember(ship)!=null){
                PersonalityAPI personality = (Global.getCombatEngine().getFleetManager(side).getDeployedFleetMember(ship)).getMember().getCaptain().getPersonalityAPI();
                if(personality.getId().equals("timid") || personality.getId().equals("cautious")){
                    if (enemy.getFluxTracker().isOverloaded() && enemy.getFluxTracker().getOverloadTimeRemaining() > ship.getFluxTracker().getTimeToVent()) {
                        continue;
                    }
                    if (enemy.getFluxTracker().isVenting() && enemy.getFluxTracker().getTimeToVent() > ship.getFluxTracker().getTimeToVent()) {
                        continue;
                    }
                }
            }

            switch (enemy.getHullSize()) {
                case CAPITAL_SHIP:
                    dangerFactor+= Math.max(0,3f-(MathUtils.getDistanceSquared(enemy.getLocation(), ship.getLocation())/1000000));
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

        float decisionLevel = (ventingNeed*hullFactor+1)/(dangerFactor+1);

        if (decisionLevel >=1.5f || (ship.getFluxTracker().getFluxLevel()>0.1f && dangerFactor == 0)) {

            vent(ship);


        }
    }

    void vent(ShipAPI ship) {
        FluxTrackerAPI fluxTracker = ship.getFluxTracker();

        ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF, fluxTracker.getTimeToVent() + 2f);
        ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_BACK_OFF_EVEN_WHILE_VENTING, fluxTracker.getTimeToVent() + 2f);
        ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE, 2f, 700f-ship.getCollisionRadius()*0.5f);
        ship.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.MANEUVER_RANGE_FROM_TARGET, 2f, 700f-ship.getCollisionRadius()*0.5f);

        ship.giveCommand(ShipCommand.VENT_FLUX, null, 0);
    }
}
