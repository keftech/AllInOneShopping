package kef.technology.allinoneshopping;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

public class MyWebClient extends WebViewClient {

    private final MainActivity activity;

    public MyWebClient(MainActivity activity){
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if(URLUtil.isValidUrl(request.getUrl().toString()) && isNotAppStoreUrl(request.getUrl())) {
            view.loadUrl(request.getUrl().toString());
        }
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(URLUtil.isValidUrl(url) && isNotAppStoreUrl(Uri.parse(url))){
            view.loadUrl(url);
        }
        return true;
    }

    private boolean isNotAppStoreUrl(Uri uri){
        return !uri.getHost().equals("play.google.com");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if(activity != null && view.getUrl().equals(request.getUrl().toString())){
           activity.webViewModel.getShowError().setValue(true);
           activity.webViewModel.getErrorMesaage().setValue(error.getDescription().toString());
        }
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if(activity != null && view.getUrl().equals(failingUrl)){
            activity.webViewModel.getShowError().setValue(true);
            activity.webViewModel.getErrorMesaage().setValue(description);
        }
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if(activity != null)
            activity.webViewModel.getShowError().setValue(false);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }
}
