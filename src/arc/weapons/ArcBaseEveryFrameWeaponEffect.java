package arc.weapons;

import arc.Index;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.awt.*;

public abstract class ArcBaseEveryFrameWeaponEffect implements EveryFrameWeaponEffectPlugin {
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        boolean hasBase = weapon.getShip().getVariant().hasHullMod(Index.BASE_HULLMOD);
        boolean hasAux = weapon.getShip().getVariant().hasHullMod(Index.AUX_HULLMOD);

        if (!hasAux && !hasBase) {
            if (!weapon.isDisabled()) {
                weapon.disable(true);
                engine.addFloatingText(
                        weapon.getLocation(),
                        "No Whitespace Core!",
                        20f,
                        Color.RED,
                        weapon.getShip(),
                        1f,
                        1f
                );
            }
        }



    }




}
