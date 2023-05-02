package arc.hullmod;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import arc.ARCUtils;
import arc.hullmod.whitespace.Venting;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;

//utility core
public class WhitespaceCore extends BaseHullMod {

	static final String TEMPORAL_FLUX = "arc_whitespace_core";
	static final float BASE_TIME_MULT = 1.05f;
	static final float MAX_TIME_MULT = 1.5f;
	public static final float FLUX_LIMIT = 1.0f;
	static final float BASE_VENT_BONUS = 400f;
	static final float MAX_VENT_BONUS = -20f;
	
	static final float AFTERIMAGE_THRESHOLD = 0.4f;


	static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
	{
		BLOCKED_HULLMODS.add("converted_hangar");
		BLOCKED_HULLMODS.add("cargo_expansion");
		BLOCKED_HULLMODS.add("additional_crew_quarters");
		BLOCKED_HULLMODS.add("fuel_expansion");
		BLOCKED_HULLMODS.add("additional_berthing");
		BLOCKED_HULLMODS.add("auxiliary_fuel_tanks");
		BLOCKED_HULLMODS.add("expanded_cargo_holds");
		BLOCKED_HULLMODS.add("surveying_equipment");
		BLOCKED_HULLMODS.add("fluxdistributor");
		BLOCKED_HULLMODS.add("fluxcapacitor");
	}


	final Venting venting = new Venting();

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		for (String tmp : BLOCKED_HULLMODS) {
			Collection<String> hullmods = ship.getVariant().getHullMods();

			if (hullmods.contains(tmp)) {
				MagicIncompatibleHullmods.removeHullmodWithWarning(ship.getVariant(), tmp, TEMPORAL_FLUX);
			}

		}
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return false;
	}
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 2f;

		Color h = Misc.getHighlightColor();
		Color bad= Misc.getNegativeHighlightColor();

		tooltip.addPara("", pad, h, "");
		tooltip.addSectionHeading("Description", Alignment.MID, 0f);
		tooltip.addPara("", pad, h, "");

		tooltip.addPara("Base time flow increased to x%s.", pad, h, "" + (int)BASE_TIME_MULT);
		tooltip.addPara("Time flow at max flux increased to x%s.", pad, h, "" + (int)MAX_TIME_MULT);
		tooltip.addPara("", pad, h, "");

		String incompatible = "any hullmod that requires significant hull changes or modifies the flux conduits";
		tooltip.addPara("Due to how the whitespace core is integrated throughout the ship, it is incompatible with %s.", pad, bad, incompatible);

		tooltip.addPara("", pad, h, "");

		tooltip.addSectionHeading("Passive Ability - Adaptive Subsystems", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);

		tooltip.addPara("", pad, h, "");
		tooltip.addPara("Every %s damage dealt to the shields is converted into %s charge regeneration", pad, h, Misc.getPositiveHighlightColor(), "1000", "0.01");
		tooltip.addPara("", pad, h, "");

		//every second that goes by should add more charges

		//reduce base performance of subsystem by 1 charges, reduce cooldown more

		tooltip.addSectionHeading("Passive Ability - Hyper Vent", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);
		tooltip.addPara("", pad, h, "");
		tooltip.addPara("Base venting speed increased to %s.", pad, h, "" + (int) BASE_VENT_BONUS + "%");
		tooltip.addPara("Venting speed while at max flux decreased to %s.", pad, h, "" + (int)MAX_VENT_BONUS + "%");
		tooltip.addPara("", pad, h, "");
		tooltip.addPara("In practice this lends itself to very aggressive vents, coupled with the Laminate Armor all ARC ships have.", pad, h);
		tooltip.addPara("", pad, h, "");
	}

	@Override
    public void advanceInCombat(ShipAPI ship, float amount) {

		CombatEngineAPI combatEngineAPI = Global.getCombatEngine();

		if (combatEngineAPI.isPaused()) return;
        if (!ship.isAlive()) return;
		if(ship.getFluxTracker().isOverloaded()) return;

		venting.advanceSafely(combatEngineAPI, ship, amount, null);
		boolean player = ship == Global.getCombatEngine().getPlayerShip();
		
		float fluxLevel = ship.getFluxTracker().getFluxLevel();
		float timeMult = ARCUtils.clamp(
				BASE_TIME_MULT,
				MAX_TIME_MULT,
				ARCUtils.remap(
						0f,
						FLUX_LIMIT,
						BASE_TIME_MULT,
						MAX_TIME_MULT,
						fluxLevel
				)
		);

		float ventMult = ARCUtils.clamp(
				MAX_VENT_BONUS,
				BASE_VENT_BONUS,
				ARCUtils.remap(
						0f,
						FLUX_LIMIT,
						BASE_VENT_BONUS,
						MAX_VENT_BONUS,
						fluxLevel
				)
		);


		ship.getMutableStats().getTimeMult().modifyMult(TEMPORAL_FLUX, timeMult); //this is dumb
		if (!ship.getFluxTracker().isVenting()) {
			ship.getMutableStats().getVentRateMult().modifyPercent(TEMPORAL_FLUX, ventMult);
		}


        if (player) {
            if (ship.isAlive()) {
				combatEngineAPI
						.getTimeMult()
						.modifyPercent(TEMPORAL_FLUX, 1f / timeMult);
				combatEngineAPI
						.maintainStatusForPlayerShip(
								TEMPORAL_FLUX,
								"graphics/icons/hullsys/temporal_shell.png",
								"Temporal Flux Core",
								"Timeflow at " + timeMult + "%", false
						);
				combatEngineAPI
						.maintainStatusForPlayerShip(
								TEMPORAL_FLUX + "cum",
								"graphics/icons/hullsys/temporal_shell.png",
								"Temporal Flux Core",
								"Vent rate at " + ship.getMutableStats().getVentRateMult().getModifiedValue() + "%", false
						);
			} else {
				combatEngineAPI.getTimeMult().unmodify(TEMPORAL_FLUX);
			}
            
        }


	}
}
