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

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.Result;
import com.mtramin.servant2.GoogleApiClientRequestSingle;

/**
 * Base Single for Awareness Requests in a GoogleApiClient
 */
abstract class BaseAwarenessSingle<T, R extends Result> extends GoogleApiClientRequestSingle<T, R> {
    BaseAwarenessSingle(Context context) {
        super(context, Awareness.API);
    }
}
