package arc.weapons.mml;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicFakeBeam;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
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

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (MathUtils.getRandomNumberInRange(0, 100) > 4) return;

        if(MagicRender.screenCheck(0.25f, projectile.getLocation())) {
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "blast_flash"),
                    new Vector2f(point),
                    new Vector2f(),
                    new Vector2f(120, 120),
                    new Vector2f(-384, -384),
                    MathUtils.getRandomNumberInRange(0, 360),
                    MathUtils.getRandomNumberInRange(-1, 1),
                    Color.PINK,
                    false,
                    0, 0, 0, 0, 0,
                    0,
                    0.1f,
                    0.4f,
                    CombatEngineLayers.JUST_BELOW_WIDGETS
            );
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "blast_core"),
                    new Vector2f(point),
                    new Vector2f(),
                    new Vector2f(190, 190),
                    new Vector2f(-196, -196),
                    MathUtils.getRandomNumberInRange(0, 360),
                    MathUtils.getRandomNumberInRange(-1, 1),
                    Color.BLUE,
                    false,
                    5, 3, 2, 1, 2,
                    0f,
                    0.15f,
                    0.65f,
                    CombatEngineLayers.JUST_BELOW_WIDGETS
            );
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "blast_diffuse"),
                    new Vector2f(point),
                    new Vector2f(),
                    new Vector2f(150, 150),
                    new Vector2f(-128, -128),
                    MathUtils.getRandomNumberInRange(0, 360),
                    MathUtils.getRandomNumberInRange(-1, 1),
                    Color.BLUE,
                    false,
                    4, 1, 2, 2, 1,
                    0.2f,
                    0.05f,
                    0.75f,
                    CombatEngineLayers.JUST_BELOW_WIDGETS
            );


            for(int i=0; i<MathUtils.getRandomNumberInRange(4, 10); i++){

                int size = MathUtils.getRandomNumberInRange(25, 140);
                float fade = MathUtils.getRandomNumberInRange(0.15f, 0.5f);
                CombatEngineLayers layer = CombatEngineLayers.JUST_BELOW_WIDGETS;
                if(Math.random()<fade){
                    layer = CombatEngineLayers.BELOW_INDICATORS_LAYER;
                }

                MagicRender.battlespace(
                        Global.getSettings().getSprite("fx","blast_flash"),
                        MathUtils.getRandomPointOnCircumference(
                                point,
                                MathUtils.getRandomNumberInRange(32, 128-size)
                        ),
                        new Vector2f(),
                        new Vector2f(size,size),
                        new Vector2f(-size/fade,-size/fade),
                        MathUtils.getRandomNumberInRange(0, 360),
                        MathUtils.getRandomNumberInRange(-1, 1),
                        new Color(128,24,200,140),
                        false,
                        4,5,0,0,0,
                        0,
                        3*fade/4,
                        fade/4,
                        layer
                );


                Color boop = new Color(MathUtils.getRandomNumberInRange(150, 200), MathUtils.getRandomNumberInRange(0, 20), MathUtils.getRandomNumberInRange(150,200), MathUtils.getRandomNumberInRange(150,255));

                engine.spawnEmpArc(
                        projectile.getSource(),
                        MathUtils.getRandomPointInCircle(
                                target.getLocation(),
                                MathUtils.getRandomNumberInRange(100,500)
                        ),
                        target,
                        target,
                        DamageType.ENERGY, 0f, 500f, 500f, "", 60f,boop, Color.BLACK);

                MagicLensFlare.createSharpFlare(
                        engine,
                        projectile.getSource(),
                        MathUtils.getRandomPointInCircle(target.getLocation(), MathUtils.getRandomNumberInRange(200f, 700f)),
                        MathUtils.getRandomNumberInRange(2f, 50f),
                        MathUtils.getRandomNumberInRange(50f, 400f),
                        MathUtils.getRandomNumberInRange(0f, 180f),
                        boop,
                        boop
                );



            }
        }


        Color boop = new Color(MathUtils.getRandomNumberInRange(150, 200), MathUtils.getRandomNumberInRange(0, 20), MathUtils.getRandomNumberInRange(150,200), MathUtils.getRandomNumberInRange(150,255));

        DamagingExplosionSpec blast = new DamagingExplosionSpec(0.2f,
                200f,
                100f,
                projectile.getDamageAmount() * 30f,
                projectile.getDamageAmount(),
                CollisionClass.PROJECTILE_FF,
                CollisionClass.PROJECTILE_FIGHTER,
                4f,
                4f,
                0.6f,
                40,
                boop,
                boop
        );
        blast.setDamageType(DamageType.ENERGY);
        blast.setShowGraphic(false);

        engine.spawnDamagingExplosion(blast, projectile.getSource(), MathUtils.getRandomPointOnCircumference(
                point,
                MathUtils.getRandomNumberInRange(32, 100)
        ));

        if (!shieldHit && target instanceof ShipAPI ) {
            ((ShipAPI) target).getEngineController().forceFlameout(); //Guarunteed flameout
        }

        //TODO giant boom

        Global.getSoundPlayer().playSound(
                "arc_degeneracy",
                0.6f,
                1.0f,
                point,
                target.getLocation()
        );
    }
}
