package org.godotengine.godot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import java.util.Locale;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.ads.mediation.admob.AdMobAdapter;

import org.godotengine.godot.*;

public class GodotAdMob extends Godot.SingletonBase
{
	private Activity activity = null; // The main activity of the game
	private int instanceId = 0;

	private boolean isReal = false; // Store if is real or not
	private boolean isForChildDirectedTreatment = false; // Store if is children directed treatment desired
	private boolean isPersonalized = true; // ads are personalized by default, GDPR compliance within the European Economic Area may require you to disable personalization.
	private String maxAdContentRating = ""; // Store maxAdContentRating ("G", "PG", "T" or "MA")
	private Bundle extras = null;

	private FrameLayout layout = null; // Store the layout

	private RewardedVideo rewardedVideo = null; // Rewarded Video object
	private Interstitial interstitial = null; // Interstitial object
	private Banner banner = null; // Banner object
	
	// create and add a new layout to Godot
	@Override
	public View onMainCreateView(Activity activity) {
		layout = new FrameLayout(activity);
		return layout;
	}

	/* Init
	 * ********************************************************************** */

	/**
	 * Prepare for work with AdMob
	 * @param boolean isReal Tell if the enviroment is for real or test
	 * @param int gdscript instance id
	 */
	public void init(boolean isReal, int instanceId) {
		this.initWithContentRating(isReal, instanceId, false, true, "");
	}

	/**
	 * Init with content rating additional options 
	 * @param boolean isReal Tell if the enviroment is for real or test
	 * @param int gdscript instance id
	 * @param boolean isForChildDirectedTreatment
	 * @param boolean isPersonalized If ads should be personalized or not.
	 *  GDPR compliance within the European Economic Area requires that you
	 *  disable ad personalization if the user does not wish to opt into
	 *  ad personalization.
	 * @param String maxAdContentRating must be "G", "PG", "T" or "MA"
	 */
	public void initWithContentRating(
		boolean isReal,
		int instanceId,
		boolean isForChildDirectedTreatment,
		boolean isPersonalized,
		String maxAdContentRating)
	{
		this.isReal = isReal;
		this.instanceId = instanceId;
		this.isForChildDirectedTreatment = isForChildDirectedTreatment;
		this.isPersonalized = isPersonalized;
		this.maxAdContentRating = maxAdContentRating;
		if (maxAdContentRating != null && maxAdContentRating != "")
		{
			extras = new Bundle();
			extras.putString("max_ad_content_rating", maxAdContentRating);
		}
		if(!isPersonalized)
		{
			// https://developers.google.com/admob/android/eu-consent#forward_consent_to_the_google_mobile_ads_sdk
			if(extras == null)
			{
				extras = new Bundle();
			}
			extras.putString("npa", "1");
		}
		Log.d("godot", "AdMob: init with content rating options");
	}


	/**
	 * Returns AdRequest object constructed considering the parameters set in constructor of this class.
	 * @return AdRequest object
	 */
	private AdRequest getAdRequest()
	{
		AdRequest.Builder adBuilder = new AdRequest.Builder();
		AdRequest adRequest;
		if (!this.isForChildDirectedTreatment && extras != null)
		{
			adBuilder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
		}
		if (this.isForChildDirectedTreatment)
		{
			adBuilder.tagForChildDirectedTreatment(true);
		}
		if (!isReal) {
			adBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			adBuilder.addTestDevice(getAdmobDeviceId());
		}
		adRequest = adBuilder.build();
		return adRequest;
	}

	/* Rewarded Video
	 * ********************************************************************** */
	
	public void initRewardedVideo() {
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				rewardedVideo = new RewardedVideo(activity, instanceId);
			}
		});	
	}

	/**
	 * Load a Rewarded Video
	 * @param String id AdMod Rewarded video ID
	 */
	public void loadRewardedVideo(final String id) {

		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (rewardedVideo == null) {
					initRewardedVideo();
				}
				rewardedVideo.load(id, getAdRequest());
			}
		});
	}

	/**
	 * Show a Rewarded Video
	 */
	public void showRewardedVideo() {
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
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
	 * @param String id AdMod Banner ID
	 * @param boolean isOnTop To made the banner top or bottom
	 */
	public void loadBanner(final String id, final boolean isOnTop)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				banner = new Banner(id, getAdRequest(), activity, instanceId, isOnTop, layout);
			}
		});
	}

	/**
	 * Show the banner
	 */
	public void showBanner()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (banner != null) {
					banner.show();
				}
			}
		});
	}

	/**
	 * Resize the banner
	 *
	 */
	public void resize()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (banner != null) {
					banner.resize();
				}
			}
		});
	}


	/**
	 * Hide the banner
	 */
	public void hideBanner()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				if (banner != null) {
					banner.hide();
				}
			}
		});
	}

	/**
	 * Get the banner width
	 * @return int Banner width
	 */
	public int getBannerWidth()
	{
		if (banner != null) {
			return banner.getWidth();
		}
		return 0;
	}

	/**
	 * Get the banner height
	 * @return int Banner height
	 */
	public int getBannerHeight()
	{
		if (banner != null) {
			return banner.getHeight();
		}
		return 0;
	}

	/* Interstitial
	 * ********************************************************************** */

	/**
	 * Load a interstitial
	 * @param String id AdMod Interstitial ID
	 */
	public void loadInterstitial(final String id)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
				interstitial = new Interstitial(id, getAdRequest(), activity, instanceId);
			}
		});
	}

	/**
	 * Show the interstitial
	 */
	public void showInterstitial()
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override public void run()
			{
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
	 * @param String s The string to generate de MD5
	 * @return String The MD5 generated
	 */
	private String md5(final String s)
	{
		try {
			// Create MD5 Hash
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i=0; i<messageDigest.length; i++) {
				String h = Integer.toHexString(0xFF & messageDigest[i]);
				while (h.length() < 2) h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();
		} catch(NoSuchAlgorithmException e) {
			//Logger.logStackTrace(TAG,e);
		}
		return "";
	}

	/**
	 * Get the Device ID for AdMob
	 * @return String Device ID
	 */
	private String getAdmobDeviceId()
	{
		String android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
		String deviceId = md5(android_id).toUpperCase(Locale.US);
		return deviceId;
	}

	/* Definitions
	 * ********************************************************************** */

	/**
	 * Initilization Singleton
	 * @param Activity The main activity
	 */
 	static public Godot.SingletonBase initialize(Activity activity)
 	{
 		return new GodotAdMob(activity);
 	}

	/**
	 * Constructor
	 * @param Activity Main activity
	 */
	public GodotAdMob(Activity p_activity) {
		registerClass("AdMob", new String[] {
			"init",
			"initWithContentRating",
			// banner
			"loadBanner", "showBanner", "hideBanner", "getBannerWidth", "getBannerHeight", "resize",
			// Interstitial
			"loadInterstitial", "showInterstitial",
			// Rewarded video
			"loadRewardedVideo", "showRewardedVideo"
		});
		activity = p_activity;
	}
}
