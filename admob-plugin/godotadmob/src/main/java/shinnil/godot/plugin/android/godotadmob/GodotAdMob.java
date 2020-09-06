package shinnil.godot.plugin.android.godotadmob;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;

public class GodotAdMob extends GodotPlugin {
    private Activity activity = null; // The main activity of the game

    private boolean isReal = false; // Store if is real or not
    private boolean isForChildDirectedTreatment = false; // Store if is children directed treatment desired
    private boolean isPersonalized = true; // ads are personalized by default, GDPR compliance within the European Economic Area may require you to disable personalization.
    private String maxAdContentRating = ""; // Store maxAdContentRating ("G", "PG", "T" or "MA")
    private Bundle extras = null;

    private FrameLayout layout = null; // Store the layout

    private RewardedVideo rewardedVideo = null; // Rewarded Video object
    private Interstitial interstitial = null; // Interstitial object
    private Banner banner = null; // Banner object


    public GodotAdMob(Godot godot) {
        super(godot);
        activity = godot;
    }

    // create and add a new layout to Godot
    @Override
    public View onMainCreate(Activity activity) {
        layout = new FrameLayout(activity);
        return layout;
    }

    @NonNull
    @Override
    public String getPluginName() {
        return "GodotAdMob";
    }

    @NonNull
    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
                "init",
                "initWithContentRating",
                // banner
                "loadBanner", "showBanner", "hideBanner", "getBannerWidth", "getBannerHeight", "resize", "move",
                // Interstitial
                "loadInterstitial", "showInterstitial",
                // Rewarded video
                "loadRewardedVideo", "showRewardedVideo");
    }

    @NonNull
    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new ArraySet<>();

        signals.add(new SignalInfo("on_admob_ad_loaded"));
        signals.add(new SignalInfo("on_admob_banner_failed_to_load", Integer.class));


        signals.add(new SignalInfo("on_interstitial_loaded"));
        signals.add(new SignalInfo("on_interstitial_failed_to_load", Integer.class));
        signals.add(new SignalInfo("on_interstitial_close"));


        signals.add(new SignalInfo("on_rewarded_video_ad_left_application"));
        signals.add(new SignalInfo("on_rewarded_video_ad_closed"));
        signals.add(new SignalInfo("on_rewarded_video_ad_failed_to_load", Integer.class));
        signals.add(new SignalInfo("on_rewarded_video_ad_loaded"));
        signals.add(new SignalInfo("on_rewarded_video_ad_opened"));
        signals.add(new SignalInfo("on_rewarded", String.class, Integer.class));
        signals.add(new SignalInfo("on_rewarded_video_started"));
        signals.add(new SignalInfo("on_rewarded_video_completed"));

        return signals;
    }

    /* Init
     * ********************************************************************** */

    /**
     * Prepare for work with AdMob
     *
     * @param isReal     Tell if the enviroment is for real or test
     */
    public void init(boolean isReal) {
        this.initWithContentRating(isReal, false, true, "");
    }

    /**
     * Init with content rating additional options
     *
     * @param isReal                      Tell if the enviroment is for real or test
     * @param isForChildDirectedTreatment
     * @param isPersonalized              If ads should be personalized or not.
     *                                    GDPR compliance within the European Economic Area requires that you
     *                                    disable ad personalization if the user does not wish to opt into
     *                                    ad personalization.
     * @param maxAdContentRating          must be "G", "PG", "T" or "MA"
     */
    public void initWithContentRating(
            boolean isReal,
            boolean isForChildDirectedTreatment,
            boolean isPersonalized,
            String maxAdContentRating) {

        this.isReal = isReal;
        this.isForChildDirectedTreatment = isForChildDirectedTreatment;
        this.isPersonalized = isPersonalized;
        this.maxAdContentRating = maxAdContentRating;

        this.setRequestConfigurations();

        if (!isPersonalized) {
            // https://developers.google.com/admob/android/eu-consent#forward_consent_to_the_google_mobile_ads_sdk
            if (extras == null) {
                extras = new Bundle();
            }
            extras.putString("npa", "1");
        }

        Log.d("godot", "AdMob: init with content rating options");
    }


    private void setRequestConfigurations() {
        if (!this.isReal) {
            List<String> testDeviceIds = Arrays.asList(AdRequest.DEVICE_ID_EMULATOR, getAdMobDeviceId());
            RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setTestDeviceIds(testDeviceIds)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
        }

        if (this.isForChildDirectedTreatment) {
            RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setTagForChildDirectedTreatment(TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
        }

        if (this.maxAdContentRating != null && this.maxAdContentRating != "") {
            RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration()
                    .toBuilder()
                    .setMaxAdContentRating(this.maxAdContentRating)
                    .build();
            MobileAds.setRequestConfiguration(requestConfiguration);
        }
    }


    /**
     * Returns AdRequest object constructed considering the extras.
     *
     * @return AdRequest object
     */
    private AdRequest getAdRequest() {
        AdRequest.Builder adBuilder = new AdRequest.Builder();
        AdRequest adRequest;
        if (!this.isForChildDirectedTreatment && extras != null) {
            adBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        adRequest = adBuilder.build();
        return adRequest;
    }

    /* Rewarded Video
     * ********************************************************************** */

    /**
     * Load a Rewarded Video
     *
     * @param id AdMod Rewarded video ID
     */
    public void loadRewardedVideo(final String id) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rewardedVideo = new RewardedVideo(activity, new RewardedVideoListener() {
                    @Override
                    public void onRewardedVideoLoaded() {
                        emitSignal("on_rewarded_video_ad_loaded");
                    }

                    @Override
                    public void onRewardedVideoFailedToLoad(int errorCode) {
                        emitSignal("on_rewarded_video_ad_failed_to_load", errorCode);
                    }

                    @Override
                    public void onRewardedVideoLeftApplication() {
                        emitSignal("on_rewarded_video_ad_left_application");
                    }

                    @Override
                    public void onRewardedVideoOpened() {
                        emitSignal("on_rewarded_video_ad_opened");
                    }

                    @Override
                    public void onRewardedVideoClosed() {
                        emitSignal("on_rewarded_video_ad_closed");
                    }

                    @Override
                    public void onRewarded(String type, int amount) {
                        emitSignal("on_rewarded", type, amount);
                    }

                    @Override
                    public void onRewardedVideoStarted() {
                        emitSignal("on_rewarded_video_started");
                    }

                    @Override
                    public void onRewardedVideoCompleted() {
                        emitSignal("on_rewarded_video_completed");
                    }
                });
                rewardedVideo.load(id, getAdRequest());
            }
        });
    }

    /**
     * Show a Rewarded Video
     */
    public void showRewardedVideo() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rewardedVideo == null) {
                    return;
                }
                rewardedVideo.show();
            }
        });
    }


    /* Banner
     * ********************************************************************** */

    /**
     * Load a banner
     *
     * @param id      AdMod Banner ID
     * @param isOnTop To made the banner top or bottom
     */
    public void loadBanner(final String id, final boolean isOnTop, final String bannerSize) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (banner != null) banner.remove();
                banner = new Banner(id, getAdRequest(), activity, new BannerListener() {
                    @Override
                    public void onBannerLoaded() {
                        emitSignal("on_admob_ad_loaded");
                    }

                    @Override
                    public void onBannerFailedToLoad(int errorCode) {
                        emitSignal("on_admob_banner_failed_to_load", errorCode);
                    }
                }, isOnTop, layout, bannerSize);
            }
        });
    }

    /**
     * Show the banner
     */
    public void showBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (banner != null) {
                    banner.show();
                }
            }
        });
    }

    /**
     * Resize the banner
     * @param isOnTop To made the banner top or bottom
     */
    public void move(final boolean isOnTop) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (banner != null) {
                    banner.move(isOnTop);
                }
            }
        });
    }

    /**
     * Resize the banner
     */
    public void resize() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (banner != null) {
                    banner.resize();
                }
            }
        });
    }


    /**
     * Hide the banner
     */
    public void hideBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (banner != null) {
                    banner.hide();
                }
            }
        });
    }

    /**
     * Get the banner width
     *
     * @return int Banner width
     */
    public int getBannerWidth() {
        if (banner != null) {
            return banner.getWidth();
        }
        return 0;
    }

    /**
     * Get the banner height
     *
     * @return int Banner height
     */
    public int getBannerHeight() {
        if (banner != null) {
            return banner.getHeight();
        }
        return 0;
    }

    /* Interstitial
     * ********************************************************************** */

    /**
     * Load a interstitial
     *
     * @param id AdMod Interstitial ID
     */
    public void loadInterstitial(final String id) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                interstitial = new Interstitial(id, getAdRequest(), activity, new InterstitialListener() {
                    @Override
                    public void onInterstitialLoaded() {
                        emitSignal("on_interstitial_loaded");
                    }

                    @Override
                    public void onInterstitialFailedToLoad(int errorCode) {
                        emitSignal("on_interstitial_failed_to_load", errorCode);
                    }

                    @Override
                    public void onInterstitialOpened() {
                        // Not Implemented
                        // emitSignal("on_interstitial_opened");
                    }

                    @Override
                    public void onInterstitialLeftApplication() {
                        // Not Implemented
                        // emitSignal("on_interstitial_left_application");
                    }

                    @Override
                    public void onInterstitialClosed() {
                        emitSignal("on_interstitial_close");
                    }
                });
            }
        });
    }

    /**
     * Show the interstitial
     */
    public void showInterstitial() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitial != null) {
                    interstitial.show();
                }
            }
        });
    }

    /* Utils
     * ********************************************************************** */

    /**
     * Generate MD5 for the deviceID
     *
     * @param s The string to generate de MD5
     * @return String The MD5 generated
     */
    private String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2) h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            //Logger.logStackTrace(TAG,e);
        }
        return "";
    }

    /**
     * Get the Device ID for AdMob
     *
     * @return String Device ID
     */
    private String getAdMobDeviceId() {
        String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase(Locale.US);
        return deviceId;
    }

}
