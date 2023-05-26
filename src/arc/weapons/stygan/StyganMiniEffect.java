package arc.weapons.stygan;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicLensFlare;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class StyganMiniEffect implements BeamEffectPlugin {

    boolean hasFired = false;

    final IntervalUtil intervalUtil = new IntervalUtil(0.1f, 0.2f);

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {

        float multiplier = 1f;

        if (beam.getDamageTarget() instanceof ShipAPI && !((ShipAPI) beam.getDamageTarget()).isFighter()) {
            multiplier = 0.2f;

            //TODO this probably doesnt work but im too lazy to test
        }

        beam.getDamage().setMultiplier(multiplier);


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
                6 * beam.getBrightness(),
                (float) (Math.pow(220, 0.5) * Math.pow(beam.getBrightness(), 2)),
                MathUtils.getRandomNumberInRange(0f, 180f),
                new Color(170, 254, 255, 230),
                new Color(216, 254, 255, 205)
        );

        float blastDamage = beam.getWeapon().getDerivedStats().getBurstDamage() * 0.07f * multiplier; //*9 = 36% extra damage, so i lied :P

        DamagingExplosionSpec blast = new DamagingExplosionSpec(0.2f,
                70f,
                25,
                blastDamage,
                blastDamage * 0.2f,
                CollisionClass.PROJECTILE_FF,
                CollisionClass.PROJECTILE_FIGHTER,
                4f,
                4f,
                0.6f,
                40,
                new Color(231, 151, 112, 110),
                new Color(255, 115,0, 42));
        blast.setDamageType(DamageType.FRAGMENTATION);
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
