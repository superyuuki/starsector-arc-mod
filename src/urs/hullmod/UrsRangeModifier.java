package urs.hullmod;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;

public class UrsRangeModifier implements WeaponBaseRangeModifier {
    
    private final static String MANUFACTURER_NAME = "Union Rail Systems";

    final float rangeModifierNormal;
    final float rangeModifierURS;

    public UrsRangeModifier(float rangeModifierNormal, float rangeModifierURS) {
        this.rangeModifierNormal = rangeModifierNormal;
        this.rangeModifierURS = rangeModifierURS;
    }


    @Override
    public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
        return 0f;
    }

    @Override
    public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
        return 1f;
    }

    @Override
    public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
        float bonus = rangeModifierNormal;
        if (weapon.getSpec().getManufacturer().equals(MANUFACTURER_NAME)) bonus += rangeModifierURS;

        return bonus;
    }



    
}
