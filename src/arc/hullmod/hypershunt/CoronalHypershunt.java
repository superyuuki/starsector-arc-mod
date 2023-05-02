package arc.hullmod.hypershunt;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicLensFlare;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.CollisionUtils;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//offensive core
public class CoronalHypershunt extends BaseHullMod {


	static {
		HashMap<HullSize, Float> map = new LinkedHashMap<>();

		map.put(HullSize.FRIGATE, -40f);
		map.put(HullSize.DESTROYER, -30f);
		map.put(HullSize.CRUISER, -25f);
		map.put(HullSize.CAPITAL_SHIP, -10f);

		FLUX_REDUCTION = map;
	}
	static final String CORONAL_HYPERSHUNT = "arc_hypershuntpinhole";
	static final Map<HullSize,Float> FLUX_REDUCTION;
	static final float OVERLOAD_TIME = 90f;
	static final float UNFOLD_RATE = 2f;
	static final float ARC_MALUS = -25f;


	static String fluxReduction() {
		StringBuilder compound = new StringBuilder();

		for (Map.Entry<HullSize, Float> entry : FLUX_REDUCTION.entrySet()) {
			compound.append(Math.abs(entry.getValue())).append("%").append("/");
		}

		return compound.toString();
	}

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getEnergyWeaponFluxCostMod().modifyPercent(id, FLUX_REDUCTION.get(hullSize));
		stats.getBallisticWeaponFluxCostMod().modifyPercent(id, FLUX_REDUCTION.get(hullSize));
		stats.getShieldUnfoldRateMult().modifyMult(id, UNFOLD_RATE);
		stats.getShieldArcBonus().modifyPercent(id, ARC_MALUS);
		stats.getOverloadTimeMod().modifyPercent(id, OVERLOAD_TIME);

	}


	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		ship.setOverloadColor(Color.RED);
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return false;
	}



	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 2f;

		Color h = Misc.getHighlightColor();
		Color good= Misc.getPositiveHighlightColor();

		tooltip.addPara("", pad, h, "");
		tooltip.addSectionHeading("Description", Alignment.MID, 0f);


		tooltip.addPara("", pad, h, "");
		tooltip.addPara("The abundance of entropic energy provided by the hypershunt gives %s weapons %s reduced flux costs, depending on hullsize", pad, good, "Ballistic and Energy", fluxReduction());
		tooltip.addPara("", pad, h, "");

		tooltip.addSectionHeading("Passive Ability - Burst Surger", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);

		tooltip.addPara("", pad, h, "");
		tooltip.addPara("Weapon ammo regeneration in Whitespace is %s", pad, good, "doubled");
		tooltip.addPara("Shield deployment is %s", pad, good, "extremely fast");
		tooltip.addPara("Shields are %s", pad, Misc.getNegativeHighlightColor(), Math.abs(ARC_MALUS) + "% smaller");
		tooltip.addPara("", pad, h, "");



		tooltip.addSectionHeading("Passive Ability - Hypershunt Ejection", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);

		tooltip.addPara("", pad, h, "");
		tooltip.addPara("On low hull, the coronal hypershunt will destabilize and overload all ships in a small radius.", pad, h);
		tooltip.addPara("Ships will enter an %s and gain increased mobility at the cost of weapons power, leading them to occasionally disable.", pad, good, "Evacuation Mode");
		tooltip.addPara("Ships above %s size will also temporarily generate a hypershunt field for the duration of the destabilization, annihilating nearby craft.", pad, h, "Destroyer");
		tooltip.addPara("", pad, h, "");


	}


	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {

		CombatEngineAPI combat = Global.getCombatEngine();

		if (combat.isPaused()) return;

		if (ship.isPhased()) {
			ship.getMutableStats().getEnergyAmmoRegenMult().modifyMult(CORONAL_HYPERSHUNT, 2);
		} else {
			ship.getMutableStats().getEnergyAmmoRegenMult().unmodify(CORONAL_HYPERSHUNT);
		}





	}
}
