package arc.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

public class ArcEveryFrameWeaponEffect extends ArcBaseEveryFrameWeaponEffect{
    @Override
    protected void advanceSub(float amount, CombatEngineAPI engine, WeaponAPI weaponAPI) {
        //noops
    }
}
