package arc.weapons.alphastar;

import arc.weapons.ArcBurstExplosion;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class AlphastarBurstExplosion extends ArcBurstExplosion {
    public AlphastarBurstExplosion() {
        super(new Color(130,180,230,255), 400f);
    }

    @Override
    public void onExplosion(DamagingProjectileAPI damagingProjectileAPI, DamagingProjectileAPI damagingProjectileAPI2) {
        super.onExplosion(damagingProjectileAPI, damagingProjectileAPI2);
        Global.getSoundPlayer().playSound(
                "arc_alphastar_explosion_little",
                MathUtils.getRandomNumberInRange(0.8f, 1.2f),
                0.6f, damagingProjectileAPI.getSpawnLocation(), new Vector2f());
    }
}
