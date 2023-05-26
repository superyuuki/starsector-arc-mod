package arc.weapons.blackbox;

import arc.Index;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlackboxEveryFrameEffect implements EveryFrameWeaponEffectPlugin {



    WeaponAPI weapon;
    ShipAPI ship;
    List<CombatEntityAPI> handledTargetStorage;
    Color colorForThisBurst;

    int maximumBurst = 3;
    int range = 0;
    float beep = 0;

    final IntervalUtil rangeIncrease = new IntervalUtil(0.1f, 0.1f);



    boolean runOnce = false;

    //TODO optimize
    public void fire(float amount) {
        beep-=amount;

        for (CombatEntityAPI possibleTarget : getThreatTargets()) {
            if (handledTargetStorage.contains(possibleTarget)) continue;
            handledTargetStorage.add(possibleTarget); //TODO if this breaks it neds to go at the start
            maximumBurst--;

            if (maximumBurst <= 0) {
                return;
            }


            //sound
            if(ship.getOwner() == 0 && beep<=0){
                beep=0.075f;
                Global.getSoundPlayer().playSound(
                        "diableavionics_virtuousTarget_beep",
                        1,1,
                        ship.getLocation(),
                        ship.getVelocity()
                );
            }

            //send a missile (or a few) after it



            Vector2f firepoint = weapon.getFirePoint(MathUtils.getRandomNumberInRange(0,5));
            float facing = weapon.getArcFacing();


            for (int a = 0; a < 3; ++a) {
                Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 50), facing - 182f, facing - 180f);
                Vector2f.add(vel, ship.getVelocity(), vel);

                float size = MathUtils.getRandomNumberInRange(10f, 30f);
                float duration = MathUtils.getRandomNumberInRange(0.3f, 1f);
                Global.getCombatEngine().addSmokeParticle(firepoint, vel, size, 30f, duration, Color.lightGray);
            }

            Global.getSoundPlayer().playSound(
                    "sabot_srm_fire",
                    1,1,
                    firepoint,
                    ship.getVelocity()
            );

            CombatEntityAPI child = Global.getCombatEngine().spawnProjectile(
                    ship,
                    weapon,
                    Index.BLACKBOX_STAGE_ONE,
                    firepoint,
                    90,
                    null
            );





            //TODO play missile launch noises


            MissileAPI childAsMissile = (MissileAPI) child;
            childAsMissile.setMissileAI(new BlackboxStageOneAI(Index.BLACKBOX_STAGE_TWO, childAsMissile, possibleTarget));


            //HUD only part
            if (!Global.getCombatEngine().isUIShowingHUD()) return;

            MagicRender.objectspace(
                    Global.getSettings().getSprite("fx","targeting"),
                    possibleTarget,
                    new Vector2f(),
                    new Vector2f(),
                    new Vector2f(64,64),
                    new Vector2f(0,0),
                    45,
                    0,
                    false,
                    colorForThisBurst,
                    false,
                    0,
                    0,
                    2, 1, 0.2f,
                    0.5f, 1f, 0.5f,
                    true,
                    CombatEngineLayers.BELOW_INDICATORS_LAYER
            );

            //swirly stolen from diable
            if(MagicRender.screenCheck(0.2f, possibleTarget.getLocation())){
                MagicRender.objectspace(
                        Global.getSettings().getSprite("fx","targeting"),
                        possibleTarget, //anchor
                        new Vector2f(), //offset
                        new Vector2f(), //velocity
                        new Vector2f(192,192), //size
                        new Vector2f(-256,-256), //growth
                        45, //angle
                        360, //spin
                        false, //parented
                        colorForThisBurst,
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

    @Override
    public void advance(float amount, CombatEngineAPI combatEngineAPI, WeaponAPI weaponAPI) {
        rangeIncrease.advance(amount);

        if (!runOnce) {
            runOnce = true;

            this.weapon = weaponAPI;
            this.ship = weapon.getShip();
            this.handledTargetStorage = new ArrayList<>(); //TODO

            //TODO enemy coloring when facing against arc ships
            this.colorForThisBurst = new Color(
                    MathUtils.getRandomNumberInRange(0, 255),
                    MathUtils.getRandomNumberInRange(0,255),
                    MathUtils.getRandomNumberInRange(0,255),
                    255
            );

        }


        if(!weaponAPI.getShip().isAlive() || combatEngineAPI.isCombatOver()){
            handledTargetStorage.clear();
        }

        //Weapon is in firing phase
        if (weaponAPI.isFiring() && weaponAPI.getChargeLevel() > 0.8) {
            if (rangeIncrease.intervalElapsed()) {
                range += weapon.getRange() / 8f; //scan bigger and bigger
            }

            fire(amount);
        }

        if (!weaponAPI.isFiring() && weaponAPI.getChargeLevel() <= 0.1f) {
            handledTargetStorage.clear();
            maximumBurst = 3; //reload virtual ammo counter to max
            range = 0;
        }

    }

    //TODO caching
    List<CombatEntityAPI> getThreatTargets() {
        List<CombatEntityAPI> out = new ArrayList<>();

        for (MissileAPI msl : CombatUtils.getMissilesWithinRange(ship.getLocation(), range)) {
            if (msl.getOwner() == ship.getOwner()) {
                continue;
            }


            if (!msl.isGuided()) {
                Vector2f projDest = Vector2f.add((Vector2f)(new Vector2f(msl.getVelocity()).scale(2.5f)), msl.getLocation(), new Vector2f(0f, 0f));
                if (CollisionUtils.getCollides(msl.getLocation(), projDest, ship.getShield().getLocation(), ship.getShield().getRadius())) {
                    continue;
                }
            }

            out.add(msl); //the missile can hit us an therefore is a threat
        }

        for (ShipAPI shipAPI : CombatUtils.getShipsWithinRange(ship.getLocation(), range)) {
            if (!shipAPI.isFighter() || !shipAPI.isAlive() || shipAPI.isPiece() || shipAPI.isPhased() || shipAPI.isAlly()) continue;
            if (ship.getOwner() == shipAPI.getOwner()) continue;


            out.add(shipAPI); //this fighter is a threat
        }

        return out;
    }

}
