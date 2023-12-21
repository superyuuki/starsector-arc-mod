package arc.campaign;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantSeededFleetManager;
import com.fs.starfarer.api.loading.VariantSource;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.Random;

import static org.lwjgl.LWJGLUtil.log;

public class CampaignUtils {


    //Thanks kessa

    public static void setAIOfficers(CampaignFleetAPI fleet){
        for (FleetMemberAPI m : fleet.getMembersWithFightersCopy()){
            setAIOfficer(m);
        }
    }
    public static void setAIOfficer(FleetMemberAPI member){

        if (member.isFighterWing()) return;
        if (member.getCaptain()==null) return;
        PersonAPI captain = member.getCaptain();
        String aiId = "";
        String portraitId = "";
        if (captain==null) return;
        int aiType = captain.getStats().getLevel();

        if (aiType==3 || aiType==4){
            aiId = "gamma_core";
            portraitId = "graphics/portraits/portrait_ai1b.png";
        }
        if (aiType==5 || aiType==6) {
            aiId = "beta_core";
            portraitId = "graphics/portraits/portrait_ai3b.png";
        }
        if (aiType==7 || aiType==8) {
            aiId = "alpha_core";
            portraitId = "graphics/portraits/portrait_ai2b.png";
        }
        if (aiId.length()>0) captain.setAICoreId(aiId);
        if (portraitId.length()>0) captain.setPortraitSprite(portraitId);

        if (captain.getStats()==null) return;
        for (MutableCharacterStatsAPI.SkillLevelAPI skill : captain.getStats().getSkillsCopy()){
            if (skill.getSkill()==null) continue;
            if (!skill.getSkill().isCombatOfficerSkill()) continue;

            //TODO add funny 'kill capship' skill
        }
    }

    public static void update(CampaignFleetAPI fleet, Random random){

        for (FleetMemberAPI m : fleet.getMembersWithFightersCopy()) {
            //IMPORTANT set id or random stuff breaks
            //syk: idk what this means lol
            if (!m.getId().startsWith("arc_")) m.setId("arc_"+m.getShipName()+random.nextLong());
            if (m.isFighterWing()) continue;
            ShipVariantAPI v = m.getVariant();
            //clone
            for (String tag : m.getVariant().getTags()){
                v.addTag(tag);
            }
            //keep Smods for all ships
            v.addTag(Tags.TAG_RETAIN_SMODS_ON_RECOVERY);
            v.addTag(Tags.VARIANT_ALWAYS_RETAIN_SMODS_ON_SALVAGE);
            //CR update
            m.getRepairTracker().setCR(m.getRepairTracker().getMaxCR());

            v.setSource(VariantSource.REFIT);
            m.setVariant(v, false, false);
        }

        //FINISHING
        fleet.getFleetData().sort();
        fleet.getFleetData().setSyncNeeded();
        fleet.getFleetData().syncIfNeeded();

        //for (FleetMemberAPI m : fleet.getMembersWithFightersCopy()) {
        //    log(fleet.getName()+" ship "+m.getHullSpec().getHullName()+" tags "+m.getVariant().getTags().toString());
        //}
    }

    public static SectorEntityToken addDormant(SectorEntityToken loc, String factionId, float minCombatPoints, float maxCombatPoints, float qualityChance, float minQuality, float maxQuality, float SmodChance, int minSmod, int maxSmod) {
        CampaignFleetAPI fleet;
        StarSystemAPI system = loc.getStarSystem();
        String name;

        float combatPoints = MathUtils.getRandomNumberInRange(minCombatPoints, maxCombatPoints);

        String type = "patrolSmall";
        name = "Splinter";
        if (combatPoints > 30f) {
            type = "patrolMedium";
            name = "Combine";
        }
        if (combatPoints > 60f) {
            type = "patrolLarge";
            name = "Swarm";
        }

        //apply settings
        combatPoints *= 40f;

        //fine?
        if (combatPoints<=0f) return null;

        final FleetParamsV3 params = new FleetParamsV3(
                new Vector2f(), factionId, 1f, type, combatPoints, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);

        if (Math.random()<qualityChance) params.qualityOverride = MathUtils.getRandomNumberInRange(minQuality, maxQuality);
        if (Math.random()<SmodChance) params.averageSMods = MathUtils.getRandomNumberInRange(minSmod, maxSmod);
        params.withOfficers = true;

        fleet = FleetFactoryV3.createFleet(params);
        system.addEntity(fleet);
        RemnantSeededFleetManager.initRemnantFleetProperties(params.random, fleet, true);

        fleet.setTransponderOn(true);

        float dist = loc.getRadius() * MathUtils.getRandomNumberInRange(2.00f, 2.50f);
        fleet.setCircularOrbit(loc, (float)Math.random() * 360.0f, dist, MathUtils.getRandomNumberInRange(60f,120f));
        fleet.setFacing((float)Math.random() * 360.0f);

        //arc dormants
        if (factionId.equals("arc")) {
            //fleet.getMemoryWithoutUpdate().set(nskr_dormantSpawner.DORMANT_KEY, true);
            fleet.setName(name);

            setAIOfficers(fleet);
        }

        //makes sure we are not in a star
        //questUtil.spawnAwayFromStarFixer(fleet, 2.0f);

        //update
        //fleetUtil.update(fleet, new Random());

        log("DORMANT added in "+ loc.getContainingLocation().getName()+" to "+loc.getName());

        return fleet;
    }
}
