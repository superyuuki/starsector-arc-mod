package arc.weapons;

import arc.Index;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.awt.*;

public abstract class ArcBaseEveryFrameWeaponEffect implements EveryFrameWeaponEffectPlugin {
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (!weapon.getShip().getVariant().hasHullMod(Index.ARC_BASE_HULLMOD)) {
            weapon.disable(true);
            engine.addFloatingText(
                    weapon.getLocation(),
                    "No Whitespace Core!",
                    5f,
                    Color.RED,
                    weapon.getShip(),
                    1f,
                    1f
            );
        } else {
            advanceSub(amount, engine, weapon);
        }


    }

    protected abstract void advanceSub(float amount, CombatEngineAPI engine, WeaponAPI weaponAPI);


}
