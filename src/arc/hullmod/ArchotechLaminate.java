package arc.hullmod;

import arc.hullmod.laminate.WhitespaceRegen;
import arc.hullmod.laminate.VentingArmor;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

//defensive core
public class ArchotechLaminate extends BaseHullMod {

    static final String ARCHOTECH_LAMINATE = "arc_archotechlaminate";
    static final float SPEED_MALUS = -7f;
    static final float EMP_RES_BONUS = -80f;

    public static final float MIN_FLUX_LEVEL = 0.05f;
    public static final float MAX_FLUX_LEVEL = 0.7f;
    public static final float MIN_DAMAGE_REDUCTION = 0.95f;
    public static final float MAX_DAMAGE_REDUCTION = 0.2f;

    final VentingArmor armor = new VentingArmor();

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 2f;
        Color h = Misc.getHighlightColor();


        tooltip.addPara("", pad, h, "");
        tooltip.addSectionHeading("Description", Alignment.MID, 0f);
        tooltip.addPara("", pad, h, "");

        tooltip.addPara("The adaptive nature of the armor reduces EMP damage by %s", pad, Misc.getPositiveHighlightColor(), (int) Math.abs(EMP_RES_BONUS) + "%");
        tooltip.addPara("The armor's weight reduces speed by %s", pad, Misc.getNegativeHighlightColor(), (int)SPEED_MALUS + "%");


        tooltip.addPara("", pad, h, "");
        tooltip.addSectionHeading("Passive Ability - Ceramite Adaptation", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);
        tooltip.addPara("", pad, h, "");
        tooltip.addPara("After damage is dealt to the hull/armor, special effects occur as a result of the chromatic armor plating", pad);
        tooltip.addPara("", pad, h, "");
        tooltip.addPara("High Explosive and Energy damage reduces future High Explosive or Energy damage by %s of the damage dealt. Capped at %s armor", pad, Misc.getPositiveHighlightColor(), "140%", ship.getArmorGrid().getArmorRating() * 1.7f + "");
        tooltip.addPara("Kinetic and Fragmentation dealt to the armor/hull is converted into %s armor in a 5 cell radius", pad, Misc.getPositiveHighlightColor(), "140%");
        tooltip.addPara("", pad, h, "");

        tooltip.addSectionHeading("Passive Ability - Whitespace Affinity", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);


        tooltip.addPara("", pad, h, "");
        tooltip.addPara("When in Whitespace and under %s hardflux, regenerate %s of armor.", pad, h, Misc.getPositiveHighlightColor(), "40%", WhitespaceRegen.armorRegen());
        tooltip.addPara("Overloading will strip %s of armor", pad, Misc.getNegativeHighlightColor(), "70%");
        tooltip.addPara("", pad, h, "");
        tooltip.addPara("", pad, h, "");

        tooltip.addSectionHeading("Passive Ability - Chroma Field", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);

        tooltip.addPara("", pad, h, "");
        tooltip.addPara("When the ship is venting, generate a damper that reduces damage based on the flux vented and lingers after venting", pad, h, "");
        tooltip.addPara("When venting %s of total flux capacity, reduce damage by %s", pad, Misc.getPositiveHighlightColor(), (int)(MIN_FLUX_LEVEL * 100f) + "%", (int)(MIN_DAMAGE_REDUCTION * 100f) + "%");
        tooltip.addPara("Above %s of total flux capacity venting will do nothing", pad, Misc.getGrayColor(), ""  + (int)MAX_FLUX_LEVEL * 100);


    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().modifyPercent(ARCHOTECH_LAMINATE, SPEED_MALUS);
        stats.getEmpDamageTakenMult().modifyPercent(ARCHOTECH_LAMINATE, EMP_RES_BONUS);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        if (!ship.hasListenerOfClass(WhitespaceRegen.class)) {
            ship.addListener(new WhitespaceRegen());
        }

    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        if (Global.getCombatEngine().isPaused()) return;
        if (!ship.isAlive()) return;

        VentingArmor.Data data = (VentingArmor.Data) ship.getCustomData().get(VentingArmor.VENTING_ARMOR);

        if (data == null) {
            data = new VentingArmor.Data();
        }

        armor.advanceSafely(Global.getCombatEngine(), ship, amount, data);
        ship.setCustomData(VentingArmor.VENTING_ARMOR, data);


    }
}