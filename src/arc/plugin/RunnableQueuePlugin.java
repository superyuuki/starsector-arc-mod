package arc.plugin;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.IntervalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RunnableQueuePlugin implements EveryFrameCombatPlugin {


    final static Map<Integer, List<Runnable>> map = new HashMap<>();
    final static IntervalUtil interval = new IntervalUtil(0.01f, 0.1f); //ever 0.05f

    static int current = 0;


    public static void queueTask(Runnable runnable, int intervalTicksLater) {
        map.computeIfAbsent(current+intervalTicksLater , i -> new ArrayList<>()).add(runnable);
    }

    @Override
    public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {

    }

    @Override
    public void advance(float amount, List<InputEventAPI> events) {
        interval.advance(amount);
        if (interval.intervalElapsed()) {
            current++;
            List<Runnable> runnables = map.remove(current);
            if (runnables == null) return;

            for (Runnable runnable : runnables) {
                runnable.run();
            }
        }
    }

    @Override
    public void renderInWorldCoords(ViewportAPI viewport) {

    }

    @Override
    public void renderInUICoords(ViewportAPI viewport) {

    }

    @Override
    public void init(CombatEngineAPI engine) {

    }
}
