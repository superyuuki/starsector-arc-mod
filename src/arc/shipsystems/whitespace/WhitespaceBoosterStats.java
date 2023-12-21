package arc.shipsystems.whitespace;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipSystemAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class WhitespaceBoosterStats extends BaseShipSystemScript {


    //What is our maximum time mult, before considering officer skills?
    public static final float MAX_TIME_MULT = 3f;

    //What is the maximum percentage of flux we can have and still activate the system?
    public static final float FLUX_CAP_FOR_USE = 0.91f;

    //These are for the speed and mobility bonuses: experiment as you see fit
    public static final float MAX_SPEED_MULT = 3.5f;
    public static final float ACCELERATION_MULT = 7f;
    public static final float MAX_TURN_SPEED_MULT = 0.5f;
    public static final float TURN_ACCELERATION_MULT = 0.25f;


    //Returns the maximum time mult we can achieve, including officer/player skills
    public static float getMaxTimeMult(MutableShipStatsAPI stats) {
        return 1f + (MAX_TIME_MULT - 1f) * stats.getDynamic().getValue(Stats.PHASE_TIME_BONUS_MULT);
    }

    static final Color WISP_COLOR = new Color(186, 220, 218, 255);


    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {




        //Basic checks to avoid nullpointers and to know if we're the player
        ShipAPI ship;
        boolean player;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
            id = id + "_" + ship.getId();
        } else {
            return;
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


        SpriteAPI sprite = ship.getSpriteAPI();
        float offsetX = sprite.getWidth()/2 - sprite.getCenterX();
        float offsetY = sprite.getHeight()/2 - sprite.getCenterY();

        float trueOffsetX = (float) FastTrig.cos(Math.toRadians(ship.getFacing()-90f))*offsetX - (float)FastTrig.sin(Math.toRadians(ship.getFacing()-90f))*offsetY;
        float trueOffsetY = (float)FastTrig.sin(Math.toRadians(ship.getFacing()-90f))*offsetX + (float)FastTrig.cos(Math.toRadians(ship.getFacing()-90f))*offsetY;


        MagicRender.battlespace(
                Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
                new Vector2f(ship.getLocation().getX() + trueOffsetX, ship.getLocation().getY() + trueOffsetY),
                new Vector2f(0, 0),
                new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
                new Vector2f(0, 0),
                ship.getFacing() - 90f,
                0f,
                WISP_COLOR,
                true,
                0f,
                0.05f,
                0.05f,
                CombatEngineLayers.BELOW_SHIPS_LAYER
        )
        ;



        //Applies the time mult, both locally and (if we're the player ship) globally
        float shipTimeMult = 1f + (getMaxTimeMult(stats) - 1f) * effectLevel;
        stats.getTimeMult().modifyMult(id, shipTimeMult);
        if (player) {
            Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
        } else {
            Global.getCombatEngine().getTimeMult().unmodify(id);
        }

        ship.setJitter(ship, new Color(157, 157, 157), 2f, 3, 2f);

        //Applies mobility bonuses
        stats.getMaxSpeed().modifyMult(id, 1f + effectLevel);
        stats.getMaxTurnRate().modifyMult(id,1f + (MAX_TURN_SPEED_MULT-1f)* effectLevel);
        stats.getTurnAcceleration().modifyMult(id,1f + (TURN_ACCELERATION_MULT-1f)* effectLevel);
        stats.getAcceleration().modifyMult(id,1f + (ACCELERATION_MULT-1f)* effectLevel);
        stats.getDeceleration().modifyMult(id,1f + (ACCELERATION_MULT-1f)* effectLevel);

        //funny
        stats.getEnergyAmmoRegenMult().modifyMult(id, 1.5f);
        stats.getBallisticAmmoRegenMult().modifyMult(id, 1.5f);


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

        Global.getCombatEngine().getTimeMult().unmodify(id);
        stats.getTimeMult().unmodify(id);

        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);

        stats.getEnergyAmmoRegenMult().unmodify(id );
        stats.getBallisticAmmoRegenMult().unmodify(id);

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
