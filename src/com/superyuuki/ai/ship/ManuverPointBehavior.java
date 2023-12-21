package com.superyuuki.ai.ship;

import arc.Index;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipCommand;
import com.superyuuki.ai.IBehavior;
import com.superyuuki.ai.RootContext;
import com.superyuuki.ai.Status;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class ManuverPointBehavior implements IBehavior {

    final RootContext ctx; //passed on construction
    final Vector2f desiredPoint;

    public ManuverPointBehavior(RootContext ctx, Vector2f desiredPoint) {
        this.ctx = ctx;
        this.desiredPoint = desiredPoint;
    }

    @Override
    public Status run() {

        ShipAPI ship = ctx.ship;

        Vector2f currentPos = ship.getLocation();
        float currentHeading = ship.getFacing();

        float desiredHeadingDiff = VectorUtils.getAngle(currentPos, desiredPoint);
        float rotAngle = MathUtils.getShortestRotation(currentHeading, desiredHeadingDiff);

        CombatEngineAPI engine = Global.getCombatEngine();


        if (rotAngle < 67.5f && rotAngle > -67.5f) {
            ship.giveCommand(ShipCommand.ACCELERATE, null, 0);
            ship.blockCommandForOneFrame(ShipCommand.ACCELERATE_BACKWARDS);
            if (Index.DEBUG) {
                Vector2f point = MathUtils.getPointOnCircumference(ship.getLocation(), 100f, ship.getFacing());
                engine.addSmoothParticle(point, ship.getVelocity(), 30f, 1f, 0.1f, Color.green);
            }
        }
        if (rotAngle > 112.5f || rotAngle < -112.5f){
            ship.giveCommand(ShipCommand.ACCELERATE_BACKWARDS, null,0);
            ship.blockCommandForOneFrame(ShipCommand.ACCELERATE);
            if(Index.DEBUG) {
                Vector2f point = MathUtils.getPointOnCircumference(ship.getLocation(), 100f, ship.getFacing()+180f);
                engine.addSmoothParticle(point, ship.getVelocity(), 30f, 1f, 0.1f, Color.green);
            }
        }

        if (rotAngle > 22.5f && rotAngle < 157.5f){
            ship.giveCommand(ShipCommand.STRAFE_LEFT, null,0);
            ship.blockCommandForOneFrame(ShipCommand.STRAFE_RIGHT);
            if(Index.DEBUG) {
                Vector2f point = MathUtils.getPointOnCircumference(ship.getLocation(), 100f, ship.getFacing()+90f);
                engine.addSmoothParticle(point, ship.getVelocity(), 30f, 1f, 0.1f, Color.green);
            }
        }

        if (rotAngle < -22.5f && rotAngle > -157.5f){
            ship.giveCommand(ShipCommand.STRAFE_RIGHT, null,0);
            ship.blockCommandForOneFrame(ShipCommand.STRAFE_LEFT);
            if(Index.DEBUG) {
                Vector2f point = MathUtils.getPointOnCircumference(ship.getLocation(), 100f, ship.getFacing()-90f);
                engine.addSmoothParticle(point, ship.getVelocity(), 30f, 1f, 0.1f, Color.green);
            }
        }


        //TODO add PID controller and use error to determine when successful

        return Status.CONTINUE;
    }

    @Override
    public String report() {
        return "null";
    }

}
