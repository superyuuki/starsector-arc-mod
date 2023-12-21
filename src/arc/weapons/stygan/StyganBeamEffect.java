package arc.weapons.stygan;

import arc.util.ARCUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicLensFlare;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class StyganBeamEffect implements BeamEffectPlugin {

    boolean hasFired = false;

    final IntervalUtil intervalUtil = new IntervalUtil(0.1f, 0.2f);

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {

        intervalUtil.advance(amount);

        if (beam.getBrightness() <= 0.3f) {
            hasFired = false;
            return;
        }
        if (!intervalUtil.intervalElapsed()) return;

        MagicLensFlare.createSharpFlare(
                engine,
                beam.getSource(),
                beam.getRayEndPrevFrame(),
                12 * beam.getBrightness(),
                (float) (Math.pow(520, 0.5) * Math.pow(beam.getBrightness(), 2)),
                MathUtils.getRandomNumberInRange(0f, 180f),
                new Color(255,105,50),
                new Color(255,200,160)
        );

        float multiplier = 0.2f;

        if (beam.getDamageTarget() != null && beam.getDamageTarget() instanceof ShipAPI) {
            ShipAPI ship = (ShipAPI) beam.getDamageTarget();

            multiplier = ARCUtils.decideBasedOnHullSize(
                    ship,
                    0.03f,
                    0.0625f,
                    0.125f,
                    0.25f
            );
        }

        float blastDamage = beam.getWeapon().getDerivedStats().getBurstDamage() * multiplier;
        // * 5 so vs frigates you get 5 * 0.1 * 1130 = ~500 HE scattered damage, LOL
        // vs cruisers you get 5 * 0.9 * 1130 = 5000 HE scattered damage xD


        DamagingExplosionSpec blast = new DamagingExplosionSpec(0.2f,
                100f,
                34f,
                blastDamage,
                blastDamage * 0.2f,
                CollisionClass.PROJECTILE_FF,
                CollisionClass.PROJECTILE_FIGHTER,
                4f,
                4f,
                0.6f,
                40,
                new Color(231, 151, 112, 218),
                new Color(255, 115,0, 42)
        );
        blast.setDamageType(DamageType.HIGH_EXPLOSIVE);
        blast.setShowGraphic(true);
        blast.setDetailedExplosionFlashColorCore(new Color(231, 151, 112, 218));
        blast.setDetailedExplosionFlashColorFringe(new Color(255, 115,0, 42));
        blast.setUseDetailedExplosion(false);

        if (!hasFired) {
            hasFired = true;

            Global.getSoundPlayer().playSound("hit_heavy_energy", 1.25f, 0.6f, beam.getTo(), new Vector2f(0,0));
            engine.spawnDamagingExplosion(blast, beam.getSource(), beam.getTo(), false);
        }




    }
}
