package arc.hullmod.microshunt;

import arc.Index;
import arc.hullmod.ARCData;
import arc.hullmod.IHullmodPart;
import arc.util.ARCUtils;
import arc.weapons.buster.BusterOnHit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.WaveDistortion;
import org.lazywizard.lazylib.combat.CombatUtils;

import java.awt.*;

public class IEDPart implements IHullmodPart<ARCData> {

    final IntervalUtil basicallyOneSecond = new IntervalUtil(0f, 1f);



    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, ARCData customData) {
        if (!customData.shouldTryToIED) return; //dont care

        if (customData.timerTicksBeforeJihad == ARCData.JIHAD_TICKS) {
            //SYSTEM OVERRIDE
            shipAPI.getMutableStats().getMaxSpeed().modifyFlat("lol", 400);
            shipAPI.getMutableStats().getAcceleration().modifyMult("lol", 2f);
            shipAPI.getMutableStats().getMaxTurnRate().modifyMult("lol",0.7f);
            Global.getCombatEngine().addFloatingText(shipAPI.getLocation(), "SELF TERMINATION ENGAGED", 50f, Color.RED, shipAPI, 2f, 2f);

            CombatUtils.applyForce(shipAPI, shipAPI.getFacing(), 20f);

        }

        //TODO stretch space shader

        if (customData.iedPrep == null) {
            WaveDistortion distortion = new WaveDistortion();
            distortion.setLocation(shipAPI.getLocation());
            distortion.setIntensity(shipAPI.getShield().getRadius() * 0.10f);
            distortion.fadeInSize(1.2f);
            distortion.fadeOutIntensity(0.9f);
            distortion.setSize(shipAPI.getShield().getRadius() * 0.35f);
            DistortionShader.addDistortion(distortion);

            customData.iedPrep = distortion;
        } else {
            customData.iedPrep.setLocation(shipAPI.getLocation());
            customData.iedPrep.setSize(shipAPI.getShield().getRadius() * (customData.timerTicksBeforeJihad / 80));
        }



        //we are jihading
        if (customData.timerTicksBeforeJihad >= 0) {
            customData.timerTicksBeforeJihad -= timestep;
        }

        if (customData.timerTicksBeforeJihad < 0) {
            customData.timerTicksBeforeJihad = 0;
        }


        //move



        if (customData.timerTicksBeforeJihad == 0) {


            Global.getCombatEngine().applyDamage(shipAPI, shipAPI.getLocation(), 9999999f, DamageType.HIGH_EXPLOSIVE, 0f, true, false, shipAPI);

            //boom

            //TODO make this actually resonant (do damage based on enemy hull size) using plugins / listeners
            BusterOnHit.resonantExplosion(
                    101,
                    2000f,
                    20f,
                    shipAPI.getLocation(),
                    shipAPI,
                    null,
                    ARCUtils.decideBasedOnHullSize(
                            shipAPI,
                            500f,
                            600f,
                            800f,
                            1200f
                    ),
                    ARCUtils.decideBasedOnHullSize(
                            shipAPI,
                            350f,
                            400f,
                            500f,
                            600f
                    ),
                    ARCUtils.decideBasedOnHullSize(
                            shipAPI,
                            0.6f,
                            0.5f,
                            0.4f,
                            0.3f
                    ),
                    true
            );
        }




    }

    @Override
    public boolean hasData() {
        return true;
    }

    @Override
    public boolean makesNewData() {
        return true;
    }

    @Override
    public ARCData makeNew() {
        return new ARCData();
    }

    @Override
    public String makeKey() {
        return Index.ARC_DATA;
    }
}
