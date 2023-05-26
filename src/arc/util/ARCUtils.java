package arc.util;

import arc.hullmod.hypershunt.ai.VentAIPart;
import com.fs.starfarer.api.combat.*;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.WaveDistortion;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.Map;
import java.util.logging.Logger;

public class ARCUtils {
    public static float lerp(float x, float y, float alpha) {
        return (1f - alpha) * x + alpha * y;
    }
    public static float invlerp(float x, float y, float alpha) {
        return (alpha - x) / (y - alpha);
    }
	public static float remap(float x1, float y1, float x2, float y2, float alpha) {
		return lerp(x2, y2, invlerp(x1, y1, alpha));
	}
	public static float clamp(float min, float max, float value) {
		return value < min ? min : Math.min(value, max);
	}

    public static float pow(final double a, final double b) {
        final long tmp = Double.doubleToLongBits(a);
        final long tmp2 = (long)(b * (tmp - 4606921280493453312L)) + 4606921280493453312L;
        return (float) Double.longBitsToDouble(tmp2);


    }

    public static String slashesOf(Map<ShipAPI.HullSize, Float> map) {
        StringBuilder compound = new StringBuilder();

        int i = 0;

        for (Map.Entry<ShipAPI.HullSize, Float> entry : map.entrySet()) {
            if (i == 0) {
                i++;
                continue;
            }

            compound.append(Math.abs(entry.getValue())).append("%").append("/");
        }

        return compound.toString();
    }


    public static <T> T decideBasedOnHullSize(ShipAPI shipAPI, T frigate, T destroyer, T cruiser, T cap) {
        switch (shipAPI.getHullSize()) {
            case DESTROYER:
                return destroyer;
            case CRUISER:
                return cruiser;
            case CAPITAL_SHIP:
                return cap;
            default:
                return frigate;
        }
    }

    static Logger logger = Logger.getLogger(String.valueOf(VentAIPart.class));

    public static void spawnWave (Vector2f loc, Vector2f vel, float size, float intensity, boolean flip, float angle, float arc, float edgeSmooth, float fadeIn, float last, float fadeOut, float growthTime, float shrinkTime){

        WaveDistortion wave = new WaveDistortion(loc, vel);

        wave.setIntensity(intensity);
        wave.setSize(size);
        wave.setArc(angle-arc/2,angle+arc/2);
        if(edgeSmooth!=0){
            wave.setArcAttenuationWidth(edgeSmooth);
        }
        wave.flip(flip);
        if(fadeIn!=0){
            wave.fadeInIntensity(fadeIn);
        }
        wave.setLifetime(last);
        if(fadeOut!=0){
            wave.setAutoFadeIntensityTime(fadeOut);
//            wave.fadeOutIntensity(fadeOut);
        } else {
            wave.setAutoFadeIntensityTime(99);
        }
        if(growthTime!=0){
            wave.fadeInSize(growthTime);
        }
        if(shrinkTime!=0){
            wave.setAutoFadeSizeTime(shrinkTime);
//            wave.fadeOutSize(shrinkTime);
        } else {
            wave.setAutoFadeSizeTime(99);
        }
        DistortionShader.addDistortion(wave);
    }

    public static boolean tooMuchShieldDamageIncoming (ShipAPI ship, float threshhold, float radiusToCheck, float durationToCheck) {
        //If we don't have a shield, always return false
        if (ship.getShield() == null || ship.getShield().getType().equals(ShieldAPI.ShieldType.NONE)) {
            return false;
        }

        //First check all "dumb" projectiles...
        float totalDamage = 0f;
        for (DamagingProjectileAPI proj : CombatUtils.getProjectilesWithinRange(ship.getLocation(), radiusToCheck)) {
            //Ignore friendlies
            if (proj.getOwner() == ship.getOwner()) {
                continue;
            }

            //Calculate the velocity vector of the projectile some time in the future, and check if this projectile would feasonably collide with us at that point
            Vector2f projDest = Vector2f.add((Vector2f)(new Vector2f(proj.getVelocity()).scale(durationToCheck)), proj.getLocation(), new Vector2f(0f, 0f));
            if (!CollisionUtils.getCollides(proj.getLocation(), projDest, ship.getShield().getLocation(), ship.getShield().getRadius())) {
                //If it doesn't collide, ignore it
                continue;
            }

            //If it's passed all checks, we add its damage (with consideration for damage type) to our counter
            totalDamage += proj.getDamageAmount() * proj.getDamageType().getShieldMult();
        }

        //...then check missiles
        for (MissileAPI msl : CombatUtils.getMissilesWithinRange(ship.getLocation(), radiusToCheck)) {
            //Ignore friendlies
            if (msl.getOwner() == ship.getOwner()) {
                continue;
            }

            //Non-guided missiles use the same threat calculation vector as projectiles, while guided ones are assumed to
            //always be capable of hitting
            if (!msl.isGuided()) {
                Vector2f projDest = Vector2f.add((Vector2f)(new Vector2f(msl.getVelocity()).scale(durationToCheck)), msl.getLocation(), new Vector2f(0f, 0f));
                if (CollisionUtils.getCollides(msl.getLocation(), projDest, ship.getShield().getLocation(), ship.getShield().getRadius())) {
                    //If it doesn't collide, ignore it
                    continue;
                }
            }

            //If it's passed all checks, we add its damage (with consideration for damage type) to our counter
            totalDamage += msl.getDamageAmount() * msl.getDamageType().getShieldMult();
        }



        //If our total incoming damage is higher than our threshhold, we return true. Otherwise, we return false
        return totalDamage >= (ship.getMaxFlux() / ship.getShield().getFluxPerPointOfDamage()) * threshhold;
    }


    public static float calculateDamageToArmor(DamageAPI damage, float armorRating, float damperCoefficient) {
        float damageAmount = damage.getDamage();
        float armorMultiplier = 1f - armorRating / (armorRating + 100f);

        return damageAmount * damage.getType().getArmorMult() * armorMultiplier* damperCoefficient;
    }



    //TODO optimize this shits (vectorize and cache)
    public static boolean tooMuchArmorDamagePossible(ShipAPI ship, float threshhold, float radiusToCheck, float durationToCheck, float multiplyAllDamageBy) {

        //TODO this is wrong, since the ship will always think it has max armor even when low
        float armorRatingForCalculation = ship.getArmorGrid().getArmorRating();



        float totalDamage = 0f;
        //count potential beam strikes
        for (ShipAPI enemy : CombatUtils.getShipsWithinRange(ship.getLocation(), radiusToCheck * 2)) {
            if (enemy.getOwner() == ship.getOwner()) continue; //it's not an emey

            for (WeaponAPI weaponAPI : enemy.getAllWeapons()) {
                //we don't care about disabled weapons or out of range weapons
                if (weaponAPI.isDisabled()) continue;
                if (MathUtils.getDistance(ship.getLocation(), enemy.getLocation()) < weaponAPI.getRange()) continue;

                float trueAngle = VectorUtils.getAngle(enemy.getLocation(), ship.getLocation());
                float desireAngle = weaponAPI.getCurrAngle();

                if (Math.abs(trueAngle-desireAngle) > 30) continue; //we don't care, it's probably gonna miss anyways

                //we care about beams
                if (weaponAPI.isBeam()) {
                    totalDamage = (totalDamage +calculateDamageToArmor(weaponAPI.getDamage(), armorRatingForCalculation, multiplyAllDamageBy)) / 2;
                }

                //we care about insta hits
                if (weaponAPI.getProjectileSpeed() > 800) { //TODO and it can hit us right now
                    totalDamage = totalDamage + calculateDamageToArmor(weaponAPI.getDamage(), armorRatingForCalculation, multiplyAllDamageBy);
                }

            }
        }

        //First check all "dumb" projectiles...

        for (DamagingProjectileAPI proj : CombatUtils.getProjectilesWithinRange(ship.getLocation(), radiusToCheck)) {
            //Ignore friendlies
            if (proj.getOwner() == ship.getOwner()) {
                continue;
            }

            //Calculate the velocity vector of the projectile some time in the future, and check if this projectile would feasonably collide with us at that point
            Vector2f projDest = Vector2f.add((Vector2f)(new Vector2f(proj.getVelocity()).scale(durationToCheck)), proj.getLocation(), new Vector2f(0f, 0f));
            if (!CollisionUtils.getCollides(proj.getLocation(), projDest, ship.getShield().getLocation(), ship.getShield().getRadius())) {
                //If it doesn't collide, ignore it
                continue;
            }

            totalDamage = totalDamage + calculateDamageToArmor(proj.getDamage(), armorRatingForCalculation, multiplyAllDamageBy);
        }

        //...then check missiles
        for (MissileAPI msl : CombatUtils.getMissilesWithinRange(ship.getLocation(), radiusToCheck)) {
            //Ignore friendlies
            if (msl.getOwner() == ship.getOwner()) {
                continue;
            }

            //Non-guided missiles use the same threat calculation vector as projectiles, while guided ones are assumed to
            //always be capable of hitting
            if (!msl.isGuided()) {
                Vector2f projDest = Vector2f.add((Vector2f)(new Vector2f(msl.getVelocity()).scale(durationToCheck)), msl.getLocation(), new Vector2f(0f, 0f));
                if (CollisionUtils.getCollides(msl.getLocation(), projDest, ship.getShield().getLocation(), ship.getShield().getRadius())) {
                    //If it doesn't collide, ignore it
                    continue;
                }
            }

            //If it's passed all checks, we add its damage (with consideration for damage type) to our counter
            totalDamage = totalDamage + calculateDamageToArmor(msl.getDamage(), armorRatingForCalculation, multiplyAllDamageBy);
        }



        //If our total incoming damage is higher than our threshhold, we return true. Otherwise, we return false
        return totalDamage >= (ship.getArmorGrid().getArmorRating()) * threshhold;
    }
}