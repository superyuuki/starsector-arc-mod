package arc.weapons.alphastar;

import arc.plugin.RunnableQueuePlugin;
import arc.util.ARCUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class AlphastarOnHitEffect implements OnHitEffectPlugin {
    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

        Global.getSoundPlayer().playSound(
                "arc_alphastar_explosion",
                MathUtils.getRandomNumberInRange(0.9f, 1.1f),
                0.6f, point, target.getVelocity());


        CombatEngineAPI combatEngine = Global.getCombatEngine();

        //main
        combatEngine.addSmoothParticle(
                projectile.getLocation(),
                new Vector2f(0.0f, 0.0f),
                400.0f,
                2.0f,
                0.21f,
                Color.WHITE
        );

        combatEngine.addHitParticle(
                projectile.getLocation(),
                new Vector2f(0.0f, 0.0f)
                , 350.0f,
                1.5f,
                1.2f,
                new Color(130,180,230,255)
        );

        combatEngine.addHitParticle(
                projectile.getLocation(),
                new Vector2f(0.0f, 0.0f),
                275.0f,
                1.5f,
                1.2f,
                Color.WHITE
        );


        for (int i = 0; i < MathUtils.getRandomNumberInRange(5, 8); i++) {
            RunnableQueuePlugin.queueTask(() -> {

                Vector2f pt = new Vector2f(
                        point.x + MathUtils.getRandomNumberInRange(-200f, 200f),
                        point.y + MathUtils.getRandomNumberInRange(-200f, 200f)
                );



                ARCUtils.spawnMine(
                        projectile.getSource(),
                        pt,
                        "arc_alphastar_minelayer"
                );
            }, i);

        }

    }
}
