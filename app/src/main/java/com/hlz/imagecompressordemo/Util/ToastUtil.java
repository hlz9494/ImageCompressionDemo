/**
 *
 */
package com.hlz.imagecompressordemo.Util;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hlz.imagecompressordemo.App;

/**
 * @author L440-9
 */
public class ToastUtil {

    private static Context context = App.getApp();
    private static Toast toast = null;

    public static int space = 40;

    public ToastUtil() {
    }

    public static Toast showShortText(String text) {
        return showText(text, null, Toast.LENGTH_SHORT);
    }

    public static Toast showLongText(String text) {
        return showText(text, null, Toast.LENGTH_LONG);
    }

    private static Toast showText(String text, Integer gravtity, int duration) {

        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = Toast.makeText(context, text, duration);

        if (gravtity != null) {
            toast.setGravity(gravtity, toast.getXOffset(), toast.getYOffset());
        }

        toast.setText(text);
        toast.show();
        return toast;
    }

    public static Toast shortImageShow(String text) {

        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageIcon = new ImageView(context);
        toastView.addView(imageIcon, 0);
        toast.show();
        return toast;
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
