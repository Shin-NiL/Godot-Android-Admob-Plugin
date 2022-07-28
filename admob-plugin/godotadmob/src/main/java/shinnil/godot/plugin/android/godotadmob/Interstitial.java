package shinnil.godot.plugin.android.godotadmob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

interface InterstitialListener {
    void onInterstitialLoaded();
    void onInterstitialFailedToLoad(int errorCode);
    void onInterstitialOpened();
    void onInterstitialClosed();
    // new
    void onInterstitialClicked();
    void onInterstitialImpression();
}

public class Interstitial {
    private final String id;
    private final AdRequest adRequest;
    private InterstitialAd interstitialAd = null; // Interstitial object
    private final Activity activity;
    private final InterstitialListener defaultInterstitialListener;

    public Interstitial(final String id, final AdRequest adRequest, final Activity activity, final InterstitialListener defaultInterstitialListener) {
        this.activity = activity;
        this.id = id;
        this.adRequest = adRequest;
        this.defaultInterstitialListener = defaultInterstitialListener;
        load();
    }

    public void show() {
        if (interstitialAd != null) {
            interstitialAd.show(activity);
        } else {
            Log.w("w", "AdMob: showInterstitial - interstitial not loaded");
        }
    }

    public boolean isLoaded() {
        return interstitialAd != null;
    }

    private void setAd(InterstitialAd interstitialAd) {
        if (interstitialAd == this.interstitialAd)
            return;
        // Avoid memory leaks
        if (this.interstitialAd != null) {
            this.interstitialAd.setFullScreenContentCallback(null);
            this.interstitialAd.setOnPaidEventListener(null);
        }
        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Log.w("godot", "AdMob: onAdClicked()");
                    defaultInterstitialListener.onInterstitialClicked();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    // TODO: Test if new video ads are loaded
                    setAd(null);
                    Log.w("godot", "AdMob: onAdDismissedFullScreenContent");
                    defaultInterstitialListener.onInterstitialClosed();
                    load();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.w("godot", "AdMob: onAdFailedToShowFullScreenContent");
                    defaultInterstitialListener.onInterstitialFailedToLoad(adError.getCode());
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.w("godot", "AdMob: onAdShowedFullScreenContent");
                    defaultInterstitialListener.onInterstitialOpened();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.w("godot", "AdMob: onAdImpression");
                    defaultInterstitialListener.onInterstitialImpression();
                }
            });
        }
        this.interstitialAd = interstitialAd;
    }

    private void load() {
        InterstitialAd.load(activity, id, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                setAd(interstitialAd);
                Log.w("godot", "AdMob: onAdLoaded: interstitial");
                defaultInterstitialListener.onInterstitialLoaded();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                // safety
                setAd(null);
                Log.w("godot", "AdMob: onAdFailedToLoad(int errorCode) - error code: " + loadAdError.getCode());
                defaultInterstitialListener.onInterstitialFailedToLoad(loadAdError.getCode());
            }
        });
    }
}