package shinnil.godot.plugin.android.godotadmob;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

interface RewardedVideoListener {
    void onRewardedVideoLoaded();
    void onRewardedVideoFailedToLoad(int errorCode);
    void onRewardedVideoLeftApplication();
    void onRewardedVideoOpened();
    void onRewardedVideoClosed();
    void onRewarded(String type, int amount);
    void onRewardedVideoStarted();
    void onRewardedVideoCompleted();
}

public class RewardedVideo {
    private RewardedVideoAd rewardedVideoAd = null;

    public RewardedVideo(Activity activity, final RewardedVideoListener defaultRewardedVideoListener) {
        MobileAds.initialize(activity);
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(activity);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                Log.w("godot", "AdMob: onRewardedVideoAdLoaded");
                defaultRewardedVideoListener.onRewardedVideoLoaded();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
                Log.w("godot", "AdMob: onRewardedVideoAdFailedToLoad. errorCode: " + errorCode);
                defaultRewardedVideoListener.onRewardedVideoFailedToLoad(errorCode);
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                Log.w("godot", "AdMob: onRewardedVideoAdLeftApplication");
                defaultRewardedVideoListener.onRewardedVideoLeftApplication();
            }

            @Override
            public void onRewardedVideoAdOpened() {
                Log.w("godot", "AdMob: onRewardedVideoAdOpened");
                defaultRewardedVideoListener.onRewardedVideoOpened();
            }

            @Override
            public void onRewardedVideoAdClosed() {
                Log.w("godot", "AdMob: onRewardedVideoAdClosed");
                defaultRewardedVideoListener.onRewardedVideoClosed();
            }

            @Override
            public void onRewarded(RewardItem reward) {
                Log.w("godot", "AdMob: "
                        + String.format(" onRewarded! currency: %s amount: %d", reward.getType(), reward.getAmount()));
                defaultRewardedVideoListener.onRewarded(reward.getType(), reward.getAmount());
            }

            @Override
            public void onRewardedVideoStarted() {
                Log.w("godot", "AdMob: onRewardedVideoStarted");
                defaultRewardedVideoListener.onRewardedVideoStarted();
            }

            @Override
            public void onRewardedVideoCompleted() {
                Log.w("godot", "AdMob: onRewardedVideoCompleted");
                defaultRewardedVideoListener.onRewardedVideoCompleted();
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