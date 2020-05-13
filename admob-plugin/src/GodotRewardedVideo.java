package org.godotengine.godot;

import android.app.Activity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.AdRequest;

import android.util.Log;
import org.godotengine.godot.*;


public class GodotRewardedVideo {
    private RewardedVideoAd rewardedVideoAd = null;

    public void init(Activity activity, final int instanceId) {
        MobileAds.initialize(activity);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.w("godot", "AdMob: onRewardedVideoAdLeftApplication");
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_left_application", new Object[] {});
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Log.w("godot", "AdMob: onRewardedVideoAdClosed");
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_closed", new Object[] {});
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                Log.w("godot", "AdMob: onRewardedVideoAdFailedToLoad. errorCode: " + errorCode);
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_failed_to_load", new Object[] { errorCode });
            }

            @Override
            public void onRewardedVideoAdLoaded() {
                Log.w("godot", "AdMob: onRewardedVideoAdLoaded");
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_loaded", new Object[] {});
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.w("godot", "AdMob: onRewardedVideoAdOpened");
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_ad_opened", new Object[] {});
            }

            @Override
            public void onRewarded(RewardItem reward) {
                Log.w("godot", "AdMob: "
                        + String.format(" onRewarded! currency: %s amount: %d", reward.getType(), reward.getAmount()));
                GodotLib.calldeferred(instanceId, "_on_rewarded",
                        new Object[] { reward.getType(), reward.getAmount() });
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.w("godot", "AdMob: onRewardedVideoStarted");
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_started", new Object[] {});
            }

            @Override
            public void onRewardedVideoCompleted() {
                Log.w("godot", "AdMob: onRewardedVideoCompleted");
                GodotLib.calldeferred(instanceId, "_on_rewarded_video_completed", new Object[] {});
            }
        });
    }

    public void load(final String id, AdRequest adRequest) {
        rewardedVideoAd.loadAd(id, adRequest);
    }

    public void show() {
        if (rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.show();
        }
    }
}