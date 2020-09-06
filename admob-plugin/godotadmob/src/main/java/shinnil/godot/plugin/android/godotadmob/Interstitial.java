package shinnil.godot.plugin.android.godotadmob;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;

interface InterstitialListener {
    void onInterstitialLoaded();
    void onInterstitialFailedToLoad(int errorCode);
    void onInterstitialOpened();
    void onInterstitialLeftApplication();
    void onInterstitialClosed();
}

public class Interstitial {
    private InterstitialAd interstitialAd = null; // Interstitial object
    private InterstitialListener defaultInterstitialListener;

    public Interstitial(final String id, final AdRequest adRequest, final Activity activity, final InterstitialListener defaultInterstitialListener) {
        this.defaultInterstitialListener = defaultInterstitialListener;
        interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(id);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.w("godot", "AdMob: onAdLoaded");
                defaultInterstitialListener.onInterstitialLoaded();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.w("godot", "AdMob: onAdFailedToLoad(int errorCode) - error code: " + Integer.toString(adError.getCode()));
                defaultInterstitialListener.onInterstitialFailedToLoad(adError.getCode());
            }

            @Override
            public void onAdOpened() {
                Log.w("godot", "AdMob: onAdOpened()");
                defaultInterstitialListener.onInterstitialOpened();
            }

            @Override
            public void onAdLeftApplication() {
                Log.w("godot", "AdMob: onAdLeftApplication()");
                defaultInterstitialListener.onInterstitialLeftApplication();
            }

            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(adRequest);
                Log.w("godot", "AdMob: onAdClosed");
                defaultInterstitialListener.onInterstitialClosed();
            }
        });

        interstitialAd.loadAd(adRequest);
    }

    public void show() {
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.w("w", "AdMob: showInterstitial - interstitial not loaded");
        }
    }
}