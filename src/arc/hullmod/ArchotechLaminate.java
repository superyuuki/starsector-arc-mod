package arc.hullmod;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class ArchotechLaminate extends BaseHullMod {

    static final String ARCHOTECH_LAMINATE = "arc_archotechlaminate";
    static final float SPEED_MALUS = -5f;
    static final float MIN_ARMOR_BONUS = 50f;

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 2f;
        Color h = Misc.getHighlightColor();

        tooltip.addPara("", pad, h, "");

        tooltip.addSectionHeading("Passive Ability - Temporal Rollback", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);

        tooltip.addPara("", pad, h, "");
        tooltip.addPara("Every seconds, when hull is above 25, the ship will spend %s of the hull to replenish %s of all lost armor", pad, h, "a", "a", "a");
        tooltip.addPara("", pad, h, "");

        tooltip.addSectionHeading("Passive Ability - Temporal Adaptation", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);

        tooltip.addPara("", pad, h, "");
        tooltip.addPara("When the ship is by damage above 200, generate a temporal field around the impact and ignore 90 in 2 cells around the impact cell. Can only happen once every second", pad, h, "");

        tooltip.addPara("", pad, h, "");
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().modifyPercent(ARCHOTECH_LAMINATE, SPEED_MALUS);
        stats.getMinArmorFraction().modifyPercent(ARCHOTECH_LAMINATE, MIN_ARMOR_BONUS);


    }
}
