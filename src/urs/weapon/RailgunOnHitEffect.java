package urs.weapon;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicTargeting;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class RailgunOnHitEffect implements OnHitEffectPlugin {

    private static final Color EXPLOSION_COLOR = new Color(100f / 255f, 205f / 255f, 255f / 255f, 130f / 255f);
    private static final float EXPLOSION_SIZE = 110f;
    private static final float EXPLOSION_DURATION = 0.5f;

    @Override
    public void onHit(DamagingProjectileAPI damagingProjectileAPI, CombatEntityAPI combatEntityAPI, Vector2f vector2f, boolean b, ApplyDamageResultAPI applyDamageResultAPI, CombatEngineAPI combatEngineAPI) {
        Global.getCombatEngine().spawnExplosion(vector2f, new Vector2f(0f, 0f), EXPLOSION_COLOR, EXPLOSION_SIZE, EXPLOSION_DURATION);
        MagicLensFlare.createSharpFlare(combatEngineAPI, damagingProjectileAPI.getSource(), damagingProjectileAPI.getLocation(), 5, 350, 0, new Color(186, 240, 255), new Color(255, 255, 255));
    }
}
