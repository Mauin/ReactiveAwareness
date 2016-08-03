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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Util class providing checks that meta-data is available in the application manifest.
 */
class ApiKeyGuard {

    static final String API_KEY_AWARENESS_API = "com.google.android.awareness.API_KEY";
    static final String API_KEY_PLACES_API = "com.google.android.geo.API_KEY";
    static final String API_KEY_BEACON_API = "com.google.android.nearby.messages.API_KEY";

    /**
     * Verifies that the meta-data with the given key is provided in the application manifest.
     * If it is not provided, {@link ApiKeyException} is thrown.
     *
     * @param context context to use
     * @param key     key to check for
     * @throws ApiKeyException
     */
    public static void guardWithApiKey(Context context, String key) {
        if (!hasApiKey(context, key)) {
            throw new ApiKeyException(key + " not found in AndroidManifest.xml. Please visit https://developers.google.com/awareness/android-api/get-started for more details.");
        }
    }

    private static boolean hasApiKey(Context context, String key) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;

            return bundle.containsKey(key);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("ReactiveAwareness", "Could not read AndroidManifest.xml while checking for API Key.", e);
            return false;
        }
    }
}
