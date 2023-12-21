package arc.hullmod.auxiliary;

import arc.hullmod.ARCBaseHullmod;
import arc.hullmod.IHullmodPart;
import arc.util.ARCUtils;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import java.awt.*;

public class ArchotechAux extends ARCBaseHullmod {

    //bonus
    static float UNFOLD_RATE = 50f;
    static float SHIELD_EFF = 0.35f;
    static float CAP = 20f;

    //malus
    static float DISSP = -10f;

    public ArchotechAux() {
        super(new IHullmodPart[]{});
    }


    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldUnfoldRateMult().modifyFlat(id, UNFOLD_RATE);
        stats.getShieldAbsorptionMult().modifyFlat(id, -SHIELD_EFF);
        stats.getFluxDissipation().modifyPercent(id, DISSP);
        stats.getFluxCapacity().modifyPercent(id, CAP);


    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        //TODO is this bad to do every frame? oh well..
        final ShieldAPI shield = ship.getShield();
        final float radius = ship.getHullSpec().getShieldSpec().getRadius();
        String inner;
        String outer;
        if (radius >= 256.0f) {
            inner = "graphics/ARC/fx/hexshield256.png";
            outer = "graphics/ARC/fx/shields256ring.png";
        }
        else if (radius >= 128.0f) {
            inner = "graphics/ARC/fx/hexshield128.png";
            outer = "graphics/ARC/fx/shields128ring.png";
        }
        else {
            inner = "graphics/ARC/fx/hexshield64.png";
            outer = "graphics/ARC/fx/shields64ring.png";
        }


        Color toUseForStuff = new Color(50, 120, (int) ARCUtils.clamp(0,200, 255), 190);

        ship.setJitterShields(true);
        ship.getShield().setInnerColor(toUseForStuff);
        ship.setJitterUnder(ship, toUseForStuff, 0.5f * 3.0f, (int)(3.0f * 0.6), 1);


        shield.setRadius(radius, inner, outer);
        shield.setInnerRotationRate(0.02f); //super slow
        shield.setRingRotationRate(0f); //do not rotate, use circle


    }
}
