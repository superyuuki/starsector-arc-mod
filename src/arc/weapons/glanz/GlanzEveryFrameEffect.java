package arc.weapons.glanz;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import data.scripts.util.MagicTargeting;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class GlanzEveryFrameEffect implements EveryFrameWeaponEffectPlugin {

    private boolean runOnce=false, IPDAI=false;
    private Map <Integer,MissileAPI> BEAMS = new HashMap<>();

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

        if (!runOnce){
            runOnce=true;

            weapon.ensureClonedSpec();
            for(int i=1; i<weapon.getSpec().getHardpointAngleOffsets().size(); i++){
                BEAMS.put(i, null);
            }

            if(weapon.getShip().getMutableStats().getDynamic().getMod(Stats.PD_IGNORES_FLARES).getFlatBonus()>0){
                IPDAI=true;
            }
        }

        if (engine.isPaused() || weapon.isDisabled()) {
            return;
        }

        if(weapon.isFiring()){
            //get all rearby missiles

            for(int i : BEAMS.keySet()){
                //does the beam has a target
                if(BEAMS.get(i)==null || !engine.isEntityInPlay(BEAMS.get(i))){
                    //find target
                    MissileAPI target = MagicTargeting.randomMissile(
                            weapon.getShip(),
                            MagicTargeting.missilePriority.DAMAGE_PRIORITY,
                            weapon.getLocation(),
                            weapon.getCurrAngle(),
                            180,
                            (int)weapon.getRange(),
                            IPDAI
                    );

                    if(target!=null){
                        //found a suitable target
                        BEAMS.put(i, target);
                        //hit that target
                        float angle=VectorUtils.getAngle(weapon.getLocation(), BEAMS.get(i).getLocation());
                        angle = MathUtils.getShortestRotation(weapon.getCurrAngle(), angle);

                        weapon.getSpec().getHardpointAngleOffsets().set(i,angle);
                        weapon.getSpec().getTurretAngleOffsets().set(i,angle);

                        weapon.getSpec().getHardpointFireOffsets().set(i, MathUtils.getPoint(new Vector2f(), 5, angle));
                        weapon.getSpec().getTurretFireOffsets().set(i, MathUtils.getPoint(new Vector2f(), 5, angle));
                    } else {
                        //no target, reset to forward firing
                        //TODO work sharing, enemy surpressing
                        BEAMS.put(i, null);
                        weapon.getSpec().getHardpointAngleOffsets().set(i,0f);
                        weapon.getSpec().getTurretAngleOffsets().set(i,0f);
                        weapon.getSpec().getHardpointFireOffsets().set(i, new Vector2f(5,0));
                        weapon.getSpec().getTurretFireOffsets().set(i, new Vector2f(5,0));
                    }
                } else if(
                        !MathUtils.isWithinRange(BEAMS.get(i).getLocation(),weapon.getLocation(),weapon.getRange())
                                || Math.abs(MathUtils.getShortestRotation(VectorUtils.getAngle(weapon.getLocation(), BEAMS.get(i).getLocation()),weapon.getCurrAngle()))>45
                ){
                    //debug
//                    engine.addFloatingText(
//                            BEAMS.get(i).getLocation(),
//                            ""+(int)Math.abs(VectorUtils.getAngle(weapon.getLocation(), BEAMS.get(i).getLocation())),
//                            15,
//                            Color.yellow,
//                            BEAMS.get(i),
//                            1,
//                            1
//                    );
                    //target left engagement arc
                    BEAMS.put(i, null);
                    weapon.getSpec().getHardpointAngleOffsets().set(i,0f);
                    weapon.getSpec().getTurretAngleOffsets().set(i,0f);
                    weapon.getSpec().getHardpointFireOffsets().set(i, new Vector2f(5,0));
                    weapon.getSpec().getTurretFireOffsets().set(i, new Vector2f(5,0));
                } else {
                    //keep hitting that target
                    float angle=VectorUtils.getAngle(weapon.getLocation(), BEAMS.get(i).getLocation());
                    angle = MathUtils.getShortestRotation(weapon.getCurrAngle(), angle);

                    weapon.getSpec().getHardpointAngleOffsets().set(i,angle);
                    weapon.getSpec().getTurretAngleOffsets().set(i,angle);

                    weapon.getSpec().getHardpointFireOffsets().set(i, MathUtils.getPoint(new Vector2f(), 5, angle));
                    weapon.getSpec().getTurretFireOffsets().set(i, MathUtils.getPoint(new Vector2f(), 5, angle));
                }
            }
        }
    }
}
