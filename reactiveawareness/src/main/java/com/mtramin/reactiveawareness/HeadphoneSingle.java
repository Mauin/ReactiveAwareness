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

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;

import io.reactivex.Single;

/**
 *  Provides {@link Single}s that provide information about the phones headphone state.
 */
class HeadphoneSingle extends BaseAwarenessSingle<Boolean, HeadphoneStateResult> {

    private HeadphoneSingle(Context context) {
        super(context);
    }

    public static Single<Boolean> create(Context context) {
        return Single.create(new HeadphoneSingle(context));
    }

    @Override
    protected PendingResult<HeadphoneStateResult> createRequest(GoogleApiClient googleApiClient) {
        return Awareness.SnapshotApi.getHeadphoneState(googleApiClient);
    }

    @Override
    protected Boolean unwrap(HeadphoneStateResult result) {
        int headphoneState = result.getHeadphoneState().getState();
        return headphoneState == HeadphoneState.PLUGGED_IN;
    }
}
