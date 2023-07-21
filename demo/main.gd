extends Node2D

onready var admob = $AdMob
onready var debug_out = $CanvasLayer/DebugOut

func _ready():
	
	if not admob.ads_using_consent:
		print('Ads without consent verification')
		loadAds()
	else:
		print('Ads with consent')
# warning-ignore:return_value_discarded
	get_tree().connect("screen_resized", self, "_on_resize")

func loadAds() -> void:
	admob.load_banner()
	admob.load_interstitial()
	admob.load_rewarded_video()
	admob.load_rewarded_interstitial()

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

func _on_BtnRewardedInterstitial_pressed() -> void:
	debug_out.text = debug_out.text + "Rewarded interstitial loaded before shown = " + str(admob.is_rewarded_interstitial_loaded()) +"\n"
	admob.show_rewarded_interstitial()
	debug_out.text = debug_out.text + "Rewarded interstitial loaded after shown = " + str(admob.is_rewarded_interstitial_loaded()) +"\n"

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

func _on_AdMob_interstitial_opened():
	debug_out.text = debug_out.text + "Interstitial opened\n"

func _on_AdMob_interstitial_loaded():
	$"CanvasLayer/BtnInterstitial".disabled = false
	debug_out.text = debug_out.text + "Interstitial loaded\n"

func _on_AdMob_interstitial_clicked():
	debug_out.text = debug_out.text + "Interstitial clicked\n"

func _on_AdMob_interstitial_closed():
	debug_out.text = debug_out.text + "Interstitial closed\n"
	$"CanvasLayer/BtnInterstitial".disabled = false

func _on_AdMob_interstitial_failed_to_load(error_code):
	debug_out.text = debug_out.text + "Interstitial failed to load: Error code " + str(error_code) + "\n"

func _on_AdMob_interstitial_impression():
	debug_out.text = debug_out.text + "Interstitial impression\n"

func _on_AdMob_network_error():
	debug_out.text = debug_out.text + "Network error\n"

func _on_AdMob_rewarded(currency, amount):
	debug_out.text = debug_out.text + "Rewarded watched, currency: " + str(currency) + " amount:"+ str(amount)+ "\n"

func _on_AdMob_rewarded_clicked():
	debug_out.text = debug_out.text + "Rewarded clicked\n"

func _on_AdMob_rewarded_impression():
	debug_out.text = debug_out.text + "Rewarded impression\n"

func _on_AdMob_rewarded_video_closed():
	debug_out.text = debug_out.text + "Rewarded video closed\n"
	$"CanvasLayer/BtnRewardedVideo".disabled = true
	admob.load_rewarded_video()

func _on_AdMob_rewarded_video_failed_to_load(error_code):
	debug_out.text = debug_out.text + "Rewarded video failed to load: Error code " + str(error_code) + "\n"

func _on_AdMob_rewarded_video_loaded():
	$"CanvasLayer/BtnRewardedVideo".disabled = false
	debug_out.text = debug_out.text + "Rewarded video loaded\n"

func _on_AdMob_rewarded_video_opened():
	debug_out.text = debug_out.text + "Rewarded video opened\n"

func _on_AdMob_rewarded_interstitial_loaded() -> void:
	$CanvasLayer/BtnRewardedInterstitial.disabled = false
	debug_out.text = debug_out.text + "Rewarded interstitial loaded\n"

func _on_AdMob_rewarded_interstitial_opened() -> void:
	debug_out.text = debug_out.text + "Rewarded interstitial opened\n"

func _on_AdMob_rewarded_interstitial_closed() -> void:
	debug_out.text = debug_out.text + "Rewarded interstitial closed\n"
	$CanvasLayer/BtnRewardedInterstitial.disabled = true
	admob.load_rewarded_interstitial()

func _on_AdMob_rewarded_interstitial_failed_to_load(error_code) -> void:
	debug_out.text = debug_out.text + "Rewarded interstitial failed to load: Error code " + str(error_code) + "\n"

func _on_AdMob_rewarded_interstitial_failed_to_show(error_code) -> void:
	debug_out.text = debug_out.text + "Rewarded interstitial failed to show: Error code " + str(error_code) + "\n"

func _on_BtnRequestConsentInfoUpdate_pressed():
	debug_out.text = debug_out.text + "Request Consent\n"
	admob.request_consent_info_update()
	


func _on_BtnResetConsent_pressed():
	debug_out.text = debug_out.text + "RESET Consent\n"
	admob.reset_consent()


func _on_AdMob_consent_app_can_request_ad(consent_status):
	debug_out.text = debug_out.text + 'App can start requesting ads.'
	print('App can start requesting ads.')
	loadAds()

func _on_AdMob_consent_info_update_failure(error_code, error_message):
	debug_out.text = debug_out.text + 'Consent failure: ' + "(" + str(error_code) + "," + error_message + ")"


func _on_AdMob_consent_info_update_success():
	debug_out.text = debug_out.text + '_on_AdMob_consent_info_update_success'
