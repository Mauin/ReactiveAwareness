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
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;

import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Func0;
import rx.subscriptions.Subscriptions;

/**
 * A GoogleApiClient wrapped as an RxJava {@link Single}. Will call
 * {@link #onClientConnected(GoogleApiClient, SingleSubscriber)} once the GoogleApiClient is
 * connected.
 */
abstract class GoogleApiClientSingle<T> implements Single.OnSubscribe<T> {

    private final Context context;
    private GoogleApiClient googleApiClient;

    protected GoogleApiClientSingle(Context context) {
        this.context = context;
    }

    @Override
    public void call(final SingleSubscriber<? super T> singleSubscriber) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        onClientConnected(googleApiClient, singleSubscriber);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        onError(singleSubscriber, new ReactiveSnapshotException("GoogleApiClient connection was suspended."));
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> onError(singleSubscriber, new ReactiveSnapshotException(connectionResult.getErrorMessage())))
                .addApi(Awareness.API)
                .build();

        googleApiClient.connect();

        singleSubscriber.add(Subscriptions.create(() -> {
            if (googleApiClient.isConnected() || googleApiClient.isConnecting()) {
                googleApiClient.disconnect();
            }
        }));
    }

    protected abstract void onClientConnected(final GoogleApiClient googleApiClient, final SingleSubscriber<? super T> singleSubscriber);

    protected void onSuccess(SingleSubscriber<? super T> singleSubscriber, Func0<T> action) {
        if (!singleSubscriber.isUnsubscribed()) {
            singleSubscriber.onSuccess(action.call());
        }
    }

    protected void onError(SingleSubscriber<? super T> singleSubscriber, Throwable throwable) {
        if (!singleSubscriber.isUnsubscribed()) {
            singleSubscriber.onError(throwable);
        }
    }
}
