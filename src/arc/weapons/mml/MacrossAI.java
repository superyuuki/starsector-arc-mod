package arc.weapons.mml;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicTargeting;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;

public class MacrossAI implements MissileAIPlugin, GuidedMissileAI {


    ShipAPI launchingShip;
    CombatEntityAPI target;
    MissileAPI missile;

    private float timer=0, check=0f, scatter=0, random, correctAngle;
    private final float DAMPING=0.1f;
    private final int SEARCH_CONE=360, MAX_SCATTER=50;
    private float PRECISION_RANGE=500, ECCM=2f;



    public MacrossAI(MissileAPI missile, ShipAPI launchingShip) {
        this.launchingShip = launchingShip;
        this.missile = missile;
        target = MagicTargeting.pickShipTarget(launchingShip, MagicTargeting.targetSeeking.LOCAL_RANDOM, (int) this.missile.getMaxRange() * 10, 200, 0, 2, 4, 10, 20);
    }

    private boolean launch=true;


    @Override
    public void advance(float amount) {

        if (target == null
                || (target instanceof ShipAPI && ((ShipAPI)target).isHulk())
                || !Global.getCombatEngine().isEntityInPlay(target)
                || target.getCollisionClass()==CollisionClass.NONE
        ){
            target = MagicTargeting.pickShipTarget(launchingShip, MagicTargeting.targetSeeking.LOCAL_RANDOM, (int) this.missile.getMaxRange() * 10, 200, 0, 2, 4, 10, 20);

            missile.giveCommand(ShipCommand.ACCELERATE);
            return;
        }

        if(launch || timer>=check){
            launch=false;

            timer -=check;

            //set the next check time
            float dist = MathUtils.getDistanceSquared(missile.getLocation(), target.getLocation())/PRECISION_RANGE;
            check = Math.min(
                    0.5f,
                    Math.max(
                            0.1f,
                            dist)
            );

            scatter = ECCM * MAX_SCATTER * random * check;
        }

        if (missile.isFizzling()) return;

        correctAngle = VectorUtils.getAngle(
                missile.getLocation(),
                MathUtils.getRandomPointInCircle(target.getLocation(), target.getCollisionRadius() + MathUtils.getRandomNumberInRange(0, target.getCollisionRadius() * 5))
        );

        correctAngle+=scatter;

        float correction = MathUtils.getShortestRotation(VectorUtils.getFacing(missile.getVelocity()),correctAngle);
        if(correction>0){
            correction= -11.25f * ( (float)Math.pow(FastTrig.cos(MathUtils.FPI*correction/90)+1, 2) -4 );
        } else {
            correction= 11.25f * ( (float)Math.pow(FastTrig.cos(MathUtils.FPI*correction/90)+1, 2) -4 );
        }
        correctAngle+= correction;

        float aimAngle = MathUtils.getShortestRotation( missile.getFacing(), correctAngle);

        if (aimAngle < 0) {
            missile.giveCommand(ShipCommand.TURN_RIGHT);
        } else {
            missile.giveCommand(ShipCommand.TURN_LEFT);
        }

        if (Math.abs(aimAngle) < Math.abs(missile.getAngularVelocity()) * DAMPING) {
            missile.setAngularVelocity(aimAngle / DAMPING);
        }

        missile.giveCommand(ShipCommand.ACCELERATE);

    }

    @Override
    public CombatEntityAPI getTarget() {
        return target;
    }

    @Override
    public void setTarget(CombatEntityAPI target) {
        this.target= target;
    }
}
