package com.image.transformation;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;


import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class ThemeColorTransformation extends BitmapTransformation {

    private final static String TAG = "color-picker";
    private final static String ID = "com.heytap.common.image.blur.ThemeColorTransformation";

    private String mImageId = null;
    private View mTargetView = null;

    // 计算过主色调的进行缓存，有效期: 整个应用的生命周期或者缓存满了
    private final static int MAX_COLOR_CACHE = 100;
    private final static Map<String, Integer> sThemeColorCache;
    static {
        sThemeColorCache = new HashMap<String, Integer>(MAX_COLOR_CACHE);
    }

    @UiThread
    public ThemeColorTransformation(@NonNull String imageId, @NonNull View targetView, int fallbackColor) {
        mImageId = imageId;
        mTargetView = targetView;
        Log.d(TAG, "ThemeColorTransformation: " + imageId + ", " + targetView.toString());

        // 如果有缓存了，直接取了就可以设置了
        int themeColor = getThemeColorFromCache(mImageId);
        if (0 != themeColor) {
            Log.d(TAG, "ThemeColorTransformation: " + mImageId + ": get themeColor from cache: 0x" + Integer.toHexString(themeColor));
            mTargetView.setBackgroundColor(themeColor);
            mTargetView = null;
        } else {
            mTargetView.setBackgroundColor(fallbackColor);
        }
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (outWidth <= 0 || outHeight <= 0
                || null == mTargetView) {
            return toTransform;
        }
        int themeColor = getThemeColorFromCache(mImageId);
        if (0 == themeColor) {
            themeColor = ThemeColorPicker.getImageThemeColor(toTransform, mImageId);
            cacheThemeColor(mImageId, themeColor);
        } else {
            Log.d(TAG, "ThemeColorTransformation: " + mImageId + ": get themeColor from cache: 0x" + Integer.toHexString(themeColor));
        }
        final int color = themeColor;
        if (0 == themeColor) {
            mTargetView = null;
        } else {
            mTargetView.post(() -> {
                if (null != mTargetView) {
                    mTargetView.setBackgroundColor(color);
                    // 防止 glide 缓存导致内存泄漏
                    mTargetView = null;
                }
            });
        }
        return toTransform;
    }

    public void releaseTargetView() {
        mTargetView = null;
    }

    @Override
    public String toString() {
        return TAG + ": id=" + mImageId;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ThemeColorTransformation)
                && TextUtils.equals(((ThemeColorTransformation)o).mImageId, mImageId);
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + mImageId.hashCode() * 1000;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + mImageId).getBytes(CHARSET));
    }

    private static int getThemeColorFromCache(String id) {
        int themeColor = 0;
        synchronized (sThemeColorCache) {
            if (sThemeColorCache.containsKey(id)) {
                themeColor = sThemeColorCache.get(id);
            }
        }
        return themeColor;
    }

    private static void cacheThemeColor(String id, int themeColor) {
        synchronized (sThemeColorCache) {
            if (sThemeColorCache.size() >= MAX_COLOR_CACHE) {
                sThemeColorCache.clear();
            }
            sThemeColorCache.put(id, themeColor);
        }
    }
}
