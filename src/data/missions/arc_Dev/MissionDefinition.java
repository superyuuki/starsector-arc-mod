package data.missions.arc_Dev;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

import java.util.Random;

public class MissionDefinition implements MissionDefinitionPlugin {

    static final Random RANDOM = new Random();

    public void defineMission(final MissionDefinitionAPI api) {
        api.initFleet(FleetSide.PLAYER, "ARC", FleetGoal.ATTACK, false, 5);
        api.initFleet(FleetSide.ENEMY, "IDK", FleetGoal.ATTACK, true);
        api.setFleetTagline(FleetSide.PLAYER, "player");
        api.setFleetTagline(FleetSide.ENEMY, "enemy");

        api.addBriefingItem("kill them all!");
        api.addToFleet(FleetSide.PLAYER, "wolf_Assault", FleetMemberType.SHIP, "ARC watcher", true);
        api.addToFleet(FleetSide.PLAYER, "arc_netzach_prototype", FleetMemberType.SHIP, "ARC bummo", true);
        api.addToFleet(FleetSide.PLAYER, "arc_daat_prototype", FleetMemberType.SHIP, "ARC oops", true);
        api.addToFleet(FleetSide.PLAYER, "arc_gevurah_prototype", FleetMemberType.SHIP, "ARC smelly balls", true);
        api.addToFleet(FleetSide.PLAYER, "arc_tiferet_prototype", FleetMemberType.SHIP, "ARC bean", true);
        api.addToFleet(FleetSide.PLAYER, "arc_tiferet_prototype", FleetMemberType.SHIP, "ARC nuggy", true);
        api.addToFleet(FleetSide.PLAYER, "arc_chokmah_prototype", FleetMemberType.SHIP, "ARC what", true);



        api.addToFleet(FleetSide.ENEMY, "paragon_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "astral_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "odyssey_Balanced", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "doom_Strike", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "aurora_Balanced", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "aurora_Balanced", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "apogee_Balanced", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "apogee_Balanced", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "sunder_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "sunder_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "sunder_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "wolf_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "wolf_Assault", FleetMemberType.SHIP, false);
        final float width = 15000.0f;
        final float height = 30000.0f;
        api.initMap(-width / 2.0f, width / 2.0f, -height / 2.0f, height / 2.0f);
        final float minX = -width / 2.0f;
        final float minY = -height / 2.0f;
        api.addNebula(minX + width * 0.75f, minY + height * 0.5f, 2000.0f);
        api.addNebula(minX + width * 0.25f, minY + height * 0.5f, 1000.0f);
        for (int i = 0; i < 5; ++i) {
            final float x = RANDOM.nextFloat() * width - width / 2.0f;
            final float y = RANDOM.nextFloat() * height - height / 2.0f;
            final float radius = 100.0f + RANDOM.nextFloat() * 400.0f;
            api.addNebula(x, y, radius);
        }
        api.addObjective(minX + width * 0.25f + 2000.0f, minY + height * 0.3f, "nav_buoy");
        api.addObjective(minX + width * 0.5f, minY + height * 0.7f, "nav_buoy");
        api.addObjective(minX + width * 0.75f - 2000.0f, minY + height * 0.3f, "sensor_array");
        api.addObjective(minX + width * 0.4f - 2000.0f, minY + height * 0.7f, "sensor_array");
        api.addAsteroidField(minY, minY, 45.0f, 1000.0f, 20.0f, 70.0f, 60);
        api.addPlanet(0.0f, 0.0f, 256.0f, "gas_giant", 250.0f, true);
    }


}
