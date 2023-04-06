package arc.hullmod;

import java.awt.Color;

import arc.ArcUtils;
import arc.Tokens;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.combat.MutableStat;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.Vector2f;

public class TemporalFluxCore extends BaseHullMod {
	static final float BASE_TIME_MULT = 1.05f;
	static final float MAX_TIME_MULT = 1.5f;
	static final float FLUX_LIMIT = 1.0f;
	
	static final float AFTERIMAGE_THRESHOLD = 0.4f;
	static final Color AFTERIMAGE_COLOR = new Color(156, 156, 156, 150);


	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return false;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if(index==0) return "" + (int)((BASE_TIME_MULT-1f) * 100f) + "%";
		if(index==1) return "" + (int)((MAX_TIME_MULT-1f) * 100f) + "%";
		if(index==2) return "" + (int)(100f * FLUX_LIMIT) + "%";
		else return null;
	}
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
		if (Global.getCombatEngine().isPaused()) {
            return;
        }
        boolean player = ship == Global.getCombatEngine().getPlayerShip();

        if ( !ship.isAlive() || ship.isPiece() ) {
            return;
        }
		
		float fluxLevel = ship.getFluxTracker().getFluxLevel();
		float timeMult = ArcUtils.clamp(BASE_TIME_MULT, MAX_TIME_MULT, ArcUtils.remap(0f, FLUX_LIMIT, BASE_TIME_MULT, MAX_TIME_MULT, fluxLevel));

		ship.getMutableStats().getTimeMult().modifyMult(Tokens.CORE, timeMult); //this is dumb
        if (player) {
            if (ship.isAlive()) {
				Global.getCombatEngine().getTimeMult().modifyPercent(Tokens.CORE, 1f / timeMult);
				Global.getCombatEngine().maintainStatusForPlayerShip(Tokens.CORE, "graphics/icons/hullsys/temporal_shell.png", "Temporal Flux Core", "Timeflow at " + timeMult + "%", false);
			} else {
				Global.getCombatEngine().getTimeMult().unmodify(Tokens.CORE);
			}
            
        }
				
		MutableStat trackerStat = ship.getMutableStats().getDynamic().getStat("ARC_TFCAfterimageTracker");
		trackerStat.modifyFlat("ARC_TFCAfterimageTrackerNullerID", -1);
		trackerStat.modifyFlat("ARC_TFCAfterimageTrackerID", trackerStat.getModifiedValue() + amount);
		if (trackerStat.getModifiedValue() > AFTERIMAGE_THRESHOLD) {

			// Sprite offset fuckery - Don't you love trigonometry?
			SpriteAPI sprite = ship.getSpriteAPI();
			float offsetX = sprite.getWidth()/2 - sprite.getCenterX();
			float offsetY = sprite.getHeight()/2 - sprite.getCenterY();

			float trueOffsetX = (float)FastTrig.cos(Math.toRadians(ship.getFacing()-90f))*offsetX - (float)FastTrig.sin(Math.toRadians(ship.getFacing()-90f))*offsetY;
			float trueOffsetY = (float)FastTrig.sin(Math.toRadians(ship.getFacing()-90f))*offsetX + (float)FastTrig.cos(Math.toRadians(ship.getFacing()-90f))*offsetY;

			MagicRender.battlespace(
					Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
					new Vector2f(ship.getLocation().getX()+trueOffsetX,ship.getLocation().getY()+trueOffsetY),
					new Vector2f(0, 0),
					new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
					new Vector2f(0, 0),
					ship.getFacing()-90f,
					0f,
					AFTERIMAGE_COLOR,
					true,
					0f,
					0f,
					0f
					// fadeout
			);

			trackerStat.modifyFlat("ARC_TFCAfterimageTrackerID", trackerStat.getModifiedValue() - AFTERIMAGE_THRESHOLD);

		}			
	}
}
