package arc.weapons.blackbox;

import arc.Index;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicTargeting;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class BlackboxStageOneAI implements MissileAIPlugin, GuidedMissileAI {

     final MissileAPI missile;
     final CombatEntityAPI target;
     final IntervalUtil intervalUtil = new IntervalUtil(0.3f, 0.7f);


    public BlackboxStageOneAI(MissileAPI missile, CombatEntityAPI target) {
        this.missile = missile;
        this.target = target;
    }

    @Override
    public void advance(float amount) {
        if (Global.getCombatEngine().isPaused()) {
            return;
        }
        missile.giveCommand(ShipCommand.ACCELERATE); //nyoom
        intervalUtil.advance(amount);
        if (intervalUtil.intervalElapsed()) {

            float facing = VectorUtils.getAngle(missile.getLocation(), target.getLocation());

            //fart
            for (int a = 0; a < 3; ++a) {
                Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 50), facing - 182f, facing - 180f);
                Vector2f.add(vel, missile.getSource().getVelocity(), vel);

                float size = MathUtils.getRandomNumberInRange(20f, 60f);
                float duration = MathUtils.getRandomNumberInRange(0.3f, 1f);
                Global.getCombatEngine().addSmokeParticle(missile.getLocation(), vel, size, 30f, duration, Color.lightGray);
            }

            CombatEntityAPI child = Global.getCombatEngine().spawnProjectile(
                    missile.getSource(),
                    missile.getWeapon(),
                    Index.BLACKBOX_STAGE_TWO,
                    missile.getLocation(),
                    facing + MathUtils.getRandomNumberInRange(-90f, 90f),
                    null
            );

            MissileAPI childAsMissile = (MissileAPI) child;

            childAsMissile.setMissileAI(new BlackboxStageTwoAI(childAsMissile, target));

            Global.getCombatEngine().removeEntity(missile); //PDM launched!
        }
    }

    @Override
    public CombatEntityAPI getTarget() {
        return target;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {

    }
}
