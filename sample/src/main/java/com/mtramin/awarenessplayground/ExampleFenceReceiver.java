/*
 * Copyright 2016 Marvin Ramin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mtramin.awarenessplayground;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mtramin.reactiveawareness_fence.BackgroundFence;
import com.mtramin.reactiveawareness_fence.FenceReceiver;

/**
 * Implementation of Fence Receiver that will receive the callbacks from BackgroundFences
 */
public class ExampleFenceReceiver extends FenceReceiver {

    public static final String HEADPHONES = "Headphones";
    public static final String TIME = "TimeFence";

    @Override
    protected void onUpdate(@NonNull Context context, @NonNull String key, boolean state, @Nullable Bundle bundle) {
        switch (key) {
            case HEADPHONES:
                Log.e("TEST", "Headphones: " + state);
                break;
            case TIME:
                if (bundle != null) {
                    long start = bundle.getLong("start");
                    long secondsAgo = (long) ((System.currentTimeMillis() - start) / 1000.0);

                    Log.e("TEST", String.format("Time fence was created %d ago", secondsAgo));
                    Log.e("TEST", "Time: " + state);

                    if (state) {
                        BackgroundFence.unregister(context, ExampleFenceReceiver.TIME);
                    }
                } else {
                    Log.e("TEST", "Time bundle was null ");
                }
                break;
        }
    }
}
