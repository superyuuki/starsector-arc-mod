package arc.weapons.glanz;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.FastTrig;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class GlanzBeamEffect implements BeamEffectPlugin {

    private float width, count=0;
    final IntervalUtil timer = new IntervalUtil(0.1f,0.1f);
    float offset = MathUtils.getRandomNumberInRange(0f, (float) (Math.PI * 2));

    boolean runOnce = false;
    boolean hasFired=false;
    Color fringe;
    final IntervalUtil beamWiggle = new IntervalUtil(0.05f,0.1f);



    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        beamWiggle.advance(amount);
        if(!runOnce){
            runOnce=true;
            width = 12;
            fringe = beam.getFringeColor();
        }

        if(beam.getBrightness()==1) {
            Vector2f start = beam.getFrom();
            Vector2f end = beam.getTo();

            if (MathUtils.getDistanceSquared(start, end)==0){
                return;
            }

            float theWidth = width * ( 0.5f * (float) FastTrig.cos( 20*MathUtils.FPI * Math.min(timer.getElapsed(),0.05f) + offset) + 0.5f ) ;
            beam.setWidth(theWidth);


            timer.advance(amount);

            boolean shouldPlayEffect = MagicRender.screenCheck(0.5f, beam.getTo());


            if (timer.intervalElapsed()) hasFired=false;
            if (beamWiggle.intervalElapsed()){

                if (beam.getWidth() > 0 && shouldPlayEffect) {

                    float blastDamage = beam.getDamage().getDamage() * 0.1f;

                    DamagingExplosionSpec blast = new DamagingExplosionSpec(0.2f,
                            10f,
                            8f,
                            blastDamage,
                            blastDamage,
                            CollisionClass.PROJECTILE_FF,
                            CollisionClass.PROJECTILE_FIGHTER,
                            2f,
                            2f,
                            0.3f,
                            30,
                            new Color(231, 151, 112, 110),
                            new Color(255, 115,0, 42));
                    blast.setDamageType(DamageType.FRAGMENTATION);
                    blast.setShowGraphic(true);
                    blast.setUseDetailedExplosion(false);

                    Global.getCombatEngine().spawnDamagingExplosion(blast, beam.getSource(), beam.getTo());
                }


            }



            if (!hasFired){
                hasFired=true;
                count++;

                //particle effects
                if(MagicRender.screenCheck(0.25f, start)){
                    engine.addHitParticle(start, beam.getSource().getVelocity(), MathUtils.getRandomNumberInRange(width*2,width*3), 0.5f, 0.15f, fringe);
                    for(int i=0; i<MathUtils.getRandomNumberInRange(3, 5); i++){
                        Vector2f point = MathUtils.getRandomPointInCone(
                                new Vector2f(),
                                width*1.5f,
                                beam.getWeapon().getCurrAngle()-5,
                                beam.getWeapon().getCurrAngle()+5
                        );
                        engine.addHitParticle(
                                new Vector2f(
                                        start.x+point.x,
                                        start.y+point.y
                                ),
                                new Vector2f(
                                        beam.getSource().getVelocity().x+point.x,
                                        beam.getSource().getVelocity().y+point.y
                                ),
                                MathUtils.getRandomNumberInRange(width/6,width/4),
                                0.25f,
                                0.1f,
                                fringe
                        );
                    }
                    engine.addHitParticle(start, beam.getSource().getVelocity(), MathUtils.getRandomNumberInRange(width,width*2), 0.5f, 0.05f, Color.WHITE);

                    //light
                }
            }
        }

    }
}
