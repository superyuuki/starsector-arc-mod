package unionrailsystem;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;

public class URSFactionRangeMod implements WeaponBaseRangeModifier {
    
    private final String MANUFACTURER_NAME = "Union Rail Systems";
    private final float WEAPON_RANGE_FLAT_MODIFIER;

    public URSFactionRangeMod(float weaponRangeFlatModifier){
        WEAPON_RANGE_FLAT_MODIFIER = weaponRangeFlatModifier;
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
        if (!(weapon.getSpec().getManufacturer().equals(MANUFACTURER_NAME))) {
            return 0f;
        }
        
        return WEAPON_RANGE_FLAT_MODIFIER;
    }

    
}
