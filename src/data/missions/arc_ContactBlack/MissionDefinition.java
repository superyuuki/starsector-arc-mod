package data.missions.arc_ContactBlack;

import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

import java.util.Random;

public class MissionDefinition implements MissionDefinitionPlugin {

    static final Random RANDOM = new Random();

    public void defineMission(final MissionDefinitionAPI api) {
        api.initFleet(FleetSide.PLAYER, "TTS", FleetGoal.ATTACK, false, 5);
        api.initFleet(FleetSide.ENEMY, "ARC", FleetGoal.ATTACK, true);
        api.setFleetTagline(FleetSide.PLAYER, "Tri-Tachyon Corporate Exploration Fleet");
        api.setFleetTagline(FleetSide.ENEMY, "???");

        api.addBriefingItem("Survive");
        api.addToFleet(FleetSide.PLAYER, "scarab_Experimental", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "scarab_Experimental", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "glimmer_Assault", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "glimmer_Assault", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "glimmer_Assault", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "glimmer_Assault", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "medusa_Attack", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "medusa_Support", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "scintilla_Support", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "scintilla_Support", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "hyperion_Attack", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "hyperion_Attack", FleetMemberType.SHIP, true);
        api.addToFleet(FleetSide.PLAYER, "brilliant_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "brilliant_Standard", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "aurora_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "aurora_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "paragon_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.PLAYER, "radiant_Assault", FleetMemberType.SHIP, false);



        api.addToFleet(FleetSide.ENEMY, "arc_netzach_prototype", FleetMemberType.SHIP, "ARC God's Hand", true);
        api.addToFleet(FleetSide.ENEMY, "arc_daat_prototype", FleetMemberType.SHIP, "ARC Will of the Maker", true);
        api.addToFleet(FleetSide.ENEMY, "arc_gevurah_prototype", FleetMemberType.SHIP, "ARC Lay Down Your Arms", true);
        api.addToFleet(FleetSide.ENEMY, "arc_malkuth_prototype", FleetMemberType.SHIP, "ARC Holy Fist", true);
        api.addToFleet(FleetSide.ENEMY, "arc_malkuth_prototype", FleetMemberType.SHIP, "ARC Divine Will", true);

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
