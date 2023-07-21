package shinnil.godot.plugin.android.godotadmob;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

import org.godotengine.godot.GodotLib;

interface CMPListener {
    void onConsentInfoUpdateSuccess();
    void onConsentInfoUpdateFailure(int errorCode, String errorMessage);

    void onAppCanRequestAds(int consentStatus);
}

public class CMP {
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;

    private final Activity activity;
    private final CMPListener defaultCMPListener;

    public CMP(Activity activity,
               final boolean testingConsent,
               final String testingDeviceId,
               CMPListener defaultCMPListener) {
        this.activity = activity;
        this.defaultCMPListener = defaultCMPListener;

        consentInformation = UserMessagingPlatform.getConsentInformation(this.activity);

        // Set tag for under age of consent. false means users are not under
        // age.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        Log.w("godot", "Consent status: " +
                String.valueOf(consentInformation.getConsentStatus()));

        if(testingConsent) {

            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this.activity)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(testingDeviceId)
                    .build();

            params = new ConsentRequestParameters
                    .Builder()
                    .setConsentDebugSettings(debugSettings)
                    .build();

            Log.w("godot", "Consent status: " +
                    String.valueOf(consentInformation.getConsentStatus()));
        }

        consentInformation.requestConsentInfoUpdate(
                this.activity,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        // The consent information state was updated.
                        // You are now ready to check if a form is available.
                        Log.w("godot", "AdMob: onConsentInfoUpdateSuccess");

                        defaultCMPListener.onConsentInfoUpdateSuccess();

                        if (consentInformation.isConsentFormAvailable()) {
                            Log.w("godot", "AdMob: Consent information available.");
                            loadForm();
                        }else{
                            Log.w("godot", "AdMob: No consent information.");
                            Log.w("godot", "Consent status: " +
                                    String.valueOf(consentInformation.getConsentStatus()));

                            if(ConsentInformation.ConsentStatus.NOT_REQUIRED ==
                                    consentInformation.getConsentStatus()){
                                defaultCMPListener.onAppCanRequestAds(consentInformation.getConsentStatus());
                            }
                        }
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
                        // Handle the error.
                        Log.w("godot", "AdMob: onConsentInfoUpdateFailure: "
                                + formError.getErrorCode()
                                + " - "
                                + formError.getMessage()
                        );
                        defaultCMPListener.onConsentInfoUpdateFailure(formError.getErrorCode(),
                                formError.getMessage());
                    }
                });
    }

    public void resetConsentInformation(){
        if(consentInformation != null) {
            consentInformation.reset();
        }
    }

    public void loadForm() {
        // Loads a consent form. Must be called on the main thread.
        UserMessagingPlatform.loadConsentForm(
                this.activity,
                new UserMessagingPlatform.OnConsentFormLoadSuccessListener() {
                    @Override
                    public void onConsentFormLoadSuccess(ConsentForm consentForm) {
                        CMP.this.consentForm = consentForm;
                        Log.w("godot", "AdMob: onConsentFormLoadSuccess");
                        if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                            consentForm.show(
                                    CMP.this.activity,
                                    new ConsentForm.OnConsentFormDismissedListener() {
                                        @Override
                                        public void onConsentFormDismissed(@Nullable FormError formError) {
                                            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                                                // App can start requesting ads.
                                                Log.w("godot", "AdMob: App can start requesting ads.");
                                                defaultCMPListener.onAppCanRequestAds(consentInformation.getConsentStatus());
                                            }

                                            // Handle dismissal by reloading form.
                                            loadForm();
                                        }
                                    });
                        }else if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED){
                            Log.w("godot", "AdMob: App can start requesting ads.");
                            defaultCMPListener.onAppCanRequestAds(consentInformation.getConsentStatus());
                        }
                    }
                },
                new UserMessagingPlatform.OnConsentFormLoadFailureListener() {
                    @Override
                    public void onConsentFormLoadFailure(FormError formError) {
                        // Handle the error.
                        Log.w("godot", "AdMob: onConsentInfoUpdateFailure: "
                                + formError.getErrorCode()
                                + " - "
                                + formError.getMessage()
                        );
                    }
                }
        );
    }

}