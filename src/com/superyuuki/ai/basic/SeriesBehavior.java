package com.superyuuki.ai.basic;

import com.superyuuki.ai.IBehavior;
import com.superyuuki.ai.IBehaviorConstructor;
import com.superyuuki.ai.RootContext;
import com.superyuuki.ai.Status;

public class SeriesBehavior implements IBehavior {

    final RootContext ctx;
    final IBehaviorConstructor<Void>[] behaviors;

    public SeriesBehavior(RootContext ctx, IBehaviorConstructor<Void>[] behaviors) {
        this.ctx = ctx;
        this.behaviors = behaviors;
    }

    int currentPosition = -1; //-1 - not running anything
    IBehavior currentBehavior = null;


    //each run should produce exactly one call to a sub run
    @Override
    public Status run() {
        if (currentBehavior == null) {
            //nothing is being run right now

            int access = ++currentPosition;
            if (access >= behaviors.length) {
                //exhausted all possibilities

                return Status.FAIL;
            }
            currentBehavior = behaviors[access].produce(ctx, null);
        }

        //we have something we are doing right now

        Status code = currentBehavior.run();

        if (code == Status.CONTINUE) {
            return Status.CONTINUE;
        }

        if (code == Status.SUCCESS) {
            return Status.SUCCESS;
        }

        if (code == Status.FAIL) {
            //uh oh

            currentBehavior = null;
            return Status.CONTINUE;
        }


        return Status.FAIL;
    }

    @Override
    public String report() {
        return null;
    }
}
