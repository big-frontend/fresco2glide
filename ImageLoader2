package com.image;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ImageLoader2 {
    public static final String TAG = "ImageLoader";
    private volatile static ImageLoader2 instance;
    private static boolean sIsGSLBDebugEnabled = false;

    public static ImageLoader2 getInstance() {
        if (instance == null) {
            synchronized (ImageLoader2.class) {
                if (instance == null) {
                    instance = new ImageLoader2();
                }
            }
        }
        return instance;
    }

    public void addAnalyticsListener(IImageAnalyticsListener listener){
        ImageAnalyticsMonitor.getInstance().addImageListener(listener);
    }

    public void removeAnalyticsListener(IImageAnalyticsListener listener){
        ImageAnalyticsMonitor.getInstance().removeImageListener(listener);
    }

    public void clearAnalyticsListener(){
        ImageAnalyticsMonitor.getInstance().clearImageListener();
    }
    @Nullable
    public static RequestManager with(Context context) {
        if (context == null || !isValidContextForGlide(context)) {
            return null;
        }
        try {
            return Glide.with(context);
        } catch (Exception e) {
            Log.i(TAG, "with RequestManager exception", e);
            return null;
        }
    }

    public static RequestManager with(Fragment fragment) {
        if (fragment == null) {
            return null;
        }
        try {
            return Glide.with(fragment);
        } catch (Exception e) {
            Log.i(TAG, "with RequestManager exception", e);
            return null;
        }
    }

    public static RequestManager with(Activity activity) {
        if (activity == null || !isValidContextForGlide(activity)) {
            return null;
        }
        try {
            return Glide.with(activity);
        } catch (Exception e) {
            Log.i(TAG, "with RequestManager exception", e);
            return null;
        }
    }

    public static RequestManager with(View view) {
        if (view == null || !isValidContextForGlide(view.getContext())) {
            return null;
        }
        try {
            return Glide.with(view);
        } catch (Exception e) {
            Log.i(TAG, "with RequestManager exception", e);
            return null;
        }
    }

    public static void pauseAll(Activity activity) {
        if (activity == null || !isValidContextForGlide(activity)) {
            return;
        }
        try {
            Glide.with(activity).pauseRequests();
        } catch (Exception e) {
            Log.i(TAG, "with RequestManager exception", e);
        }
    }

    public static void resumeAll(Activity activity) {
        if (activity == null || !isValidContextForGlide(activity)) {
            return;
        }
        try {
            Glide.with(activity).resumeRequests();
        } catch (Exception e) {
            Log.i(TAG, "with RequestManager exception", e);
        }
    }

    public static void pauseAll(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        try {
            Glide.with(fragment).pauseRequests();
        } catch (Exception e) {
            Log.i(TAG, "with RequestManager exception", e);
        }
    }

    public static void resumeAll(Fragment fragment) {
        if (fragment == null) {
            return;
        }
        try {
            Glide.with(fragment).resumeRequests();
        } catch (Exception e) {
            Log.i(TAG, "with RequestManager exception", e);
        }
    }

    //https://stackoverflow.com/questions/39093730/you-cannot-start-a-load-for-a-destroyed-activity-in-relativelayout-image-using-g
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    private ImageLoader2() {
    }

    public static void init(Context context) {
        GlideBuilder builder = new GlideBuilder();
        Glide.init(context, builder);
    }

    public static void clearView(View view) {
        RequestManager requestManager = with(view);
        if (requestManager != null) {
            requestManager.clear(view);
        }
    }

    public void loadImage(ImageView view, String url) {
        loadImage(view, url, null, null, null, null, null, null, null);
    }

    public void loadImage(ImageView view, String url, RequestOptions requestOptions) {
        loadImage(view, url, requestOptions, null, null, null, null, null, null);
    }

    public void loadImage(ImageView view, String url, RequestListener listener) {
        loadImage(view, url, null, null, null, null, null, null, listener);
    }

    public void loadImage(ImageView view, String url, RequestOptions requestOptions,
                          TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy, Drawable placeholderImage, Drawable errorImage, DecodeFormat format, RequestListener listener) {
        loadImage(view, url, requestOptions, transitionOptions, diskCacheStrategy, false, placeholderImage, errorImage, format, listener);
    }

    public void loadImage(Fragment lifecycle, ImageView view, String url, RequestOptions requestOptions,
                          TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy, Drawable placeholderImage, Drawable errorImage, DecodeFormat format, RequestListener listener) {
        loadImage(lifecycle, view, url, requestOptions, transitionOptions, diskCacheStrategy, false, placeholderImage, errorImage, format, listener);
    }

    public void loadImage(ImageView view, String url, RequestOptions requestOptions,
                          TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                          boolean skipMemory,
                          Drawable placeholderImage, Drawable errorImage, DecodeFormat format, RequestListener listener) {
        loadImage(view, url, null, requestOptions, transitionOptions, diskCacheStrategy, skipMemory, format, placeholderImage, errorImage, listener);
    }

    public void loadImage(Fragment fragment, ImageView view, String url, RequestOptions requestOptions,
                          TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                          boolean skipMemory,
                          Drawable placeholderImage, Drawable errorImage, DecodeFormat format, RequestListener listener) {
        loadImage(fragment, view, url, null, requestOptions, transitionOptions, diskCacheStrategy, skipMemory, format, placeholderImage, errorImage, listener);
    }

    public void loadImage(Activity activity, ImageView view, String url, RequestOptions requestOptions,
                          TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                          boolean skipMemory,
                          Drawable placeholderImage, Drawable errorImage, DecodeFormat format, RequestListener listener) {
        loadImage(activity, view, url, null, requestOptions, transitionOptions, diskCacheStrategy, skipMemory, format, placeholderImage, errorImage, listener);
    }

    public void loadImage(ImageView view, Drawable drawable, RequestOptions requestOptions,
                          TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                          boolean skipMemory,
                          Drawable placeholderImage, Drawable errorImage, DecodeFormat format, RequestListener listener) {
        loadImage(view, null, drawable, requestOptions, transitionOptions, diskCacheStrategy, skipMemory, format, placeholderImage, errorImage, listener);
    }

    public void loadImageCircleCrop(ImageView view, String url, int placeHolderRes, int failedRes) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.transform(new CircleCrop());
        loadImage(view, url, requestOptions, null, null, false, DecodeFormat.PREFER_RGB_565, placeHolderRes, failedRes, null);
    }

    public void loadImage(ImageView view, String url, int placeHolderRes, int failedRes) {
        loadImage(view, url, null, null, null, false, DecodeFormat.PREFER_RGB_565, placeHolderRes, failedRes, null);
    }

    /**
     *  图片有两个来源url和drawable，优先使用drawable(提前预加载得到的drawable)作为图片来源
     */
    public void loadImage(ImageView view, String url, Drawable drawable, RequestOptions requestOptions,
                          TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                          boolean skipMemory, DecodeFormat format,
                          Drawable placeholderImage, Drawable errorImage, RequestListener listener) {
        RequestManager requestManager = with(view);
        if (requestManager == null) {
            return;
        }
        RequestBuilder requestBuilder;
        if (null != drawable) {
            requestBuilder = requestManager.load(drawable);
        } else {
            requestBuilder = requestManager.load(url);
        }
        if (placeholderImage != null) {
            requestBuilder.placeholder(placeholderImage);
        }
        if (errorImage != null) {
            requestBuilder.error(errorImage);
        }
        if (format == null) {
            format = DecodeFormat.PREFER_RGB_565;
        }
        setWithCrossFade(requestBuilder, view);
        prepareThumbnail(requestBuilder, view);
        settingBuilder(requestBuilder, requestOptions, transitionOptions, diskCacheStrategy, format, skipMemory, listener);
        requestBuilder.into(view);
    }

    private void loadImage(Fragment fragment, ImageView view, String url, Drawable drawable, RequestOptions requestOptions,
                           TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                           boolean skipMemory, DecodeFormat format,
                           Drawable placeholderImage, Drawable errorImage, RequestListener listener) {
        RequestManager requestManager = with(fragment);
        if (requestManager == null) {
            return;
        }
        RequestBuilder requestBuilder;
        if (null != drawable) {
            requestBuilder = requestManager.load(drawable);
        } else {
            requestBuilder = requestManager.load(url);
        }
        if (placeholderImage != null) {
            requestBuilder.placeholder(placeholderImage);
        }
        if (errorImage != null) {
            requestBuilder.error(errorImage);
        }
        setWithCrossFade(requestBuilder, view);
        prepareThumbnail(requestBuilder, view);
        settingBuilder(requestBuilder, requestOptions, transitionOptions, diskCacheStrategy, format, skipMemory, listener);
        requestBuilder.into(view);
    }

    private void loadImage(Context context, ImageView view, String url, Drawable drawable, RequestOptions requestOptions,
                           TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                           boolean skipMemory, DecodeFormat format,
                           Drawable placeholderImage, Drawable errorImage, RequestListener listener) {
        RequestManager requestManager = with(context);
        if (requestManager == null) {
            return;
        }
        RequestBuilder requestBuilder;
        if (null != drawable) {
            requestBuilder = requestManager.load(drawable);
        } else {
            requestBuilder = requestManager.load(url);
        }
        if (placeholderImage != null) {
            requestBuilder.placeholder(placeholderImage);
        }
        if (errorImage != null) {
            requestBuilder.error(errorImage);
        }
        setWithCrossFade(requestBuilder, view);
        prepareThumbnail(requestBuilder, view);
        settingBuilder(requestBuilder, requestOptions, transitionOptions, diskCacheStrategy, format, skipMemory, listener);
        requestBuilder.into(view);
    }


    public void loadImage(ImageView view, String url, RequestOptions requestOptions,
                          TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                          boolean skipMemory, DecodeFormat format,
                          int placeholderImage, int errorImage, RequestListener listener) {
        RequestManager requestManager = with(view);
        if (requestManager == null) {
            return;
        }
        RequestBuilder requestBuilder = requestManager.load(url);
        requestBuilder.placeholder(placeholderImage).error(errorImage);
        setWithCrossFade(requestBuilder, view);
        prepareThumbnail(requestBuilder, view);
        settingBuilder(requestBuilder, requestOptions, transitionOptions, diskCacheStrategy, format, skipMemory, listener);
        requestBuilder.into(view);
    }

    @SuppressLint("CheckResult")
    private void setWithCrossFade(RequestBuilder requestBuilder, ImageView view) {
        if (!(view instanceof SimpleDraweeView)) {
            return;
        }
        SimpleDraweeView drawableView = (SimpleDraweeView) view;
        boolean withCrossFadeEnable = drawableView.getWithCrossFadeEnable();
        int fadeDuration = drawableView.getCrossFadeDuration();
        if (withCrossFadeEnable) {
            requestBuilder.transition(DrawableTransitionOptions.withCrossFade(fadeDuration));
        }
    }

    @SuppressLint("CheckResult")
    private void prepareThumbnail(RequestBuilder requestBuilder, ImageView view) {
        if (!(view instanceof SimpleDraweeView)) {
            return;
        }
        SimpleDraweeView drawableView = (SimpleDraweeView) view;
        String staticUrl = drawableView.getStaticImgUrl();
        if (!TextUtils.isEmpty(staticUrl)) {
            requestBuilder.thumbnail(
                    with(view)
                    .load(staticUrl)
            );
        }
    }

    @SuppressLint("CheckResult")
    private void settingBuilder(RequestBuilder requestBuilder, RequestOptions requestOptions,
                                TransitionOptions transitionOptions, DiskCacheStrategy diskCacheStrategy,
                                DecodeFormat format, boolean skipMemory, RequestListener listener) {
        if (requestBuilder == null) {
            return;
        }
        if (requestOptions != null) {
            requestBuilder.apply(requestOptions);
        }
        if (transitionOptions != null) {
            requestBuilder.transition(transitionOptions);
        }
        if (diskCacheStrategy != null) {
            requestBuilder.diskCacheStrategy(diskCacheStrategy);
        }
        if (format != null) {
            requestBuilder.format(format);
        }
        if (listener != null) {
            requestBuilder.listener(listener);
        }
        requestBuilder.skipMemoryCache(skipMemory);
    }

    public void loadImage(ImageView view, String url,RequestOptions requestOptions, int placeHolderRes, int failedRes) {
        RequestManager requestManager = with(view);
        if (requestManager == null) {
            return;
        }
        loadImage(view, url, requestOptions, null, null, false, DecodeFormat.PREFER_RGB_565, placeHolderRes, failedRes, null);
    }
    public void loadBitmap(Context context, String url, RequestOptions options, RequestListener<Bitmap> requestListener) {
        RequestManager requestManager = with(context);
        if (requestManager == null) {
            return;
        }
        RequestBuilder<Bitmap> requestBuilder = requestManager.asBitmap().load(url);
        if (options != null) {
            requestBuilder.apply(options);
        }
        requestBuilder.addListener(requestListener).into(new CustomTarget() {
            @Override
            public void onResourceReady(@NonNull Object o, @Nullable Transition transition) {

            }

            @Override
            public void onLoadCleared(@Nullable Drawable drawable) {

            }
        });
    }
    public Bitmap loadBitmap(Context context, String url) {
        RequestManager requestManager = with(context);
        if (requestManager == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            FutureTarget<Bitmap> futureTarget = requestManager
                    .asBitmap()
                    .load(url)
                    .submit();
            bitmap = futureTarget.get();
        } catch (Exception e) {
            Log.e(TAG, "loadBitmapImage error!", e);
        }
        return bitmap;
    }

    public Bitmap loadBitmap(Context context, String url, float scale) {
        RequestManager requestManager = with(context);
        if (requestManager == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            FutureTarget<Bitmap> futureTarget = requestManager
                    .asBitmap()
                    .load(url)
                    .thumbnail(scale)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .submit();
            bitmap = futureTarget.get();
        } catch (Exception e) {
            Log.e(TAG, "loadBitmapImage error!", e);
        }
        return bitmap;
    }

    public void preloadImage(Context context, String url, RequestListener listener) {
        RequestManager requestManager = with(context);
        if (requestManager == null) {
            return;
        }
        RequestBuilder requestBuilder = requestManager.load(url);
        if (listener != null) {
            requestBuilder.listener(listener);
        }
        requestBuilder.preload();
    }

    public File downloadImage(Context context, String url) {
        RequestManager requestManager = with(context);
        if (requestManager == null) {
            return null;
        }
        File file = null;
        try {
            file = requestManager.asFile().load(url).submit().get();
        } catch (Exception e) {
            Log.e(TAG, "cacheImage error!", e);
        }
        return file;
    }

    public Bitmap loadBitmap(Context context, String filePath, int width, int height) {
        final RequestManager requestManager = with(context);
        if (requestManager == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            bitmap = requestManager.asBitmap().load(filePath)
                    .skipMemoryCache(true).format(DecodeFormat.PREFER_RGB_565)
                    .submit(width, height).get();
        } catch (Exception e) {
            Log.e(TAG, "loadBitmapImageFromFileSync error!", e);
        }
        return bitmap;
    }

    public Bitmap loadBitmap(Context context, Uri uri, int width, int height) {
        final RequestManager requestManager = with(context);
        if (requestManager == null) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            bitmap = requestManager.asBitmap().load(uri).format(DecodeFormat.PREFER_RGB_565)
                    .submit(width, height).get();
        } catch (Exception e) {
            Log.e(TAG, "loadBitmapImageFromFileSync error!", e);
        }

        return bitmap;
    }

    public long getMemorySize(Context applicationContext) {
        return Glide.get(applicationContext).getBitmapPool().getMaxSize();
    }

    public static boolean shouldEnabledGSLBTestEnv(Context context, boolean isMonkey) {
        // monkey版本暂时使用这种方式判断
        if (isMonkey) {
            return true;
        } else {
            return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 && sIsGSLBDebugEnabled;
        }
    }

    /**
     * 5.0以上限制回收内存大小，避免内存持续增长
     *
     * @return
     */

    public static void setsIsGSLBDebugEnabled(boolean isGSLBDebugEnabled) {
        sIsGSLBDebugEnabled = isGSLBDebugEnabled;
    }

}
