package kef.technology.allinoneshopping;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class AppOpenManager {
    protected static final String LOG_TAG = "AppOpenAdManager";
    private static final String AD_UNIT_ID = "ca-app-pub-8327472816877927/6709317650";//"ca-app-pub-3940256099942544/3419835294";

    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    protected boolean isShowingAd = false;
    private long loadtime = 0;

    public void showAdIfAvailable(@NonNull final Activity activity, @NonNull OnShowAdListener onShowAdListener){
        if (isShowingAd) {
            Log.d(LOG_TAG, "The app open ad is already showing.");
            return;
        }

        if (!isAdAvailable()) {
            Log.d(LOG_TAG, "The app open ad is not ready yet.");
            onShowAdListener.onShowAdComplete();
            loadAd(activity);
            return;
        }

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                Log.i(LOG_TAG, "Ad dismissed fullscreen content.");
                appOpenAd = null;
                isShowingAd = false;

                onShowAdListener.onShowAdComplete();
                loadAd(activity);
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.e(LOG_TAG, adError.getMessage());
                appOpenAd = null;
                isShowingAd = false;

                onShowAdListener.onShowAdComplete();
                loadAd(activity);
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Log.i(LOG_TAG, "Ad showed fullscreen content.");
            }
        });
        isShowingAd = true;
        appOpenAd.show(activity);
    }

    public void loadAd(Context context) {
        if (isLoadingAd || isAdAvailable()) {
            return;
        }

        isLoadingAd = true;
        AdRequest request = new AdRequest.Builder().build();
        AppOpenAd.load(
                context, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        Log.i(LOG_TAG, "Ad was loaded.");
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadtime = new Date().getTime();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(LOG_TAG, loadAdError.getMessage());
                        isLoadingAd = false;
                    }
                });
    }

    private boolean isAdAvailable() {
        return appOpenAd != null && !isAdExpired();
    }

    private boolean isAdExpired(){
        long timeInMillis = 3600000;
        return new Date().getTime() - loadtime > timeInMillis*4;
    }

    public interface OnShowAdListener {
        void onShowAdComplete();
    }
}
