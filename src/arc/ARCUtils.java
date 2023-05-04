package arc;

import arc.hullmod.whitespace.Venting;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

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
		return value < min ? min : value > max ? max : value;
	}

    public static float pow(final double a, final double b) {
        final long tmp = Double.doubleToLongBits(a);
        final long tmp2 = (long)(b * (tmp - 4606921280493453312L)) + 4606921280493453312L;
        return (float) Double.longBitsToDouble(tmp2);


    }

    public static float calculateDistance(Vector2f v1, Vector2f v2) {
        float xDiff = v1.x - v2.x;
        float yDiff = v1.y - v2.y;
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
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

    static Logger logger = Logger.getLogger(String.valueOf(Venting.class));

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


    public static float calculateDamageToArmor(DamageAPI damage, float armorRating) {
        float damageToArmor = 0f;
        float damageAmount = damage.getDamage();
        float armorMultiplier = 1f - armorRating / (armorRating + 100f);
        float armorDamage = damageAmount * armorMultiplier;
        damageToArmor += armorDamage * damage.getType().getArmorMult();
        return damageToArmor;
    }

    //TODO optimize this shits
    public static boolean tooMuchArmorDamagePossible(ShipAPI ship, float threshhold, float radiusToCheck, float durationToCheck, float multiplyAllDamageBy) {

        float armorRatingForCalculation = ship.getArmorGrid().getArmorRating();
        float totalDamage = 0f;
        //count potential beam strikes
        for (ShipAPI enemy : CombatUtils.getShipsWithinRange(ship.getLocation(), radiusToCheck * 2)) {
            for (WeaponAPI weaponAPI : enemy.getAllWeapons()) {

                if (weaponAPI.isBeam() && !weaponAPI.hasAIHint(WeaponAPI.AIHints.PD) && calculateDistance(ship.getLocation(), enemy.getLocation()) <= weaponAPI.getRange() * 0.9) {
                    totalDamage = totalDamage +calculateDamageToArmor(weaponAPI.getDamage(), armorRatingForCalculation);
                }

                if (weaponAPI.getProjectileSpeed() > radiusToCheck / durationToCheck ) {
                    totalDamage = totalDamage + calculateDamageToArmor(weaponAPI.getDamage(), armorRatingForCalculation);
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

            totalDamage = totalDamage + calculateDamageToArmor(proj.getDamage(), armorRatingForCalculation);
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
            totalDamage = totalDamage + calculateDamageToArmor(msl.getDamage(), armorRatingForCalculation);
        }



        totalDamage = totalDamage * multiplyAllDamageBy;


        //If our total incoming damage is higher than our threshhold, we return true. Otherwise, we return false
        return totalDamage >= (armorRatingForCalculation) * threshhold;
    }
}