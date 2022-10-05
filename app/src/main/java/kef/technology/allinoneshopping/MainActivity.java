package kef.technology.allinoneshopping;

import static kef.technology.allinoneshopping.NavigateActivity.ITEM_KEY;
import static kef.technology.allinoneshopping.NavigateActivity.getAdsize;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

import kef.technology.allinoneshopping.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyWebview webView;
    protected SharedPreferences prefs;
    private LinearProgressIndicator progressIndicator;
    private FloatingActionButton fab_action, fab_home, fab_back, fab_forward, fab_refresh;
    private float dY_action;
    private boolean isFABOpen = false;
    private View fab_background;
    protected WebViewModel webViewModel;
    private LinearLayout errorPage;
    private TextView errorMessageBox;
    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;
    protected static final String TAG = "Log message: ";
    protected static final String OPENED_NUM_KEY = "Opened_Num_Key";
    protected static final String PREFS_KEY = "PREFS_KEY";

    private AdView adView;
    private FrameLayout adContainer;
    private ListItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);

        webViewModel = new ViewModelProvider(this).get(WebViewModel.class);
        progressIndicator = binding.mainRootLay.progress;
        fab_action = binding.mainRootLay.fabAction;
        fab_home = binding.mainRootLay.fabHome;
        fab_back = binding.mainRootLay.fabBack;
        fab_forward = binding.mainRootLay.fabForward;
        fab_refresh = binding.mainRootLay.fabRefresh;
        errorPage = binding.mainRootLay.errorPageLay;
        errorMessageBox = binding.mainRootLay.errorMessage;
        fab_background = binding.mainRootLay.fabBackground;
        fab_refresh.setOnClickListener(onFabClickListener);
        fab_forward.setOnClickListener(onFabClickListener);
        fab_back.setOnClickListener(onFabClickListener);
        fab_home.setOnClickListener(onFabClickListener);
        fab_action.setOnClickListener(onFabClickListener);
        binding.mainRootLay.retryButton.setOnClickListener(onFabClickListener);
        fab_action.setOnLongClickListener(onFabLongClickListener);
        binding.mainRootLay.getRoot().setOnDragListener(onDragListener);
        webView = binding.mainRootLay.homeWebview;
        adContainer = binding.mainRootLay.adContainer;
        //adContainer.post(() -> loadBannerAd(binding.getRoot().getWidth()));
        init(); onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getData() != null){
            loadUrl(intent.getData().toString());
        }
        else if(intent.hasExtra(ITEM_KEY)) {
            item = (ListItem) intent.getSerializableExtra(ITEM_KEY);
            loadUrl(item.getUrl());
            int num = prefs.getInt(item.getTitle(), 0)+1;
            item.setNum_freq(num);
            prefs.edit().putInt(item.getTitle(), num).apply();
        }
    }

    private void loadUrl(String url){
        url = getHttpsLink(url);
        if(URLUtil.isValidUrl(url))
            webView.loadUrl(url);
        else{
            if(webView.canGoBack())
                webView.goBack();
            else
                finish();
        }
    }

    protected static String getHttpsLink(String url){
        if(url.startsWith("http:"))
            return url.replaceFirst("http:", "https:");
        return url;
    }

    private void loadBannerAd(int width){
        adView = new AdView(this);
        adView.setAdSize(getAdsize(width, this));
        adView.setAdUnitId("ca-app-pub-8327472816877927/2347144626");//"ca-app-pub-3940256099942544/6300978111");
        adContainer.removeAllViews();
        adContainer.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void init(){
        webView.setWebViewClient(new MyWebClient(this));
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressIndicator.setProgress(newProgress);
            }
        });
        webView.setOnTouchListener((view, event) -> {
                if(isFABOpen)
                    closeFABMenu();
                return false;
        });

        fab_action.post(() -> {
            float dX_action = fab_action.getX()+(((float)fab_action.getWidth()/2)-((float)fab_back.getWidth()/2));
            dY_action = fab_action.getY()+(((float)fab_action.getHeight()/2)-((float)fab_back.getHeight()/2));
            fab_back.setX(dX_action); fab_back.setY(dY_action);
            fab_forward.setX(dX_action); fab_forward.setY(dY_action);
            fab_refresh.setX(dX_action); fab_refresh.setY(dY_action);
            fab_home.setX(dX_action); fab_home.setY(dY_action);
        });
        fab_background.setAlpha(0.0f);

        final Observer<Boolean> showErrorObserver = showError -> {
            if(showError){
                errorPage.setVisibility(View.VISIBLE);
                errorPage.setAlpha(0.85f);
                errorPage.animate().alpha(1.0f);
            }
            else
                errorPage.setVisibility(View.GONE);
        };
        final Observer<String> errorMessageObserver = errorMessage -> {
            String errorMsg = getString(R.string.no_connect_message) +" or "+errorMessage;
            errorMessageBox.setText(errorMsg);
        };
        webViewModel.getErrorMesaage().observe(this, errorMessageObserver);
        webViewModel.getShowError().observe(this, showErrorObserver);
        int openedNum = prefs.getInt(OPENED_NUM_KEY, 0)+1;
        startUpdate(openedNum);
        showReview(openedNum);
        prefs.edit().putInt(OPENED_NUM_KEY, openedNum).apply();
    }

    private final View.OnClickListener onFabClickListener = view -> {
        int itemID = view.getId();
        if(itemID == R.id.fab_back){
            if(webView.canGoBack())
                webView.goBack();
        }
        else if(itemID == R.id.fab_forward){
            if(webView.canGoForward())
                webView.goForward();
        }
        else if(itemID == R.id.fab_refresh || itemID == R.id.retry_button){
            webView.reload();
        }
        else if(itemID == R.id.fab_home)
            finish();
        else if(itemID == R.id.fab_action) {
            if(isFABOpen)
                closeFABMenu();
            else
                showFABMenu();
        }
    };

    private void showFABMenu(){
        isFABOpen=true;
        fab_background.animate().alpha(0.4f);
        fab_back.animate().y(dY_action - getResources().getDimension(R.dimen.standard_205));
        fab_forward.animate().y(dY_action - getResources().getDimension(R.dimen.standard_155));
        fab_refresh.animate().y(dY_action - getResources().getDimension(R.dimen.standard_105));
        fab_home.animate().y(dY_action - getResources().getDimension(R.dimen.standard_55));
        fab_action.setImageResource(R.drawable.ic_baseline_close_24);
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab_back.animate().y(dY_action);
        fab_forward.animate().y(dY_action);
        fab_refresh.animate().y(dY_action);
        fab_home.animate().y(dY_action);
        fab_background.animate().alpha(0.0f);
        fab_action.bringToFront();
        fab_action.setImageResource(R.drawable.ic_baseline_control_action_24);
    }

    private final View.OnLongClickListener onFabLongClickListener = view -> {
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(fab_action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            view.startDragAndDrop(null, myShadow, null, View.DRAG_FLAG_GLOBAL);
        else
            view.startDrag(null, myShadow, null, 0);
        return true;
    };

    private final View.OnDragListener onDragListener = (view, event) -> {
        if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
            if(isFABOpen)
                closeFABMenu();
            float dX = event.getX(), dY = event.getY();
            dY_action = dY-(float) fab_back.getHeight()/2;
            fab_action.setX(dX-(float) fab_action.getWidth()/2); fab_action.setY(dY-(float) fab_action.getHeight()/2);
            fab_back.setX(dX-(float) fab_back.getWidth()/2); fab_back.setY(dY_action);
            fab_forward.setX(dX-(float) fab_forward.getWidth()/2); fab_forward.setY(dY_action);
            fab_refresh.setX(dX-(float) fab_refresh.getWidth()/2); fab_refresh.setY(dY_action);
            fab_home.setX(dX-(float) fab_home.getWidth()/2); fab_home.setY(dY_action);
        }
        return true;
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "onActivityResult: app download failed");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack(); return;
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        if(item != null)
            setResult(RESULT_OK, new Intent().putExtra(ITEM_KEY, item));
        super.finish();
    }

    @Override
    protected void onDestroy() {
        webView.clearHistory();
        webView.clearCache(true);
        webView.destroy();
        if(adView != null)
            adView.destroy();
        super.onDestroy();
    }

    private void startUpdate(int openedNum){
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.registerListener(installStateUpdatedListener);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED){
                popupSnackbarForCompleteUpdate();
            } else if (openedNum%5 == 0 && appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE )){
                try {
                    mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, MainActivity.this, RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, e.toString());
                }
            } else {
                Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });
    }

    private void showReview(int openedNum){
        if(!canShowReview(openedNum))
            return;
        ReviewManager reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> manager = reviewManager.requestReviewFlow();
        manager.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                if (reviewInfo != null) {
                    Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                    flow.addOnCompleteListener(task1 -> Log.d(TAG, "In App Rating complete"));
                }
                else {
                    Log.e(TAG, "In App Rating failed");
                }
            } else {
                Log.e(TAG, "In App ReviewFlow failed to start");
            }
        });
    }
    private boolean canShowReview(int openedNum){
        return openedNum < 95 && (openedNum == 16 || openedNum%31 == 0);
    }

    private final InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                if (mAppUpdateManager != null) {
                    mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                }
            } else {
                Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
            }
        }

    };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), getString(R.string.download_success_msg), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.install), view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        }).show();
    }

    @Override
    protected void onPause() {
        if(adView != null)
            adView.pause();
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        if(adView != null)
            adView.resume();
    }

    @Override
    protected void onStop() {
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
        super.onStop();
    }
}