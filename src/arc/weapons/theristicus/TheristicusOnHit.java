package arc.weapons.theristicus;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

import com.fs.starfarer.api.impl.combat.DisintegratorEffect;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class TheristicusOnHit implements OnHitEffectPlugin {

    final float penMulti = 300;


    public void dealArmorDamage(DamagingProjectileAPI projectile, ShipAPI target, Vector2f point) {
        CombatEngineAPI engine = Global.getCombatEngine();

        ArmorGridAPI grid = target.getArmorGrid();
        int[] cell = grid.getCellAtLocation(point);
        if (cell == null) return;

        int gridWidth = grid.getGrid().length;
        int gridHeight = grid.getGrid()[0].length;

        float damageTypeMult = DisintegratorEffect.getDamageTypeMult(projectile.getSource(), target);
        float damage = (projectile.getDamageAmount() / 3f) * damageTypeMult;

        float totalArmor = 0f;
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if ((i == 2 || i == -2) && (j == 2 || j == -2)) continue; // skip corners

                int cx = cell[0] + i;
                int cy = cell[1] + j;

                if (cx < 0 || cx >= gridWidth || cy < 0 || cy >= gridHeight) continue;

                float mult = 0.5f;
                if ((i <= 1 && i >= -1 && j <= 1 && j >= -1)) { // S hits
                    mult = 1f;
                }

                float armorInCell = grid.getArmorValue(cx, cy) * mult;
                totalArmor += armorInCell;
            }
        }
        float maxRed = target.getMutableStats().getMaxArmorDamageReduction().getModifiedValue();
        float damageMult = Math.max(((damage * penMulti) / (totalArmor + damage * penMulti)), 1 - maxRed);
        float damageDealt = 0f;
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if ((i == 2 || i == -2) && (j == 2 || j == -2)) continue; // skip corners

                int cx = cell[0] + i;
                int cy = cell[1] + j;

                if (cx < 0 || cx >= gridWidth || cy < 0 || cy >= gridHeight) continue;

                float damMult = 1/30f;
                if (i == 0 && j == 0) {
                    damMult = 1/15f;
                } else if (i <= 1 && i >= -1 && j <= 1 && j >= -1) { // S hits
                    damMult = 1/15f;
                }

                float armorInCell = grid.getArmorValue(cx, cy);
                float currDamage = damage * damMult * damageMult;
                currDamage = Math.min(currDamage, armorInCell);
                if (damage <= 0) continue;

                target.getArmorGrid().setArmorValue(cx, cy, Math.max(0, armorInCell - currDamage));
                damageDealt += currDamage;
            }
        }

        if (damageDealt > 0) {
            if (Misc.shouldShowDamageFloaty(projectile.getSource(), target)) {
                engine.addFloatingDamageText(point, damageDealt, new Color(100,0,200,50), target, projectile.getSource());
            }
            target.syncWithArmorGridState();
        }
    }



    @Override
    public void onHit(DamagingProjectileAPI damagingProjectileAPI, CombatEntityAPI combatEntityAPI, Vector2f vector2f, boolean b, ApplyDamageResultAPI applyDamageResultAPI, CombatEngineAPI combatEngineAPI) {
        //pop after 10 shots


        if (!b && combatEntityAPI instanceof ShipAPI && ((ShipAPI) combatEntityAPI).isFighter()) {


            applyDamageResultAPI.setType(DamageType.HIGH_EXPLOSIVE);
            dealArmorDamage(damagingProjectileAPI, (ShipAPI) combatEntityAPI, vector2f);


        }




    }
}
