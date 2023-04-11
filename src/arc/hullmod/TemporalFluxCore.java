package arc.hullmod;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import arc.ArcUtils;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicIncompatibleHullmods;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Vector2f;

public class TemporalFluxCore extends BaseHullMod {

	static final String TEMPORAL_FLUX = "arc_temporal_flux";
	static final float BASE_TIME_MULT = 1.05f;
	static final float MAX_TIME_MULT = 1.5f;
	static final float FLUX_LIMIT = 1.0f;
	static final float BASE_VENT_BONUS = 200f;
	static final float MAX_VENT_BONUS = -50f;
	
	static final float AFTERIMAGE_THRESHOLD = 0.4f;


	static final Set<String> BLOCKED_HULLMODS = new HashSet<>();
	{
		// These hullmods will automatically be removed
		// This prevents unexplained hullmod blocking
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
		tooltip.addPara("Base time multiplier increased to x%s.", pad, h, "" + (int)BASE_TIME_MULT);
		tooltip.addPara("Time multiplier at max flux increased to x%s.", pad, h, "" + (int)MAX_TIME_MULT);
		tooltip.addPara("", pad, h, "");
		tooltip.addPara("Base venting speed increased to %s.", pad, h, "" + (int) BASE_VENT_BONUS + "%");
		tooltip.addPara("Venting speed while at max flux decreased to %s.", pad, h, "" + (int)MAX_VENT_BONUS + "%");
		tooltip.addPara("", pad, h, "");
		String incompatible = "any hullmod that requires significant hull changes or modifies the flux conduits";
		tooltip.addPara("Due to how the flux core is integrated throughout the ship, it is incompatible with %s.", pad, bad, incompatible);
	}

	@Override
    public void advanceInCombat(ShipAPI ship, float amount) {

		CombatEngineAPI combatEngineAPI = Global.getCombatEngine();

		if (combatEngineAPI.isPaused()) return;
        if (!ship.isAlive()) return;
		if(ship.getFluxTracker().isOverloaded()) return;
		boolean player = ship == Global.getCombatEngine().getPlayerShip();
		
		float fluxLevel = ship.getFluxTracker().getFluxLevel();
		float timeMult = ArcUtils.clamp(
				BASE_TIME_MULT,
				MAX_TIME_MULT,
				ArcUtils.remap(
						0f,
						FLUX_LIMIT,
						BASE_TIME_MULT,
						MAX_TIME_MULT,
						fluxLevel
				)
		);

		float ventMult = ArcUtils.clamp(
				MAX_VENT_BONUS,
				BASE_VENT_BONUS,
				ArcUtils.remap(
						0f,
						FLUX_LIMIT,
						BASE_VENT_BONUS,
						MAX_VENT_BONUS,
						fluxLevel
				)
		);


		ship.getMutableStats().getTimeMult().modifyMult(TEMPORAL_FLUX, timeMult); //this is dumb
		ship.getMutableStats().getVentRateMult().modifyPercent(TEMPORAL_FLUX, ventMult);

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

		if (fluxLevel > AFTERIMAGE_THRESHOLD) {





		}			
	}
}
