package kef.technology.allinoneshopping;

import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

public class LazyImageLoader implements ComponentCallbacks2 {

    private final Context context;
    private final TCLruCache cache;
    private static final String IMAGE_KEY = "MyImage";
    public static final int MAX_WIDTH = 250;

    public LazyImageLoader(Context context) {
        this.context = context;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int maxKb = am.getMemoryClass() * 1024;
        int limitKb = maxKb / 8; // 1/8th of total ram
        cache = new TCLruCache(limitKb);
    }

    public Bitmap loadImage(int imageResourceID) {
        Bitmap image = cache.get(IMAGE_KEY+imageResourceID);
        if (image != null)
            return image;
        else
            return loadImageFromResources(imageResourceID);
    }

    private Bitmap loadImageFromResources(int imageResourceID) {
        BitmapFactory.Options bitOptions = new BitmapFactory.Options();
        bitOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), imageResourceID, bitOptions);

        int scale = 1;
        while(bitOptions.outWidth / scale / 2 >= MAX_WIDTH) {
            scale *= 2;
        }

        BitmapFactory.Options bitOption2 = new BitmapFactory.Options();
        bitOption2.inSampleSize = scale;
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), imageResourceID, bitOption2);
        if(imageBitmap != null)
            cache.put(IMAGE_KEY+imageResourceID, imageBitmap);
        return imageBitmap;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {}

    @Override
    public void onLowMemory() {}

    @Override
    public void onTrimMemory(int level) {
        if (level >= TRIM_MEMORY_MODERATE) {
            cache.evictAll();
        }
        else if (level >= TRIM_MEMORY_BACKGROUND) {
            cache.trimToSize(cache.size() / 2);
        }
    }

    private static class TCLruCache extends LruCache<String, Bitmap> {

        public TCLruCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount() / 1024;
        }
    }

}