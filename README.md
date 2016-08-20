# ReactiveAwareness: Android Awareness APIs in a reactive manner with RxJava

ReactiveAwareness lets you use the Android Awareness API with RxJava and without handling all
those GoogleApiClients yourself.

Learn more about the Android Awareness APIs at
<a href="https://developers.google.com/awareness/">developers.google.com</a>.

This library supports both the Snapshot API for providing polling based information about the users
context as well as the Fence API for getting callbacks for specific states (fences) the users
context might be in.

## Set-up

To use ReactiveAwareness in your project, add the library as a dependency in your `build.gradle`
file:
```groovy
dependencies {
    compile 'com.mtramin:reactiveawareness:1.0.0'
}
```

To use ReactiveAwareness Fences in your project, add the library as a dependency in your
`build.gradle` file:
```groovy
dependencies {
    compile 'com.mtramin:reactiveawareness-fence:1.0.0'
}
```

### MinSdk

This libraries minSdkVersion is 15.

Requests regarding beacons need at least API level 18 to run. Make sure at runtime that you have a
supported Sdk version for these calls.

### Permissions

Depending on your usage of ReactiveAwareness you might also have to declare some permissions
 in your `AndroidManifest.xml`. Remember that from Android Marshmallow (API 23) you will have to
 support runtime permissions for those:
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="com.google.android.gms.permission.ACTIVITY_RECOGNITION" />
```

The Location permission is used for all location based events in the Awareness API.

The Activity Recognition permission is used for recognizing the current activity of the user.

### API Keys

As ReactiveAwareness will query Google APIs for the results, you need to provide API Keys in your
applications `AndroidManifest.xml`. For more information about which API Keys you need and how to
aquire them, please read the official Google documentation at
<a href="https://developers.google.com/awareness/android-api/get-a-key">developers.google.com</a>

Should you call a method that requires an API Key but you don't specify it correctly in your
applications Manifest, ReactiveAwareness will throw an Exception.

## Using the reactive Snapshot API (ReactiveSnapshot)

All your interactions with the SnapshotAPI through the library will be through the
`ReactiveSnapshot` class.

Obtain an instance by calling:
``` java
ReactiveSnapshot.create(context)
```

Then you can use this instance to reactively retrieve context information about the current state of
the device such as the weather at the device location. Be sure to unsubscribe from the Singles as
soon as you don't care about their result anymore as this will then also cancel the request with the
GoogleApiClient (e.g. in onStop of your Activity).

Simply subscribe to the provided methods (using weather as an example here).

``` java
reactiveSnapshot.getWeather()
    .subscribe(
        weather -> handleWeather(weather),
        throwable -> handleError(throwable)
    );
```

## Using the reactive Fence API (ReactiveFences)

For using reactive fences there are two different options depending on the use case.

To retrieve updates to specific fences while the application is in foreground you can use an
ObservableFence which will provide updates to its fences via an Observable.

To get updates to fences while the application is either in the foreground but also when it is in
the background you can use a BackgroundFence.

### Defining Fences

For more information about how to define AwarenessFences please refer to the documentation on
<a href="https://developers.google.com/awareness/">developers.google.com</a>.

ReactiveAwareness supports all Fences that are available.

### ObservableFence

To use an ObservableFence simply create one by calling:

``` java
ObservableFence.create(context, fence)
    .subscribe(
            isTrue -> Log.e(TAG, "Observable Fence State: " + isTrue),
            throwable -> handleError(throwable)
    )
```

As long as you are subscribed to this Observable you will continue to receive status updates when
the state of the fence changes. Please remember to unsubscribe from the Observable when appropriate.
This will also automatically unregister the fence and will stop further updates.

### BackgroundFence

BackgroundFences will also receive updates to fence states when the application is in the background
via a BroadcastReceiver. To use BackgroundFences you have to extend the libraries FenceReceiver in
your application and implement the onUpdate method which will notify your implementation about state
changes in one of your fences.

Please register your implementation of the FenceReceiver in your AndroidManifest.xml. Please also
keep the action name and use "ReactiveAwarenessFence" as the action.

``` xml
<receiver android:name=".ExampleFenceReceiver">
    <intent-filter>
        <action android:name="ReactiveAwarenessFence"/>
    </intent-filter>
</receiver>
```

BackgroundFences are named with unique names. This way you can identify which of your fences was
updated.

For registering and unregistering use the BackgroundFence class which provides you the necessary
methods to do so:

``` java
BackgroundFence.register(context, "name_example", fence);

BackgroundFence.unregister(context, "name_example");
```

To query which fences are currently registered and to retrieve their current states you can also
call the query method.

## Dependencies

ReactiveAwareness brings the following dependencies:

- RxJava
- Google Play Services (contextmanager and location) which provides the Awareness API
- Support Annotations to let you know which requests need permissions to successfully run

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/mauin/ReactiveAwareness/issues).

## LICENSE

Copyright 2016 Marvin Ramin.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.