package urs.hullmod;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import urs.hullmod.URSFactionRangeMod;

public class URSShipHullMod extends BaseHullMod {

    private static final Map<HullSize,Float> BASE_RANGE_FLAT_BONUS = new HashMap<>();
    private static final Map<HullSize,Float> URS_RANGE_FLAT_BONUS = new HashMap<>();
    private static final Map<HullSize,Float> FLUX_REDUCTION_BONUS = new HashMap<>();

    static {
        BASE_RANGE_FLAT_BONUS.put(HullSize.FRIGATE, 25f);
        BASE_RANGE_FLAT_BONUS.put(HullSize.DESTROYER, 50f);
        BASE_RANGE_FLAT_BONUS.put(HullSize.CRUISER, 75f);
        BASE_RANGE_FLAT_BONUS.put(HullSize.CAPITAL_SHIP, 100f);

        URS_RANGE_FLAT_BONUS.put(HullSize.FRIGATE, 25f);
        URS_RANGE_FLAT_BONUS.put(HullSize.DESTROYER, 25f);
        URS_RANGE_FLAT_BONUS.put(HullSize.CRUISER, 50f);
        URS_RANGE_FLAT_BONUS.put(HullSize.CAPITAL_SHIP, 50f);

        FLUX_REDUCTION_BONUS.put(HullSize.FRIGATE, 5f);
        FLUX_REDUCTION_BONUS.put(HullSize.DESTROYER, 5f);
        FLUX_REDUCTION_BONUS.put(HullSize.CRUISER, 10f);
        FLUX_REDUCTION_BONUS.put(HullSize.CAPITAL_SHIP, 10f);
    }


    @Override
    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponFluxCostMod().modifyPercent(id, FLUX_REDUCTION_BONUS.get(hullSize));
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        HullSize key = ship.getHullSize();

        ship.addListener(new URSFactionRangeMod(BASE_RANGE_FLAT_BONUS.get(key), URS_RANGE_FLAT_BONUS.get(key)));
    }

    static final Color[] STYLING = new Color[] { Misc.getGrayColor(), Misc.getPositiveHighlightColor() };

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addSectionHeading("Modifies", Alignment.MID, 3f);
        tooltip.addPara("",0f);
        tooltip.addPara("• Increases range of all ballistic weapons by a flat %s, depending on hull size", 0f, Misc.getPositiveHighlightColor(), "25/50/75/100");
        tooltip.addPara("• Further increases range of all URS ballistic weapons by a flat %s, depending on hull size", 0f, Misc.getPositiveHighlightColor(), "25/25/50/50");
        tooltip.addPara("• Reduces flux cost of ballistics by %s percent, depending on hull size", 0f, Misc.getPositiveHighlightColor(), "");
        tooltip.addPara("",0f);

        String name = ship.getName();
        tooltip.addSectionHeading("Current Ship", Alignment.MID, 3f);
        tooltip.addPara("",0f);
        tooltip.addPara("The %s currently benefits from a total range bonus of %s units on all ballistic weapons", 0f, STYLING, name, BASE_RANGE_FLAT_BONUS.get(hullSize).intValue() + "");
        int combine = BASE_RANGE_FLAT_BONUS.get(hullSize).intValue() + URS_RANGE_FLAT_BONUS.get(hullSize).intValue();
        tooltip.addPara("All URS weapons on the %s have a total range bonus of %s units", 0f, STYLING, name, combine + "");
        tooltip.addPara("All ballistic weapons on the %s have flux costs reduced by %s percent", 0f, STYLING, name, FLUX_REDUCTION_BONUS.get(hullSize) + "");
        tooltip.addPara("",0f);
    }





}
