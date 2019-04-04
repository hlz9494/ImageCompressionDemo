package com.nanchen.compresshelper;

import android.graphics.Color;

/**
 * Created by dws on 2018/4/24.
 */

public class Watermark {
    private String text;
    private int textSize = 20;
    private int textColor = Color.RED;
    private int top = 26;
    private int left = 26;

    public Watermark() {
    }

    public Watermark(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }
}
