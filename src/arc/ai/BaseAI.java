package arc.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lwjgl.util.vector.Vector2f;

public abstract class BaseAI implements ShipAIPlugin {
    protected final ShipAPI self;
    protected final ShipwideAIFlags AIFlags = new ShipwideAIFlags();
    protected final ShipAIConfig AIConfig = new ShipAIConfig();
    private float dontFireUntil = 0f;
    protected final IntervalUtil circumstanceEvaluationTimer = new IntervalUtil(0.05f, 0.15f);
    private static final float DEFAULT_FACING_THRESHOLD = 3f;

    protected boolean mayFire() {
        return dontFireUntil <= Global.getCombatEngine().getTotalElapsedTime(false);
    }

    protected boolean isFacing(CombatEntityAPI target) {
        return isFacing(target.getLocation(), DEFAULT_FACING_THRESHOLD);
    }

    protected boolean isFacing(CombatEntityAPI target, float threshholdDegrees) {
        return isFacing(target.getLocation(), threshholdDegrees);
    }

    protected boolean isFacing(Vector2f point) {
        return isFacing(point, DEFAULT_FACING_THRESHOLD);
    }

    protected boolean isFacing(Vector2f point, float threshholdDegrees) {
        return (Math.abs(getAngleTo(point)) <= threshholdDegrees);
    }

    protected float getAngleTo(CombatEntityAPI entity) {
        return getAngleTo(entity.getLocation());
    }

    protected float getAngleTo(Vector2f point) {
        float angleTo = VectorUtils.getAngle(self.getLocation(), point);
        return MathUtils.getShortestRotation(self.getFacing(), angleTo);
    }

    protected ShipCommand strafe(float degreeAngle, boolean strafeAway) {
        float angleDif = MathUtils.getShortestRotation(self.getFacing(), degreeAngle);

        if ((!strafeAway && Math.abs(angleDif) < DEFAULT_FACING_THRESHOLD) || (strafeAway && Math.abs(angleDif) > 180f - DEFAULT_FACING_THRESHOLD)) {
            return null;
        }

        ShipCommand direction = (angleDif > 0f) ^ strafeAway ? ShipCommand.STRAFE_LEFT : ShipCommand.STRAFE_RIGHT;
        self.giveCommand(direction, null, 0);

        return direction;
    }

    protected ShipCommand strafe(Vector2f location, boolean strafeAway) {
        return strafe(VectorUtils.getAngle(self.getLocation(), location), strafeAway);
    }

    protected ShipCommand strafe(CombatEntityAPI entity, boolean strafeAway) {
        return strafe(entity.getLocation(), strafeAway);
    }

    protected ShipCommand strafeToward(float degreeAngle) {
        return strafe(degreeAngle, false);
        //        float angleDif = MathUtils.getShortestRotation(ship.getFacing(), degreeAngle);
        //
        //        if(Math.abs(angleDif) < 5) return null;
        //
        //        ShipCommand direction = (angleDif > 0) ? ShipCommand.STRAFE_LEFT : ShipCommand.STRAFE_RIGHT;
        //        ship.giveCommand(direction, null, 0);
        //
        //        return direction;
    }

    protected ShipCommand strafeToward(Vector2f location) {
        return strafeToward(VectorUtils.getAngle(self.getLocation(), location));
    }

    protected ShipCommand strafeToward(CombatEntityAPI entity) {
        return strafeToward(entity.getLocation());
    }

    protected ShipCommand strafeAway(float degreeAngle) {
        return strafe(degreeAngle, true);
    }

    protected ShipCommand strafeAway(Vector2f location) {
        return strafeAway(VectorUtils.getAngle(self.getLocation(), location));
    }

    protected ShipCommand strafeAway(CombatEntityAPI entity) {
        return strafeAway(entity.getLocation());
    }

    protected ShipCommand turn(float degreeAngle, boolean turnAway) {
        float angleDif = MathUtils.getShortestRotation(self.getFacing(), degreeAngle);

        //Check to see if we should slow down to avoid overshooting
        float secondsTilDesiredFacing = angleDif / self.getAngularVelocity();
        if (secondsTilDesiredFacing > 0f) {
            float turnAcc = self.getMutableStats().getTurnAcceleration().getModifiedValue();
            float rotValWhenAt = Math.abs(self.getAngularVelocity()) - secondsTilDesiredFacing * turnAcc;
            if (rotValWhenAt > 0f) {
                turnAway = !turnAway;
            }
        }

        //        if((!turnAway && Math.abs(angleDif) < DEFAULT_FACING_THRESHHOLD)
        //                || (turnAway && Math.abs(angleDif) > 180 - DEFAULT_FACING_THRESHHOLD))
        //            return null;

        ShipCommand direction = (angleDif > 0f) ^ turnAway ? ShipCommand.TURN_LEFT : ShipCommand.TURN_RIGHT;
        self.giveCommand(direction, null, 0);

        //        float amount = Global.getCombatEngine().getElapsedInLastFrame();
        //        float turnAcc = ship.getMutableStats().getTurnAcceleration().getModifiedValue();
        //        float maxTurn = ship.getMutableStats().getMaxTurnRate().getModifiedValue();
        //        float angleVel = ship.getAngularVelocity();
        //        float dAngleVel = turnAcc * ((direction == ShipCommand.TURN_RIGHT) ? -1 : 1) * amount;
        //        float newAngleVel = angleVel + dAngleVel;
        //
        //        ship.setAngularVelocity(Math.max(-maxTurn, Math.min(maxTurn, newAngleVel)));

        return direction;
    }

    protected ShipCommand turn(Vector2f location, boolean strafeAway) {
        return turn(VectorUtils.getAngle(self.getLocation(), location), strafeAway);
    }

    protected ShipCommand turn(CombatEntityAPI entity, boolean strafeAway) {
        return turn(entity.getLocation(), strafeAway);
    }

    protected ShipCommand turnToward(float degreeAngle) {
        return turn(degreeAngle, false);
    }

    protected ShipCommand turnToward(Vector2f location) {
        return turnToward(VectorUtils.getAngle(self.getLocation(), location));
    }

    protected ShipCommand turnToward(CombatEntityAPI entity) {
        return turnToward(entity.getLocation());
    }

    protected ShipCommand turnAway(float degreeAngle) {
        return turn(degreeAngle, true);
        //        float angleDif = MathUtils.getShortestRotation(ship.getFacing(), degreeAngle);
        //
        //        if(Math.abs(angleDif) < 5) return null;
        //
        //        ShipCommand direction = (angleDif <= 0) ? ShipCommand.TURN_LEFT : ShipCommand.TURN_RIGHT;
        //        ship.giveCommand(direction, null, 0);
        //
        //        return direction;
    }

    protected ShipCommand turnAway(Vector2f location) {
        return turnAway(VectorUtils.getAngle(self.getLocation(), location));
    }

    protected ShipCommand turnAway(CombatEntityAPI entity) {
        return turnAway(entity.getLocation());
    }

    protected void accelerate() {
        self.giveCommand(ShipCommand.ACCELERATE, null, 0);
    }

    protected void accelerateBackward() {
        self.giveCommand(ShipCommand.ACCELERATE_BACKWARDS, null, 0);
    }

    protected void decelerate() {
        self.giveCommand(ShipCommand.DECELERATE, null, 0);
    }

    protected void turnLeft() {
        self.giveCommand(ShipCommand.TURN_LEFT, null, 0);
    }

    protected void turnRight() {
        self.giveCommand(ShipCommand.TURN_RIGHT, null, 0);
    }

    protected void strafeLeft() {
        self.giveCommand(ShipCommand.STRAFE_LEFT, null, 0);
    }

    protected void strafeRight() {
        self.giveCommand(ShipCommand.STRAFE_RIGHT, null, 0);
    }

    protected void vent() {
        self.giveCommand(ShipCommand.VENT_FLUX, null, 0);
    }

    protected boolean useSystem() {
        boolean canDo = AIUtils.canUseSystemThisFrame(self);

        if (canDo) {
            self.giveCommand(ShipCommand.USE_SYSTEM, null, 0);
        }

        return canDo;
    }

    protected void toggleDefenseSystem() {
        self.giveCommand(ShipCommand.TOGGLE_SHIELD_OR_PHASE_CLOAK, null, 0);
    }

    protected void fireSelectedGroup(Vector2f at) {
        self.giveCommand(ShipCommand.FIRE, at, 0);
    }

    protected void toggleAutofire(int group) {
        self.giveCommand(ShipCommand.TOGGLE_AUTOFIRE, null, group);
    }

    protected void selectWeaponGroup(int group) {
        self.giveCommand(ShipCommand.SELECT_GROUP, null, group);
    }

    protected BaseAI(ShipAPI self) {
        this.self = self;
    }

    @Override
    public void advance(float amount) {
        if (self == null) {
            return;
        }

        circumstanceEvaluationTimer.advance(amount);
        if (circumstanceEvaluationTimer.intervalElapsed()) {
            evaluateCircumstances();
        }
    }

    @Override
    public void forceCircumstanceEvaluation() {
        evaluateCircumstances();
    }

    //No @Override
    protected void evaluateCircumstances() {
    }

    @Override
    public boolean needsRefit() {
        return false;
    }

    @Override
    public void cancelCurrentManeuver() {
    }

    @Override
    public void setDoNotFireDelay(float amount) {
        dontFireUntil = amount + Global.getCombatEngine().getTotalElapsedTime(false);
    }

    @Override
    public ShipwideAIFlags getAIFlags() {
        return AIFlags;
    }

    @Override
    public ShipAIConfig getConfig() {
        return AIConfig;
    }
}