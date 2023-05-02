package arc.hullmod.hypershunt;

import arc.hullmod.HullmodPart;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicLensFlare;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static arc.hullmod.hypershunt.CoronalHypershunt.CORONAL_HYPERSHUNT;

public class LowExplode implements HullmodPart<HypershuntData> {

    static double coef(double mass) {;
        double k = 0.02;
        double m1 = 100;
        double m2 = 2000;
        return (1 / (1 + Math.exp(-k*(mass-m1)))) * 1.5 + 0.5 / (1 + Math.exp(k*(mass-m2)));
    }

    @Override
    public void advanceSafely(CombatEngineAPI combat, ShipAPI ship, float timestep, HypershuntData info) {


        float left = ship.getHitpoints() / ship.getMaxHitpoints();


        if (left < 0.4f &&  !info.inFuckedMode) info.inFuckedMode = true;
        if (!info.inFuckedMode) return;

        if (info.fuckedLevel == 0) {
            info.overloadCounter++;

            Vector2f textLocation = CollisionUtils.getCollisionPoint(
                    ship.getLocation(),
                    new Vector2f(ship.getLocation().x, ship.getLocation().y + ship.getCollisionRadius() + 50),
                    ship);

            combat.addFloatingText(
                    textLocation,
                    "Hypershunt Destabilized!",
                    200f,
                    Color.RED,
                    ship,
                    30f,
                    30f
            );

            Global.getSoundPlayer().playSound("arc_hypershunt_explode", 1.0f, 2.0f, ship.getLocation(), ship.getVelocity());
        }



        if (info.fuckedLevel < 300) {

            info.fuckedLevel++;

            ship.setJitterShields(true);
            ship.setJitterUnder(ship, Color.RED,  10,10,10);

            //NYOOOOOM
            MagicLensFlare.createSharpFlare(
                    Global.getCombatEngine(),
                    ship,
                    ship.getLocation(),
                    5,
                    1000,
                    49,
                    Color.RED,
                    Color.WHITE
            );



            if (info.fuckedLevel == 1) {
                final RippleDistortion ripple = new RippleDistortion(ship.getLocation(), new Vector2f());
                ripple.setSize(1000f);
                ripple.setIntensity(120f);
                ripple.setFrameRate(60.0f);
                ripple.fadeInSize(0.3f);
                ripple.fadeOutIntensity(120f);
                ripple.flip(false);
                DistortionShader.addDistortion(ripple);
            }

            if (info.fuckedLevel > 0 && info.fuckedLevel < 20) {
                //Yoink all ships once
                for (CombatEntityAPI smallVehicle : CombatUtils.getEntitiesWithinRange(ship.getLocation(), 3000f)) {
                    if (ship.getOwner() == smallVehicle.getOwner()) continue;

                    if (smallVehicle instanceof ShipAPI) {
                        ((ShipAPI)smallVehicle).getFluxTracker().forceOverload(40f);
                    }


                    CombatUtils.applyForce(
                            smallVehicle,
                            new Vector2f(2, 2),
                            500f
                    );
                }

            }


            float blastDamage = (float) (ship.getMass() * 1/coef(ship.getMass()));

            DamagingExplosionSpec blast = new DamagingExplosionSpec(0.1f,
                    450f,
                    200f,
                    blastDamage,
                    blastDamage,
                    CollisionClass.PROJECTILE_FF,
                    CollisionClass.PROJECTILE_FIGHTER,
                    4f,
                    4f,
                    0.6f,
                    40,
                    new Color(255,100,0),
                    new Color(255,100,0,20));
            blast.setDamageType(DamageType.FRAGMENTATION);
            blast.setShowGraphic(true);
            blast.setDetailedExplosionFlashColorCore(new Color(255,100,0));
            blast.setDetailedExplosionFlashColorFringe(new Color(255,100,0,20));
            blast.setUseDetailedExplosion(true);
            blast.setDetailedExplosionRadius(600f);
            blast.setDetailedExplosionFlashRadius(700f);
            blast.setDetailedExplosionFlashDuration(0.4f);


            Vector2f blastPoint = MathUtils.getRandomPointInCircle(ship.getLocation(), (float) (2000 / coef(ship.getMass())));

            final RippleDistortion ripple = new RippleDistortion(blastPoint, new Vector2f());
            ripple.setSize(200f);
            ripple.setIntensity(10f);
            ripple.setFrameRate(60.0f);
            ripple.fadeInSize(0.3f);
            ripple.fadeOutIntensity(10f);
            ripple.flip(true);
            DistortionShader.addDistortion(ripple);

            combat.spawnDamagingExplosion(blast, ship, blastPoint, false);

        } else {
            //berserker mode on!
            ship.getMutableStats().getMaxSpeed().modifyPercent(CORONAL_HYPERSHUNT, 50f);
            ship.getMutableStats().getAcceleration().modifyPercent(CORONAL_HYPERSHUNT, 50f);
            ship.getMutableStats().getTurnAcceleration().modifyPercent(CORONAL_HYPERSHUNT, 75f);


            info.inFuckedMode = false;
            info.berserkMode =true;
        }
    }
}
