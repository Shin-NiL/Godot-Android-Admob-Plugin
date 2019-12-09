GodotAdMob
=====
This is an Android AdMob plugin for Godot Engine (https://github.com/okamstudio/godot) 3.2 or higher. For Godot < 3.2 or iOS support you can use the [old module](https://github.com/kloder-games/godot-admob).

Currently, this plugin supports:
- Banner
- Interstitial
- Rewarded Video

How to use
----------
- Configure, install  and enable the "Android Custom Template" for your project, just follow the [official documentation](https://docs.godotengine.org/en/latest/getting_started/workflow/export/android_custom_build.html);
- download or clone this repository;
- drop the ```admob-plugin``` directory (from this repository) inside the ```res://android/``` directory on your Godot project.
- on the Project -> Export -> Android -> Options -> Permissions: check the permissions for _Access Network State_ and _Internet_
- on the Project Settings -> Android -> Modules, add the string:

```
org/godotengine/godot/GodotAdMob
```

Sample code
-----
In the demo directory you'll find a working sample project where you can see how the things works (specially the callbacks) on the scripting side.

Donations
---------
Was this project useful for you? Wanna make a donation? These are the options:

### Paypal

My [Paypal donation link](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=3MJE3M4FMJYGN&lc=BR&item_name=Shin%2dNiL%27s%20Github&item_number=Github&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted)

### Brave browser

If you don't have any money, but still willing to help me you can install [Brave browser](https://brave.com/) using my [referral link](https://brave.com/shi012	). 
If you do so and keep using the browser, I'll receive some reward.

If you're already a Brave user, please consider donating some BATs ;) 


API Reference
-------------

The following methods are available:
```python

# Init AdMob
# @param bool isReal Show real ad or test ad
# @param int instance_id The instance id from Godot (get_instance_ID())
init(isReal, instance_id)

# Init AdMob with additional Content Rating parameters
# @param bool isReal Show real ad or test ad
# @param int instance_id The instance id from Godot (get_instance_ID())
# @param boolean isForChildDirectedTreatment If isForChildDirectedTreatment is true, maxAdContetRating will be ignored (your maxAdContentRating would can not be other than "G")
# @param String maxAdContentRating It's value must be "G", "PG", "T" or "MA". If the rating of your app in Play Console and your config of maxAdContentRating in AdMob are not matched, your app can be banned by Google.
initWithContentRating(isReal, instance_id, isForChildDirectedTreatment, maxAdContentRating)


# Banner Methods
# --------------

# Load Banner Ads (and show inmediatly)
# @param String id The banner unit id
# @param boolean isTop Show the banner on top or bottom
loadBanner(id, isTop)

# Show the banner
showBanner()

# Hide the banner
hideBanner()

# Resize the banner (when orientation change for example)
resize()

# Get the Banner width
# @return int Banner width
getBannerWidth()

# Get the Banner height
# @return int Banner height
getBannerHeight()

# Callback on ad loaded (Banner)
_on_admob_ad_loaded()

# Callback on ad network error (Banner)
_on_admob_network_error()

# Callback for banner on ad failed to load (other than network error)
_on_admob_banner_failed_to_load()

# Interstitial Methods
# --------------------

# Load Interstitial Ads
# @param String id The interstitial unit id
loadInterstitial(id)

# Show the interstitial ad
showInterstitial()

# Callback for interstitial ad fail on load
_on_interstitial_not_loaded()

# Callback for interstitial loaded
_on_interstitial_loaded

# Callback for insterstitial ad close action
_on_interstitial_close()

# Rewarded Videos Methods
# -----------------------

# Load rewarded videos ads
# @param String id The rewarded video unit id
loadRewardedVideo(id)

# Show the rewarded video ad
showRewardedVideo()

# Callback for rewarded video ad left application
_on_rewarded_video_ad_left_application()

# Callback for rewarded video ad closed 
_on_rewarded_video_ad_closed()

# Callback for rewarded video ad failed to load
# @param int errorCode the code of error
_on_rewarded_video_ad_failed_to_load(errorCode)

# Callback for rewarded video ad loaded
_on_rewarded_video_ad_loaded()

# Callback for rewarded video ad opened
_on_rewarded_video_ad_opened()

# Callback for rewarded video ad reward user
# @param String currency The reward item description, ex: coin
# @param int amount The reward item amount
_on_rewarded(currency, amount)

# Callback for rewarded video ad started do play
_on_rewarded_video_started()
```

Troubleshooting
--------------
* First of all, please make sure you're able to compile the custom build for Android without the Admob plugin, this way we can isolate the cause of the issue.

* Using logcat for Android is the best way to troubleshoot most issues. You can filter Godot only messages with logcat using the command: 
```
adb logcat -s godot
```

* _ERROR_CODE_NO_FILL_ is a common issue with Admob, but out of the scope to this module. Here's the description on the API page: [ERROR_CODE_NO_FILL: The ad request was successful, but no ad was returned due to lack of ad inventory.](https://developers.google.com/android/reference/com/google/android/gms/ads/AdRequest.html#ERROR_CODE_NO_FILL)

References
-------------
Based on the works of:
* https://github.com/Mavhod/GodotAdmob
* https://github.com/kloder-games/godot-admob

License
-------------
MIT license
