//by Tartiflette, Anti-missile missile AI: precise and able to randomly choose a target between nearby enemy missiles.
//feel free to use it, credit is appreciated but not mandatory
//V2 done
package arc.weapons.blackbox;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
//import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
import data.scripts.util.MagicTargeting;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.Vector2f;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;

public class BlackboxAI implements MissileAIPlugin, GuidedMissileAI {


    static final float DAMPING = 0.05f;
    static final int NUM_PARTICLES = 10;

    static final String HAS_LOCK = "arc_has_locked";

    enum Mode {
        ANTI_FIGHTER,
        ANTI_MUNITION,
        LOITER
    }

    static MissileAPI pick(CombatEntityAPI source,  Vector2f lookAround, float direction, Integer searchCone, Integer maxRange) {
        CombatEngineAPI engine = Global.getCombatEngine();
        boolean allAspect = searchCone >= 360;
        WeightedRandomPicker<MissileAPI> missilePicker = new WeightedRandomPicker<>();
        List<MissileAPI> missiles = engine.getMissiles();
        if (missiles.isEmpty()) {
            return null;
        } else {
            Iterator<MissileAPI> i$ = missiles.iterator();

            while (true) {
                MissileAPI m;
                do {
                    do {
                        do {
                            do {
                                do {
                                    do {
                                        do {
                                            if (!i$.hasNext()) {
                                                if (!missilePicker.isEmpty()) {
                                                    return missilePicker.pick();
                                                }

                                                return null;
                                            }

                                            m = i$.next();
                                        } while (m.isFading());
                                    } while (m.getOwner() == source.getOwner());
                                } while (m.getCollisionClass() == CollisionClass.NONE);
                            } while (!CombatUtils.isVisibleToSide(m, source.getOwner()));
                        } while (!MathUtils.isPointWithinCircle(lookAround, m.getLocation(), (float) maxRange));
                    } while (!allAspect && !(Math.abs(MathUtils.getShortestRotation(direction, VectorUtils.getAngle(source.getLocation(), m.getLocation()))) < (float) (searchCone / 2)));
                } while (m.getCustomData().get(HAS_LOCK) != null);

                missilePicker.add(m, m.getDamageAmount());
            }
        }
    }

    final MissileAPI missile;

    final float MAX_SPEED;

    CombatEntityAPI currentTarget;

    public BlackboxAI(MissileAPI missile) {
        this.missile = missile;
        MAX_SPEED = missile.getMaxSpeed()*1.25f; //slight over lead
    }



    void colorTheMissile() {
        if (currentTarget instanceof ShipAPI) {
            missile.setJitter(missile, Color.CYAN, 4,4,4);
        }

        if (currentTarget instanceof MissileAPI){
            missile.setJitter(missile, Color.RED, 4,4,4);
        }

        if (currentTarget == null) {
            missile.setJitter(missile, Color.ORANGE, 4,4,4);
        }
    }

    void retargetInitial() {

        MissileAPI potentialLock = pick(
                missile,
                missile.getLocation(),
                missile.getFacing(),
                360,
                (int)(missile.getWeapon().getRange()*1.5f*(missile.getMaxFlightTime()-missile.getFlightTime())/missile.getMaxFlightTime())
        );


        if (potentialLock != null) {
            //we found a missile!
            potentialLock.setCustomData(HAS_LOCK, missile.getOwner());
            setTarget(potentialLock);
            return;
        }

        //well... all missiles are already locked. Let's find a fighter


        CombatEntityAPI entityAPI = MagicTargeting.pickShipTarget(
                missile.getSource(),
                MagicTargeting.targetSeeking.LOCAL_RANDOM,
                3000,
                360,
                20,
                0,
                0,
                0,
                0
        );

        if (entityAPI != null) {
            setTarget(entityAPI);
            return;
        }

        //well.. nothing was found :(

    }

    void retargetWithTarget() {
        if (currentTarget != null && currentTarget instanceof ShipAPI) {
            //let's check if any new missiles have spawned

            MissileAPI potentialLock = pick(
                    missile,
                    missile.getLocation(),
                    missile.getFacing(),
                    360,
                    (int)(missile.getWeapon().getRange()*1.5f*(missile.getMaxFlightTime()-missile.getFlightTime())/missile.getMaxFlightTime())
            );


            if (potentialLock != null) {
                //we found a missile!

                currentTarget.setCustomData(HAS_LOCK, null);
                potentialLock.setCustomData(HAS_LOCK, missile.getOwner());
                setTarget(potentialLock);
            }
        }
    }

    void untargetCurrentTarget() {
        if (currentTarget != null) {
            currentTarget.getCustomData().remove(HAS_LOCK);
        }
    }




    @Override
    public void advance(float amount) {


        if (missile.isFizzling() || missile.isFading() || missile.isExpired()) {
            if (currentTarget != null) {
                currentTarget.setCustomData(HAS_LOCK, null);
            }

            return;
        }

        CombatEngineAPI engine = Global.getCombatEngine();

        if (engine.isPaused()){
            return;
        }

        colorTheMissile();

        if (missile.getVelocity().length() < missile.getMaxSpeed() / 8.0f) {
            missile.giveCommand(ShipCommand.ACCELERATE); //this is a loitering munition, don't send it flying off if we dont have to
        }

        if (!Global.getCombatEngine().isEntityInPlay(currentTarget) || (currentTarget != null && currentTarget.getOwner()==missile.getOwner() ) || (currentTarget != null && currentTarget.isExpired()) || (currentTarget != null && currentTarget instanceof ShipAPI && !((ShipAPI) currentTarget).isPiece())) {

            if (currentTarget instanceof MissileAPI) {
                currentTarget.setCustomData(HAS_LOCK, null);
                currentTarget = null;
            }

        }

        if (currentTarget == null) {
            retargetInitial();
        }

        if (currentTarget != null) {
            retargetWithTarget();
        }

        if (currentTarget == null) {
            if (missile.getVelocity().length() > missile.getMaxSpeed() / 8.0f) {
                missile.giveCommand(ShipCommand.DECELERATE); //this is a loitering munition, don't send it flying off if we dont have to
            }
        }

        if (currentTarget == null) return; //stop that

        
        //finding lead point to aim to    
        float dist = MathUtils.getDistanceSquared(missile.getLocation(), currentTarget.getLocation());
        if (dist<2500){

            proximityFuse();

            return;
        }


        //TODO new interception
        Vector2f lead = null;
        Vector2f point = missile.getLocation();
        Vector2f targetLoc = currentTarget.getLocation();
        Vector2f targetVel = currentTarget.getVelocity();
        Vector2f difference = new Vector2f(targetLoc.x - point.x, targetLoc.y - point.y);

        final float b = 2f * ((targetVel.x * difference.x) + (targetVel.y * difference.y)),
                c = (difference.x * difference.x) + (difference.y * difference.y);

        float a1 = (targetVel.x * targetVel.x) + (targetVel.y * targetVel.y) - (MAX_SPEED * MAX_SPEED);
        Vector2f solution = null;

        if (Float.compare(Math.abs(a1), 0) == 0)
        {
            if (Float.compare(Math.abs(b), 0) == 0)
            {
                solution = (Float.compare(Math.abs(c), 0) == 0)
                        ? new Vector2f(0, 0) : null;
            }
            else
            {
                solution = new Vector2f(-c / b, -c / b);
            }
        }
        else
        {
            float d = (b * b) - (4 * a1 * c);
            if (d >= 0)
            {
                d = (float) Math.sqrt(d);
                a1 = 2 * a1;
                solution = new Vector2f((-b - d) / a1, (-b + d) / a1);
            }
        }

        Vector2f solutionSet = solution;
        if (solutionSet != null)
        {
            float bestFit = Math.min(solutionSet.x, solutionSet.y);
            if (bestFit < 0f)
            {
                bestFit = Math.max(solutionSet.x, solutionSet.y);
            }
            if (bestFit > 0f)
            {
                lead = new Vector2f(targetLoc.x + targetVel.x * bestFit,
                        targetLoc.y + targetVel.y * bestFit);
            }
        }

        if (lead == null ) {
            lead = currentTarget.getLocation();
        }

        float correctAngle = VectorUtils.getAngle(
                        missile.getLocation(), lead
        );

        float correction = MathUtils.getShortestRotation(VectorUtils.getFacing(missile.getVelocity()),correctAngle);
        if(correction>0){
            correction= -11.25f * ( (float)Math.pow(FastTrig.cos(MathUtils.FPI*correction/90)+1, 2) -4 );
        } else {
            correction= 11.25f * ( (float)Math.pow(FastTrig.cos(MathUtils.FPI*correction/90)+1, 2) -4 );
        }
        correctAngle+= correction;        
        
        //turn the missile
        float aimAngle = MathUtils.getShortestRotation(missile.getFacing(), correctAngle);
        if (aimAngle < 0) {
            missile.giveCommand(ShipCommand.TURN_RIGHT);
        } else {
            missile.giveCommand(ShipCommand.TURN_LEFT);
        }
        if (Math.abs(aimAngle)<45){
            missile.giveCommand(ShipCommand.ACCELERATE);
        }
        
        // Damp angular velocity if we're getting close to the target angle
        if (Math.abs(aimAngle) < Math.abs(missile.getAngularVelocity()) * DAMPING) {
            missile.setAngularVelocity(aimAngle / DAMPING);
        }
    }
    
    void proximityFuse(){

        CombatEngineAPI engine = Global.getCombatEngine();

        Color color1 = null;
        Color color2 = null;

        if (currentTarget == null || currentTarget instanceof MissileAPI) {
            color1 = new Color(231, 151, 112, 218);
            color2 = new Color(176, 90, 38,255);
        }

        if (currentTarget instanceof ShipAPI) {
            color1 = new Color(95, 166, 178);
            color2 = new Color(51, 114, 134);
        }

        float radius = 0;
        float coreRadius = 0;

        if (currentTarget == null || currentTarget instanceof MissileAPI) {

            radius = 150;
            coreRadius = 20;
        }
        if (currentTarget instanceof ShipAPI) {
            radius = 60;
            coreRadius = 40;
        }

        double damage = missile.getDamageAmount();

        if (currentTarget instanceof ShipAPI) {
            damage = damage * 1.2;
        } else {
            damage = damage * 2.54;
        }

        DamagingExplosionSpec boom = new DamagingExplosionSpec(
                0.2f,
                radius,
                coreRadius,
                missile.getDamageAmount(),
                20,
                CollisionClass.PROJECTILE_NO_FF,
                CollisionClass.PROJECTILE_FIGHTER,
                2,
                5,
                5,
                25,
                color1,
                color2
        );


        if (currentTarget instanceof ShipAPI) {
            //TODO custom explosion: small and energy
            boom.setDamageType(DamageType.ENERGY);
        } else {
            //TODO custom explosion: large and fragmentation
            boom.setDamageType(DamageType.FRAGMENTATION);
        }

        boom.setSoundSetId("explosion_flak");

        if (currentTarget instanceof ShipAPI) {
            boom.setUseDetailedExplosion(true);
            boom.setDetailedExplosionRadius(120f);
            boom.setDetailedExplosionFlashRadius(200f);
            boom.setDetailedExplosionFlashDuration(0.2f);

            MagicLensFlare.createSharpFlare(
                    Global.getCombatEngine(),
                    missile.getSource(),
                    currentTarget.getLocation(),
                    15,
                    700,
                    15,
                    color1,
                    color2
            );
        } else {
            boom.setShowGraphic(true);
        }


        engine.spawnDamagingExplosion(boom, missile.getSource(), missile.getLocation());
        
        if(MagicRender.screenCheck(0.1f, missile.getLocation())){
            engine.addHitParticle(
                missile.getLocation(),
                new Vector2f(),
                100,
                1,
                0.25f,
                color2
            );
            for (int i=0; i<NUM_PARTICLES; i++){
                float axis = (float)Math.random()*360;
                float range = (float)Math.random()*100;
                engine.addHitParticle(
                    MathUtils.getPointOnCircumference(missile.getLocation(), range/5, axis),
                    MathUtils.getPointOnCircumference(new Vector2f(), range, axis),
                    2+(float)Math.random()*2,
                    1,
                    1+(float)Math.random(),
                    color1
                );
            }
            engine.applyDamage(
                    missile,
                    missile.getLocation(),
                    missile.getHitpoints() * 2f,
                    DamageType.FRAGMENTATION,
                    0f,
                    false,
                    false,
                    missile
            );
        } else {
            engine.removeEntity(missile);
        }
    }

    @Override
    public CombatEntityAPI getTarget() {
        return currentTarget;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.currentTarget = target;
    }


}
