package arc.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class PhaseDiveStats extends BaseShipSystemScript {
    //The opacity of the ship when in phase
    public static final float SHIP_ALPHA_MULT = 0.25f;

    //When does the ship "actually" enter phase? (0.5f would mean it's not in phase until after half of the charge-up. Leave as 0f for vanilla behaviour, which is instant)
    public static final float VULNERABLE_FRACTION = 0f;

    //What is our maximum time mult, before considering officer skills?
    public static final float MAX_TIME_MULT = 3f;
    
    //What is the maximum percentage of flux we can have and still activate the system?
    public static final float FLUX_CAP_FOR_USE = 0.79f;

    //These are for the speed and mobility bonuses: experiment as you see fit
    public static final float MAX_SPEED_MULT = 2.5f;
    public static final float ACCELERATION_MULT = 4f;
    public static final float MAX_TURN_SPEED_MULT = 2.5f;
    public static final float TURN_ACCELERATION_MULT = 3f;

    //These are just used to display our status messages, ignore them
    private Object STATUSKEY1 = new Object();
    private Object STATUSKEY2 = new Object();


    //Returns the maximum time mult we can achieve, including officer/player skills
    public static float getMaxTimeMult(MutableShipStatsAPI stats) {
        return 1f + (MAX_TIME_MULT - 1f) * stats.getDynamic().getValue(Stats.PHASE_TIME_BONUS_MULT);
    }

    //Determines how and when status messages should be displayed to the player
    private void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {
        float level = effectLevel;
        float f = VULNERABLE_FRACTION;

        //Ensure we have a system
        ShipSystemAPI cloak  = playerShip.getSystem();
        if (cloak == null) return;

        //Status messages shown to the player in phase
        if (level > f) {
            Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY1,
                    cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "mobility increased", false);
            Global.getCombatEngine().maintainStatusForPlayerShip(STATUSKEY2,
                    cloak.getSpecAPI().getIconSpriteName(), cloak.getDisplayName(), "time flow altered", false);
        }
    }


    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        //Basic checks to avoid nullpointers and to know if we're the player
        ShipAPI ship = null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
            id = id + "_" + ship.getId();
        } else {
            return;
        }

        //Show status messages if we're the player, depending on the function declared earlier
        if (player) {
            maintainStatus(ship, state, effectLevel);
        }

        //No reason to run stat changes when paused
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        //If the cloak is off, remove the bonuses we've gotten
        if (state == State.COOLDOWN || state == State.IDLE) {
            unapply(stats, id);
            return;
        }

        float level = effectLevel;
        float levelForAlpha = level;

        ShipSystemAPI cloak = ship.getSystem();

        //Phases the ship
        if (state == State.IN || state == State.ACTIVE) {
            ship.setPhased(true);
            levelForAlpha = level;
        } else if (state == State.OUT) {
            ship.setPhased(true);
            levelForAlpha = level;
        }

        //Sets our ship's alpha
        ship.setExtraAlphaMult(1f - (1f - SHIP_ALPHA_MULT) * levelForAlpha);
        ship.setApplyExtraAlphaToEngines(true);

        //Applies the time mult, both locally and (if we're the player ship) globally
        float shipTimeMult = 1f + (getMaxTimeMult(stats) - 1f) * levelForAlpha;
        stats.getTimeMult().modifyMult(id, shipTimeMult);
        if (player) {
            Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
        } else {
            Global.getCombatEngine().getTimeMult().unmodify(id);
        }

        //Applies mobility bonuses
        stats.getMaxSpeed().modifyMult(id, 1f + (MAX_SPEED_MULT-1f)*level);
        stats.getMaxTurnRate().modifyMult(id,1f + (MAX_TURN_SPEED_MULT-1f)*level);
        stats.getTurnAcceleration().modifyMult(id,1f + (TURN_ACCELERATION_MULT-1f)*level);
        stats.getAcceleration().modifyMult(id,1f + (ACCELERATION_MULT-1f)*level);
        stats.getDeceleration().modifyMult(id,1f + (ACCELERATION_MULT-1f)*level);
    }


    public void unapply(MutableShipStatsAPI stats, String id) {
        ShipAPI ship = null;
        //boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            //player = ship == Global.getCombatEngine().getPlayerShip();
            //id = id + "_" + ship.getId();
        } else {
            return;
        }

        //Reverts time mult
        Global.getCombatEngine().getTimeMult().unmodify(id);
        stats.getTimeMult().unmodify(id);

        //Unphases the ship and reverts to normal opacity
        ship.setPhased(false);
        ship.setExtraAlphaMult(1f);

        //Removes the mobility bonuses
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
    }

    //We use another method to add this, so ignore this one
    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }
    
        //Makes the system unusable while overloading and when flux is too high
    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        if (ship.getFluxTracker().isOverloadedOrVenting()) {
            return false;
        } else if (ship.getFluxTracker().getCurrFlux() > ship.getFluxTracker().getMaxFlux() * (FLUX_CAP_FOR_USE)) {
            return false;
        }
        return super.isUsable(system, ship);
    }
    
}
