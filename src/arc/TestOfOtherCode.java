package arc;

import cmu.subsystems.BaseSubsystem;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public class TestOfOtherCode extends BaseSubsystem {
    @Override
    public void apply(MutableShipStatsAPI mutableShipStatsAPI, String s, SubsystemState subsystemState, float v) {
        mutableShipStatsAPI.getMaxSpeed().modifyFlat("me", 500);
    }

    @Override
    public void unapply(MutableShipStatsAPI mutableShipStatsAPI, String s) {
        mutableShipStatsAPI.getMaxSpeed().unmodifyFlat("me");
    }

    @Override
    public String getStatusString() {
        return "me";
    }

    @Override
    public String getInfoString() {
        return "me";
    }

    @Override
    public String getFlavourString() {
        return "me";
    }

    @Override
    public int getNumGuiBars() {
        return 1;
    }

    @Override
    public void aiInit() {

    }

    @Override
    public void aiUpdate(float v) {

    }
}
