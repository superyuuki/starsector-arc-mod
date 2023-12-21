package arc.weapons.dusk;

import arc.util.ARCUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicLensFlare;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.WaveDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class DuskOnHit implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {


        //Yes, i am aware that this will cause the dusk to do 1 + the multiplied amount. No, i do not care.
        float multiplier = 1;
        if (target instanceof ShipAPI) {
            multiplier = ARCUtils.decideBasedOnHullSize((ShipAPI)target, 1f, 1f, 1.5f, 2f, 2.5f);
        }

        if (!shieldHit) {
            Global.getSoundPlayer().playSound(
                    "arc_dusk_hit",
                    MathUtils.getRandomNumberInRange(0.9f, 1.1f),
                    0.6f, point, target.getVelocity());
        }

        engine.applyDamage(target, point, projectile.getDamageAmount() * multiplier, DamageType.KINETIC, 0f, false, false, projectile.getSource(), false);





    }


   



}
