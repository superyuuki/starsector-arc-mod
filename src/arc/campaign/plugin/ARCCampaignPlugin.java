package arc.campaign.plugin;

import arc.Index;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;

public class ARCCampaignPlugin extends BaseCampaignPlugin {

    public PluginPick<FleetInflater> pickFleetInflater(CampaignFleetAPI fleet, Object params) {
        if (params instanceof DefaultFleetInflaterParams) {
            DefaultFleetInflaterParams p = (DefaultFleetInflaterParams) params;


            //ARC fleets should use...
            if (fleet.getFaction().getId().contains(Index.ARC_FACTION)) {
               // return new PluginPick<FleetInflater>(new ARCFleetInflater(p), PickPriority.MOD_SET);

                return null;
            }
        }
        return null;
    }

    public PluginPick<AICoreOfficerPlugin> pickAICoreOfficerPlugin(String commodityId) {


        return null;
    }

}
