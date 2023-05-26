package data.missions.arc_randomVsArc;

import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import data.missions.BaseRandomARCMission;

public class MissionDefinition extends BaseRandomARCMission {

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        chooseFactions(null, "arc");
        super.defineMission(api);
    }

}
