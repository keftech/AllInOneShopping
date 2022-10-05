package kef.technology.allinoneshopping;

import static kef.technology.allinoneshopping.AppOpenManager.LOG_TAG;
import static kef.technology.allinoneshopping.MainActivity.OPENED_NUM_KEY;
import static kef.technology.allinoneshopping.MainActivity.PREFS_KEY;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kef.technology.allinoneshopping.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private final long seconds = 6;
    private SharedPreferences prefs;
    private Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
        uri = getIntent().getData();
        openPage();
        /*if(shouldSkipAdLoading())
            openPage();
        else
            startLoading();*/
    }

    private void startLoading(){
        TextView loadingTxt = binding.loadingTxt;
        new CountDownTimer(seconds*1000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                String loadTxt = "App loads in "+ (millisUntilFinished/1000) +" seconds";
                loadingTxt.setText(loadTxt);
            }

            @Override
            public void onFinish() {
                Application myApp = getApplication();
                if(!(myApp instanceof MyApp)){
                    Log.e(LOG_TAG, "Failed casting application to MyApp");
                    openPage();
                    return;
                }
                ((MyApp)myApp).appOpenManager.showAdIfAvailable(SplashActivity.this, () -> openPage());
            }
        }.start();
    }

    private void openPage(){
        Intent pageIntent = new Intent(this, NavigateActivity.class);
        if(uri != null)
            pageIntent.setData(uri);
        startActivity(pageIntent);
        finish();
    }

    private boolean shouldSkipAdLoading(){
        return prefs.getInt(OPENED_NUM_KEY, 0) < 3 || uri != null;
    }
}
