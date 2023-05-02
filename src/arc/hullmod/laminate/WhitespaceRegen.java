package arc.hullmod.laminate;

import arc.ARCUtils;
import arc.hullmod.HullmodPart;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.DamageListener;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class WhitespaceRegen implements HullmodPart<Void> {

    static final Map<ShipAPI.HullSize,Float> ARMOR_REGEN;

    static {
        HashMap<ShipAPI.HullSize, Float> map = new LinkedHashMap<>();

        map.put(ShipAPI.HullSize.FRIGATE, 20f);
        map.put(ShipAPI.HullSize.DESTROYER, 15f);
        map.put(ShipAPI.HullSize.CRUISER, 10f);
        map.put(ShipAPI.HullSize.CAPITAL_SHIP, 10f);

        ARMOR_REGEN = map;
    }

    public static String armorRegen() {
        StringBuilder compound = new StringBuilder();

        for (Map.Entry<ShipAPI.HullSize, Float> entry : ARMOR_REGEN.entrySet()) {
            compound.append(Math.abs(entry.getValue())).append("%").append("/");
        }

        return compound.toString();
    }

    @Override
    public void advanceSafely(CombatEngineAPI engineAPI, ShipAPI shipAPI, float timestep, Void customData) {
        //regenerate the fuckin armor!
        if (shipAPI.getSystem().getState() == ShipSystemAPI.SystemState.IN && !shipAPI.isFighter()) {
            ArmorGridAPI armorGrid = shipAPI.getArmorGrid();
            float toRegenerate = armorGrid.getArmorRating() * ARMOR_REGEN.get(shipAPI.getHullSize());
            float[][] armorArray = armorGrid.getGrid();
            float cellSize = armorGrid.getCellSize();


            for (int i = 0; i < armorArray.length; i++) {
                for (int j = 0; j < armorArray[i].length; j++) {
                    armorGrid.setArmorValue(
                            i,j,
                            Math.min(
                                    armorGrid.getArmorRating(),
                                    armorArray[i][j] + toRegenerate
                            )
                    );
                    Vector2f cellLoc = getCellLocation(shipAPI, i, j);
                    cellLoc.x += cellSize * 0.1f - cellSize * (float) Math.random();
                    cellLoc.y += cellSize * 0.1f - cellSize * (float) Math.random();
                    Global.getCombatEngine().addHitParticle(cellLoc,
                            shipAPI.getVelocity(),
                            (7f * (float) Math.random()) + 5f,
                            0.9f,
                            0.35f,
                            SPARK_COLOR);

                    Global.getCombatEngine().spawnExplosion(cellLoc, shipAPI.getVelocity(), SPARK_COLOR, 20f, 0.2f);

                }
            }

        }
    }

    public static Vector2f getCellLocation(ShipAPI ship, float x, float y) {
        float xx = x - (ship.getArmorGrid().getGrid().length / 2f);
        float yy = y - (ship.getArmorGrid().getGrid()[0].length / 2f);
        float cellSize = ship.getArmorGrid().getCellSize();
        Vector2f cellLoc = new Vector2f();
        float theta = (float) (((ship.getFacing() - 90f) / 360f) * (Math.PI * 2.0));
        cellLoc.x = (float) (xx * Math.cos(theta) - yy * Math.sin(theta)) * cellSize + ship.getLocation().x;
        cellLoc.y = (float) (xx * Math.sin(theta) + yy * Math.cos(theta)) * cellSize + ship.getLocation().y;
        return cellLoc;
    }

    private static final Color SPARK_COLOR = new Color(186, 220, 218);

}
