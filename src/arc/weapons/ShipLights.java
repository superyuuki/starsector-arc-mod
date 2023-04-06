package arc.weapons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;

import java.awt.*;

public class ShipLights implements EveryFrameWeaponEffectPlugin {
    private static final float[] COLOR_NORMAL = {255f/255f, 255f/255f, 255f/255f};
    private static final float[] COLOR_VENTING = {255f/255f, 80f/255f, 80f/255f};


    private static final float MAX_OPACITY = 1.0f;
    private static final float MIN_OPACITY = 0.2f;

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
        float currentBrightness = ship.getFluxLevel();
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

        //Brightness clamp, cause there's some weird cases with flux level > 1f, I guess
        currentBrightness = Math.min(1f, currentBrightness);

        //Now, set the color to the one we want, and include opacity
        Color colorToUse = new Color(COLOR_NORMAL[0], COLOR_NORMAL[1], COLOR_NORMAL[2], currentBrightness*MAX_OPACITY);

        if (isVenting) {
            colorToUse = new Color(COLOR_VENTING[0], COLOR_VENTING[1], COLOR_VENTING[2], currentBrightness*MAX_OPACITY);
        }

        //And finally actually apply the color
        weapon.getSprite().setColor(colorToUse);
    }
}