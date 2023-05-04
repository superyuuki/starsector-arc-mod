package arc;

import arc.weapons.blackbox.BlackboxAI;
import arc.weapons.blackbox.BlackboxStageOneAI;
import arc.weapons.mml.MacrossAI;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class ARCPlugin extends BaseModPlugin {

    @Override
    public void onApplicationLoad() {
        System.out.println("[ARC] Loading Base Plugin");



//        if (!Global.getSettings().getModManager().isModEnabled("MagicLib")) {
//            throw new RuntimeException(
//                    "[ARC] Missing MagicLib, which is needed to start! \nGet it at http://fractalsoftworks.com/forum/index.php?topic=13718"
//            );
//        }
    }


    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        switch (missile.getProjectileSpecId()) {
            /*case Index.BLACKBOX_STAGE_ONE:
                return new PluginPick<>(new BlackboxStageOneAI(missile, missile.getDamageTarget()), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            case Index.BLACKBOX_STAGE_TWO:
                return new PluginPick<>(new BlackboxAI(missile), CampaignPlugin.PickPriority.MOD_SPECIFIC);*/
            case Index.MACROSS:
                return new PluginPick<>(new MacrossAI(missile, launchingShip), CampaignPlugin.PickPriority.MOD_SPECIFIC);
            default:

        }
        return null;

    }

    @Override
    public void onNewGame() {

    }


}
