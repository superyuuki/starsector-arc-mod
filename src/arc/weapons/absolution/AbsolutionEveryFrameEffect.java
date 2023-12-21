package arc.weapons.absolution;

import arc.weapons.ArcBaseEveryFrameWeaponEffect;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class AbsolutionEveryFrameEffect extends ArcBaseEveryFrameWeaponEffect {

    final IntervalUtil util = new IntervalUtil(0.05f, 0.15f);


    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        super.advance(amount, engine, weapon);
        util.advance(amount);

        ShipAPI ship = weapon.getShip();
        float chargeLevel = weapon.getChargeLevel();
        float facing = weapon.getCurrAngle();

        if (weapon.getChargeLevel() > 0 && !weapon.isDisabled() && weapon.getCooldownRemaining() <= 0) {

            Global.getSoundPlayer().playLoop("arc_yesod_spinal_charge", weapon, 1f, 1f, weapon.getLocation(), new Vector2f(), 0, 0.035f);

        }

        if (!util.intervalElapsed()) return;
        if (chargeLevel < 0.1f || weapon.getCooldownRemaining() > 0) return;

        Vector2f goBackAllTheWay = new Vector2f(-180, 0);
        VectorUtils.rotate(goBackAllTheWay, facing, goBackAllTheWay);


        Vector2f goBackThisMuch = new Vector2f(-180 * Math.max(1 - chargeLevel, 0), 0);
        VectorUtils.rotate(goBackThisMuch, facing, goBackThisMuch);

        Vector2f shotLocation = weapon.getFirePoint(0);

        Vector2f.add(shotLocation, goBackAllTheWay, goBackAllTheWay);
        Vector2f.add(shotLocation, goBackThisMuch, goBackThisMuch);

        engine.spawnEmpArc(
                ship,
                goBackAllTheWay,
                ship,
                new AnchoredEntity(ship, goBackThisMuch),
                DamageType.ENERGY,
                0.0f,
                0.0f,
                0.0f,
                null,
                MathUtils.getRandomNumberInRange(5, 8f), Color.CYAN, Color.WHITE
        );

        engine.addSmoothParticle(goBackThisMuch, ship.getVelocity(), 150f, 1.0f, 0.1f, Color.CYAN);


    }
}
