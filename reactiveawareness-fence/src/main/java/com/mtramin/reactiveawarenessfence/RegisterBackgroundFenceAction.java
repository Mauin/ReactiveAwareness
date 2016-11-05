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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mtramin.servant2.ClientException;
import com.mtramin.servant2.Servant;

/**
 * Registers a background fence
 */
class RegisterBackgroundFenceAction {

    private final Context context;
    private final Bundle data;
    private String name;
    private AwarenessFence fence;

    private RegisterBackgroundFenceAction(Context context,
                                          String name,
                                          AwarenessFence fence,
                                          @Nullable Bundle data) {
        this.context = context;
        this.name = name;
        this.fence = fence;
        this.data = data;

        Servant.actions(context, Awareness.API, this::onClientConnected, this::onClientError);
    }

    /**
     * Registers the given fence to receive background updates.
     *
     * @param context context to use
     * @param name    name of the fence
     * @param fence   fence to register
     */
    static void register(Context context, String name, AwarenessFence fence) {
        new RegisterBackgroundFenceAction(context.getApplicationContext(), name, fence, null);
    }

    /**
     * Registers the given fence with extra data to be delivered on callbacks.
     * <p>
     * Will receive updates in the background.
     *
     * @param context context to use
     * @param name    name of the fence
     * @param fence   fence to register
     * @param data    data to attach to the fence
     */
    static void registerWithData(Context context,
                                 String name,
                                 AwarenessFence fence,
                                 @Nullable Bundle data) {
        new RegisterBackgroundFenceAction(context.getApplicationContext(), name, fence, data);
    }

    private void onClientConnected(GoogleApiClient googleApiClient) {
        FenceUpdateRequest fenceRequest = new FenceUpdateRequest.Builder()
                .addFence(name, fence, FenceReceiver.createPendingIntent(context, fence.hashCode(), data))
                .build();

        Awareness.FenceApi.updateFences(googleApiClient, fenceRequest)
                .setResultCallback(status -> {
                    if (!status.isSuccess()) {
                        onClientError(new ClientException("Adding fence failed. " + status.getStatusMessage()));
                    }
                    googleApiClient.disconnect();
                });
    }

    private void onClientError(Throwable throwable) {
        Log.e("ReactiveAwareness", "Error when updating Fence in GoogleApiClient " + throwable.getLocalizedMessage());
    }
}
