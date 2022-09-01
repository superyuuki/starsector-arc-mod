package unionrailsystem;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class URSShipHullMod extends BaseHullMod {

    private final float WEAPON_PERCENT_MODIFIER = 20f;

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        ship.addListener(new URSFactionDamageMod(WEAPON_PERCENT_MODIFIER));
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return false;
    }

    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {


    }

}
