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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceStateMap;

import rx.Single;

/**
 * Defines an Awareness Fence that will receive status updates even when the application is in the
 * background via a {@link FenceReceiver}. This {@link android.content.BroadcastReceiver} has to be
 * extended in the app and registered in the applications AndroidManifest.xml.
 * <p>
 * Should you not need callbacks while the Application is in background and only while it is in
 * foreground, please consider using {@link ObservableFence}.
 * <p>
 * By calling {@link #register(Context, String, AwarenessFence)} you can register your new
 * background fence. Registering the same background fence with the same name will result in just
 * replacing the old fence. So make sure that Fence names are unique and to only register fences
 * when they are not already registered.
 * <p>
 * Once you don't need callbacks from a background fence anymore, unregister it by calling
 * {@link #unregister(Context, String)}.
 * <p>
 * With {@link #query(Context)} you can check which fences are currently registered.
 */
public class BackgroundFence {

    /**
     * Registers a background fence that will receive status callbacks via a {@link FenceReceiver}
     * that should be extended in the application and registered in the AndroidManifest.xml.
     * <p>
     * Once the state of the fence changes, the fence status will be sent to the receiver where you
     * can act on the fence results.
     * <p>
     * If you need to attach additional data to your fence result, consider calling
     * {@link #registerWithData(Context, String, AwarenessFence, Bundle)}.
     *
     * @param context        Context to use for registering the fence
     * @param name           name of the fence to register. Should be unique
     * @param awarenessFence The fence description
     */
    public static void register(Context context, String name, AwarenessFence awarenessFence) {
        RegisterBackgroundFenceAction.register(context, name, awarenessFence);
    }

    /**
     * Registers a background fence that will receive status callbacks via a {@link FenceReceiver}
     * that should be extended in the application and registered in the AndroidManifest.xml.
     * <p>
     * Once the state of the fence changes, the fence status will be sent to the receiver where you
     * can act on the fence results.
     * <p>
     * Additional data about the fence can be added via the data {@link Bundle} that will also be
     * delivered once the Fence receives a callback in the {@link FenceReceiver}.
     *
     * @param context        Context to use for registering the fence
     * @param name           name of the fence to register. Should be unique
     * @param awarenessFence The fence description
     * @param data           data to attach to the fence. This data bundle will also be returned on
     *                       Fence Callbacks. Instead of passing {@code null}, call
     *                       {@link #register(Context, String, AwarenessFence)} instead.
     */
    public static void registerWithData(Context context, String name, AwarenessFence awarenessFence, @Nullable Bundle data) {
        RegisterBackgroundFenceAction.registerWithData(context, name, awarenessFence, data);
    }

    /**
     * Unregisters the background fence with the given name. This fence will then not receive any
     * status updates anymore.
     *
     * @param context Context to use for unregistering the fence. It does not have to be the same
     *                context that the fence was registered with
     * @param name    name of the fene to unregister.
     */
    public static void unregister(Context context, String name) {
        UnregisterBackgroundFenceAction.unregister(context, name);
    }

    /**
     * Queries the currently registered fences and delivers the result as a {@link Single}.
     * <p>
     * Please note that the result of this query will contain all fences registered for your
     * GoogleApiClient API key. This means that also fences you manually registered for your
     * application or {@link ObservableFence}s you registered will be returned.
     *
     * @param context Context to use for the query operation
     * @return Single {@link FenceStateMap} describing all the fences that are currently registered.
     */
    public static Single<FenceStateMap> query(Context context) {
        return QueryBackgroundFenceSingle.query(context);
    }
}
