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

package com.mtramin.reactiveawareness2;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.BeaconStateResult;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.awareness.state.BeaconState.TypeFilter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.reactivex.Single;

/**
 * Provides {@link Single}s that provide information about nearby beacons to the device.
 */
class BeaconSingle extends BaseAwarenessSingle<List<BeaconState.BeaconInfo>, BeaconStateResult> {

    private Collection<TypeFilter> typeFilters;

    private BeaconSingle(Context context, TypeFilter... typeFilters) {
        super(context);
        this.typeFilters = new ArrayList<>(Arrays.asList(typeFilters));
    }

    private BeaconSingle(Context context, Collection<TypeFilter> typeFilters) {
        super(context);
        this.typeFilters = typeFilters;
    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static Single<List<BeaconState.BeaconInfo>> create(Context context, TypeFilter... typeFilters) {
        return Single.create(new BeaconSingle(context, typeFilters));
    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static Single<List<BeaconState.BeaconInfo>> create(Context context, Collection<TypeFilter> typeFilters) {
        return Single.create(new BeaconSingle(context, typeFilters));
    }

    @Override
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected PendingResult<BeaconStateResult> createRequest(GoogleApiClient googleApiClient) {
        return Awareness.SnapshotApi.getBeaconState(googleApiClient, typeFilters);
    }

    @Override
    protected List<BeaconState.BeaconInfo> unwrap(BeaconStateResult result) {
        return result.getBeaconState().getBeaconInfo();
    }
}
