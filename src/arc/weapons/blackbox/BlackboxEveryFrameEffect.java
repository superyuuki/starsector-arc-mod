package arc.weapons.blackbox;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlackboxEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    float beep=0;

    static final List <ShipAPI> TARGETS = new ArrayList<>();

    void pickTargets(CombatEngineAPI engine, float level, ShipAPI ship, float amount, WeaponAPI we){

        ship.getOwner();

        beep-=amount;
        //adding ships
        List<ShipAPI> nearby = AIUtils.getNearbyEnemies(ship, we.getRange());
        for(ShipAPI s : nearby){
            if(!TARGETS.contains(s)){
                TARGETS.add(s);

                //sound
                if(ship.getOwner() == 0 && beep<=0){
                    beep=0.075f;
                    Global.getSoundPlayer().playSound("diableavionics_virtuousTarget_beep", 1, 1, ship.getLocation(), ship.getVelocity());
                }
                if(engine.isUIShowingHUD()){
                    //add a targeting diamond
                    MagicRender.objectspace(
                            Global.getSettings().getSprite("diableavionics","DIAMOND"),
                            s, //anchor
                            new Vector2f(), //offset
                            new Vector2f(), //velocity
                            new Vector2f(64,64), //size
                            new Vector2f(0,0), //growth
                            45, //angle
                            0, //spin
                            false, //parented
                            Color.orange,
                            false, //additive
                            0, 0, //jitter
                            2, 1, 0.2f, //flicker
                            0.5f, 4 - 0.2f*level, 0.5f, //timing //TODO tihs doesn't work since i ripped it out
                            true,
                            CombatEngineLayers.BELOW_INDICATORS_LAYER
                    );
                    //exclude the swirly one if it is too far off screen
                    if(MagicRender.screenCheck(0.2f, s.getLocation())){
                        MagicRender.objectspace(
                                Global.getSettings().getSprite("diableavionics","DIAMOND"),
                                s, //anchor
                                new Vector2f(), //offset
                                new Vector2f(), //velocity
                                new Vector2f(192,192), //size
                                new Vector2f(-256,-256), //growth
                                45, //angle
                                360, //spin
                                false, //parented
                                Color.orange,
                                false, //additive
                                0, 0, //jitter
                                0, 0, 0, //flicker
                                0.35f, 0.05f, 0.1f, //timing
                                true,
                                CombatEngineLayers.BELOW_INDICATORS_LAYER
                        );
                    }
                }
            }
        }
    }

    boolean runOnce = false;
    ShipAPI ship;


    @Override
    public void advance(float v, CombatEngineAPI combatEngineAPI, WeaponAPI weaponAPI) {

        if (!runOnce || ship==null){
            ship=weaponAPI.getShip();
            runOnce=true;return;
        }


        if(!weaponAPI.getShip().isAlive() || combatEngineAPI.isCombatOver()){
            TARGETS.clear();
        }




        if (weaponAPI.isFiring()) {
            //fire once

            pickTargets(combatEngineAPI, 1, ship, v, weaponAPI);


            //Lock onto fighters in range
            CombatUtils.getShipsWithinRange(
                    weaponAPI.getLocation(),
                    weaponAPI.getRange()
            );

            CombatUtils.getMissilesWithinRange(
                    weaponAPI.getLocation(),
                    weaponAPI.getRange()
            );

        }
    }
}
