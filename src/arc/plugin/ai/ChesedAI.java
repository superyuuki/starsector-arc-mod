package arc.plugin.ai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import org.lazywizard.lazylib.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class ChesedAI extends BaseAI {

    //priorities stuff from Sundog
    static final float UPDATE_FREQ = 5f;
    static final Map<ShipAPI, Integer> GLOBAL_ASSISTANCE = new HashMap<>();
    static final Map<ShipAPI, Float> GLOBAL_PRIORITIES = new HashMap<>();

    static float nextTimestamp = 0f;

    final ShipAPI carrier;

    public ChesedAI(ShipAPI ship, ShipAPI carrier) {
        super(ship);
        this.carrier = carrier;
    }

    ChesedState currentState = ChesedState.IDLE;
    int repairTicksLeft = 0;




    @Override
    protected void evaluateCircumstances() {


        //static singleton in instance LOL (sundog)
        if (
                nextTimestamp <= Global.getCombatEngine().getTotalElapsedTime(false) ||
                nextTimestamp > Global.getCombatEngine().getTotalElapsedTime(false) + UPDATE_FREQ
        ) {
            for (ShipAPI ship : Global.getCombatEngine().getShips()) {
                if (!ship.isAlive()) continue;
                if (!ship.getHullSpec().getHullId().contentEquals("arc_chesed")) {
                    if (!ship.isDrone() && !ship.isFighter()) {
                        GLOBAL_PRIORITIES.put(ship, calculatePriorityOfShip(ship));
                    }
                } else {
                    ShipAPI chesedTarget = ship.getShipTarget();
                    Integer current = GLOBAL_ASSISTANCE.get(chesedTarget);

                    if (GLOBAL_ASSISTANCE.containsKey(chesedTarget)) {
                        GLOBAL_ASSISTANCE.put(chesedTarget, current + 1);
                    } else {
                        GLOBAL_ASSISTANCE.put(chesedTarget, 1);
                    }
                }
            }

            //delta forward, block out other cheseds
            nextTimestamp = Global.getCombatEngine().getTotalElapsedTime(false) + UPDATE_FREQ;
        }

        //state machine

        ChesedState desiredState = null;

        if (needsRefit() || carrier.isPullBackFighters()) {
            desiredState = ChesedState.MOVING_TO_HOME;
            self.setShipTarget(carrier);
        } else if (currentState != ChesedState.REPAIRING) {
            float record = 0f;
            ShipAPI leader = null;

            for (Map.Entry<ShipAPI, Float> entry : GLOBAL_PRIORITIES.entrySet()) {
                ShipAPI s = entry.getKey();
                float ranking = entry.getValue();

                if (s.getOwner() != this.self.getOwner() || s.isDrone() || s.isFighter()) {
                    continue;
                }

                float score = ranking / (500f + MathUtils.getDistance(this.self, s));

                if (score > record) {
                    record = score;
                    leader = s;
                }
            }

            if (leader != null) {
                desiredState = ChesedState.MOVING_TO_TARGET;
                self.setShipTarget(leader);
            }

        }

        if (desiredState != null) {
            //change
        }

        //otherwise keep doing what you were doing!

/*


        ShipAPI previousTarget = target;
        setTarget(chooseTarget());

        if (returning) {
            targetOffset = toRelative(target, system.getLandingLocation(self));
        } else if (target != previousTarget || ticksLeftToMx < 1) {
            ticksLeftToMx = 5;

            do {
                targetOffset = MathUtils.getRandomPointInCircle(target.getLocation(), target.getCollisionRadius());
            } while (!CollisionUtils.isPointWithinBounds(targetOffset, target));

            targetOffset = toRelative(target, targetOffset);
        }

        if (!target.isPhased() && !returning && MathUtils.getDistance(self, target) < REPAIR_RANGE && mxPriorities.containsKey(target) && mxPriorities.get(target) > 0) {
            performMaintenance();
        } else {
            doingMx = false;
        }*/
    }

    float calculatePriorityOfShip(ShipAPI shipAPI) {
        float priority = 0f;

        for (WeaponAPI weapon : self.getUsableWeapons()) {
            if (!weapon.usesAmmo()) continue;
            if (weapon.getAmmoPerSecond() > 0) continue;

            priority += (1f - weapon.getAmmo() / (float) weapon.getMaxAmmo()) * weapon.getSpec().getOrdnancePointCost(null);
        }

        priority /= (2f + GLOBAL_ASSISTANCE.getOrDefault(self, 0));

        FleetMemberAPI member = self.getFleetMember();
        float fp = member == null ? 0f : member.getFleetPointCost();

        float totalArmor = 0;
        int width = self.getArmorGrid().getGrid().length;
        int height = self.getArmorGrid().getGrid()[0].length;

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                totalArmor += self.getArmorGrid().getArmorFraction(x, y);
            }
        }

        float armorPercent = totalArmor / (width * height);


        if (self == Global.getCombatEngine().getPlayerShip()) {
            priority *= 2f;
        }

        return priority;
    }

    @Override
    public void advance(float amount) {
        repairTicksLeft--;



        if (repairTicksLeft < 0) repairTicksLeft = 0;
    }
}
