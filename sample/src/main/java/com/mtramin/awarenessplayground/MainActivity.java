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

package com.mtramin.awarenessplayground;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.state.BeaconState;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.mtramin.reactiveawareness.ReactiveSnapshot;
import com.mtramin.reactiveawareness_fence.BackgroundFence;
import com.mtramin.reactiveawareness_fence.ObservableFence;

import java.util.Locale;

import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 38928;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private ReactiveSnapshot reactiveSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reactiveSnapshot = ReactiveSnapshot.create(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        subscribeToContexts();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            subscribeToLocationBasedSnapshots();
        }


        subscribeToBackgroundFence();
        subscribeToRuntimeFence();
    }

    private void subscribeToBackgroundFence() {
        subscriptions.add(BackgroundFence.query(this)
                .subscribe(
                        fenceStateMap -> {
                            if (!fenceStateMap.getFenceKeys().contains(ExampleFenceReceiver.HEADPHONES)) {
                                AwarenessFence fence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
                                BackgroundFence.register(this, ExampleFenceReceiver.HEADPHONES, fence);
                            }
                        },
                        throwable -> logError(throwable, "query")
                )
        );
    }

    private void subscribeToRuntimeFence() {
        AwarenessFence fence = TimeFence.inInterval(5000, 10000);
        subscriptions.add(
                ObservableFence.create(this, fence)
                        .subscribe(
                                isTrue -> Log.e("TEST", "Runtime Fence: " + isTrue),
                                throwable -> logError(throwable, "observable fence")
                        )
        );
    }

    @Override
    protected void onStop() {
        subscriptions.clear();
        super.onStop();
    }

    private void subscribeToContexts() {
        subscriptions.add(
                reactiveSnapshot.getActivity()
                        .subscribe(
                                this::setActivity,
                                throwable -> logError(throwable, "activity")
                        )
        );

        subscriptions.add(
                reactiveSnapshot.headphonesPluggedIn()
                        .subscribe(
                                this::setHeadphoneState,
                                throwable -> logError(throwable, "headphones")
                        )
        );
    }

    private void subscribeToLocationBasedSnapshots() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        subscriptions.add(
                reactiveSnapshot.getLocation()
                        .subscribe(
                                this::setLocation,
                                throwable -> logError(throwable, "location")
                        )
        );

        subscriptions.add(
                reactiveSnapshot.getWeather()
                        .subscribe(
                                this::setWeather,
                                throwable -> logError(throwable, "weather")
                        )
        );

        subscriptions.add(
                reactiveSnapshot.getNearbyPlaces()
                        .subscribe(
                                places -> Log.e("Awareness", "have nearby locations "),
                                throwable -> logError(throwable, "nearby places")
                        )
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            subscriptions.add(
                    reactiveSnapshot.getBeacons(BeaconState.TypeFilter.with("this", "that"))
                            .subscribe(
                                    places -> Log.e("Awareness", "have nearby beacons"),
                                    throwable -> logError(throwable, "beacons")
                            )
            );
        }

    }

    private int logError(Throwable throwable, String task) {
        return Log.e("MainActivity", "Error when getting " + task + " " + throwable.getMessage());
    }

    private void setLocation(Location location) {
        String locationText = String.format(Locale.US, "%f/%f - %f (%f km/h)", location.getLatitude(), location.getLongitude(), location.getBearing(), location.getSpeed());
        ((TextView) findViewById(R.id.location)).setText(locationText);
    }

    private void setHeadphoneState(Boolean pluggedIn) {
        String headphoneStateText = pluggedIn ? "Plugged in" : "Unplugged";
        ((TextView) findViewById(R.id.headphones)).setText(headphoneStateText);
    }

    private void setActivity(ActivityRecognitionResult result) {
        String activityText = String.format(Locale.US, "%s (%d)", DetectedActivity.zzsy(result.getMostProbableActivity().getType()), result.getActivityConfidence(result.getMostProbableActivity().getType()));
        ((TextView) findViewById(R.id.activity)).setText(activityText);
    }

    private void setWeather(Weather weather) {
        String weatherText = String.format(Locale.US, "%sC - %s", weather.getTemperature(Weather.CELSIUS), weather.getConditions()[0]);
        ((TextView) findViewById(R.id.weather)).setText(weatherText);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                subscribeToLocationBasedSnapshots();
            }
        }
    }

}
