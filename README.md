# GodotAdMob

This is an Android AdMob plugin for Godot Engine (https://github.com/okamstudio/godot) 3.2.2 or higher. For Godot < 3.2 or iOS support you can use the [old module](https://github.com/kloder-games/godot-admob).

Currently, this plugin supports:
- Banner
- Interstitial
- Rewarded Video
- [Rewarded Interstitial](https://developers.google.com/admob/android/rewarded-interstitial)

## Setup

### Video Guide
Our friend *dQuigz* created a [nice video tutorial on how to use this plugin](https://youtu.be/0a6EvlNgLL0).

### Text Instructions
- Configure, install  and enable the "Android Custom Template" for your project, just follow the [official documentation](https://docs.godotengine.org/en/stable/getting_started/workflow/export/android_custom_build.html);
- go to the [release tab](https://github.com/Shin-NiL/Godot-Android-AdMob-Plugin/releases), choose a version and download the respective ```GodotAdMobPlugin-x.x.x.zip``` package;
- extract the content of the ```admob-plugin``` directory (```GodotAdmob.gdap``` and ```GodotAdmob.release.aar``` from the zip package) inside the ```res://android/plugins``` directory on your Godot project.
- extract the ```admob-lib``` directory (from the zip package) inside the ```res://``` directory on your Godot project.
- on the Project -> Export... -> Android -> Options ->
    - Permissions: check the permissions for _Access Network State_ and _Internet_
    - Custom Template: check the _Use Custom Build_
    - Plugins: check the _Godot Ad Mob_ (this plugin)
- edit the file ```res://android/build/AndroidManifest.xml``` to add your App ID as described [here](https://developers.google.com/admob/android/quick-start#update_your_androidmanifestxml). For the demo project, for example, you should use:
```
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3347511713"/>
```
One good place to add this metadata is just below these lines, inside of the **application** tag:
```
<application>
...
<!-- Custom application XML added by add-ons. -->
<!--CHUNK_APPLICATION_BEGIN-->
<!--CHUNK_APPLICATION_END-->

Here
</application>
```
- To avoid the mergeDex error, enable the multidex support. Edit the file ```res://android/build/build.gradle``` and insert the line `multiDexEnabled = true` inside the `android` => `defaultConfig`:
```
android {
    defaultConfig {
        ...
        multiDexEnabled = true
        ...
    }
}
```

- (Temporary workaround, not required for Godot 3.5 and later) edit the file ```res://android/build/config.gradle``` to set proper SDK version.  Change compileSdk to 30 and buildTools to '30.0.0'
Play services after version 20.4.0 require compileSdk 31 or higher.  Applications will not compile with 30.  The Godot 3.x, branch after 3.4, sets the compileSdk to 32, which is why Godot 3.5 will no longer require this step to modify config.gradle.

**NOTE**: everytime you install a new version of the Android Build Template this step must be done again, as the ```AndroidManifest.xml``` file will be overriden.


Now you'll be able to add an AdMob Node to your scene (**only one node should be added per scene**)

![Searching AdMob node](images/search_node.png)

Edit its properties

![AdMob properties](images/properties.png)

And connect its signals

![AdMob signals](images/signals.png)

## Sample Code

In the demo directory you'll find a working sample project where you can see how the things works on the scripting side.

__REMEMBER__: You still need to configure the project as described in the "Setup" section to be able to run the demo project.

## Showcase

Does this plugin really work? Yes sir! You can see a list of published games [here](showcase.md).


## Donations

Was this project useful for you? Wanna make a donation? These are the options:

### Paypal

My [Paypal donation link](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=3MJE3M4FMJYGN&lc=BR&item_name=Shin%2dNiL%27s%20Github&item_number=Github&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted)

### Brave Browser
If you're a Brave browser user, please consider donating some BATs ;)


## API Reference

### Properties
```python
# If true use your real ad, if false use test ads. Make sure to only set it to true with your published apk, otherwise you can be banned by Google
# type bool, default false
is_real

# If true, displays banner on the top of the screen, if false displays on the bottom
# type bool, default true
banner_on_top

# The banner size constants
# Valid values are: "ADAPTIVE_BANNER", "SMART_BANNER", "BANNER", "LARGE_BANNER", "MEDIUM_RECTANGLE", "FULL_BANNER", "LEADERBOARD"
banner_size

# Your app banner ad ID
# type String, optional
banner_id

# Your app interstitial ad ID
# type String, optional
interstitial_id

# Your app rewarded video ad ID
# type String, optional
rewarded_id

# Your app rewarded interstitial ad ID
# type String, optional
rewarded_interstitial_id

# If true, set the ads to children directed. If true, max_ad_content_rate will be ignored (your max_ad_content_rate would can not be other than "G")
# type bool, default false
child_directed

# If ads should be personalized. In the European Economic Area, GDPR requires ad personalization to be opt-in.
# type bool, default true
is_personalized

# Its value must be "G", "PG", "T" or "MA". If the rating of your app in Play Console and your config of max_ad_content_rate in AdMob are not matched, your app can be banned by Google
# type String, default G
max_ad_content_rate
```

### Methods
```python

# Load the banner (and show inmediatly)
load_banner()

# Load the interstitial ad
load_interstitial()

# Load the rewarded video ad
load_rewarded_video()

# Load the rewarded interstitial ad
load_rewarded_interstitial()

# Show the banner ad
show_banner()

# Hide the banner ad
hide_banner()

# Move banner after loaded
move_banner(on_top: bool)

# Show the interstitial ad
show_interstitial()

# Show the rewarded video ad
show_rewarded_video()

# Show the rewarded interstitial ad
show_rewarded_interstitial()

# Check if the interstitial ad is loaded
# @return bool true if is loaded
is_interstitial_loaded()

# Check if the rewarded video ad is loaded
# @return bool true if is loaded
is_rewarded_video_loaded()

# Check if the rewarded interstitial ad is loaded
# @return bool true if is loaded
is_rewarded_interstitial_loaded()

# Resize the banner (useful when the orientation changes for example)
banner_resize()

# Get the current banner dimension
# @return Vector2 (width, height)
get_banner_dimension()

```
### Signals
```python
# Banner ad was loaded with success
banner_loaded

# Banner ad has failed to load
# @param int error_code the error code
banner_failed_to_load(error_code)

# Interstitial ad was loaded with success
interstitial_loaded

# Interstitial ad was opened
interstitial_opened

# Interstitial ad was closed
interstitial_closed

# Interstitial ad has failed to load
# @param int error_code the error code
interstitial_failed_to_load(error_code)

# Interstitial ad has been clicked
interstitial_clicked

# The user has provided an interstitial impression.
interstitial_impression

# Rewarded video ad was loaded with success
rewarded_video_loaded

# Rewarded video ad was opened
rewarded_video_opened

# Rewarded video ad was closed
rewarded_video_closed

# Rewarded video ad has failed to load
# @param int error_code the error code
rewarded_video_failed_to_load(error_code)

# Rewarded interstitial ad was loaded with success
rewarded_interstitial_loaded

# Rewarded interstitial ad was opened
rewarded_interstitial_opened

# Rewarded interstitial ad was closed
rewarded_interstitial_closed

# Rewarded interstitial ad has failed to load
# @param int error_code the error code
rewarded_interstitial_failed_to_load(error_code)

# Rewarded interstitial ad has failed to show
# @param int error_code the error code
rewarded_interstitial_failed_to_show(error_code)

# Rewarded video/interstitial ad was watched and will reward the user
# @param String currency The reward item description, ex: coin
# @param int amount The reward item amount
rewarded(currency, amount)

# Rewarded video was clicked
rewarded_clicked

# The user has given an impression for a rewarded video.
rewarded_impression
```

## Compiling the Plugin (optional)

If you want to compile the plugin by yourself, it's very easy:
1. Clone this repository;
2. Checkout the desired version;
3. Download the AAR library for Android plugin from the official Godot website;
4. Copy the downloaded AAR file into the `admob-plugin/godot-lib.release/` directory and rename it to `godot-lib.release.aar`;
5. Configure the demo project according to the "Setup" instructions. Install the "Android Custom Template" for the demo project. This generates the required build configuration.
6. Using command line go to the `admob-plugin/` directory;
7. Run `gradlew build`.

If everything goes fine, you'll find the `.aar` files at `admob-plugin/godotadmob/build/outputs/aar/`.

## Troubleshooting

* First of all, please make sure you're able to compile the custom build for Android without the AdMob plugin, this way we can isolate the cause of the issue.

* Using logcat for Android is the best way to troubleshoot most issues. You can filter Godot only messages with logcat using the command:
```
adb logcat -s godot
```
* _AdMob Java Singleton not found_:
    1. this plugin is Android only, so the AdMob Java singleton will only exists on the Android platform. In other words, you will be able to run it on an Android device (or emulator) only, it will not work on editor or on another platform;
    2. make sure you checked the _Use Custom Build_ and _Godot Ad Mob_ options in the export window.

* App is crashing at startup: use the `logcat` to check what's happening. It can be caused for many different reasons, if you forgot to or not configure correctly you App ID on the `AndroidManifest.xml`, for example, your app will crash.

* Error code 3 (_ERROR_CODE_NO_FILL_) is a common issue with Admob, but out of the scope to this plugin. Here's the description on the API page: [ERROR_CODE_NO_FILL: The ad request was successful, but no ad was returned due to lack of ad inventory.](https://developers.google.com/android/reference/com/google/android/gms/ads/AdRequest.html#ERROR_CODE_NO_FILL)

* Any other error code: you can find more information about the error codes [here](https://support.google.com/admob/thread/3494603). Please don't open issues on this repository asking for help about that, as we can't provide any, sorry.

* Banner sizes: [Adaptive Banners](https://developers.google.com/admob/android/banner/adaptive) and [Smart Banners](https://developers.google.com/admob/android/banner/smart) uses dynamic banner sizes, the [other options](https://developers.google.com/admob/android/banner) uses fixed sizes, please check its respectives documentation for more details. Smart banners are deprecated and may not work properly.

## References

Based on the works of:
* https://github.com/Mavhod/GodotAdmob
* https://github.com/kloder-games/godot-admob

## License

MIT license
