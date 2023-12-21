package arc.deco;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import data.scripts.util.MagicAnim;
import org.lazywizard.lazylib.MathUtils;

import java.util.List;

//diable rip
public class MuraAnimation implements EveryFrameWeaponEffectPlugin
{
    WeaponAPI headgun, rgun, lgun, rpauldron, lpauldron, chest, back, head;
    private ShipAPI ship;
    private ShipSystemAPI system;
    
    final static String headgunID = "HEAD";
    final static String leftgunID = "LEFTGUN";
    final static String rightgunID = "RIGHTGUN";
    final static String backID = "TRANSFORM_04";
    final static String chestID = "TRANSFORM_05";
    final static String headID = "TRANSFORM_06";
    final static String leftpauldronID = "TRANSFORM_07";
    final static String rightpauldronID = "TRANSFORM_08";
    
    boolean runOnce=false, check=true;
    
    float backWidth, backHeight, backFrames;
    float chestWidth, chestHeight, chestFrames;
    float headWidth, headHeight, headFrames;
    float pauldronWidth, pauldronHeight, pauldronFrames;

    float rate=1;
    boolean travelDrive = false;

    float lrecoil=0, rrecoil=0;
    float backOffsetY=-8;
    float chestOffsetY=1;
    float headOffsetY=2;
    float pauldronOffsetX=-7;
    float pauldronOffsetY=8;
	
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        
        if (Global.getCombatEngine().isPaused()) {
            return;
        }
        
        //initialise the variables
        if (!runOnce || ship==null || system==null){
            ship=weapon.getShip();
            system = ship.getSystem();
            List<WeaponAPI> weapons = ship.getAllWeapons();
            for (WeaponAPI w : weapons){
                switch(w.getSlot().getId()){
                    case headgunID:
                        headgun=w;
                        break;
                    case leftgunID:
                        lgun=w;
                        break;   
                    case rightgunID:
                        rgun=w;
                        break;
                    case backID:
                        back=w;
                        backHeight=w.getSprite().getHeight();
                        backWidth=w.getSprite().getWidth();
                        backFrames=w.getAnimation().getNumFrames();
                        break;             
                    case chestID:
                        chest=w;
                        chestHeight=w.getSprite().getHeight();
                        chestWidth=w.getSprite().getWidth();
                        chestFrames=w.getAnimation().getNumFrames();
                        break;      
                    case headID:
                        head=w;
                        headHeight=w.getSprite().getHeight();
                        headWidth=w.getSprite().getWidth();
                        headFrames=w.getAnimation().getNumFrames();
                        break; 
                    case leftpauldronID:
                        lpauldron=w;
                        pauldronHeight=w.getSprite().getHeight();
                        pauldronWidth=w.getSprite().getWidth();
                        pauldronFrames=w.getAnimation().getNumFrames();
                        break;
                    case rightpauldronID:
                        rpauldron=w;
                        break;       

                }                
            }            
            runOnce=true;
            //return to avoid a null error on the ship
            return;
        }
        
        float FACING=ship.getFacing();
        float LGUN=lgun.getCurrAngle();
        float RGUN=rgun.getCurrAngle();   
        float HEAD=headgun.getCurrAngle();       
        
        //CUSTOM RECOIL
        
        if(lgun.getChargeLevel()==1){
            lrecoil=Math.min(1, lrecoil+0.33f);
        } else {
            lrecoil=Math.max(0, lrecoil-(0.75f*amount));
        }
        
        if(rgun.getChargeLevel()==1){
            rrecoil=Math.min(1, rrecoil+0.33f);
        } else {
            rrecoil=Math.max(0, rrecoil-(0.75f*amount));
        }
        
        //ALL THE STUFF
        
        if(ship.getTravelDrive().isActive()){
            rate = Math.min(1,rate+1.25f*amount);
            travelDrive=true;
        } else if (travelDrive){
            rate = Math.max(0,rate-1.25f*amount);
            if(rate==0){
                travelDrive=false;
            }
        } else {
            rate = system.getEffectLevel();
        }
        
        if (system.isActive() || rate > 0){
            check=true;
            lgun.setRemainingCooldownTo(0.75f);
            rgun.setRemainingCooldownTo(0.75f);
                        
            float smooth1 = MagicAnim.smoothNormalizeRange(rate,0f,0.5f);
            float smooth2 = MagicAnim.smoothNormalizeRange(rate,0.25f,0.75f);
            float smooth3 = MagicAnim.smoothNormalizeRange(rate,0.5f,1f);
            float straight1 = MagicAnim.normalizeRange(rate,0f,0.5f);
            float straight2 = MagicAnim.normalizeRange(rate,0.25f,0.75f);
            float straight3 = MagicAnim.normalizeRange(rate,0.5f,1f);
//            float bump = RSO(rate,0.5f,1f);
            
            //BACK
            
            int baF = Math.round(Math.max(0, Math.min(backFrames-1, (straight2*backFrames)-0.5f)));
            
            back.getAnimation().setFrame(baF);
            
            float baX = backWidth/2 ;            
            float baY = backHeight/2 + backOffsetY*smooth3;
                    
            back.getSprite().setCenter(baX, baY);            
            
            //CHEST
            
            int cF = Math.round(Math.max(0, Math.min(chestFrames-1, (straight2*chestFrames)-0.5f)));
            
            chest.getAnimation().setFrame(cF);
            
            float cX = chestWidth/2 ;            
            float cY = chestHeight/2 + chestOffsetY*smooth3;
                    
            chest.getSprite().setCenter(cX, cY);
            
            //HEAD
            
            int hF = Math.round(Math.max(0, Math.min(headFrames-1, (straight1*headFrames)-0.5f)));
            
            head.getAnimation().setFrame(hF);
            
            head.setCurrAngle(headgun.getCurrAngle()+smooth1*MathUtils.getShortestRotation(HEAD,FACING));
            
            float hX = headWidth/2 ;            
            float hY = headHeight/2 + headOffsetY*smooth1;
                    
            head.getSprite().setCenter(hX, hY);
            
            //PAULDRONS       
            
            int pF = Math.round(Math.max(0, Math.min(pauldronFrames-1, (straight3*pauldronFrames)-0.5f)));
            
            lpauldron.getAnimation().setFrame(pF);
            rpauldron.getAnimation().setFrame(pF);
            
            lpauldron.setCurrAngle(lgun.getCurrAngle()+smooth1*MathUtils.getShortestRotation(LGUN,FACING));
            rpauldron.setCurrAngle(rgun.getCurrAngle()+smooth1*MathUtils.getShortestRotation(RGUN,FACING));
            
            float lpX = pauldronWidth/2 + pauldronOffsetX*smooth3;
            float rpX = pauldronWidth/2 - pauldronOffsetX*smooth3;
            
            float pY = pauldronHeight/2 + pauldronOffsetY*smooth2;
                    
            lpauldron.getSprite().setCenter(lpX, pY);
            rpauldron.getSprite().setCenter(rpX, pY);     
            

            
        } else {
            if(check){
                check=false;
                lpauldron.getSprite().setCenter(pauldronWidth/2, pauldronHeight/2);
                rpauldron.getSprite().setCenter(pauldronWidth/2, pauldronHeight/2);                     
                head.getSprite().setCenter(headWidth/2, headHeight/2);                   
                back.getSprite().setCenter(backWidth/2, backHeight/2);                   
                chest.getSprite().setCenter(chestWidth/2, chestHeight/2);                   

                
            }

            
            lpauldron.setCurrAngle(LGUN);
            rpauldron.setCurrAngle(RGUN);
            head.setCurrAngle(HEAD);

            lpauldron.getAnimation().setFrame(0);
            rpauldron.getAnimation().setFrame(0);
            head.getAnimation().setFrame(0);
            chest.getAnimation().setFrame(0);
            back.getAnimation().setFrame(0);         
        }
    }
}
