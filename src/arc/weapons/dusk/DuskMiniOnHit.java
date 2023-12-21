package arc.weapons.dusk;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class DuskMiniOnHit implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        if (!shieldHit) {
            Global.getSoundPlayer().playSound(
                    "arc_dusk_hit",
                    MathUtils.getRandomNumberInRange(0.9f, 1.1f),
                    0.3f, point, target.getVelocity());
        }


        RippleDistortion wave2 = new RippleDistortion();
        wave2.setLocation(point);
        wave2.setSize(200f);
        wave2.setIntensity(70f);
        wave2.fadeInSize(0.15f);
        wave2.fadeOutIntensity(0.3f);
        DistortionShader.addDistortion(wave2);

        float blastDamage = projectile.getDamageAmount() * 0.5f;

        DamagingExplosionSpec blast = new DamagingExplosionSpec(0.2f,
                80f,
                40f,
                blastDamage,
                blastDamage * 0.2f,
                CollisionClass.PROJECTILE_FF,
                CollisionClass.PROJECTILE_FIGHTER,
                4f,
                4f,
                0.6f,
                40,
                new Color(101, 145, 255, 200),
                new Color(51, 77, 134, 255));
        blast.setDamageType(DamageType.ENERGY);
        blast.setShowGraphic(true);
        blast.setDetailedExplosionFlashColorCore(new Color(186, 196, 220));
        blast.setDetailedExplosionFlashColorFringe(new Color(95, 96, 178, 255));
        blast.setUseDetailedExplosion(true);
        blast.setDetailedExplosionRadius(60f);
        blast.setDetailedExplosionFlashRadius(100f);
        blast.setDetailedExplosionFlashDuration(0.4f);

        Global.getCombatEngine().spawnDamagingExplosion(blast, projectile.getSource(), point);
    }
}
