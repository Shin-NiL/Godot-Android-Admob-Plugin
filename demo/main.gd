extends Node2D

onready var admob = $AdMob
onready var debug_out = $CanvasLayer/DebugOut

func _ready():
	loadAds()
# warning-ignore:return_value_discarded
	get_tree().connect("screen_resized", self, "_on_resize")

func loadAds() -> void:
	admob.load_banner()
	admob.load_interstitial()
	admob.load_rewarded_video()

# buttons callbacks
func _on_BtnReload_pressed() -> void:
	loadAds()
	
func _on_BtnBanner_toggled(button_pressed):
		if button_pressed: admob.show_banner()
		else: admob.hide_banner()

func _on_BtnBannerMove_toggled(button_pressed: bool) -> void:
	admob.move_banner(button_pressed)
	$"CanvasLayer/BtnBannerResize".disabled = true
	$"CanvasLayer/BtnBanner".disabled = true
	$"CanvasLayer/BtnBannerMove".disabled = true

func _on_BtnBannerResize_pressed() -> void:
	admob.banner_resize()

func _on_BtnInterstitial_pressed():
	debug_out.text = debug_out.text + "Interstitial loaded before shown = " + str(admob.is_interstitial_loaded()) +"\n"
	admob.show_interstitial()
	debug_out.text = debug_out.text + "Interstitial loaded after shown = " + str(admob.is_interstitial_loaded()) +"\n"

func _on_BtnRewardedVideo_pressed():
	debug_out.text = debug_out.text + "Rewarded loaded before shown = " + str(admob.is_rewarded_video_loaded()) +"\n"
	admob.show_rewarded_video()
	debug_out.text = debug_out.text + "Rewarded loaded after shown = " + str(admob.is_rewarded_video_loaded()) +"\n"

# AdMob callbacks
func _on_resize():
	debug_out.text = debug_out.text + "Banner resized\n"
	admob.banner_resize()

func _on_AdMob_banner_failed_to_load(error_code):
	debug_out.text = debug_out.text + "Banner failed to load: Error code " + str(error_code) + "\n"

func _on_AdMob_banner_loaded():
	$"CanvasLayer/BtnBannerResize".disabled = false
	$"CanvasLayer/BtnBanner".disabled = false
	$"CanvasLayer/BtnBannerMove".disabled = false
	debug_out.text = debug_out.text + "Banner loaded\n"
	debug_out.text = debug_out.text + "Banner size = " + str(admob.get_banner_dimension()) +  "\n"

func _on_AdMob_interstitial_loaded():
	$"CanvasLayer/BtnInterstitial".disabled = false
	debug_out.text = debug_out.text + "Interstitial loaded\n"

func _on_AdMob_interstitial_closed():
	debug_out.text = debug_out.text + "Interstitial closed\n"
	$"CanvasLayer/BtnInterstitial".disabled = true

func _on_AdMob_interstitial_failed_to_load(error_code):
	debug_out.text = debug_out.text + "Interstitial failed to load: Error code " + str(error_code) + "\n"

func _on_AdMob_network_error():
	debug_out.text = debug_out.text + "Network error\n"

func _on_AdMob_rewarded(currency, amount):
	debug_out.text = debug_out.text + "Rewarded watched, currency: " + str(currency) + " amount:"+ str(amount)+ "\n"

func _on_AdMob_rewarded_video_closed():
	debug_out.text = debug_out.text + "Rewarded video closed\n"
	$"CanvasLayer/BtnRewardedVideo".disabled = true
	admob.load_rewarded_video()

func _on_AdMob_rewarded_video_failed_to_load(error_code):
	debug_out.text = debug_out.text + "Rewarded video failed to load: Error code " + str(error_code) + "\n"

func _on_AdMob_rewarded_video_left_application():
	debug_out.text = debug_out.text + "Rewarded video left application\n"

func _on_AdMob_rewarded_video_loaded():
	$"CanvasLayer/BtnRewardedVideo".disabled = false
	debug_out.text = debug_out.text + "Rewarded video loaded\n"

func _on_AdMob_rewarded_video_opened():
	debug_out.text = debug_out.text + "Rewarded video opened\n"

func _on_AdMob_rewarded_video_started():
	debug_out.text = debug_out.text + "Rewarded video started\n"
