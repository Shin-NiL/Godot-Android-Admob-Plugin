extends Node

class_name Admob, "res://admob-lib/icon.png"

# signals
signal banner_loaded
signal banner_failed_to_load(error_code)
signal insterstitial_failed_to_load(error_code)
signal interstitial_loaded
signal interstitial_closed
signal rewarded_video_loaded
signal rewarded_video_closed
signal rewarded(currency, ammount)
signal rewarded_video_left_application
signal rewarded_video_failed_to_load(error_code)
signal rewarded_video_opened
signal rewarded_video_started

# properties
export var is_real:bool setget is_real_set
export var banner_on_top:bool = true
export var banner_id:String
export var interstitial_id:String
export var rewarded_id:String
export var child_directed:bool = false
export var is_personalized:bool = true
export(String, "G", "PG", "T", "MA") var max_ad_content_rate

# "private" properties
var _admob_singleton = null
var _is_interstitial_loaded:bool = false
var _is_rewarded_video_loaded:bool = false


func _enter_tree():
	if not init():
		print("Admob Java Singleton not found")

# setters
func is_real_set(new_val) -> void:
	is_real = new_val
# warning-ignore:return_value_discarded
	init()
	
func child_directed_set(new_val) -> void:
	child_directed = new_val
# warning-ignore:return_value_discarded
	init()

func is_personalized_set(new_val) -> void:
	is_personalized = new_val
# warning-ignore:return_value_discarded
	init()

func max_ad_content_rate_set(new_val) -> void:
	if new_val != "G" and new_val != "PG" \
		and new_val != "T" and new_val != "MA":
			
		max_ad_content_rate = "G"
		print("Invalid max_ad_content_rate, using 'G'")


# initialization
func init() -> bool:
	if(Engine.has_singleton("AdMob")):
		_admob_singleton = Engine.get_singleton("AdMob")
		_admob_singleton.initWithContentRating(
			is_real,
			get_instance_id(),
			child_directed,
			is_personalized,
			max_ad_content_rate
		)
		return true
	return false
	
# load

func load_banner() -> void:
	if _admob_singleton != null:
		_admob_singleton.loadBanner(banner_id, banner_on_top)

func load_interstitial() -> void:
	if _admob_singleton != null:
		_admob_singleton.loadInterstitial(interstitial_id)
		
func is_interstitial_loaded() -> bool:
	if _admob_singleton != null:
		return _is_interstitial_loaded
	return false
		
func load_rewarded_video() -> void:
	if _admob_singleton != null:
		_admob_singleton.loadRewardedVideo(rewarded_id)
		
func is_rewarded_video_loaded() -> bool:
	if _admob_singleton != null:
		return _is_rewarded_video_loaded
	return false

# show / hide

func show_banner() -> void:
	if _admob_singleton != null:
		_admob_singleton.showBanner()
		
func hide_banner() -> void:
	if _admob_singleton != null:
		_admob_singleton.hideBanner()

func show_interstitial() -> void:
	if _admob_singleton != null:
		_admob_singleton.showInterstitial()
		_is_interstitial_loaded = false
		
func show_rewarded_video() -> void:
	if _admob_singleton != null:
		_admob_singleton.showRewardedVideo()
		_is_rewarded_video_loaded = false

# resize

func banner_resize() -> void:
	if _admob_singleton != null:
		_admob_singleton.resize()
		
# dimension
func get_banner_dimension() -> Vector2:
	if _admob_singleton != null:
		return Vector2(_admob_singleton.getBannerWidth(), _admob_singleton.getBannerHeight())
	return Vector2()

# callbacks

func _on_admob_ad_loaded() -> void:
	emit_signal("banner_loaded")
	
func _on_admob_banner_failed_to_load(error_code:int) -> void:
	emit_signal("banner_failed_to_load", error_code)
	
func _on_insterstitial_failed_to_load(error_code:int) -> void:
	_is_interstitial_loaded = false
	emit_signal("insterstitial_failed_to_load", error_code)

func _on_interstitial_loaded() -> void:
	_is_interstitial_loaded = true
	emit_signal("interstitial_loaded")

func _on_interstitial_close() -> void:
	emit_signal("interstitial_closed")

func _on_rewarded_video_ad_loaded() -> void:
	_is_rewarded_video_loaded = true
	emit_signal("rewarded_video_loaded")

func _on_rewarded_video_ad_closed() -> void:
	emit_signal("rewarded_video_closed")

func _on_rewarded(currency:String, amount:int) -> void:
	emit_signal("rewarded", currency, amount)
	
func _on_rewarded_video_ad_left_application() -> void:
	emit_signal("rewarded_video_left_application")
	
func _on_rewarded_video_ad_failed_to_load(error_code:int) -> void:
	_is_rewarded_video_loaded = false
	emit_signal("rewarded_video_failed_to_load", error_code)
	
func _on_rewarded_video_ad_opened() -> void:
	emit_signal("rewarded_video_opened")
	
func _on_rewarded_video_started() -> void:
	emit_signal("rewarded_video_started")

