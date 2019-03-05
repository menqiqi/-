package com.msq.mini_everything.core.Interceptor;

import com.msq.mini_everything.core.model.Thing;

@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
