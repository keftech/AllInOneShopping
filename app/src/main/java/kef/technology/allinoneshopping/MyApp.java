package kef.technology.allinoneshopping;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Collections;

public class MyApp extends Application{

    protected AppOpenManager appOpenManager;
    private Activity currentActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        /*registerActivityLifecycleCallbacks(lifeCycleCallbacks);
        MobileAds.initialize(this, initializationStatus -> {});
        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList(AdRequest.DEVICE_ID_EMULATOR)).build());
        MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("47F867730A9A5E3E5890396F9D6742BA")).build());
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifeCycleObserver);
        appOpenManager = new AppOpenManager();*/
    }

   private final LifecycleEventObserver lifeCycleObserver = (source, event) -> {
     if(event == Lifecycle.Event.ON_START)
         appOpenManager.showAdIfAvailable(currentActivity, () -> {});
   };

    private final ActivityLifecycleCallbacks lifeCycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            if(!appOpenManager.isShowingAd)
                currentActivity = activity;
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {}

        @Override
        public void onActivityPaused(@NonNull Activity activity) {}

        @Override
        public void onActivityStopped(@NonNull Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {}
    };
}
