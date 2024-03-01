package com.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.drawee.drawable.ScalingUtils
import com.heytap.browser.core.common.log.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Force the calling coroutine to suspend before throwing [this].
 *
 * This is needed when a checked exception is synchronously caught in a [java.lang.reflect.Proxy]
 * invocation to avoid being wrapped in [java.lang.reflect.UndeclaredThrowableException].
 *
 * The implementation is derived from:
 * https://github.com/Kotlin/kotlinx.coroutines/pull/1667#issuecomment-556106349
 */
internal suspend fun Throwable.suspendAndThrow(): Nothing {
    suspendCoroutineUninterceptedOrReturn<Nothing> { continuation ->
        Dispatchers.Default.dispatch(continuation.context) {
            continuation.intercepted().resumeWithException(this@suspendAndThrow)
        }
        COROUTINE_SUSPENDED
    }
}

private suspend fun <R> loadImage(
    requestManager: RequestManager?,
    requestBuilder: RequestBuilder<R>?,
    tarW: Int = Integer.MIN_VALUE,
    tarH: Int = Integer.MIN_VALUE
): R? {
    if (requestBuilder == null || requestManager == null) return null
    return suspendCancellableCoroutine { continuation ->
        val futureTarget = requestBuilder.addListener(object : RequestListener<R> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<R>?,
                isFirstResource: Boolean
            ): Boolean {
                if (e == null) {
                    Log.e(
                        ImageLoader2.TAG,
                        "model:$model target:$target isFirstResource:$isFirstResource"
                    )
                } else {
                    continuation.resumeWithException(e)
                }
                return false
            }

            override fun onResourceReady(
                p0: R?,
                p1: Any?,
                p2: Target<R>?,
                p3: DataSource?,
                p4: Boolean
            ): Boolean {
                continuation.resume(p0)
                return false
            }
        }).submit(tarW, tarH)
        continuation.invokeOnCancellation {
            requestManager.clear(futureTarget)
        }
    }
}

suspend fun ImageLoader2.loadImageFromCache(context: Context, url: String): Drawable? {
    val requestManager = ImageLoader2.with(context)
    val requestBuilder = requestManager?.load(url)?.onlyRetrieveFromCache(true)
    return loadImage(requestManager, requestBuilder)

}

suspend fun ImageLoader2.loadImageSkipMemoryCache(context: Context, url: String): Drawable? {
    val requestManager = ImageLoader2.with(context)
    val requestBuilder = requestManager?.load(url)?.skipMemoryCache(true)
    return loadImage(requestManager, requestBuilder)
}

suspend fun ImageLoader2.loadDrawable(
    context: Context,
    url: String,
    placeHolderRes: Int,
    failedRes: Int,
    scaleType: Int,
    tarW: Int = Integer.MIN_VALUE, tarH: Int = Integer.MIN_VALUE
): Drawable? {
    val requestManager = ImageLoader2.with(context)
    val requestBuilder = requestManager?.load(url)
        ?.format(DecodeFormat.PREFER_RGB_565)
        ?.placeholder(placeHolderRes)
        ?.error(failedRes)

    when (scaleType) {
        ScalingUtils.ScaleType.CENTER_CROP -> requestBuilder?.centerCrop()
        ScalingUtils.ScaleType.CENTER_INSIDE -> requestBuilder?.centerInside()
        ScalingUtils.ScaleType.FIT_CENTER -> requestBuilder?.fitCenter()
        else -> {}
    }
    return loadImage(requestManager, requestBuilder,tarW, tarH)
}

suspend fun ImageLoader2.loadImageBytes(
    context: Context,
    url: String,
    tarW: Int = Integer.MIN_VALUE,
    tarH: Int = Integer.MIN_VALUE
): ByteArray? {
    val requestManager = ImageLoader2.with(context)
    val requestBuilder = requestManager?.`as`(ByteArray::class.java)?.load(url)
    return loadImage(requestManager, requestBuilder, tarW, tarH)
}

suspend fun ImageLoader2.loadBitmap(
    context: Context,
    url: String,
    tarW: Int = Integer.MIN_VALUE,
    tarH: Int = Integer.MIN_VALUE
): Bitmap? {
    val requestManager = ImageLoader2.with(context)
    val requestBuilder = requestManager?.asBitmap()?.load(url)
    return loadImage(requestManager, requestBuilder, tarW, tarH)
}

suspend fun ImageLoader2.loadBitmap(
    context: Context,
    filePath: String,
): Bitmap? {
    val requestManager = ImageLoader2.with(context)
    val requestBuilder = requestManager?.asBitmap()?.load(filePath)
        ?.skipMemoryCache(true)?.format(DecodeFormat.PREFER_RGB_565)
    return loadImage(requestManager, requestBuilder)
}

suspend fun ImageLoader2.downloadFile(context: Context, url: String): File? {
    val requestManager = ImageLoader2.with(context)
    val requestBuilder = requestManager?.asFile()?.load(url)
    return loadImage(requestManager, requestBuilder)

}

suspend fun clearDisk(applicationContext: Context) = withContext(Dispatchers.IO) {
    Glide.get(applicationContext).clearDiskCache()
}

suspend fun clearCache(applicationContext: Context) = withContext(Dispatchers.Main) {
    Glide.get(applicationContext).clearMemory()
}
