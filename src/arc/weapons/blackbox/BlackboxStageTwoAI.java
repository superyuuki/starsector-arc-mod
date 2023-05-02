package arc.weapons.blackbox;

import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.GuidedMissileAI;
import com.fs.starfarer.api.combat.MissileAIPlugin;

public class BlackboxStageTwoAI implements MissileAIPlugin {

    final CombatEntityAPI target;
    final CombatEntityAPI self = null;

    public BlackboxStageTwoAI(CombatEntityAPI target) {
        this.target = target;
    }


    @Override
    public void advance(float v) {

    }
}
