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
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.util.List;

import rx.Single;

/**
 *  Provides {@link Single}s that provide information about nearby places to the device.
 */
class NearbySingle extends BaseAwarenessSingle<List<PlaceLikelihood>, PlacesResult> {

    private NearbySingle(Context context) {
        super(context);
    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public static Single<List<PlaceLikelihood>> create(Context context) {
        return Single.create(new NearbySingle(context));
    }

    @Override
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    protected PendingResult<PlacesResult> createRequest(GoogleApiClient googleApiClient) {
        return Awareness.SnapshotApi.getPlaces(googleApiClient);
    }

    @Override
    protected List<PlaceLikelihood> unwrap(PlacesResult result) {
        return result.getPlaceLikelihoods();
    }
}
