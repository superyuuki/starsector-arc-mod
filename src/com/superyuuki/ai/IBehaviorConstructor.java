package com.superyuuki.ai;

public interface IBehaviorConstructor<GoalThought> {

    IBehavior produce(RootContext ctx, GoalThought goalThought);

}
