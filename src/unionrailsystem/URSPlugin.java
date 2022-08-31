package unionrailsystem;

import java.util.ArrayList;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.impl.campaign.skills.ShipDesign;

public class URSPlugin extends BaseModPlugin {


    public static PluginState 
    @Override
    public void onApplicationLoad() throws Exception {
        throw new Exception("Mod template successfully loaded. Happy modding.");
    }

}


public class PluginState {
    private ArrayList<ShipDesign> AllMYShips;
}