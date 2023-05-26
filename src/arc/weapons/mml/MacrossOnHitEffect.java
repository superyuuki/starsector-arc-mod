package arc.weapons.mml;

import arc.util.ARCUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;

public class MacrossOnHitEffect implements OnHitEffectPlugin {
    static Color color = new Color(100, 50, 210, 255);

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        float chance = 0;
        float damage = 0;
        float baseMult = 0;

        if (target instanceof ShipAPI) {
            baseMult = ARCUtils.decideBasedOnHullSize(
                    (ShipAPI) target,
                    0.1f,
                    0.2f,
                    0.4f,
                    1f
            );

            chance = ARCUtils.decideBasedOnHullSize(
                    (ShipAPI)target,
                    1f,
                    2f,
                    3f,
                    4f
            );

            damage = ARCUtils.decideBasedOnHullSize(
                    (ShipAPI) target,
                    3,
                    5,
                    10,
                    15
            );


        } else {
            baseMult = 0.2f;
            chance = 3f;
            damage = 4f;
        }

        projectile.setDamageAmount(projectile.getDamageAmount() * baseMult);

        if (MathUtils.getRandomNumberInRange(0, 100) > chance) return;

        if(MagicRender.screenCheck(0.25f, projectile.getLocation())) {
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "blast_flash"),
                    new Vector2f(point),
                    new Vector2f(),
                    new Vector2f(120, 120),
                    new Vector2f(-384, -384),
                    MathUtils.getRandomNumberInRange(0, 360),
                    MathUtils.getRandomNumberInRange(-1, 1),
                    Color.WHITE,
                    false,
                    0, 0, 0, 0, 0,
                    0,
                    0.1f,
                    0.6f,
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
                    color,
                    false,
                    5, 3, 2, 1, 2,
                    0f,
                    0.15f,
                    1.45f,
                    CombatEngineLayers.JUST_BELOW_WIDGETS
            );
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "blast_diffuse"),
                    new Vector2f(point),
                    new Vector2f(),
                    new Vector2f(150, 150),
                    new Vector2f(350, 350),
                    MathUtils.getRandomNumberInRange(0, 360),
                    MathUtils.getRandomNumberInRange(-1, 1),
                    color,
                    false,
                    4, 1, 2, 2, 1,
                    0.2f,
                    0.05f,
                    0.9f,
                    CombatEngineLayers.JUST_BELOW_WIDGETS
            );

            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "blast_outer"),
                    new Vector2f(point),
                    new Vector2f(),
                    new Vector2f(200, 200),
                    new Vector2f(600, 600),
                    MathUtils.getRandomNumberInRange(0, 360),
                    MathUtils.getRandomNumberInRange(-1, 1),
                    color,
                    false,
                    4, 1, 2, 2, 1,
                    0.1f,
                    0.4f,
                    1.9f,
                    CombatEngineLayers.JUST_BELOW_WIDGETS
            );


            for(int i=0; i<MathUtils.getRandomNumberInRange(4, 2 + chance); i++){


                Color boop = new Color(MathUtils.getRandomNumberInRange(150, 200), MathUtils.getRandomNumberInRange(0, 20), MathUtils.getRandomNumberInRange(150,200), MathUtils.getRandomNumberInRange(150,255));

                engine.spawnEmpArc(
                        projectile.getSource(),
                        MathUtils.getRandomPointInCircle(
                                target.getLocation(),
                                MathUtils.getRandomNumberInRange(100,500)
                        ),
                        target,
                        target,
                        DamageType.ENERGY, 0f, projectile.getDamageAmount() * damage / 4, 500f, "", 60f,boop, Color.BLACK);

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
                projectile.getDamageAmount() * damage,
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
