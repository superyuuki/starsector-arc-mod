package arc.fx;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.util.EnumSet;

public abstract class BaseCombatEffect implements CombatLayeredRenderingPlugin {

    static final EnumSet<CombatEngineLayers> layers = EnumSet.of(CombatEngineLayers.ABOVE_SHIPS_LAYER);

    float time = 0f;
    float lifeTime = 0f;
    CombatEntityAPI entity = null;

    boolean shouldEnd = false;
    Vector2f loc = new Vector2f();
    float radius = 9999999999f;
    public boolean renderInShader = false;

    public BaseCombatEffect(float lifeTime, CombatEntityAPI entity) {
            this.lifeTime = lifeTime;
            this.entity = entity;
    }


    @Override
    public void cleanup() {
        shouldEnd = true;
    }

    @Override
    public boolean isExpired() {
        if(shouldEnd){
            readyToEnd();
            radius = -1f;
            return true;
        }
        return false;
    }


    @Override
    public void advance(float amount) {
        //若 entity不为空，则进行 entity检测，不过就直接结束
        if(entity != null) {
            loc = entity.getLocation(); //OR vector2f if not present
            if (loc == null) loc = new Vector2f();

            if(!Global.getCombatEngine().isEntityInPlay(entity)){
                shouldEnd = true;
            }

            if(entity instanceof ShipAPI){
                if(!((ShipAPI) entity).isAlive() || ((ShipAPI) entity).isHulk()){
                    shouldEnd = true;
                }
            }
        }

        if(shouldEnd) return;
        time += amount;
        MathUtils.clamp(time,0f,lifeTime);
        advanceImpl(amount);
        if(time >= lifeTime && lifeTime > 0){
            shouldEnd = true;
        }
    }


    protected abstract void advanceImpl(float amout);
    protected abstract void readyToEnd();

    @Override
    public EnumSet<CombatEngineLayers> getActiveLayers() {
        return layers;
    }

    @Override
    public float getRenderRadius() {
        return radius;
    }

    @Override
    public void render(CombatEngineLayers layer, ViewportAPI viewport) {
        if(!layers.contains(layer)) return;
        float screenDist = radius * viewport.getViewMult();
        if(!viewport.isNearViewport(loc,screenDist * 1.1f)) return;
        renderImpl(layer,viewport);
    }

    public abstract void renderImpl(CombatEngineLayers layer, ViewportAPI viewport);




}
