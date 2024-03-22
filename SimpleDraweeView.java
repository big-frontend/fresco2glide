package com.facebook.drawee.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.drawable.ScalingUtils;


public class SimpleDraweeView extends AppCompatImageView {
    public static final String TAG = "SimpleDraweeView";
    private final AspectRatioMeasure.Spec mMeasureSpec = new AspectRatioMeasure.Spec();
    private String mUrl;
    private Drawable mPlaceholderImage;
    private RequestListener<Drawable> mRequestListener;
    private RequestOptions mRequestOptions;
    private TransitionOptions mTransitionOptions;
    // 设置采样策略，如果设置了该策略建议配置mBitmapTransformation一起使用，否则可能会导致图片获取异常
    private DownsampleStrategy mDownsampleStrategy = null;
    private BitmapTransformation mBitmapTransformation = null;
    private BitmapTransformation mRound;
    private Drawable mFailureImage;
    private boolean mForceUpdate;
    private boolean mSkipMemory;
//    private boolean mRoundAsCircle;
    private boolean autoPlayAnimations;
    //    private boolean dynamicWebp;
    private String staticImgUrl;
    private float mAspectRatio = 0;
    private float mRoundSmoothRadius;
    private boolean mWithCrossFadeEnable;
    private int mFadeDuration = 300;

    private DiskCacheStrategy mDiskCacheStrategy;
    private RoundRectDrawable mRoundBackgroundDrawable;//圆角背景
    private Drawable mBackgroundDrawable;//普通background
    private DecodeFormat decodeFormat;
    int mScaleType;
    private boolean needBorder;
    private float borderWidth;
    private int borderColor;
    private Paint mPaint;
    private RectF drawRoundRect;

    public SimpleDraweeView(Context context) {
        super(context);
        init(context, null, 0);

    }

    public SimpleDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SimpleDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }


    private void init(Context context, AttributeSet attrs, int defStyle) {
        final TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.SimpleDraweeView, defStyle, 0);

        mUrl = typedArray.getString(
                R.styleable.SimpleDraweeView_frescoUrl);

        if (typedArray.hasValue(R.styleable.SimpleDraweeView_placeholderImage)) {
            mPlaceholderImage = typedArray.getDrawable(R.styleable.SimpleDraweeView_placeholderImage);
            mPlaceholderImage.setCallback(this);
        }
//        if (typedArray.hasValue(R.styleable.SimpleDraweeView_roundAsCircle)) {
//            mRoundAsCircle = typedArray.getBoolean(R.styleable.SimpleDraweeView_roundAsCircle, false);
//        }


        if (typedArray.hasValue(R.styleable.SimpleDraweeView_failureImage)) {
            mFailureImage = typedArray.getDrawable(R.styleable.SimpleDraweeView_failureImage);
            mFailureImage.setCallback(this);
        }

        if (typedArray.hasValue(R.styleable.SimpleDraweeView_viewAspectRatio)) {
            mAspectRatio = typedArray.getFloat(R.styleable.SimpleDraweeView_viewAspectRatio, 0f);
        }

        if (typedArray.hasValue(R.styleable.SimpleDraweeView_actualImageScaleType)) {
            mScaleType = typedArray
                    .getInt(R.styleable.SimpleDraweeView_actualImageScaleType, ScalingUtils.ScaleType.CENTER_CROP);
        }

        if (typedArray.hasValue(R.styleable.SimpleDraweeView_roundSmoothRadius)) {
            mRoundSmoothRadius = typedArray
                    .getDimension(R.styleable.SimpleDraweeView_roundSmoothRadius, 0);
        }

        if (typedArray.hasValue(R.styleable.SimpleDraweeView_needRoundingBorder)) {
            needBorder = typedArray.getBoolean(R.styleable.SimpleDraweeView_needRoundingBorder, false);
        }

        if (typedArray.hasValue(R.styleable.SimpleDraweeView_roundingBorderWidth)) {
            borderWidth = typedArray.getDimension(R.styleable.SimpleDraweeView_roundingBorderWidth, 0);
        }

        if (typedArray.hasValue(R.styleable.SimpleDraweeView_roundingBorderColor)) {
            borderColor = typedArray.getColor(R.styleable.SimpleDraweeView_roundingBorderColor, 0);
        }

        if (borderWidth != 0 && borderColor != 0 && needBorder) {
            mPaint = new Paint();
            drawRoundRect = new RectF();
            mPaint.setAntiAlias(true);
            mPaint.setColor(borderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(borderWidth);
        }

        typedArray.recycle();
//        if (mRoundAsCircle) {
//            setOutlineProvider(new ViewOutlineProvider() {
//                @Override
//                public void getOutline(View view, Outline outline) {
//                    int size = Math.min(getWidth(), getHeight());
//                    outline.setRoundRect(0, 0, getWidth(), getHeight(), size / 2f);
//                }
//            });
//            setClipToOutline(true);
//        } else {
        mRoundBackgroundDrawable = new RoundRectDrawable(mRoundSmoothRadius);
        if (mRoundSmoothRadius > 0) {
            setBackground(mRoundBackgroundDrawable);
            setClipToOutline(true);
        }
//        }

        post(new Runnable() {
            @Override
            public void run() {
                load();
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint != null && drawRoundRect != null && needBorder) {
            float v = borderWidth * 0.5f;
            drawRoundRect.set(0 + v, 0 + v, getMeasuredWidth() - v, getMeasuredHeight() - v);
            canvas.drawRoundRect(drawRoundRect, mRoundSmoothRadius, mRoundSmoothRadius, mPaint);
        }
    }

    /**
     * 圆角使用RoundRectDrawable作为背景来实现
     *
     * @param background
     */
    @Override
    public void setBackground(Drawable background) {
        if (background instanceof RoundRectDrawable) {
            super.setBackground(background);
        } else {
            mBackgroundDrawable = background;
            if (mRoundSmoothRadius <= 0) {//不显示圆角时直接设置背景
                super.setBackground(background);
            }
        }
    }

    /**
     * Sets the desired aspect ratio (w/h).
     */
    public void setAspectRatio(float aspectRatio) {
        if (aspectRatio == mAspectRatio) {
            return;
        }
        mAspectRatio = aspectRatio;
        requestLayout();
    }

    /**
     * Gets the desired aspect ratio (w/h).
     */
    public float getAspectRatio() {
        return mAspectRatio;
    }


    public void setDiskCacheStrategy(DiskCacheStrategy mDiskCacheStrategy) {
        this.mDiskCacheStrategy = mDiskCacheStrategy;
    }

    public void setDownsampleStrategyAndTransformation(DownsampleStrategy downsampleStrategy,
                                                       BitmapTransformation bitmapTransformation) {
        this.mDownsampleStrategy = downsampleStrategy;
        this.mBitmapTransformation = bitmapTransformation;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureSpec.width = widthMeasureSpec;
        mMeasureSpec.height = heightMeasureSpec;
        AspectRatioMeasure.updateMeasureSpec(
                mMeasureSpec,
                mAspectRatio,
                getLayoutParams(),
                getPaddingLeft() + getPaddingRight(),
                getPaddingTop() + getPaddingBottom());
        super.onMeasure(mMeasureSpec.width, mMeasureSpec.height);
    }

    @Deprecated
    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }

    @Deprecated
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    @Deprecated
    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    public void setImageURI(@Nullable Uri uri) {
        String uriPath = null;
        if (uri != null) {
            uriPath = uri.toString();
        }
        setImageURI(uriPath);
    }
    public void setImageURI(@Nullable String uriString) {
        if (TextUtils.isEmpty(uriString)) {
            return;
        }
        mUrl = uriString;
        load();
    }

    public void loadImage(Fragment lifecycle, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mUrl = url;
        load(lifecycle);
    }

    public void loadImage(Activity lifecycle, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        mUrl = url;
        load(lifecycle);
    }

    @SuppressLint("CheckResult")
    public void setRequestListener(RequestListener requestListener) {
        this.mRequestListener = requestListener;
    }

    public void setRequestOptions(RequestOptions mRequestOptions) {
        this.mRequestOptions = mRequestOptions;
    }

    public void setTransitionOptions(TransitionOptions transitionOptions) {
        this.mTransitionOptions = transitionOptions;
    }

    public void setPlaceholderImage(Drawable mPlaceholderImage) {
        this.mPlaceholderImage = mPlaceholderImage;
    }

    public void setPlaceholderImage(int mPlaceholderImageID) {
        this.mPlaceholderImage = getResources().getDrawable(mPlaceholderImageID, null);
    }

    public void setFailureImage(Drawable mFailureImage) {
        this.mFailureImage = mFailureImage;
    }

    public void setFailureImage(int mFailureImageID) {
        this.mFailureImage = getResources().getDrawable(mFailureImageID, null);
    }

    public void setRequestScaleType(int scaleType) {
        this.mScaleType = scaleType;
    }

    public void setForceUpdate(boolean mForceUpdate) {
        this.mForceUpdate = mForceUpdate;
    }

    public boolean isForceUpdate() {
        return mForceUpdate;
    }

    public void setSkipMemory(boolean mSkipMemory) {
        this.mSkipMemory = mSkipMemory;
    }

    public void setAutoPlayAnimations(boolean autoPlayAnimations) {
        this.autoPlayAnimations = autoPlayAnimations;
    }

    public boolean isAutoPlayAnimations() {
        return autoPlayAnimations;
    }

//    public void setDynamicWebp(boolean dynamicWebp) {
//        this.dynamicWebp = dynamicWebp;
//    }

    public void setDecodeFormat(DecodeFormat format) {
        this.decodeFormat = format;
    }

    public void setStaticImgUrl(String staticImgUrl) {
        this.staticImgUrl = staticImgUrl;
    }

    public String getStaticImgUrl() {
        return staticImgUrl;
    }

    public void setWithCrossFadeEnable(boolean withCrossFadeEnable) {
        mWithCrossFadeEnable = withCrossFadeEnable;
    }

    public boolean getWithCrossFadeEnable() {
        return mWithCrossFadeEnable;
    }

    public void setCrossFadeDuration(int fadeDuration) {
        mFadeDuration = fadeDuration;
    }

    public int getCrossFadeDuration() {
        return mFadeDuration;
    }


    public void setSmoothRoundRadius(float radius) {
        this.mRoundSmoothRadius = radius;
        mRoundBackgroundDrawable.setRadius(mRoundSmoothRadius);
        if (radius > 0) {//圆角半径大于0时需要设置圆角背景
            setClipToOutline(true);
            setBackground(mRoundBackgroundDrawable);
        } else {
            setBackground(mBackgroundDrawable);
        }
        invalidate();
    }

    public void load() {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        Drawable placeHolder = getPlaceHolder();
        ImageLoader2.getInstance().loadImage(this, mUrl,
                prepareRequestOptions(), mTransitionOptions, prepareDiskStrategy(),
                mSkipMemory, placeHolder, mFailureImage, decodeFormat, mRequestListener);
    }

    public void load(Fragment lifecycle) {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        Drawable placeHolder = getPlaceHolder();
        ImageLoader2.getInstance().loadImage(lifecycle, this, mUrl,
                prepareRequestOptions(), mTransitionOptions, prepareDiskStrategy(),
                mSkipMemory, placeHolder, mFailureImage, decodeFormat, mRequestListener);
    }

    public void load(Activity lifecycle) {
        if (TextUtils.isEmpty(mUrl)) {
            return;
        }
        Drawable placeHolder = getPlaceHolder();
        ImageLoader2.getInstance().loadImage(lifecycle, this, mUrl,
                prepareRequestOptions(), mTransitionOptions, prepareDiskStrategy(),
                mSkipMemory, placeHolder, mFailureImage, decodeFormat, mRequestListener);
    }


    @SuppressLint("CheckResult")
    private RequestOptions prepareRequestOptions() {
        if (mRequestOptions == null) {
            mRequestOptions = new RequestOptions();
        }

        Transformation<Bitmap> transformation;
        // 如果设置了采样策略，就不能再通过fitCenter等方式设置，这会存在冲突
        // 这里限制两者必须同时设置
        if (mDownsampleStrategy != null && mBitmapTransformation != null) {
            mRequestOptions.downsample(mDownsampleStrategy);
            transformation = mBitmapTransformation;
        } else {
            switch (mScaleType) {
                case ScalingUtils.ScaleType.FIT_CENTER:
                    mRequestOptions.fitCenter();
                    break;
                case ScalingUtils.ScaleType.CENTER_INSIDE:
                    mRequestOptions.centerInside();
                    break;
                case ScalingUtils.ScaleType.CENTER_CROP:
                    mRequestOptions.centerCrop();
                    break;
                case ScalingUtils.ScaleType.CIRCLE_CROP:
                    mRequestOptions.circleCrop();
                    break;
                default:
                    break;
            }
            transformation = new CenterCrop();
        }

//        if (!mRoundAsCircle && mRoundSmoothRadius == 0) {
            // glide不处理圆角的情况，单独处理 webp 动图
//            if (dynamicWebp) {
//                mRequestOptions.optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(transformation));
//            }
//            return mRequestOptions;
//        }

//        if (dynamicWebp) {
//            mRequestOptions.optionalTransform(WebpDrawable.class, new WebpDrawableTransformation(transformation));
//        } else {
//            mRequestOptions.transform(transformation);
//        }

        if (mRound != null) {
            mRequestOptions.transform(transformation, mRound);
        } else {
            mRequestOptions.transform(transformation);
        }

        return mRequestOptions;
    }

    public void setRoundedCornerRadius(@Px @IntRange(from = 0) int radius) {
        if (radius > 0) {
            mRound = new RoundedCorners(radius);
        } else {
            mRound = null;
        }
    }

    public void setRoundedCornerRadius(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        mRound = new GranularRoundedCorners(topLeft, topRight, bottomRight, bottomLeft);
    }

    private DiskCacheStrategy prepareDiskStrategy() {
        if (mForceUpdate) {
            return DiskCacheStrategy.NONE;
        }
        return mDiskCacheStrategy;
    }

    private Drawable getPlaceHolder() {
        Drawable placeHolder;
        if (mRoundSmoothRadius > 0) {
            placeHolder = mPlaceholderImage == null ? mBackgroundDrawable : mPlaceholderImage;
        } else {
            placeHolder = mPlaceholderImage;
        }
        return placeHolder;
    }

    public void clear() {
        ImageLoader2.getInstance().clearView(this);
    }

}
