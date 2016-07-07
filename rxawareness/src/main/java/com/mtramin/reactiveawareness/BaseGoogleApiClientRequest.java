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

package com.mtramin.reactiveawareness;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;

import rx.SingleSubscriber;

/**
 * Base request for GoogleApiClient Singles
 */

abstract class BaseGoogleApiClientRequest<T, R extends Result> extends GoogleApiClientSingle<T> {

    protected BaseGoogleApiClientRequest(Context context) {
        super(context);
    }

    @Override
    protected void onClientConnected(GoogleApiClient googleApiClient, final SingleSubscriber<? super T> singleSubscriber) {
        createRequest(googleApiClient)
                .setResultCallback(result -> {
                    if (!result.getStatus().isSuccess()) {
                        onError(singleSubscriber, new ReactiveSnapshotException("Failed to execute request. " + result.getStatus().getStatusMessage()));
                        return;
                    }

                    onSuccess(singleSubscriber,  () -> unwrap(result));
                });
    }

    /**
     * Creates the request to execute for the given {@link GoogleApiClient}
     *
     * @param googleApiClient client to use
     * @return the result
     */
    protected abstract PendingResult<R> createRequest(GoogleApiClient googleApiClient);

    /**
     * Unwraps the given result to return only the data needed.
     *
     * @param result result data to unwrap
     * @return the resulting data
     */
    protected abstract T unwrap(R result);
}
