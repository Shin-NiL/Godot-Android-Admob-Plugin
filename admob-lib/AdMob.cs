using Godot;
using System;

///AUTHOR: Alexander Jungert - 28/08/2020
public class AdMob : Node {
	#region Signals
	[Signal] public delegate void BannerLoaded();
	[Signal] public delegate void BannerFailedToLoad(int error_code);
	[Signal] public delegate void InterstitialFailedToLoad(int error_code);
	[Signal] public delegate void InterstitialLoaded();
	[Signal] public delegate void InterstitialClosed();
	[Signal] public delegate void RewardedVideoLoaded();
	[Signal] public delegate void RewardedVideoClosed();
	[Signal] public delegate void Rewarded(string currency, int ammount);
	[Signal] public delegate void RewardedVideoLeftApplication();
	[Signal] public delegate void RewardedVideoFailedToLoad(int error_code);
	[Signal] public delegate void RewardedVideoOpened();
	[Signal] public delegate void RewardedVideoStarted();
	#endregion
	#region Properties
	[Export] bool isReal;
	[Export] bool bannerOnTop = true;
	[Export] String bannerId;
	[Export] String interstitialId;
	[Export] String rewardedId;
	[Export] bool childDirected = false;
	[Export] bool isPersonalized = true;
	//(PropertyHint.Flags, "G,PG,T,MA")
	[Export] String maxAdContentRate = "G";
	#endregion
	#region PrivateProperties
	Godot.Object admobSingleton = null;
	bool isInterstitialLoaded = false;
	bool isRewardedVideoLoaded = false;
	#endregion
	public override void _EnterTree() {
		GD.Print("GodotAdMob SINGLETON FOUND!: " + Engine.HasSingleton("GodotAdMob"));
		if (!Init()) {
			GD.Print("AdMob can not be loaded. We may be not on android.");
		} else GD.Print("AdMob:" + this);
	}

	private void GetLib() {
		admobSingleton = Engine.GetSingleton("GodotAdMob");
	}
	public bool Init() {
		if (Engine.HasSingleton("GodotAdMob")) {
			GetLib();
			GD.Print("AdMob:" + admobSingleton);
			GD.Print("AdMob:" + admobSingleton);
			GD.Print("AdMob:" + admobSingleton);
			GD.Print("GetSignalList" + admobSingleton.GetSignalList());
			GD.Print("GetMethodList" + admobSingleton.GetMethodList());
			admobSingleton.Connect("on_admob_ad_loaded", this, "OnAdmobAdLoaded");
			admobSingleton.Connect("on_admob_banner_failed_to_load", this, nameof(OnAdmobBannerFailedToLoad));
			admobSingleton.Connect("on_interstitial_failed_to_load", this, nameof(OnInterstitialFailedToLoad));
			admobSingleton.Connect("on_interstitial_loaded", this, nameof(OnInterstitialLoaded));
			admobSingleton.Connect("on_interstitial_close", this, nameof(OnInterstitialClose));
			admobSingleton.Connect("on_rewarded_video_ad_loaded", this, nameof(OnRewardedVideoAdLoaded));
			admobSingleton.Connect("on_rewarded_video_ad_closed", this, nameof(OnRewardedVideoAdClosed));
			admobSingleton.Connect("on_rewarded", this, nameof(OnRewarded));
			admobSingleton.Connect("on_rewarded_video_ad_left_application", this, nameof(OnRewardedVideoAdLeftApplication));
			admobSingleton.Connect("on_rewarded_video_ad_failed_to_load", this, nameof(OnRewardedVideoAdFailedToLoad));
			admobSingleton.Connect("on_rewarded_video_ad_opened", this, nameof(OnRewardedVideoAdOpened));
			admobSingleton.Connect("on_rewarded_video_started", this, nameof(OnRewardedVideoStarted));

			admobSingleton.Call("initWithContentRating", isReal, childDirected, isPersonalized, maxAdContentRate);
			return true;
		}
		return false;
	}
	#region LoaderFunctions
	public void LoadBanner() {
		if (admobSingleton != null) {
			admobSingleton.Call("loadBanner", bannerId, bannerOnTop);
		}
	}
	public void LoadInterstitial() {
		if (admobSingleton != null) {
			admobSingleton.Call("loadInterstitial", interstitialId);
		}
	}
	public bool IsInterstitialLoaded() {
		if (admobSingleton != null) {
			return isInterstitialLoaded;
		}
		return false;
	}
	public void LoadRewardedVideo() {
		if (admobSingleton != null) {
			admobSingleton.Call("loadRewardedVideo", rewardedId);
			GD.Print("Try LoadRewardedVideo");
		}
	}
	public bool IsRewardedVideoLoaded() {
		if (admobSingleton != null) {
			return isRewardedVideoLoaded;
		}
		return false;
	}
	#endregion
	#region ShowHide

	public void ShowBanner() {
		if (admobSingleton != null) {
			admobSingleton.Call("showBanner");

		}
	}
	public void HideBanner() {
		if (admobSingleton != null) {
			admobSingleton.Call("hideBanner");
		}
	}
	public void MoveBanner(bool onTop) {
		if (admobSingleton != null) {
			bannerOnTop = onTop;
			admobSingleton.Call("move", bannerOnTop);
		}
	}
	public void ShowInterstitial() {
		if (admobSingleton != null) {
			admobSingleton.Call("showInterstitial");
			isInterstitialLoaded = false;
		}
	}
	public void ShowRewardedVideo() {
		if (admobSingleton != null) {
			admobSingleton.Call("showRewardedVideo");
			isRewardedVideoLoaded = false;
		}
	}
	#endregion
	public void BannerResize() {
		if (admobSingleton != null) {
			admobSingleton.Call("resize");
		}
	}
	//TODO: We need to learn how to get variables from the java lib
	//public void GetBannerDimension() {
	//    if(admobSingleton != null) {
	//        return new Vector2(admobSingleton.Call("getBannerWidth"), admobSingleton.Call("getBannerHeight"));
	//    }
	//}
	#region Callbacks
	public void OnAdmobAdLoaded() {
		EmitSignal(nameof(BannerLoaded));
	}
	public void OnAdmobBannerFailedToLoad(int error_code) {
		EmitSignal(nameof(BannerFailedToLoad), error_code);
	}
	public void OnInterstitialFailedToLoad(int error_code) {
		isInterstitialLoaded = false;
		EmitSignal(nameof(InterstitialFailedToLoad), error_code);
	}
	public void OnInterstitialLoaded() {
		isInterstitialLoaded = true;
		EmitSignal(nameof(InterstitialLoaded));
	}
	public void OnInterstitialClose() {
		EmitSignal(nameof(InterstitialClosed));
	}
	public void OnRewardedVideoAdLoaded() {
		isRewardedVideoLoaded = true;
		EmitSignal(nameof(RewardedVideoLoaded));
	}
	public void OnRewardedVideoAdClosed() {
		EmitSignal(nameof(RewardedVideoClosed));
	}
	public void OnRewarded(String currency, int amount) {
		EmitSignal(nameof(Rewarded), currency, amount);
	}
	public void OnRewardedVideoAdLeftApplication() {
		EmitSignal(nameof(RewardedVideoLeftApplication));
	}
	public void OnRewardedVideoAdFailedToLoad(int error_code) {
		isRewardedVideoLoaded = false;
		EmitSignal(nameof(RewardedVideoFailedToLoad), error_code);
	}
	public void OnRewardedVideoAdOpened() {
		EmitSignal(nameof(RewardedVideoOpened));
	}
	public void OnRewardedVideoStarted() {
		EmitSignal(nameof(RewardedVideoStarted));
	}
	#endregion
}