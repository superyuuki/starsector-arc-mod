package com.superyuuki.ai.oo;

import java.util.Optional;

public interface Effect<T> {

    T interpolate(float deltaT_seconds);

    default Optional<String> asFunction() {
        return Optional.empty();
    } //optimizable

}
