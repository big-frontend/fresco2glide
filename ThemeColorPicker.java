package com.image.transformation;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;



import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据图片提取主色调的算法:
 * https://www.figma.com/file/4DBOwmgz8vcC846i3vl91I/%E5%95%86%E4%B8%9A%E5%8C%96%E5%B9%BF%E5%91%8A%E6%9C%80%E6%96%B0%E6%96%87%E4%BB%B6?node-id=2364%3A24644
 *
 * @author 80347639
 * @since 2022/1/13
 */
public class ThemeColorPicker {

    private final static String TAG = "color-picker";

    // 内存换速度开关: 申请 buffer 一次性获取完比循环每次去 getPixel 快大概3倍左右
    private final static boolean USE_PIXELS_BUFFER = true;

    // 剔除无效颜色的 sv 阈值:
    // s 或者 v 在 0-5(范围是 0-100，android 是按 0-1 算，所以需要转化一下) 之间需要剔除
    private final static float INVALID_S_V_LOW_LIMIT = 0.0f;
    private final static float INVALID_S_V_HIGH_LIMIT = 0.05f;

    // 提取主题色之后最终转化的 sv 值:
    // sRGB 转化为 hsv，固定 s 为 50, v 为 50
    private final static float TRANSFORM_S = 0.5f;
    private final static float TRANSFORM_V = 0.5f;

    private static float[] userHsv = null;

    /**
     * 根据RGB颜色模型统计图片像素，并查出主题色
     *
     * @param image Bitmap 对象
     * @param statTag 统计耗时的标记
     * @param hsv hsv数值
     * @return 主题色的RGB值, 0 很特殊，表示提取失败
     */
    public static int getImageThemeColor(@NonNull Bitmap image, @Nullable String statTag, float[] hsv) {
        if (image.isRecycled()) {
            Log.w(TAG, "getImageThemeColor: bmp is null !");
            return 0;
        }
        userHsv = hsv;

        long cost = System.currentTimeMillis();
        int finalColor = applyThemeColorTransformation(getImageMaxColor(image, statTag));
        cost = System.currentTimeMillis() - cost;
        Log.d(TAG, "getImageThemeColor: " + statTag + ": themeColor: 0x" + Integer.toHexString(finalColor)
                + ", total cost: " + cost + "ms");
        return finalColor;
    }

    /**
     * 根据RGB颜色模型统计图片像素，并查出主题色
     *
     * @param image Bitmap 对象
     * @param statTag 统计耗时的标记
     * @return 主题色的RGB值, 0 很特殊，表示提取失败
     */
    public static int getImageThemeColor(@NonNull Bitmap image, @Nullable String statTag) {
        if (image.isRecycled()) {
            Log.w(TAG, "getImageThemeColor: bmp is null !");
            return 0;
        }
        userHsv = null;

        long cost = System.currentTimeMillis();
        int finalColor = applyThemeColorTransformation(getImageMaxColor(image, statTag));
        cost = System.currentTimeMillis() - cost;
        Log.d(TAG, "getImageThemeColor: " + statTag + ": themeColor: 0x" + Integer.toHexString(finalColor)
                + ", total cost: " + cost + "ms");
        return finalColor;
    }

    private static int getImageMaxColor(@NonNull Bitmap image, @Nullable String statTag) {
        int width = image.getWidth();
        int height = image.getHeight();
        int maxCnt = 0;
        int maxColor = 0;
        int sRGB = 0;
        int temp = 0;
        int[] pixels;
        long cost = System.currentTimeMillis();
        HashMap<Integer, Integer> colorMap = new HashMap<>();
        if (USE_PIXELS_BUFFER) {
            pixels = new int[width * height];
            image.getPixels(pixels, 0, width, 0, 0, width, height);
        }
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (USE_PIXELS_BUFFER) {
                    sRGB = pixels[j * width + i];
                } else {
                    sRGB = image.getPixel(i, j);
                }
                if (colorMap.containsKey(sRGB)) {
                    temp = colorMap.get(sRGB);
                    colorMap.put(sRGB, temp + 1);
                    if ((temp + 1) > maxCnt) {
                        maxCnt = temp + 1;
                        maxColor = sRGB;
                    }
                } else {
                    colorMap.put(sRGB, 1);
                }
            }
        }
        cost = System.currentTimeMillis() - cost;
        Log.d(TAG, "getImageThemeColor: " + statTag
                + ": size: " + width + "x" + height
                + ", statColor cost: " + cost + "ms");

        cost = System.currentTimeMillis();
        int themeColor = findThemeColorFromMap(colorMap, maxColor, maxColor);
        cost = System.currentTimeMillis() - cost;
        Log.d(TAG, "getImageThemeColor: " + statTag + ": find theme color cost: " + cost + "ms");

        return themeColor;
    }

    private static int findThemeColorFromMap(Map<Integer, Integer> colorMap, int maxColor, int fallbackColor) {
        if (isThemeColorMeetStandard(maxColor)) {
            return maxColor;
        } else {
            List<Map.Entry<Integer, Integer>> list = sortColorMap(colorMap);
            for (Map.Entry<Integer, Integer> entry : list) {
                if (!isThemeColorMeetStandard(entry.getKey())) {
                    continue;
                }
                return entry.getKey();
            }
        }
        return fallbackColor;
    }

    private static List<Map.Entry<Integer, Integer>> sortColorMap(Map<Integer, Integer> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return list;
    }

    private static boolean isThemeColorMeetStandard(int sRGB) {
        float[] hsv = new float[3];
        Color.colorToHSV(sRGB, hsv);
        float s = hsv[1];
        float v = hsv[2];
        if ((s >= INVALID_S_V_LOW_LIMIT && s <= INVALID_S_V_HIGH_LIMIT)
                || (v >= INVALID_S_V_LOW_LIMIT && v <= INVALID_S_V_HIGH_LIMIT)) {
            return false;
        } else {
            return true;
        }
    }

    private static int applyThemeColorTransformation(int sRGB) {
        float[] hsv = new float[3];
        Color.colorToHSV(sRGB, hsv);
        if (userHsv != null) {
            hsv[1] = userHsv[1];
            hsv[2] = userHsv[2];
        } else {
            hsv[1] = TRANSFORM_S;
            hsv[2] = TRANSFORM_V;
        }
        return Color.HSVToColor(hsv);
    }

}
