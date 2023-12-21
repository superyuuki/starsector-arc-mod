package arc.weapons.mml;

import arc.StopgapUtils;
import arc.util.ARCUtils;
import arc.weapons.buster.BusterOnHit;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicLensFlare;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MacrossOnHitEffect implements OnHitEffectPlugin {


    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {


        float chance = 0;

        if (target instanceof ShipAPI) {
            chance = ARCUtils.decideBasedOnHullSize(
                    (ShipAPI)target,
                    2f,
                    3f,
                    4f,
                    6f
            );

        } else {
            chance = 3f;
        }

        if (chance > Math.random()) {
            explode(point, projectile, MathUtils.getRandomNumberInRange(0.7f, 2f), 1f);
        }






    }

    public static void explode(Vector2f explosionPoint, DamagingProjectileAPI projectile, float scale, float damageScale) {

        if (projectile.isExpired() || !Global.getCombatEngine().isInPlay(projectile)) return;

        final RippleDistortion ripple = new RippleDistortion(explosionPoint, new Vector2f());
        ripple.setSize(80f * scale);
        ripple.setIntensity(30.0f);
        ripple.setFrameRate(60.0f);
        ripple.fadeInSize(0.3f * scale);
        ripple.fadeOutIntensity(0.3f);
        DistortionShader.addDistortion(ripple);
        MagicLensFlare.createSharpFlare(
                Global.getCombatEngine(),
                projectile.getSource(),
                explosionPoint,
                3f * scale,
                130f * scale,
                0,
                Color.PINK,
                Color.RED
        );

        DamagingExplosionSpec explosionSpec = new DamagingExplosionSpec(
                2f,
                100f * scale,
                50f * scale,
                projectile.getDamageAmount() * 0.8f * damageScale,
                projectile.getDamageAmount() * 1.2f * damageScale,
                CollisionClass.HITS_SHIPS_AND_ASTEROIDS,
                CollisionClass.NONE,
                0.2f,
                1f,
                0.2f,
                3,
                Color.PINK,
                Color.PINK
        );

        explosionSpec.setDamageType(DamageType.HIGH_EXPLOSIVE);
        explosionSpec.setShowGraphic(false);
        explosionSpec.setUseDetailedExplosion(false);

        StopgapUtils.getShipsWithinRange(explosionPoint, 120f).forEachRemaining(ship -> {
            Global.getCombatEngine().applyDamage(ship, ship.getLocation(), 5f, DamageType.ENERGY, 20f, false, true, projectile);
        });
        Global.getCombatEngine().spawnExplosion(explosionPoint, new Vector2f(), Color.PINK, 10f, 2f);
        Global.getCombatEngine().spawnDamagingExplosion(explosionSpec, projectile.getSource(), explosionPoint);

        Global.getSoundPlayer().playSound("arc_macross_explode", 1.0f, 1f, explosionPoint, new Vector2f());
    }



}
