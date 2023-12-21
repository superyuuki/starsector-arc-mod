package com.superyuuki.ai.ship;

import com.superyuuki.ai.oo.Effect;
import com.superyuuki.ai.oo.IBehaviorSource;
import com.superyuuki.ai.states.DisabledTime;

public class FullVentSource implements IBehaviorSource {

    static class VentOverTimeEffect implements Effect<DisabledTime> {

        @Override
        public DisabledTime interpolate(float deltaT_seconds) {
            return null;
        }
    }

    @Override
    public Effect<?>[] describeEffects() {
        return new Effect[0];
    }
}
