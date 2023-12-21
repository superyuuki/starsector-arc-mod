package arc.weapons.tenebre;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;


//handles charging effect????
public class TenebreBeamEffect {

    final IntervalUtil fireInterval = new IntervalUtil(0.2f, 0.3f);;

    static final Vector2f ZERO = new Vector2f();
    static final float CHARGEUP_PARTICLE_ANGLE_SPREAD = 150.0f;
    static final float A_2 = CHARGEUP_PARTICLE_ANGLE_SPREAD / 2.0f;
    static final float CHARGEUP_PARTICLE_BRIGHTNESS = 1f;
    static final float CHARGEUP_PARTICLE_DISTANCE_MAX = 150f;
    static final float CHARGEUP_PARTICLE_DISTANCE_MIN = 100f;
    static final float CHARGEUP_PARTICLE_DURATION = 0.5f;
    static final float CHARGEUP_PARTICLE_SIZE_MAX = 5f;
    static final float CHARGEUP_PARTICLE_SIZE_MIN = 1f;
    static final float TURRET_OFFSET = 20f;

    boolean cooling = false;
    boolean runOnce = false;
    boolean wasZero = true;

    float level = 0f;


    public void advance(final float n, final CombatEngineAPI combatEngineAPI, final BeamAPI beamAPI) {
        final CombatEntityAPI damageTarget = beamAPI.getDamageTarget();
        final WeaponAPI weapon = beamAPI.getWeapon();
        final float width = beamAPI.getWidth();
        final ShipAPI ship = weapon.getShip();
        if (weapon.getChargeLevel() >= 1.0f && !this.runOnce) {
            this.runOnce = true;
        }
        if (damageTarget != null) {
            float dpsDuration = beamAPI.getDamage().getDpsDuration();
            if (!this.wasZero) {
                dpsDuration = 0.0f;
            }
            this.wasZero = (beamAPI.getDamage().getDpsDuration() <= 0.0f);
            this.fireInterval.advance(dpsDuration);
            final Vector2f vector2f = new Vector2f(weapon.getLocation());
            final Vector2f vector2f2 = new Vector2f(TURRET_OFFSET, -0.0f);
            VectorUtils.rotate(vector2f2, weapon.getCurrAngle(), vector2f2);
            Vector2f.add(vector2f2, vector2f, vector2f);
            weapon.getCurrAngle();
            weapon.getShip().getVelocity();
            final Vector2f sub = Vector2f.sub(beamAPI.getTo(), beamAPI.getFrom(), new Vector2f());
            if (sub.lengthSquared() > 0.0f) {
                sub.normalise();
            }
            sub.scale(50.0f);
            Vector2f.sub(beamAPI.getTo(), sub, new Vector2f());



            if (weapon.isFiring() || weapon.getChargeLevel() > 0.0f) {
                final float n2 = 30.0f + weapon.getChargeLevel() * weapon.getChargeLevel() * MathUtils.getRandomNumberInRange(25.0f, 75.0f);
                final float currAngle = beamAPI.getWeapon().getCurrAngle();
                if ((float)Math.random() <= 0.25f) {
                    for (int n3 = 1 + (int)(weapon.getChargeLevel() * 5.0f), i = 0; i < n3; ++i) {
                        final float n4 = 0.75f * (MathUtils.getRandomNumberInRange(CHARGEUP_PARTICLE_DISTANCE_MIN + 1.0f, CHARGEUP_PARTICLE_DISTANCE_MAX + 1.0f) * weapon.getChargeLevel()) / this.CHARGEUP_PARTICLE_DURATION * weapon.getChargeLevel();
                        combatEngineAPI.addHitParticle(beamAPI.getTo(), MathUtils.getPointOnCircumference(null, MathUtils.getRandomNumberInRange(n4 * -1.0f, n4 * -1.5f), MathUtils.getRandomNumberInRange(currAngle - this.A_2, currAngle + this.A_2)), MathUtils.getRandomNumberInRange(this.CHARGEUP_PARTICLE_SIZE_MIN + 1.0f, this.CHARGEUP_PARTICLE_SIZE_MAX + 1.0f) * weapon.getChargeLevel(), this.CHARGEUP_PARTICLE_BRIGHTNESS * Math.min(weapon.getChargeLevel() + 0.5f, 1.0f) * MathUtils.getRandomNumberInRange(0.75f, 1.25f), this.CHARGEUP_PARTICLE_DURATION, beamAPI.getFringeColor());
                    }
                }
                if (Math.random() <= 0.05000000074505806) {
                    combatEngineAPI.addHitParticle(beamAPI.getTo(), ZERO, n2, 0.1f + weapon.getChargeLevel() * 0.3f, 0.2f, beamAPI.getCoreColor());
                    combatEngineAPI.addHitParticle(beamAPI.getTo(), ZERO, n2 / 2.0f, 0.1f + weapon.getChargeLevel() * 0.3f, 0.2f, beamAPI.getFringeColor());
                }
            }
            if (this.cooling && weapon.getChargeLevel() <= 0.0f) {
                this.cooling = false;
            }
        }
        this.level = weapon.getChargeLevel();
        if (this.runOnce) {
            final float n5 = width * 0.1f * MathUtils.getDistance(beamAPI.getTo(), beamAPI.getFrom()) * n * 0.15f * weapon.getChargeLevel();
            for (int n6 = 0; n6 < n5; ++n6) {
                final Vector2f randomPointInCircle = MathUtils.getRandomPointInCircle(MathUtils.getRandomPointOnLine(beamAPI.getFrom(), beamAPI.getTo()), width * 0.1f);
                if (Global.getCombatEngine().getViewport().isNearViewport(randomPointInCircle, 30.0f)) {
                    final Vector2f randomPointInCircle2 = MathUtils.getRandomPointInCircle(new Vector2f(ship.getVelocity().x * 0.5f, ship.getVelocity().y * 0.5f), 50.0f);
                    if ((float)Math.random() <= 0.05f) {
                        combatEngineAPI.addNebulaParticle(randomPointInCircle, randomPointInCircle2, 40.0f * (0.75f + (float)Math.random() * 0.5f), MathUtils.getRandomNumberInRange(1.0f, 3.0f), 0.0f, 0.0f, 1.0f, new Color(beamAPI.getFringeColor().getRed(), beamAPI.getFringeColor().getGreen(), beamAPI.getFringeColor().getBlue(), 100), true);
                    }
                    combatEngineAPI.addSmoothParticle(randomPointInCircle, randomPointInCircle2, MathUtils.getRandomNumberInRange(5.0f, 10.0f), weapon.getChargeLevel(), MathUtils.getRandomNumberInRange(0.4f, 0.9f), beamAPI.getFringeColor());
                }
            }
        }
    }


}
