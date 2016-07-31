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

package com.mtramin.reactiveawareness_fence;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.awareness.fence.FenceState;

/**
 * BackgroundReceiver that receives fence state updates to BackgroundFences.
 * <p>
 * To use Background fences, extend this class in your application and register the receiver in
 * the AndroidManifest.xml.
 * <p>
 * <receiver android:name=".ExampleFenceReceiver">
 * <intent-filter>
 * <action android:name="ReactiveAwarenessFence"/>
 * </intent-filter>
 * </receiver>
 *
 * The Action name for the registered receiver should be "ReactiveAwarenessFence".
 *
 * On each fence state update your implementation of this receiver will retrieve the result in
 * {@link #onUpdate(String, boolean)} with the name of the fence and it's state.
 *
 * The state will be {@code true} if the fence condition is valid.
 */
public abstract class FenceReceiver extends BroadcastReceiver {
    private static final int REQUEST_CODE = 4791848;
    private static final String ACTION_BACKGROUND_FENCE = "ReactiveAwarenessFence";

    /**
     * Creates a pending intent that will call this receiver
     * @param context context to use
     * @return PendingIntent that will call this receiver
     */
    static PendingIntent createPendingIntent(Context context) {
        return PendingIntent.getBroadcast(context, REQUEST_CODE, createIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Creates an Intent that calls this receiver
     * @return Intent that will call this receiver
     */
    private static Intent createIntent() {
        return new Intent(ACTION_BACKGROUND_FENCE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState state = FenceState.extract(intent);

        boolean result = state.getCurrentState() == FenceState.TRUE;
        String key = state.getFenceKey();

        onUpdate(key, result);
    }

    /**
     * Called once the fence changed it's state.
     * @param key the key/name of the fence that received an update
     * @param state the current state of the fence
     */
    protected abstract void onUpdate(String key, boolean state);
}
