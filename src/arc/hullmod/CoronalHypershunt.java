package arc.hullmod;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
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
import java.util.Random;

public class CoronalHypershunt extends BaseHullMod {

	static final String CORONAL_HYPERSHUNT = "arc_hypershuntpinhole";
	static final float FLUX_CAPACITY = 25f;
	static final float FLUX_DISSIPATION = 10f;
	static final float OVERLOAD_TIME = 90f;
	static final float HULL_FUCK_SPEED = 25f;

	static final float HULL_FUCK_DAMAGE = 150f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION);
		stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY);
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
		Color bad= Misc.getNegativeHighlightColor();

		tooltip.addPara("", pad, h, "");
		tooltip.addSectionHeading("Description", Alignment.MID, 0f);


		tooltip.addPara("", pad, h, "");
		tooltip.addPara("Flux capacity increased by %s.", pad, h, "" + (int)FLUX_CAPACITY + "%");
		tooltip.addPara("Flux dissipation increased by %s.", pad, h, "" + (int)FLUX_DISSIPATION + "%");
		tooltip.addPara("", pad, h, "");

		tooltip.addSectionHeading("Passive Ability - Pinhole Dilation", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);

		tooltip.addPara("", pad, h, "");
		tooltip.addPara("Every %s seconds the hypershunt pinhole will dilate, providing a temporary boost to speed", pad, h, 2 + "");
		tooltip.addPara("", pad, h, "");

		tooltip.addSectionHeading("Passive Ability - Hypershunt Ejection", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);

		tooltip.addPara("", pad, h, "");
		tooltip.addPara("On low hull, the coronal hypershunt will destabilize, eject, and overload all ships in a radius of %s, then boost speed by a flat %s", pad, h, "" + (int)FLUX_DISSIPATION + "%", "" + HULL_FUCK_SPEED);
		tooltip.addPara("", pad, h, "");

		tooltip.addPara("Damage taken will increase by %s.", pad, h, "" + (int)HULL_FUCK_DAMAGE + "%");
		tooltip.addPara("Damage output for energy weapons will increase by %s.", pad, h, "" + (int)HULL_FUCK_DAMAGE + "%");

		tooltip.addPara("", pad, h, "");


	}

	final IntervalUtil PULSE = new IntervalUtil(0.03f,0.03f);
	final IntervalUtil EMP = new IntervalUtil(0.5f,0.5f);


	static class Data {
		boolean inFuckedMode = false;
		int fuckedLevel = 0;
		int overloadCounter = 0;
		boolean berserkMode = false;
		int berserkerFlow = 0;
	}

	static double coef(double mass) {;
		double k = 0.02;
		double m1 = 100;
		double m2 = 2000;
		return (1 / (1 + Math.exp(-k*(mass-m1)))) * 1.5 + 0.5 / (1 + Math.exp(k*(mass-m2)));
	}


	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {

		CombatEngineAPI combat = Global.getCombatEngine();

		if (combat.isPaused()) return;
		PULSE.advance(amount);
		EMP.advance(amount);
		if (!PULSE.intervalElapsed()) return;
		//init data storage
		Data info = (Data) Global.getCombatEngine().getCustomData().get(CORONAL_HYPERSHUNT + ship.getId());
		if (info == null) {
			info = new Data();
		}

		float left = ship.getHitpoints() / ship.getMaxHitpoints();

		if (info.berserkMode) {
			if (EMP.intervalElapsed()) {
				combat.spawnEmpArc(ship, ship.getLocation(), ship, ship,
						DamageType.OTHER,
						0, // Damage
						100, // Emp damage
						40f, // Max range
						"tachyon_lance_emp_impact", // Impact sound
						40f, // Width of the arc
						new Color(255, 100, 255, 255), // Arc color
						new Color(255, 150, 255, 255)); // Fringe color
			}


			ship.setJitterShields(false);
			ship.setCircularJitter(false);
			ship.getEngineController().extendFlame(ship, 6f, 0f, 0f);
		}

		if (left < 0.4f &&  !info.inFuckedMode) info.inFuckedMode = true;
		if (!info.inFuckedMode) return;

		if (info.fuckedLevel == 0) {
			info.overloadCounter++;

			Vector2f textLocation = CollisionUtils.getCollisionPoint(
					ship.getLocation(),
					new Vector2f(ship.getLocation().x, ship.getLocation().y + ship.getCollisionRadius() + 50),
					ship);

			combat.addFloatingText(
					textLocation,
					"Hypershunt Destabilized!",
					200f,
					Color.RED,
					ship,
					30f,
					30f
			);

			Global.getSoundPlayer().playSound("arc_hypershunt_explode", 1.0f, 2.0f, ship.getLocation(), ship.getVelocity());
		}



		if (info.fuckedLevel < 300) {

			info.fuckedLevel++;

			ship.setJitterShields(true);
			ship.setJitterUnder(ship, Color.RED,  10,10,10);

			//NYOOOOOM
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



			if (info.fuckedLevel == 1) {
				final RippleDistortion ripple = new RippleDistortion(ship.getLocation(), new Vector2f());
				ripple.setSize(1000f);
				ripple.setIntensity(120f);
				ripple.setFrameRate(60.0f);
				ripple.fadeInSize(0.3f);
				ripple.fadeOutIntensity(120f);
				ripple.flip(false);
				DistortionShader.addDistortion(ripple);
			}

			if (info.fuckedLevel > 0 && info.fuckedLevel < 20) {
				//Yoink all ships once
				for (CombatEntityAPI smallVehicle : CombatUtils.getEntitiesWithinRange(ship.getLocation(), 3000f)) {
					if (ship.getOwner() == smallVehicle.getOwner()) continue;

					if (smallVehicle instanceof ShipAPI) {
						((ShipAPI)smallVehicle).getFluxTracker().forceOverload(40f);
					}


					CombatUtils.applyForce(
							smallVehicle,
							new Vector2f(2, 2),
							500f
					);
				}

			}


			float blastDamage = (float) (ship.getMass() * 1/coef(ship.getMass()));

			DamagingExplosionSpec blast = new DamagingExplosionSpec(0.1f,
					450f,
					200f,
					blastDamage,
					blastDamage,
					CollisionClass.PROJECTILE_FF,
					CollisionClass.PROJECTILE_FIGHTER,
					4f,
					4f,
					0.6f,
					40,
					new Color(255,100,0),
					new Color(255,100,0,20));
			blast.setDamageType(DamageType.FRAGMENTATION);
			blast.setShowGraphic(true);
			blast.setDetailedExplosionFlashColorCore(new Color(255,100,0));
			blast.setDetailedExplosionFlashColorFringe(new Color(255,100,0,20));
			blast.setUseDetailedExplosion(true);
			blast.setDetailedExplosionRadius(600f);
			blast.setDetailedExplosionFlashRadius(700f);
			blast.setDetailedExplosionFlashDuration(0.4f);


			Vector2f blastPoint = MathUtils.getRandomPointInCircle(ship.getLocation(), (float) (2000 / coef(ship.getMass())));

			final RippleDistortion ripple = new RippleDistortion(blastPoint, new Vector2f());
			ripple.setSize(200f);
			ripple.setIntensity(10f);
			ripple.setFrameRate(60.0f);
			ripple.fadeInSize(0.3f);
			ripple.fadeOutIntensity(10f);
			ripple.flip(true);
			DistortionShader.addDistortion(ripple);

			combat.spawnDamagingExplosion(blast, ship, blastPoint, false);

		} else {
			//berserker mode on!
			ship.getMutableStats().getMaxSpeed().modifyPercent(CORONAL_HYPERSHUNT, 50f);
			ship.getMutableStats().getAcceleration().modifyPercent(CORONAL_HYPERSHUNT, 50f);
			ship.getMutableStats().getTurnAcceleration().modifyPercent(CORONAL_HYPERSHUNT, 75f);
			ship.getMutableStats().getProjectileDamageTakenMult().modifyPercent(CORONAL_HYPERSHUNT, 50);
			ship.getMutableStats().getEnergyWeaponDamageMult().modifyPercent(CORONAL_HYPERSHUNT, 50);

			info.inFuckedMode = false;
			info.berserkMode =true;
		}

		combat.getCustomData().put(CORONAL_HYPERSHUNT + ship.getId(), info);



	}
}
