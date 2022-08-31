package unionrailsystem;

import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.DamageDealtModifier;

public class URSFactionDamageMod implements DamageDealtModifier {

    private final String MANUFACTURER_NAME = "Union Rail Systems";

    @Override
    public String modifyDamageDealt(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point,
            boolean shieldHit) {
        if (param == null) {
            return null;
        }

        if (!(param instanceof DamagingProjectileAPI)) {
            return null;
        }

        DamagingProjectileAPI projectile = (DamagingProjectileAPI) param;

        ShipAPI owner = projectile.getSource();
        WeaponAPI weapon = projectile.getWeapon();

        if (owner.getHullSpec().getManufacturer().equals(MANUFACTURER_NAME)) {
            return null;
        }

        if (!(weapon.getSpec().getManufacturer().equals(MANUFACTURER_NAME))) {
            return null;
        }

        MutableStat currentDamage = damage.getModifier();

        String id = this.getClass().getSimpleName();
        currentDamage.modifyFlat(id, 1000f, "Passive Bonus applied for URS Ships");

        return id;
    }

}
