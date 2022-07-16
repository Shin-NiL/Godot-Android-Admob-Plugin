package shinnil.godot.plugin.android.godotadmob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.godotengine.godot.GodotLib;


public class GodotRewardedVideo {
    private RewardedAd rewardedAd = null;
    private String id;
    private int instanceId;
    private AdRequest adRequest;
    private Activity activity;

    public void init(Activity activity, final int instanceId) {
        this.activity = activity;
        this.instanceId = instanceId;
        MobileAds.initialize(activity);
    }

    public boolean isLoaded() {
        return rewardedAd != null;
    }

    public void load(final String id, AdRequest adRequest) {
        this.id = id;
        this.adRequest = adRequest;

        RewardedAd.load(activity, id, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                setAd(rewardedAd);
                Log.w("godot", "AdMob: onAdLoaded");
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_loaded", new Object[]{});
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                // safety
                setAd(null);
                Log.w("godot", "AdMob: onAdFailedToLoad. errorCode: " + loadAdError.getCode());
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_failed_to_load", new Object[]{loadAdError.getCode()});
            }
        });
    }

    public void show() {
        if (rewardedAd != null) {
            rewardedAd.show(activity, rewardItem -> {
                Log.w("godot", "AdMob: "
                        + String.format(" onRewarded! currency: %s amount: %d", rewardItem.getType(), rewardItem.getAmount()));
                GodotLib.calldeferred(instanceId, "_on_rewarded",
                        new Object[]{rewardItem.getType(), rewardItem.getAmount()});
            });
        }
    }

    private void setAd(RewardedAd rewardedAd) {
        // Avoid memory leaks.
        if (this.rewardedAd != null)
            this.rewardedAd.setFullScreenContentCallback(null);
        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Log.w("godot", "AdMob: onAdClicked");
                    GodotLib.calldeferred(instanceId, "_on_rewarded_clicked", new Object[]{});
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    // TODO: Test if new video ads are loaded
//                    setAd(null);
//                    RewardedAd.load(activity, id, adRequest, rewardedAdLoadCallback);
                    Log.w("godot", "AdMob: onAdDismissedFullScreenContent");
                    GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_closed", new Object[]{});
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.w("godot", "AdMob: onAdFailedToShowFullScreenContent");
                    GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_failed_to_load", new Object[]{adError.getCode()});
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.w("godot", "AdMob: onAdImpression");
                    GodotLib.calldeferred(instanceId, "_on_rewarded_impression", new Object[]{});
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.w("godot", "AdMob: onAdShowedFullScreenContent");
                    GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_opened", new Object[]{});
                }
            });
        }
        this.rewardedAd = rewardedAd;
    }
}