package com.image;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.LibraryGlideModule;
import com.image.analyse.ImageAnalyticsMonitor;
import com.image.bitmap.YoliByteBufferBitmapDecoder;
import com.image.bitmap.YoliDownsampler;
import com.image.bitmap.YoliStreamBitmapDecoder;
import com.image.http.GlideOkhttpLoader;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.ByteBuffer;

@GlideModule
public class GlideCommonModule extends LibraryGlideModule {
    @Override
    public void registerComponents(@NonNull @NotNull Context context, @NonNull @NotNull Glide glide, @NonNull @NotNull Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new GlideOkhttpLoader.Factory(ImageAnalyticsMonitor.getInstance()));
        YoliDownsampler yoliDownsampler = new YoliDownsampler(registry.getImageHeaderParsers(), context.getResources().getDisplayMetrics(), glide.getBitmapPool(), glide.getArrayPool());

        YoliByteBufferBitmapDecoder byteBufferBitmapDecoder = new YoliByteBufferBitmapDecoder(yoliDownsampler);
        YoliStreamBitmapDecoder streamBitmapDecoder = new YoliStreamBitmapDecoder(yoliDownsampler, glide.getArrayPool());

        registry.prepend(Registry.BUCKET_BITMAP, ByteBuffer.class, Bitmap.class, byteBufferBitmapDecoder);
        registry.prepend(Registry.BUCKET_BITMAP, InputStream.class, Bitmap.class, streamBitmapDecoder);
    }
}
