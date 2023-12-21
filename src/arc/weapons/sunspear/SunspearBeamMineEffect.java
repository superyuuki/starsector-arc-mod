package arc.weapons.sunspear;

import arc.util.ARCUtils;
import arc.weapons.ArcChargeupWeaponEffect;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lwjgl.util.vector.Vector2f;

import java.util.Iterator;
import java.util.List;

import static com.fs.starfarer.api.impl.combat.RiftBeamEffect.TARGET_RANGE;

public class SunspearBeamMineEffect extends ArcChargeupWeaponEffect {

    static final float RIFT_RANGE = 150f;
    final IntervalUtil burstBeamInterval = new IntervalUtil(0.025f, 0.05f);


    public void advance(final float n, final CombatEngineAPI combatEngineAPI, final WeaponAPI weaponAPI) {
        super.advance(n, combatEngineAPI, weaponAPI);

        final List<BeamAPI> beams = weaponAPI.getBeams();
        if (beams.isEmpty()) {
            return;
        }
        final BeamAPI beamAPI = beams.get(0);
        if (beamAPI.getBrightness() < 1.0f) {
            return;
        }
        burstBeamInterval.advance(n * 2.0f);
        if (burstBeamInterval.intervalElapsed()) {
            if (beamAPI.getLengthPrevFrame() < 10.0f) {
                return;
            }
            final CombatEntityAPI target = this.findTarget(beamAPI, beamAPI.getWeapon(), combatEngineAPI);
            Vector2f vector2f8;
            if (target == null || Math.random() < 0.25) {
                vector2f8 = this.pickNoTargetDest(beamAPI, beamAPI.getWeapon(), combatEngineAPI);
            }
            else {
                vector2f8 = target.getLocation();
            }
            final Vector2f closestPointOnSegmentToPoint = Misc.closestPointOnSegmentToPoint(beamAPI.getFrom(), beamAPI.getRayEndPrevFrame(), vector2f8);
            final Vector2f unitVectorAtDegreeAngle = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(closestPointOnSegmentToPoint, vector2f8));
            unitVectorAtDegreeAngle.scale(Math.min(Misc.getDistance(closestPointOnSegmentToPoint, vector2f8), RIFT_RANGE));
            Vector2f.add(closestPointOnSegmentToPoint, unitVectorAtDegreeAngle, unitVectorAtDegreeAngle);
            ARCUtils.spawnMine(beamAPI.getSource(), unitVectorAtDegreeAngle, "arc_sunspear_minelayer");
        }
    }

    public Vector2f pickNoTargetDest(final BeamAPI beamAPI, final WeaponAPI weaponAPI, final CombatEngineAPI combatEngineAPI) {
        Vector2f from = beamAPI.getFrom();
        Vector2f rayEndPrevFrame = beamAPI.getRayEndPrevFrame();
        float lengthPrevFrame = beamAPI.getLengthPrevFrame();


        //get a vec a little ways away from the pt
        float n = 0.25f + (float)Math.random() * 0.75f;
        Vector2f unitVectorAtDegreeAngle = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(from, rayEndPrevFrame));
        unitVectorAtDegreeAngle.scale(lengthPrevFrame * n);
        Vector2f.add(from, unitVectorAtDegreeAngle, unitVectorAtDegreeAngle);

        return Misc.getPointWithinRadius(unitVectorAtDegreeAngle, RIFT_RANGE);
    }

    public CombatEntityAPI findTarget(final BeamAPI beamAPI, final WeaponAPI weaponAPI, final CombatEngineAPI combatEngineAPI) {
        final Vector2f rayEndPrevFrame = beamAPI.getRayEndPrevFrame();
        final Iterator<Object> checkIterator = Global.getCombatEngine().getAllObjectGrid().getCheckIterator(rayEndPrevFrame, RIFT_RANGE * 2.0f, RIFT_RANGE * 2.0f);
        final int owner = weaponAPI.getShip().getOwner();
        final WeightedRandomPicker<CombatEntityAPI> weightedRandomPicker = new WeightedRandomPicker<>();
        while (checkIterator.hasNext()) {
            final CombatEntityAPI next = (CombatEntityAPI) checkIterator.next();
            if (!(next instanceof MissileAPI) && !(next instanceof ShipAPI)) {
                continue;
            }
                if (next.getOwner() == owner) {
                continue;
            }
            if (next instanceof ShipAPI) {
                final ShipAPI shipAPI = (ShipAPI) next;
                if (!shipAPI.isFighter() && !shipAPI.isDrone()) {
                    continue;
                }
            }
            if (Misc.getDistance(Misc.closestPointOnSegmentToPoint(beamAPI.getFrom(), beamAPI.getRayEndPrevFrame(), next.getLocation()), next.getLocation()) - Misc.getTargetingRadius(rayEndPrevFrame, next, false) > TARGET_RANGE) {
                continue;
            }
            weightedRandomPicker.add(next);
        }
        return weightedRandomPicker.pick();
    }


}
