extends Node2D

onready var admob = $Admob
onready var debug_out = $CanvasLayer/DebugOut

func _ready():
	admob.load_banner()
	admob.load_interstitial()
	admob.load_rewarded_video()
# warning-ignore:return_value_discarded
	get_tree().connect("screen_resized", self, "_on_resize")

# buttons callbacks
func _on_BtnBanner_toggled(button_pressed):
		if button_pressed: admob.show_banner()
		else: admob.hide_banner()

func _on_BtnInterstitial_pressed():
	debug_out.text = debug_out.text + "Interstitial loaded before shown = " + str(admob.is_interstitial_loaded()) +"\n"
	admob.show_interstitial()
	debug_out.text = debug_out.text + "Interstitial loaded after shown = " + str(admob.is_interstitial_loaded()) +"\n"

func _on_BtnRewardedVideo_pressed():
	debug_out.text = debug_out.text + "Rewarded loaded before shown = " + str(admob.is_rewarded_video_loaded()) +"\n"
	admob.show_rewarded_video()
	debug_out.text = debug_out.text + "Rewarded loaded after shown = " + str(admob.is_rewarded_video_loaded()) +"\n"

# Admob callbacks
func _on_resize():
	debug_out.text = debug_out.text + "Banner resized\n"
	admob.banner_resize()

func _on_Admob_banner_failed_to_load(error_code):
	debug_out.text = debug_out.text + "Banner failed to load: Error code " + str(error_code) + "\n"

func _on_Admob_banner_loaded():
	$"CanvasLayer/BtnBanner".disabled = false
	debug_out.text = debug_out.text + "Banner loaded\n"

func _on_Admob_interstitial_loaded():
	$"CanvasLayer/BtnInterstitial".disabled = false
	debug_out.text = debug_out.text + "Interstitial loaded\n"

func _on_Admob_interstitial_closed():
	debug_out.text = debug_out.text + "Interstitial closed\n"
	$"CanvasLayer/BtnInterstitial".disabled = true

func _on_Admob_interstitial_failed_to_load(error_code):
	debug_out.text = debug_out.text + "Interstitial failed to load: Error code " + str(error_code) + "\n"

func _on_Admob_network_error():
	debug_out.text = debug_out.text + "Network error\n"

func _on_Admob_rewarded(currency, amount):
	debug_out.text = debug_out.text + "Rewarded watched, currency: " + str(currency) + " amount:"+ str(amount)+ "\n"

func _on_Admob_rewarded_video_closed():
	debug_out.text = debug_out.text + "Rewarded video closed\n"
	$"CanvasLayer/BtnRewardedVideo".disabled = true
	admob.load_rewarded_video()

func _on_Admob_rewarded_video_failed_to_load(error_code):
	debug_out.text = debug_out.text + "Rewarded video failed to load: Error code " + str(error_code) + "\n"

func _on_Admob_rewarded_video_left_application():
	debug_out.text = debug_out.text + "Rewarded video left application\n"

func _on_Admob_rewarded_video_loaded():
	$"CanvasLayer/BtnRewardedVideo".disabled = false
	debug_out.text = debug_out.text + "Rewarded video loaded\n"

func _on_Admob_rewarded_video_opened():
	debug_out.text = debug_out.text + "Rewarded video opened\n"

func _on_Admob_rewarded_video_started():
	debug_out.text = debug_out.text + "Rewarded video started\n"
