package arc.plugin.ai;

import arc.Index;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import java.util.List;

public class AIDescriptorPlugin extends BaseEveryFrameCombatPlugin {

    final IntervalUtil interval = new IntervalUtil(0.05f, 0.15f);


    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        interval.advance(amount);
        if (!interval.intervalElapsed()) return; //uohhh



        //update stats
        for (ShipAPI shipAPI : Global.getCombatEngine().getShips()) {

            StatData data = (StatData) shipAPI.getCustomData().get(Index.STAT_DATA);
            if (data == null) {
                data = new StatData();
            }


            //ogic









            shipAPI.setCustomData(Index.STAT_DATA, data);


        }


    }
}
