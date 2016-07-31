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

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.mtramin.servant.GoogleApiClientRequestSingle;

import rx.Single;

/**
 * Single that will query the currently registered fences and their states.
 *
 * The result will be delivered through a {@link Single}
 */
class QueryBackgroundFenceSingle extends GoogleApiClientRequestSingle<FenceStateMap, FenceQueryResult> {

    private QueryBackgroundFenceSingle(Context context) {
        super(context);
    }

    /**
     * Creates the query Single.
     * @param context context to use
     * @return Single map of all registered fence states.
     */
    static Single<FenceStateMap> query(Context context) {
        return Single.create(new QueryBackgroundFenceSingle(context.getApplicationContext()));
    }

    @Override
    protected Api getApi() {
        return Awareness.API;
    }

    @Override
    protected FenceStateMap unwrap(FenceQueryResult result) {
        return result.getFenceStateMap();
    }

    @Override
    protected PendingResult<? super FenceQueryResult> createRequest(GoogleApiClient googleApiClient) {
        return Awareness.FenceApi.queryFences(googleApiClient, FenceQueryRequest.all());
    }
}
