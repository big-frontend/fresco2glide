

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.nio.ByteBuffer;

@GlideModule
public class GlideAppModule extends AppGlideModule {
    public static final String TAG = "GlideCacheModule";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        CacheManager.applyOptions(context, builder);
    }

    @Override
    public void registerComponents(@NonNull @NotNull Context context, @NonNull @NotNull Glide glide, @NonNull @NotNull Registry registry) {
        super.registerComponents(context, glide, registry);

    }
}
