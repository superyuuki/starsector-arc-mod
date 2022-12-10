package urs.hullmod;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicTargeting;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PDMatrixHullMod extends BaseHullMod {

    private static final Map<ShipAPI.HullSize,Float> BASE_RANGE_FLAT_BONUS = new HashMap<>();
    private static final Map<ShipAPI.HullSize,Float> THRESHOLD = new HashMap<>();
    static final float CUTOFF = 700f;

    static {
        BASE_RANGE_FLAT_BONUS.put(ShipAPI.HullSize.FRIGATE, 150f);
        BASE_RANGE_FLAT_BONUS.put(ShipAPI.HullSize.DESTROYER, 150f);
        BASE_RANGE_FLAT_BONUS.put(ShipAPI.HullSize.CRUISER, 200f);
        BASE_RANGE_FLAT_BONUS.put(ShipAPI.HullSize.CAPITAL_SHIP, 250f);
    }

    private static final Set<String> BLOCKED_HULLMODS = new HashSet<>(2);

    static {
        BLOCKED_HULLMODS.add("safetyoverrides");
    }


    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().modifyFlat(id, BASE_RANGE_FLAT_BONUS.get(hullSize));
        stats.getWeaponRangeThreshold().modifyFlat(id, CUTOFF);
        stats.getWeaponRangeMultPastThreshold().modifyMult(id, (float) (1 - 0.2));
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getVariant().getHullMods().contains("safetyoverrides"))
            return "Incompatible with Safety Overrides";
        return null;
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        for (String tmp : BLOCKED_HULLMODS) {
            if (ship.getVariant().getHullMods().contains(tmp)) {
                ship.getVariant().removeMod(tmp);
            }
        }

    }

    static final Color[] STYLING = new Color[] { Misc.getGrayColor(), Misc.getPositiveHighlightColor() };

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {

        tooltip.addSectionHeading("Modifies", Alignment.MID, 3f);
        tooltip.addPara("",0f);
        tooltip.setBulletedListMode("  - ");
        tooltip.addPara("Increases range of all ballistic weapons by a flat %s, depending on hull size", 0f, Misc.getPositiveHighlightColor(), "25/50/75/100");
        tooltip.addPara("Further increases range of all URS ballistic weapons by a flat %s, depending on hull size", 0f, Misc.getPositiveHighlightColor(), "25/25/50/50");
        tooltip.addPara("Reduces flux cost of ballistics by %s percent, depending on hull size", 0f, Misc.getPositiveHighlightColor(), "");
        tooltip.addPara("",0f);
    }

}
