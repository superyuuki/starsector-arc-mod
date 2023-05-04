package arc.util;

import com.fs.starfarer.api.util.IntervalUtil;

import java.util.function.Supplier;

public class CachedCalculation<T> {

    final Supplier<T> expensiveCalculation;
    final IntervalUtil refreshInterval;

    T cached;

    public CachedCalculation(Supplier<T> expensiveCalculation, float refreshInterval) {
        this.expensiveCalculation = expensiveCalculation;
        this.refreshInterval = new IntervalUtil(refreshInterval, refreshInterval);

        cached = null;
    }

    public void advance(float time) {
        refreshInterval.advance(time);

        if (refreshInterval.intervalElapsed()) {
            cached = null; //mark it as unfresh
        }

    }

    public T acquire() {

        if (cached == null) {
            cached = expensiveCalculation.get();
        }

        return cached;

    }

}
