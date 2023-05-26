package arc.weapons;

import arc.Index;
import arc.hullmod.ARCData;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;

import java.awt.*;

public class ShipLights implements EveryFrameWeaponEffectPlugin {
    private static final int[] COLOR_NORMAL = {255, 255, 255};
    private static final int[] COLOR_VENTING = {255, 80, 80};


    private static final int MAX_OPACITY = 255;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        ShipAPI ship = weapon.getShip();
        if (ship == null) {
            return;
        }

        //Brightness based on flux under normal conditions
        float currentBrightness = Math.min(ship.getFluxLevel() + 0.1f, 1.0f);
		boolean isVenting = ship.getFluxTracker().isVenting();
		if(isVenting){
			currentBrightness = 0.5f;
		}
		if(ship.getFluxTracker().isOverloaded()){
			currentBrightness = 0f;
		}
		
        //No glows on wrecks or in refit
        if ( ship.isPiece() || !ship.isAlive() || ship.getOriginalOwner() == -1) {
            currentBrightness = 0f;
        }

        //Switches to the proper sprite
        if (currentBrightness > 0) {
            weapon.getAnimation().setFrame(1);
        } else {
            weapon.getAnimation().setFrame(0);
        }

        ARCData data = (ARCData) ship.getCustomData().get(Index.ARC_DATA);


        switch (data.mode) {

            case SUPPRESSION:
                break;
            case JAMMER:
                break;
            case GLIDE:
                break;
            case BERSERK:
                break;
            case GLIDE_DRIVE:
                break;
        }

        currentBrightness = Math.min(1f, currentBrightness);
        Color colorToUse = new Color(COLOR_NORMAL[0], COLOR_NORMAL[1], COLOR_NORMAL[2], (int)(currentBrightness*MAX_OPACITY));

        if (isVenting) {
            colorToUse = new Color(COLOR_VENTING[0], COLOR_VENTING[1], COLOR_VENTING[2], (int)(currentBrightness*MAX_OPACITY));
        }

        //And finally actually apply the color
        weapon.getSprite().setColor(colorToUse);
    }
}