package arc.hullmod.hypershunt;

import arc.util.ARCUtils;
import arc.hullmod.ARCBaseHullmod;
import arc.hullmod.IHullmodPart;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.*;

import static arc.hullmod.VentAcceleratorPart.BASE_VENT_BONUS;
import static arc.hullmod.VentAcceleratorPart.MAX_VENT_BONUS;

//offensive core
public class CoronalHypershunt extends ARCBaseHullmod {

	static {
		HashMap<HullSize, Float> fluxRedirection = new LinkedHashMap<>();

		fluxRedirection.put(HullSize.FIGHTER, -50f);
		fluxRedirection.put(HullSize.FRIGATE, -45f);
		fluxRedirection.put(HullSize.DESTROYER, -40f);
		fluxRedirection.put(HullSize.CRUISER, -35f);
		fluxRedirection.put(HullSize.CAPITAL_SHIP, -30f);

		FLUX_REDUCTION = fluxRedirection;

		HashMap<HullSize, Float> maxTimeMult = new LinkedHashMap<>();

		maxTimeMult.put(HullSize.FIGHTER, 1.8f);
		maxTimeMult.put(HullSize.FRIGATE, 1.6f);
		maxTimeMult.put(HullSize.DESTROYER, 1.4f);
		maxTimeMult.put(HullSize.CRUISER, 1.3f);
		maxTimeMult.put(HullSize.CAPITAL_SHIP, 1.1f);

		MAX_TIME_MULT = maxTimeMult;

		Set<String> badMods = new HashSet<>();

		badMods.add("safetyoverrides");
		badMods.add("heavyarmor");
		badMods.add("fluxcoil");
		badMods.add("fluxdistributor");
		badMods.add("fluxbreakers");
		badMods.add("eis_aquila");
		badMods.add("eis_vanagloria");
		badMods.add("converted_hangar");
		badMods.add("roider_fighterClamps");

		BAD_HULLMODS = badMods;
	}
	static final String CORONAL_MICROSHUNT = "arc_microshunt";

	static final Map<HullSize,Float> FLUX_REDUCTION;
	static final Map<HullSize, Float> MAX_TIME_MULT;
	static final Set<String> BAD_HULLMODS;

	static final float OVERLOAD_TIME = 90f;
	static final float UNFOLD_RATE = 3f;

	static final float SUPPLY = 40f;
	static final float REPAIR_TIME = -80f;

	static final String BAD_HULLMOD_NOTIFICATION_SOUND = "cr_allied_critical";

	@SuppressWarnings("unchecked")
	public CoronalHypershunt() {
		super(new IHullmodPart[]{
		});
	}


	@Override
	public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return true;
	}

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//good
		stats.getEnergyWeaponFluxCostMod().modifyPercent(id, FLUX_REDUCTION.get(hullSize));
		stats.getBallisticWeaponFluxCostMod().modifyPercent(id, FLUX_REDUCTION.get(hullSize));
		stats.getShieldUnfoldRateMult().modifyFlat(id, UNFOLD_RATE);


		//bad
		stats.getOverloadTimeMod().modifyPercent(id, OVERLOAD_TIME);
		stats.getSuppliesPerMonth().modifyFlat(id, SUPPLY);

		stats.getCombatWeaponRepairTimeMult().modifyPercent(id, REPAIR_TIME);

	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		boolean shouldSoundError = false;

		ArrayList<String> deletionList = new ArrayList<>();

		for (String s : ship.getVariant().getNonBuiltInHullmods()) {
			if (BAD_HULLMODS.contains(s)) {
				deletionList.add(s);
			}
		}

		if (deletionList.size() > 0) {
			ship.getVariant().addMod("ML_incompatibleHullmodWarning");
			shouldSoundError = true;
		}
		for (String s : deletionList) {
			ship.getVariant().removeMod(s);
		}

		if (shouldSoundError) {
			Global.getSoundPlayer().playUISound(BAD_HULLMOD_NOTIFICATION_SOUND, 0.7f, 1f);
		}

	}



	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 2f;

		Color h = Misc.getHighlightColor();
		Color good= Misc.getPositiveHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();

		tooltip.addPara("", pad);
		tooltip.addSectionHeading("Description", Alignment.MID, 0f);
		tooltip.addPara("", pad);
		tooltip.addPara("• %s weapons have %s reduced flux costs", pad, good, "All weapons", ARCUtils.slashesOf(FLUX_REDUCTION));
		tooltip.addPara("• Shield deployment is %s faster", pad, good, (int)(UNFOLD_RATE * 100) + "%");
		tooltip.addPara("• Supply usage increased by %s", pad, bad, ((int)(SUPPLY)) + "%");
		tooltip.addPara("• Weapon repair time increased by %s", pad, bad, ((int)(REPAIR_TIME)) + "%");

		tooltip.addPara("This ship can %s", pad, Misc.getStoryBrightColor(), "access Archotech Research weapons and fighters");

		tooltip.addPara("", pad, h, "");

		tooltip.addSectionHeading("Passive Ability - Hyper Vent", Misc.getPositiveHighlightColor(), Misc.getStoryDarkColor(), Alignment.MID, 0f);
		tooltip.addPara("", pad, h, "");
		tooltip.addPara("Venting instead diverts power to the microshunt granting %s to the ARC ship", pad, good, "unique utilities");
		tooltip.addPara("", pad, h, "");
		tooltip.addPara("Base venting speed increased to %s.", pad, h, "" + (int) BASE_VENT_BONUS + "%");
		tooltip.addPara("Venting speed while at max flux decreased to %s.", pad, h, "" + (int)MAX_VENT_BONUS + "%");
		tooltip.addPara("Scales with Archotech Research's venting based technologies", pad);
		tooltip.addPara("", pad, h, "");


		tooltip.addSectionHeading("Incompatibilities", bad, Misc.getStoryDarkColor(), Alignment.MID, 0f);
		tooltip.addPara("", pad, h, "");
		TooltipMakerAPI text = tooltip.beginImageWithText("graphics/ARC/icons/arc_incompatible.png", 40);
		text.addPara("This ship cannot install the following hullmods", 0f);
		text.addPara("• Safeties Overrides", Misc.getNegativeHighlightColor(), 0f);
		text.addPara("• Heavy Armor", Misc.getNegativeHighlightColor(), 0f);
		text.addPara("• Converted Hangars", Misc.getNegativeHighlightColor(), 0f);
		text.addPara("• Resistant Flux Conduits", Misc.getNegativeHighlightColor(), 0f);

		if (Global.getSettings().getModManager().isModEnabled("apex_design")) {
			text.addPara("• Nanolaminate Plating", Misc.getNegativeHighlightColor(), 0f);
			text.addPara("• Cryocooled Armor Lattice", Misc.getNegativeHighlightColor(), 0f);
		}
		if (Global.getSettings().getModManager().isModEnabled("roider")) {
			text.addPara("• Fighter Clamps", Misc.getNegativeHighlightColor(), 0f);
		}
		if (Global.getSettings().getModManager().isModEnabled("timid_xiv")) {
			text.addPara("• Aquila Reactor", Misc.getNegativeHighlightColor(), 0f);
			text.addPara("• Vanagloria Ionized Armor", Misc.getNegativeHighlightColor(), 0f);
		}

		tooltip.addImageWithText(0f);





	}

	//    private static float debuff=0;

	//    private static final Map<String,Float> HULLMOD_DEBUFF = new HashMap<>();
//    static{
//        HULLMOD_DEBUFF.put("safetyoverrides",0.2f);
////        HULLMOD_DEBUFF.put("unstable_injector",0.15f);
////        HULLMOD_DEBUFF.put("auxiliarythrusters",0.15f);
////        HULLMOD_DEBUFF.put("SCY_lightArmor",0.15f);
//    }
	private  final Set<String> BLOCKED_HULLMODS = new HashSet<>();
	{
		// These hullmods will automatically be removed
		// This prevents unexplained hullmod blocking
		BLOCKED_HULLMODS.add("safetyoverrides");
	}

	private final Map<String, Integer> SWITCH_TO = new HashMap<>();
	{
		SWITCH_TO.put("diableavionics_versant_harvest_LEFT",1);
		SWITCH_TO.put("diableavionics_versant_harvestB_LEFT",2);
		SWITCH_TO.put("diableavionics_versant_harvestC_LEFT",0);
	}

	private final Map<Integer,String> SWITCH = new HashMap<>();
	{
		SWITCH.put(0,"diableavionics_selector_auto");
		SWITCH.put(1,"diableavionics_selector_burst");
		SWITCH.put(2,"diableavionics_selector_semi");
	}

	private final String leftslotID = "GUN_LEFT";
	private final String rightslotID = "GUN_RIGHT";

	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		//trigger a weapon switch if none of the selector hullmods are present
		boolean toSwitch=true;
		for(int i=0; i<SWITCH.size(); i++){
			if(stats.getVariant().getHullMods().contains(SWITCH.get(i))){
				toSwitch=false;
			}
		}

		//remove the weapons to change and swap the hullmod for the next fire mode
		if(toSwitch){
			//select new fire mode
			int selected;
			boolean random=false;
			if(stats.getVariant().getWeaponSpec(leftslotID)!=null){
				selected=SWITCH_TO.get(stats.getVariant().getWeaponSpec(leftslotID).getWeaponId());

			} else {
				selected=MathUtils.getRandomNumberInRange(0, SWITCH_TO.size()-1);
				random=true;
			}

			//add the proper hullmod
			stats.getVariant().addMod(SWITCH.get(selected));
		}
	}

	@Override
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return txt("hm_warning");
		if (index == 1) return Global.getSettings().getHullModSpec("safetyoverrides").getDisplayName();
		return null;
	}


	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {

		super.advanceInCombat(ship, amount);

		CombatEngineAPI combat = Global.getCombatEngine();

		if (combat.isPaused()) return;


		//  Burst Surger / Variable Surger
		if (ship.isPhased()) {
			ship.getMutableStats().getBallisticAmmoRegenMult().modifyMult(CORONAL_MICROSHUNT, 2);
			ship.getMutableStats().getEnergyAmmoRegenMult().modifyMult(CORONAL_MICROSHUNT, 2);
			ship.getMutableStats().getBallisticRoFMult().modifyMult(CORONAL_MICROSHUNT, 2);
		} else {
			ship.getMutableStats().getBallisticAmmoRegenMult().unmodify(CORONAL_MICROSHUNT);
			ship.getMutableStats().getEnergyAmmoRegenMult().unmodify(CORONAL_MICROSHUNT);
		}

		//   Venting AI






	}

	// Useless methods


	@Override
	public boolean affectsOPCosts() {
		return true;
	}
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return false; //TODO never
	}

}
