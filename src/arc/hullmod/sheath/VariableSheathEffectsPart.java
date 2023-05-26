package arc.hullmod.sheath;

import arc.hullmod.ARCData;
import arc.hullmod.IHullmodPart;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipwideAIFlags;
import com.fs.starfarer.api.combat.WeaponAPI;

import java.awt.*;
import java.util.EnumSet;

public class VariableSheathEffectsPart implements IHullmodPart<ARCData> {

    static final String SHEATH_GENERAL = "arc_sheath_general";
    static final Color AIMBOT = new Color(100, 140, 255, 250);

    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, ARCData customData) {

        // modes

        switch (customData.mode) {

            case SUPPRESSION:
                shipAPI.getAIFlags().setFlag(ShipwideAIFlags.AIFlags.BACK_OFF_MIN_RANGE);
                shipAPI.setWeaponGlow(2f, AIMBOT, EnumSet.of(WeaponAPI.WeaponType.BALLISTIC, WeaponAPI.WeaponType.ENERGY));

                break;
            case GLIDE:



                break;
            case BERSERK:
                break;
        }




    }

    @Override
    public ARCData makeNew() {
        return new ARCData();
    }

    @Override
    public String makeKey() {
        return SHEATH_GENERAL;
    }
}
