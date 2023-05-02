package combat.plugin;

import arc.fx.BaseCombatEffect;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CombatEffectPlugin extends BaseEveryFrameCombatPlugin implements CombatLayeredRenderingPlugin {

    float amount = 0f;
    EnumSet<CombatEngineLayers> layers = EnumSet.allOf(CombatEngineLayers.class);
    boolean shouldEnd = false;
    final List<BaseCombatEffect> effects = new ArrayList<>();
    final List<BaseCombatEffect> newEffects = new ArrayList<>();



    @Override
    public void init(CombatEngineAPI api) {

        api.addLayeredRenderingPlugin(this);
        api.getCustomData().put("aEP_CombatRenderPlugin", this);
        effects.clear();
        Global.getLogger(this.getClass()).info("aEP_CombatEffectPlugin register in EveryFrameCombatPlugin");
    }


    @Override
    public void advance(float amount) {
        if ( Global.getCombatEngine().isPaused() ) this.amount = 0f; else this.amount = amount;
        effects.addAll(newEffects);
        newEffects.clear();


        List<BaseCombatEffect> toRemove = new ArrayList<>();
        for (BaseCombatEffect e : effects) {
            if(e.isExpired()){
                toRemove.add(e);
                continue;
            };
            e.advance(amount);
        }
        effects.removeAll(toRemove);
    }


    @Override
    public float getRenderRadius() {
        return 99999999f;
    }


    @Override
    public void render(CombatEngineLayers layer , ViewportAPI viewport ) {
        if (layer == null || viewport == null) return;

        for (BaseCombatEffect e : effects) {
            if(!e.getActiveLayers().contains(layer)) continue;
            if(e.renderInShader) continue; //TODO wtf why do you call this here weird chinese mod

            e.render(layer, viewport);
        }
    }

    @Override
    public EnumSet<CombatEngineLayers> getActiveLayers()  {
        return layers;
    }

    @Override
    public void init(CombatEntityAPI combatEntityAPI) {
        //Don't do shit here
    }

    @Override
    public void cleanup() {
        shouldEnd = true;
    }

    @Override
    public boolean isExpired() {
        return shouldEnd;
    }

    public static void addEffect(BaseCombatEffect e){
        CombatEffectPlugin c = (CombatEffectPlugin) Global.getCombatEngine().getCustomData().get("aEP_CombatRenderPlugin");
        if (c == null) return;
        c.newEffects.add(e);
    }

}
