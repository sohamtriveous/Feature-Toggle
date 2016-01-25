[![Build Status](https://travis-ci.org/triveous/Feature-Toggle.svg?branch=master)](https://travis-ci.org/triveous/Feature-Toggle)

##A simple android library to add Feature-Toggle capability in your app 

###Philosophy

A feature toggle, is a technique in software development that helps alter the state of a feature in an application dynamically. Let us illustrate the problem with an example:

Lets say, you have discovered a bug in a custom network component that only shows up on a certain device, to resolve an issue you'd have to
- make the fix (might take a couple of days)
- test it
- launch it on the play store
- hope the user then downloads the update (might take some more days, if it happens at all)

This is often a long process and by that time the user might even uninstall the app. To shorten the turn around time, you can issue an immediate solution: you can dynamically disable the feature for the device using this library. This happens the next time the user opens the app or even over the air (via push messaging).

With this library the following would happen
- You would update a configuration file on your server disabling the feature for that device
- The next time the app is opened (or over the air) the app downloads the config again
- It then checks whether the feature is enabled/disabled and disables the feature silently

Other use cases:
- You can also use this library to enable features (show messages for example) at certain times or on certain devices etc.
- Test certain features for certain devices/api levels dynamically

In the context of android, it is often important to have the ability to 
- change the capability/featureset of an app in a live environment
- change the capability/featureset of an app based on api-level/app-version/date/device-model/device-manufacturer and other such parameters

###Example

####Downloading a new configuration
To download a new configuration please do the following
```java
Toggle.with(context).setConfig(myUrl);
```

You can also set the configuration manually by calling setConfig on a String (in the Config JSON format) or from a Config object
```java
String configInJson = ... // my custom code for downloading the config from my server and retrieving it as a json
Toggle.with(context).setConfig(configInJson);
```
or as a Config object
```java
Config config = ... // my custom call (say Retrofit for example) for retrieveing the config from my server
Toggle.with(context).setConfig(config);
```

Once setConfig is called (in any form) the config is then cached locally, so you can always check for a feature later

####Checking for a feature
You can check for a feature using the check method
```java
Toggle.with(context).check("custom_network_component").defaultState(Toggle.ENABLED).start(new cc.soham.toggle.callbacks.Callback() {
            @Override
            public void onStatusChecked(String feature, String state, String metadata, boolean cached) {
                updateUiAfterResponse(feature, state, metadata, cached);
            }
        });
```

In case you are used a URL in setConfig, you can also use the getLatest flag to get the latest config before making the callback
```java
Toggle.with(context).check("custom_network_component").getLatest().defaultState(Toggle.ENABLED).start(new cc.soham.toggle.callbacks.Callback() {
            @Override
            public void onStatusChecked(String feature, String state, String metadata, boolean cached) {
                updateUiAfterResponse(feature, state, metadata, cached);
            }
        });
```        
