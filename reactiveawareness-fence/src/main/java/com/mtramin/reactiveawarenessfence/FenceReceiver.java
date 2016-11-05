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

package com.mtramin.reactiveawarenessfence;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
 * <p>
 * The Action name for the registered receiver should be "ReactiveAwarenessFence".
 * <p>
 * On each fence state update your implementation of this receiver will retrieve the result in
 * {@link #onUpdate(Context, String, boolean, Bundle)} with the name of the fence and it's state.
 * <p>
 * The state will be {@code true} if the fence condition is valid.
 */
public abstract class FenceReceiver extends BroadcastReceiver {
    private static final String EXTRA_BUNDLE = "EXTRA_BUNDLE";
    private static final String ACTION_BACKGROUND_FENCE = "ReactiveAwarenessFence";

    /**
     * Creates a pending intent that will call this receiver
     *
     * @param context     context to use
     * @param requestCode request code for the pending intent. Should be unique for the fence
     * @param data        data to be attached to the pending intent
     * @return PendingIntent that will call this receiver
     */
    static PendingIntent createPendingIntent(Context context, int requestCode, @Nullable Bundle data) {
        return PendingIntent.getBroadcast(context, requestCode, createIntent(data), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Creates an Intent that calls this receiver
     *
     * @param data data to be attached to the intent
     * @return Intent that will call this receiver
     */
    private static Intent createIntent(@Nullable Bundle data) {
        Intent intent = new Intent(ACTION_BACKGROUND_FENCE);

        if (data != null) {
            intent.putExtra(EXTRA_BUNDLE, data);
        }

        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState state = FenceState.extract(intent);

        Bundle bundle = intent.getBundleExtra(EXTRA_BUNDLE);

        boolean result = state.getCurrentState() == FenceState.TRUE;
        String key = state.getFenceKey();

        onUpdate(context, key, result, bundle);
    }

    /**
     * Called once the fence changed it's state.
     *
     * @param context context to use
     * @param key     the key/name of the fence that received an update
     * @param state   the current state of the fence
     * @param bundle  bundle with additional data that was attached to this fence
     */
    protected abstract void onUpdate(@NonNull Context context, @NonNull String key, boolean state, @Nullable Bundle bundle);
}
