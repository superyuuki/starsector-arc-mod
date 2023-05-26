package data.missions.arc_arcVsRandom;

import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import data.missions.BaseRandomARCMission;

public class MissionDefinition extends BaseRandomARCMission {

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        chooseFactions("arc", null);
        super.defineMission(api);
    }

}
