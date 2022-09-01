package unionrailsystem;

import java.util.List;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class URSShipHullMod extends BaseHullMod {

    private final float WEAPON_RANGE_FLAT_MODIFIER = 100f;
    private final float WEAPON_PERCENT_MODIFIER = 20f;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        ship.addListener(new URSFactionDamageMod(WEAPON_PERCENT_MODIFIER));
        ship.addListener(new URSFactionRangeMod(WEAPON_RANGE_FLAT_MODIFIER));
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }

    @Override
    public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + (int) WEAPON_RANGE_FLAT_MODIFIER;     
        if (index == 1) return "" + (int) WEAPON_PERCENT_MODIFIER + "%";
        return null; 
            
        
    }

}
