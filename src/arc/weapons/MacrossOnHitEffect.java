package arc.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
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

        final RippleDistortion ripple = new RippleDistortion(point, new Vector2f());
        ripple.setSize(80f);
        ripple.setIntensity(30.0f);
        ripple.setFrameRate(60.0f);
        ripple.fadeInSize(0.3f);
        ripple.fadeOutIntensity(0.3f);
        ripple.flip(true);
        DistortionShader.addDistortion(ripple);
        final List<MissileAPI> missileAPIs = AIUtils.getNearbyEnemyMissiles((CombatEntityAPI)projectile, 300.0f);
        for (final MissileAPI missileAPI : missileAPIs) {
            missileAPI.flameOut();
        }
        MagicLensFlare.createSmoothFlare(
                Global.getCombatEngine(),
                projectile.getSource(),
                point,
                20,
                100,
                0,
                Color.YELLOW,
                Color.RED
        );

        engine.spawnExplosion(point, new Vector2f(), Color.RED, 1f, 1f);



    }

    static {
        CORE_COLOR = new Color(255, 255, 255, 200);
        FLARE_COLOR = new Color(110, 80, 255, 200);
        GLOW_COLOR = new Color(173, 150, 255);
    }
}
