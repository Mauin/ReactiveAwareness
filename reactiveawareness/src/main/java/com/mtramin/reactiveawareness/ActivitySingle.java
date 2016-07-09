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
import android.support.annotation.RequiresPermission;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.ActivityRecognitionResult;

import rx.Single;

/**
 * Provides {@link Single}s that provide the current activity state of the device.
 */
class ActivitySingle extends BaseGoogleApiClientRequest<ActivityRecognitionResult, DetectedActivityResult> {

    private ActivitySingle(Context context) {
        super(context);
    }

    public static Single<ActivityRecognitionResult> create(Context context) {
        return Single.create(new ActivitySingle(context));
    }

    @Override
    @RequiresPermission("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
    protected PendingResult<DetectedActivityResult> createRequest(GoogleApiClient googleApiClient) {
        return Awareness.SnapshotApi.getDetectedActivity(googleApiClient);
    }

    @Override
    protected ActivityRecognitionResult unwrap(DetectedActivityResult result) {
        return result.getActivityRecognitionResult();
    }
}
