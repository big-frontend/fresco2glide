package com.image.bitmap;


import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/** Decodes {@link android.graphics.Bitmap Bitmaps} from {@link java.nio.ByteBuffer ByteBuffers}. */
public class YoliByteBufferBitmapDecoder implements ResourceDecoder<ByteBuffer, Bitmap> {
    private final YoliDownsampler downsampler;

    public YoliByteBufferBitmapDecoder(YoliDownsampler downsampler) {
        this.downsampler = downsampler;
    }

    @Override
    public boolean handles(@NonNull ByteBuffer source, @NonNull Options options) {
        return downsampler.handles(source);
    }

    @Override
    public Resource<Bitmap> decode(
            @NonNull ByteBuffer source, int width, int height, @NonNull Options options)
            throws IOException {
        InputStream is = ByteBufferUtil.toStream(source);
        return downsampler.decode(is, width, height, options);
    }
}
