package arc.hullmod;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicLensFlare;
import org.lazywizard.lazylib.combat.CombatUtils;

import java.awt.*;

public class CoronalHypershunt extends BaseHullMod {
	private static final float FLUX_CAPACITY = 20f;
	private static final float FLUX_DISSIPATION = 5f;
	private static final float OVERLOAD_TIME = 70f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION);
		stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY);
		stats.getOverloadTimeMod().modifyPercent(id, OVERLOAD_TIME);
	}

	int fuckedLevel = 0;

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return false;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 2f;
		float opad = 10f;

		Color h = Misc.getHighlightColor();
		Color drk = Misc.getDarkHighlightColor();
		Color bad= Misc.getNegativeHighlightColor();

		tooltip.addSectionHeading("Description", Alignment.MID, 0f);


		tooltip.addPara("", pad, h, "");
		LabelAPI label = tooltip.addPara("Flux capacity increased by %s.", pad, h, "" + (int)FLUX_CAPACITY + "%");
		label.setHighlight("" + (int)FLUX_CAPACITY + "%");
		label.setHighlightColors(h);
		tooltip.addPara("", pad, h, "");
		label = tooltip.addPara("Flux dissipation increased by %s.", pad, h, "" + (int)FLUX_DISSIPATION + "%");
		label.setHighlight("" + (int)FLUX_DISSIPATION + "%");
		label.setHighlightColors(h);
		tooltip.addPara("", pad, h, "");
		label = tooltip.addPara("Overload time increased by %s.", pad, h, "" + (int)OVERLOAD_TIME + "%");
		label.setHighlight("" + (int)OVERLOAD_TIME + "%");
		label.setHighlightColors(bad);
		tooltip.addPara("", pad, h, "");

		tooltip.addSectionHeading("Abilities", Misc.getPositiveHighlightColor(), Misc.getGrayColor(), Alignment.MID, 0f);

		tooltip.addPara("adds sex", 0);
	}

	boolean inFuckedMode = false;

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {

		if (true) return;

		if (Global.getCombatEngine().isPaused()) return;


		if (ship.getFluxLevel() > 0.95f && ship.getFluxLevel() < 1f && !inFuckedMode) inFuckedMode = true;


		if (inFuckedMode) {
			if (fuckedLevel < 200) {
				fuckedLevel++;

				ship.setJitterShields(true);
				ship.setJitterUnder(ship, Color.RED,  10,10,10);


				MagicLensFlare.createSharpFlare(
						Global.getCombatEngine(),
						ship,
						ship.getLocation(),
						5,
						1000,
						49,
						Color.RED,
						Color.WHITE
				);

				ship.getEngineController().forceFlameout();

				for (ShipAPI shipAPI : CombatUtils.getShipsWithinRange(ship.getLocation(), 1000f)) {

					DamagingExplosionSpec blast = new DamagingExplosionSpec(0.2f,
							160f,
							90f,
							500,
							500 * 0.2f,
							CollisionClass.PROJECTILE_FF,
							CollisionClass.PROJECTILE_FIGHTER,
							4f,
							4f,
							0.6f,
							40,
							new Color(255,0,0),
							new Color(0,0,0,20));
					blast.setDamageType(DamageType.ENERGY);
					blast.setShowGraphic(true);
					blast.setDetailedExplosionFlashColorCore(new Color(255,0,0));
					blast.setDetailedExplosionFlashColorFringe(new Color(0,0,0,20));
					blast.setUseDetailedExplosion(true);
					blast.setDetailedExplosionRadius(140f);
					blast.setDetailedExplosionFlashRadius(190f);
					blast.setDetailedExplosionFlashDuration(0.4f);
					Global.getCombatEngine().spawnDamagingExplosion(

							blast,
							ship,
							shipAPI.getLocation()

					);
				}



				//TODO shunt
			} else {
				inFuckedMode = false;
			}
		}


	}
}
