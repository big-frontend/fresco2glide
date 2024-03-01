package com.image.transformation;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;


import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;

public class BlurTransformation extends BitmapTransformation {
    final String ID = "com.heytap.common.image.blur.BlurTransformation";
    private static final int DEFAULT_RADIUS = 80;
    private static final int DEFAULT_DOWN_SAMPLING = 1;

    private final static String TAG = "blur-tran";

    private final int radius;
    private final int sampling;
    private final int coverAlpha;

    public BlurTransformation() {
        this(DEFAULT_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius) {
        this(radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransformation(int radius, int sampling) {
        this(radius, sampling, 0);
    }

    /**
     * 图片模糊变化
     *
     * @param radius fast blur 系数，使用 fast blur 算法，如果设置了缩放系数，这个参数会被忽略
     * @param sampling 缩放系数，使用缩小/放大的模糊算法，如果设置为 1 或者 0，则使用 fast blur 算法
     * @param coverAlpha 黑色遮罩 alpha 系数，如果设置 0 则不采用黑色遮罩
     */
    public BlurTransformation(int radius, int sampling, int coverAlpha) {
        this.radius = radius;
        this.sampling = sampling;
        this.coverAlpha = coverAlpha;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        if (outWidth <= 0 || outHeight <= 0) {
            return toTransform;
        }

        Bitmap cut;
        if (sampling > 1) {
            cut = scaleBlurTransform(pool, toTransform, outWidth, outHeight);
        } else {
            cut = fastBlurTransform(pool, toTransform, outWidth, outHeight);
        }

        // TODO: 代码 draw 的方式盖蒙版无法盖住模糊的水波纹，用 View 叠加的效果却可以盖住，比较神奇，只能先用 View 叠加了
        if (coverAlpha > 0) {
            Canvas canvas = new Canvas(cut);
            canvas.drawColor(Color.argb(coverAlpha, 0, 0,0));
            canvas.setBitmap(null);
        }

        return cut;
    }

    @SuppressLint("DefaultLocale")
    private Bitmap fastBlurTransform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        Log.d(TAG, String.format("fastBlur: src bmp: %dx%d, out: %dx%d", width, height, outWidth, outHeight));

        long costTime = System.currentTimeMillis();
        Bitmap cut = Bitmap.createBitmap(toTransform, 0, height - outHeight, outWidth, outHeight);
        Log.d(TAG, "fastBlur: createBitmap: cost " + (System.currentTimeMillis() - costTime) + "ms");

        costTime = System.currentTimeMillis();
        cut = FastBlur.blur(cut, radius, true);
        Log.d(TAG, "fastBlur: blur: cost " + (System.currentTimeMillis() - costTime) + "ms");

        return cut;
    }

    @SuppressLint("DefaultLocale")
    private Bitmap scaleBlurTransform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        float scaleFactor = 1.0f / (float)sampling;
        Log.d(TAG, String.format("scaleBlur: rc bmp: %dx%d, out: %dx%d, scale: %d", width, height, outWidth, outHeight, sampling));
        //testSaveBmp(toTransform, "/sdcard/test1.jpg");

        // 先缩小
        long costTime = System.currentTimeMillis();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);
        Bitmap scale = Bitmap.createBitmap(toTransform, 0, 0, width, height, matrix, true);
        //testSaveBmp(scale, "/sdcard/test2.jpg");
        Log.d(TAG, "scaleBlur: createScaleBitmap: cost " + (System.currentTimeMillis() - costTime) + "ms");

        // 再放大
        costTime = System.currentTimeMillis();
        Bitmap cut = pool.get(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(cut);
        Paint paint = new Paint();
        // 开抗锯齿+线性过滤
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        matrix.reset();
        matrix.postScale(sampling, sampling);

        canvas.drawBitmap(scale, matrix, paint);
        canvas.setBitmap(null);
        //testSaveBmp(cut, "/sdcard/test3.jpg");

        Log.d(TAG, "scaleBlur: blur: cost " + (System.currentTimeMillis() - costTime) + "ms");

        return cut;
    }

    private void testSaveBmp(Bitmap bmp, String path) {
        try {
            File file = new File(path);
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.d(TAG, "error: ", e);
        }
    }

    @Override
    public String toString() {
        return "BlurTransformation(radius=" + radius + ", sampling=" + sampling + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BlurTransformation &&
                ((BlurTransformation) o).radius == radius &&
                ((BlurTransformation) o).sampling == sampling &&
                ((BlurTransformation) o).coverAlpha == coverAlpha;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + radius * 1000 + sampling * 100 + coverAlpha * 10;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((ID + radius + sampling + coverAlpha).getBytes(CHARSET));
    }
}
