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
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Single;

import static com.mtramin.reactiveawareness.ApiKeyGuard.API_KEY_AWARENESS_API;
import static com.mtramin.reactiveawareness.ApiKeyGuard.API_KEY_BEACON_API;
import static com.mtramin.reactiveawareness.ApiKeyGuard.API_KEY_PLACES_API;
import static com.mtramin.reactiveawareness.ApiKeyGuard.guardWithApiKey;

/**
 * Accessor class for Reactive Context values. All methods exposed query the Snapshot API to give
 * you more information about the users current context.
 * <p>
 * All context events are provided as {@link Single}s which will provide you with exactly the
 * current context state.
 */
public class ReactiveSnapshot {

    private final Context context;

    private ReactiveSnapshot(Context context) {
        this.context = context;
    }

    /**
     * Creates a new instance of ReactiveSnapshot to give you access to all Snapshot API calls.
     * @param context context to use, will default to your application context
     * @return instance of ReactiveSnapshot
     */
    public static ReactiveSnapshot create(Context context) {
        return new ReactiveSnapshot(context.getApplicationContext());
    }

    /**
     * Returns the current weather information at the devices current location
     *
     * @return Single event of weather information
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<Weather> getWeather() {
        guardWithApiKey(context, API_KEY_AWARENESS_API);
        return WeatherSingle.create(context);
    }

    /**
     * Provides the current temperature at the devices current location
     *
     * @param temperatureUnit temperature unit to use
     * @return Single event of the current temperature
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<Float> getTemperature(final int temperatureUnit) {
        return getWeather()
                .map(weather -> weather.getTemperature(temperatureUnit));
    }

    /**
     * Provides the current feels-like temperature at the devices current location
     *
     * @param temperatureUnit temperature unit to use
     * @return Single event of the current feels-like temperature
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<Float> getFeelsLikeTemperature(final int temperatureUnit) {
        return getWeather()
                .map(weather -> weather.getFeelsLikeTemperature(temperatureUnit));
    }

    /**
     * Provides the current dew point at the devices current location
     *
     * @param temperatureUnit temperature unit to use
     * @return Single event of the current dew point
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<Float> getDewPoint(final int temperatureUnit) {
        return getWeather()
                .map(weather -> weather.getDewPoint(temperatureUnit));
    }

    /**
     * Provides the current humidity at the devices current location
     *
     * @return Single event of the current humidity
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<Integer> getHumidity() {
        return getWeather()
                .map(Weather::getHumidity);
    }

    /**
     * Provides the current weather conditions at the devices current location
     *
     * @return Single event of the current weather conditions
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<List<Integer>> getWeatherConditions() {
        return getWeather()
                .map(Weather::getConditions)
                .map(conditions -> {
                    List<Integer> list = new ArrayList<>(conditions.length);
                    for (int condition : conditions) {
                        list.add(condition);
                    }
                    return list;
                });
    }

    /**
     * Provides the current location of the device
     *
     * @return Single event of the current location
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<Location> getLocation() {
        guardWithApiKey(context, API_KEY_AWARENESS_API);
        return LocationSingle.create(context);
    }

    /**
     * Provides the current latitude/longitude of the device
     *
     * @return Single event of the current latitude/longitude
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<LatLng> getLatLng() {
        return getLocation()
                .map(location -> new LatLng(location.getLatitude(), location.getLongitude()));
    }

    /**
     * Provides the current speed of the device
     *
     * @return Single event of the current speed
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<Float> getSpeed() {
        return getLocation()
                .map(Location::getSpeed);
    }

    /**
     * Provides the current {@link ActivityRecognitionResult} of the device
     *
     * @return Single event of the current devices activity
     */
    @RequiresPermission("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
    public Single<ActivityRecognitionResult> getActivity() {
        guardWithApiKey(context, API_KEY_AWARENESS_API);
        return ActivitySingle.create(context);
    }

    /**
     * Provides the current most probable {@link DetectedActivity} of the device
     *
     * @return Single event of the most probable activity
     */
    @RequiresPermission("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
    public Single<DetectedActivity> getMostProbableActivity() {
        return getActivity()
                .map(ActivityRecognitionResult::getMostProbableActivity);
    }

    /**
     * Provides the current most probable {@link DetectedActivity} of the device which has at least
     * the given probability. Should no activity reach this minimum probability, {@code null} will
     * be emitted.
     * <p>
     * <b>Be sure to check the result for null!</b>
     *
     * @return Single event of the most probable activity
     */
    @RequiresPermission("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
    public Single<DetectedActivity> getMostProbableActivity(int minimumProbability) {
        return getActivity()
                .map(activity -> {
                    DetectedActivity mostProbableActivity = activity.getMostProbableActivity();
                    if (activity.getActivityConfidence(mostProbableActivity.getType()) < minimumProbability) {
                        return null;
                    }
                    return mostProbableActivity;
                });
    }

    /**
     * Provides the current probable {@link DetectedActivity}s of the device
     *
     * @return Single event of the most probable activities
     */
    @RequiresPermission("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
    public Single<List<DetectedActivity>> getProbableActivities() {
        return getActivity()
                .map(ActivityRecognitionResult::getProbableActivities);
    }

    /**
     * Provides the current probable {@link DetectedActivity}s of the device which have at least
     * the given probability. Should no activity reach this minimum probability, the resulting list
     * will be empty.
     *
     * @param minimumProbability minimum probabilities of the activities
     * @return Single event of the most probable activities
     */
    @RequiresPermission("com.google.android.gms.permission.ACTIVITY_RECOGNITION")
    public Single<List<DetectedActivity>> getProbableActivities(int minimumProbability) {
        return getActivity()
                .map(activity -> {
                    List<DetectedActivity> probableActivities = activity.getProbableActivities();
                    List<DetectedActivity> matchingActivities = new ArrayList<>(probableActivities.size());

                    for (DetectedActivity probableActivity : probableActivities) {
                        if (activity.getActivityConfidence(probableActivity.getType()) >= minimumProbability) {
                            matchingActivities.add(probableActivity);
                        }
                    }

                    return matchingActivities;
                });
    }

    /**
     * Provides the current state of the headphones.
     *
     * @return Single event of {@code true} if the headphones are currently plugged in
     */
    public Single<Boolean> headphonesPluggedIn() {
        guardWithApiKey(context, API_KEY_AWARENESS_API);
        return HeadphoneSingle.create(context);
    }

    /**
     * Provides the currently nearby places to the current device location.
     *
     * @return Single event of the currently nearby places
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Single<List<PlaceLikelihood>> getNearbyPlaces() {
        guardWithApiKey(context, API_KEY_AWARENESS_API);
        guardWithApiKey(context, API_KEY_PLACES_API);
        return NearbySingle.create(context);
    }

    /**
     * Provides the currently nearby beacons to the current device locations.
     *
     * @param typeFilters Beacon TypeFilters to filter for
     * @return Single event of matching nearby beacons
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Single<List<BeaconState.BeaconInfo>> getBeacons(BeaconState.TypeFilter... typeFilters) {
        guardWithApiKey(context, API_KEY_AWARENESS_API);
        guardWithApiKey(context, API_KEY_BEACON_API);
        return BeaconSingle.create(context, typeFilters);
    }

    /**
     * Provides the currently nearby beacons to the current device locations.
     *
     * @param typeFilters Beacon TypeFilters to filter for
     * @return Single event of matching nearby beacons
     */
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Single<List<BeaconState.BeaconInfo>> getBeacons(Collection<BeaconState.TypeFilter> typeFilters) {
        guardWithApiKey(context, API_KEY_AWARENESS_API);
        guardWithApiKey(context, API_KEY_BEACON_API);
        return BeaconSingle.create(context, typeFilters);
    }
}
