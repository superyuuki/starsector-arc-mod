package arc.weapons.blackbox;

import arc.Index;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicTargeting;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlackboxStageOneAI implements MissileAIPlugin, GuidedMissileAI {

     final MissileAPI missile;
     final IntervalUtil intervalUtil = new IntervalUtil(0.7f, 1.5f);


    public BlackboxStageOneAI(MissileAPI missile) {
        this.missile = missile;
    }

    @Override
    public void advance(float amount) {

        //cancelling IF: skip the AI if the game is paused, the missile is engineless or fading
        if (Global.getCombatEngine().isPaused()) {
            return;
        }
        missile.giveCommand(ShipCommand.ACCELERATE);
        intervalUtil.advance(0.01f);
        if (intervalUtil.intervalElapsed()) {
            mirv(missile);
        }
    }

    private void mirv(MissileAPI missile) {

        float facing = missile.getFacing();


        ShipAPI possible = MagicTargeting.pickShipTarget(
                missile.getSource(),
                MagicTargeting.targetSeeking.FULL_RANDOM,
                (int) missile.getMaxRange(),
                360,
                50,
                20,
                1,1,1
        );

        if (possible != null) {
            facing = VectorUtils.getAngle(missile.getLocation(), possible.getLocation());
        }

        for (int a = 0; a < 3; ++a) {
            Vector2f vel = MathUtils.getRandomPointInCone(new Vector2f(), (float) (a * 50), facing - 182f, facing - 180f);
            Vector2f.add(vel, missile.getSource().getVelocity(), vel);

            float size = MathUtils.getRandomNumberInRange(5f, 20f);
            float duration = MathUtils.getRandomNumberInRange(0.3f, 1f);
            Global.getCombatEngine().addSmokeParticle(missile.getLocation(), vel, size, 30f, duration, Color.lightGray);
        }

        CombatEntityAPI child = Global.getCombatEngine().spawnProjectile(
                missile.getSource(),
                missile.getWeapon(),
                Index.BLACKBOX_STAGE_TWO,
                missile.getLocation(),
                facing,
                null
        );

        //((MissileAPI) child).setMissileAI();

        Global.getCombatEngine().removeEntity(missile);
    }

    @Override
    public CombatEntityAPI getTarget() {
        return null;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
    }
}
