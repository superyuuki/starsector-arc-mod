package arc.weapons.mml;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicFakeBeam;
import data.scripts.util.MagicLensFlare;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;
import java.util.List;
import java.util.Random;

public class MacrossOnHitEffect implements OnHitEffectPlugin {

    private static final Color CORE_COLOR;
    private static final Color FLARE_COLOR;
    private static final Color GLOW_COLOR;

    static final Random RANDOM = new Random();

    public void onHit(final DamagingProjectileAPI projectile, final CombatEntityAPI target, final Vector2f point, final boolean shieldHit, final ApplyDamageResultAPI damageResult, final CombatEngineAPI engine) {
        if (point == null) {
            return;
        }




        int randInt =(int) ( RANDOM.nextFloat() * 5);

        for (int i = 0; i < randInt; i++) {

            Vector2f blastPoint = MathUtils.getRandomPointInCircle(point, 100f);

            MagicFakeBeam.spawnFakeBeam(engine, blastPoint, RANDOM.nextFloat() * 400 + 100, VectorUtils.getAngle(point, blastPoint), 10, 0.3f, 0.2f, 10, Color.PINK, Color.RED, projectile.getDamage().getDamage() / 2.0f, DamageType.HIGH_EXPLOSIVE, 0, projectile.getSource());

            MagicLensFlare.createSharpFlare(
                    engine,
                    projectile.getSource(),
                    point,
                    20,
                    200,
                    MathUtils.getRandomNumberInRange(0f, 180f),
                    new Color(255,105,50, 20),
                    new Color(255,200,160, 20)
            );
        }

        engine.spawnExplosion(point, new Vector2f(), Color.RED, 1f, 1f);



    }

    static {
        CORE_COLOR = new Color(255, 255, 255, 200);
        FLARE_COLOR = new Color(110, 80, 255, 200);
        GLOW_COLOR = new Color(173, 150, 255);
    }
}
