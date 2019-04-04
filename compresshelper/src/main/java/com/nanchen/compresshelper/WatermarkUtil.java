package com.nanchen.compresshelper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by dws on 2018/4/24.
 */

public class WatermarkUtil {

    public static Bitmap drawTextToLeftTop(Bitmap bitmap, String text,
                                           int size, int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(size);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, paint, bounds,
                paddingLeft,
                paddingTop + bounds.height());
    }

    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Bitmap bitmap, String watermarkText,
                                           Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);
        String[] texts = watermarkText.split("\n");

        int top = paddingTop;
        for (String text : texts) {
            text = text.replace("\r", "");
            canvas.drawText(text, paddingLeft, top, paint);
            top += paint.getTextSize() + 10;
        }

        return bitmap;
    }


}
