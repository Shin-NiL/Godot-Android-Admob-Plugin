using Godot;
using System;

///AUTHOR: Alexander Jungert - 28/08/2020
public class AdMob : Node 
    {
    #region ClassEnums
    public enum AdResponse { Closed, Opened, Loaded, Started, Finished, Error, Rewarded }
    #endregion
    
    #region Signals
    [Signal] public delegate void AdCallback(AdResponse response);
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
    [Export] bool IsReal = false;
    [Export] bool BannerOnTop = true;
    [Export] String BannerId = "ca-app-pub-3940256099942544/6300978111";
    [Export] String InterstitialId = "ca-app-pub-3940256099942544/1033173712";
    [Export] String RewardedId = "ca-app-pub-3940256099942544/5224354917";
    [Export] bool ChildDirected = false;
    [Export] bool IsPersonalized = true;
    //(PropertyHint.Flags, "G,PG,T,MA")
    [Export] String MaxAdContentRate = "G";
    #endregion

    #region PrivateProperties
    private Godot.Object admobSingleton = null;
    private bool isInterstitialLoaded = false;
    private bool isRewardedVideoLoaded = false;
    #endregion

    public override void _EnterTree() 
	{
        if (!Init()) 
	    {
            GD.Print("[AdMob.cs]: can not be loaded. We may be not on android.");
        }
    }

    public bool Init() 
	{
        if (Engine.HasSingleton("GodotAdMob")) 
	    {
            GetLib();
            admobSingleton.Connect("on_admob_ad_loaded", this, nameof(OnAdmobAdLoaded));
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
            admobSingleton.Call("initWithContentRating", IsReal, ChildDirected, IsPersonalized, MaxAdContentRate);
            return true;
        }
        return false;
    }

    private void GetLib() 
	{
        admobSingleton = Engine.GetSingleton("GodotAdMob");
    }

    #region LoaderFunctions
    public void LoadBanner() 
	{
        admobSingleton?.Call("loadBanner", BannerId, BannerOnTop);
    }

    public void LoadBanner(string IdFromCode, bool? BannerOnTop) 
	{
        admobSingleton?.Call("loadBanner", IdFromCode, BannerOnTop);
    }

    public void LoadInterstitial() 
	{
        admobSingleton?.Call("loadInterstitial", InterstitialId);
    }

    public void LoadInterstitial(string IdFromCode) 
	{
        admobSingleton?.Call("loadInterstitial", IdFromCode);
    }

    public bool IsInterstitialLoaded() 
	{
        return isInterstitialLoaded;
    }

    public void LoadRewardedVideo() 
	{
        GD.Print("[AdMob]: RewardedId:", RewardedId);
        admobSingleton?.Call("loadRewardedVideo", RewardedId);
    }

    public void LoadRewardedVideo(string IdFromCode) 
	{
        GD.Print("[AdMob]: custom RewardedId:", RewardedId);
        admobSingleton?.Call("loadRewardedVideo", IdFromCode);
    }

    public bool IsRewardedVideoLoaded() 
	{
        return isRewardedVideoLoaded;
    }

    #endregion

    #region ShowHide    
    public void ShowBanner() 
	{
        admobSingleton?.Call("showBanner");
    }

    public void HideBanner() 
	{
        admobSingleton?.Call("hideBanner");
    }

    public void MoveBanner(bool onTop) 
	{
        BannerOnTop = onTop;
        admobSingleton?.Call("move", BannerOnTop);
    }

    public void ShowInterstitial() 
	{
        admobSingleton?.Call("showInterstitial");
        isInterstitialLoaded = false;
    }

    public void ShowRewardedVideo() 
	{
        admobSingleton?.Call("showRewardedVideo");
        isRewardedVideoLoaded = false;
    }

    #endregion

    public void BannerResize() 
	{
        admobSingleton.Call("resize");
    }

    //TODO: We need to learn how to get variables from the java lib
    //public void GetBannerDimension() 
	//{
    //    if(admobSingleton != null) 
        //{
    //        return new Vector2(admobSingleton.Call("getBannerWidth"), admobSingleton.Call("getBannerHeight"));
    //    }
    //}

    #region Callbacks
    public void OnAdmobAdLoaded() 
	{
        EmitSignal(nameof(BannerLoaded));
        EmitSignal(nameof(AdCallback), AdResponse.Loaded);
    }

    public int OnAdmobBannerFailedToLoad(int error_code = 0) 
	{
        EmitSignal(nameof(BannerFailedToLoad), error_code);
        EmitSignal(nameof(AdCallback), AdResponse.Error);
        return error_code;
    }

    public int OnInterstitialFailedToLoad(int error_code) 
	{
        isInterstitialLoaded = false;
        EmitSignal(nameof(InterstitialFailedToLoad), error_code);
        EmitSignal(nameof(AdCallback), AdResponse.Error);
        return error_code;
    }

    public void OnInterstitialLoaded() 
	{
        isInterstitialLoaded = true;
        EmitSignal(nameof(InterstitialLoaded));
        EmitSignal(nameof(AdCallback), AdResponse.Loaded);
    }

    public void OnInterstitialClose() 
	{
        EmitSignal(nameof(InterstitialClosed));
        EmitSignal(nameof(AdCallback), AdResponse.Closed);
    }

    public void OnRewardedVideoAdLoaded() 
	{
        isRewardedVideoLoaded = true;
        EmitSignal(nameof(RewardedVideoLoaded));
        EmitSignal(nameof(AdCallback), AdResponse.Loaded);
    }

    public void OnRewardedVideoAdClosed() 
	{
        EmitSignal(nameof(RewardedVideoClosed));
        EmitSignal(nameof(AdCallback), AdResponse.Closed);
    }

    public void OnRewarded(String currency, int amount) 
	{
        EmitSignal(nameof(Rewarded), currency, amount);
        EmitSignal(nameof(AdCallback), AdResponse.Rewarded);
    }

    public void OnRewardedVideoAdLeftApplication() 
	{
        EmitSignal(nameof(RewardedVideoLeftApplication));
        EmitSignal(nameof(AdCallback), AdResponse.Finished);
    }

    public int OnRewardedVideoAdFailedToLoad(int error_code) 
	{
        isRewardedVideoLoaded = false;
        EmitSignal(nameof(RewardedVideoFailedToLoad), error_code);
        EmitSignal(nameof(AdCallback), AdResponse.Error);
        return error_code;
    }

    public void OnRewardedVideoAdOpened() 
	{
        EmitSignal(nameof(RewardedVideoOpened));
        EmitSignal(nameof(AdCallback), AdResponse.Opened);
    }

    public void OnRewardedVideoStarted() 
	{
        EmitSignal(nameof(RewardedVideoStarted));
        EmitSignal(nameof(AdCallback), AdResponse.Started);
    }

    #endregion
}
