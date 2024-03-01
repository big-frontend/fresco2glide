package com.image;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.heytap.browser.core.common.GlobalConstants;
import com.heytap.browser.core.common.log.Log;

import java.io.File;

public class CacheManager {
    public static final String TAG = "CacheManager";
    //Cache size = 200M
    public static final int DISK_CACHE_SIZE = 200 * 1024 * 1024;

    //Memory Cache size = 30M
    public static final int MEMORY_CACHE_SIZE = 30 * 1024 * 1024;

    //Memory Cache size = 20M
    public static final int BITMAP_POOL_CACHE_SIZE = 20 * 1024 * 1024;

    public static void applyOptions(Context context, GlideBuilder builder) {
        Log.i(TAG, "disk :" + DISK_CACHE_SIZE + ", mem:" + MEMORY_CACHE_SIZE + ", pool: " + BITMAP_POOL_CACHE_SIZE);
        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, DISK_CACHE_SIZE));

        builder.setMemoryCache(new LruResourceCache(MEMORY_CACHE_SIZE));
        builder.setBitmapPool(new LruBitmapPool(BITMAP_POOL_CACHE_SIZE));
    }

}
