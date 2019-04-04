package com.nanchen.compresshelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * 压缩方法工具类
 * <p>
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-03-08  9:03
 */

public class CompressHelper {
    private static volatile CompressHelper INSTANCE;

    private Context context;
    /**
     * 最大宽度，默认为720
     */
    private float maxWidth = 720.0f;
    /**
     * 最大高度,默认为960
     */
    private float maxHeight = 960.0f;
    /**
     * 默认压缩后的方式为JPEG
     */
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;

    /**
     * 默认的图片处理方式是ARGB_8888
     */
    private Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
    /**
     * 默认压缩质量为80
     */
    private int quality = 80;

    /**
     * 压缩后最大文件大小(KB) 默认100KB
     */
    private long maxSize = 100;

    /**
     * 存储路径
     * 默认 Environment.DIRECTORY_PICTURES
     */
    private String destinationDirectoryPath;
    /**
     * 文件名前缀
     */
    private String fileNamePrefix;
    /**
     * 文件名
     */
    private String fileName;

    public static CompressHelper getDefault(Context context) {
        if (INSTANCE == null) {
            synchronized (CompressHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CompressHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    public CompressHelper setBaseConfig(float maxWidth, float maxHeight, long maxSize, int quality) {
        this.maxSize = maxSize;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.quality = quality;

        return this;
    }


    private CompressHelper(Context context) {
        this.context = context;
        //destinationDirectoryPath = context.getCacheDir().getPath() + File.pathSeparator + FileUtil.FILES_PATH;
        destinationDirectoryPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    /**
     * 压缩成文件
     *
     * @param file 原始文件
     * @return 压缩后的文件
     */
    public File compressToFile(File file) {
        return BitmapUtil.compressImage(context, Uri.fromFile(file), maxWidth, maxHeight, maxSize,
                compressFormat, bitmapConfig, quality, destinationDirectoryPath,
                fileNamePrefix, fileName);
    }

    /**
     * 压缩成文件
     *
     * @param file 原始文件
     * @return 压缩后的文件
     */
    public File compressToFileWithWatermark(File file, Watermark watermark) {
        if (watermark != null) {
            Bitmap bitmap = BitmapUtil.getScaledBitmap(context, Uri.fromFile(file), maxWidth, maxHeight, bitmapConfig);
            Bitmap waterbmp = WatermarkUtil.drawTextToLeftTop(bitmap,
                    watermark.getText(),
                    watermark.getTextSize(),
                    watermark.getTextColor(), watermark.getLeft(), watermark.getTop());

            String filename = generateFilePath(context, destinationDirectoryPath, Uri.fromFile(file), compressFormat.name().toLowerCase(), fileNamePrefix, fileName);
            return BitmapUtil.compressToFile(waterbmp, maxSize, compressFormat, bitmapConfig, quality, filename);
        }

        return BitmapUtil.compressImage(context, Uri.fromFile(file), maxWidth, maxHeight, maxSize,
                compressFormat, bitmapConfig, quality, destinationDirectoryPath,
                fileNamePrefix, fileName);
    }

    /**
     * 压缩为Bitmap
     *
     * @param file 原始文件
     * @return 压缩后的Bitmap
     */
    public Bitmap compressToBitmap(File file) {
        return BitmapUtil.getScaledBitmap(context, Uri.fromFile(file), maxWidth, maxHeight, bitmapConfig);
    }


    /**
     * 采用建造者模式，设置Builder
     */
    public static class Builder {
        private CompressHelper mCompressHelper;

        public Builder(Context context) {
            mCompressHelper = new CompressHelper(context);
        }

        /**
         * 设置图片最大宽度
         *
         * @param maxWidth 最大宽度
         */
        public Builder setMaxWidth(float maxWidth) {
            mCompressHelper.maxWidth = maxWidth;
            return this;
        }

        /**
         * 设置图片最大高度
         *
         * @param maxHeight 最大高度
         */
        public Builder setMaxHeight(float maxHeight) {
            mCompressHelper.maxHeight = maxHeight;
            return this;
        }

        /**
         * 设置图片最大大小
         *
         * @param maxSize 最大大小
         */
        public Builder setMaxSize(long maxSize) {
            mCompressHelper.maxSize = maxSize;
            return this;
        }

        /**
         * 设置压缩的后缀格式
         */
        public Builder setCompressFormat(Bitmap.CompressFormat compressFormat) {
            mCompressHelper.compressFormat = compressFormat;
            return this;
        }

        /**
         * 设置Bitmap的参数
         */
        public Builder setBitmapConfig(Bitmap.Config bitmapConfig) {
            mCompressHelper.bitmapConfig = bitmapConfig;
            return this;
        }

        /**
         * 设置压缩质量，建议80
         *
         * @param quality 压缩质量，[0,100]
         */
        public Builder setQuality(int quality) {
            mCompressHelper.quality = quality;
            return this;
        }

        /**
         * 设置目的存储路径
         *
         * @param destinationDirectoryPath 目的路径
         */
        public Builder setDestinationDirectoryPath(String destinationDirectoryPath) {
            mCompressHelper.destinationDirectoryPath = destinationDirectoryPath;
            return this;
        }

        /**
         * 设置文件前缀
         *
         * @param prefix 前缀
         */
        public Builder setFileNamePrefix(String prefix) {
            mCompressHelper.fileNamePrefix = prefix;
            return this;
        }

        /**
         * 设置文件名称
         *
         * @param fileName 文件名
         */
        public Builder setFileName(String fileName) {
            mCompressHelper.fileName = fileName;
            return this;
        }

        public CompressHelper build() {
            return mCompressHelper;
        }
    }

    private static String generateFilePath(Context context, String parentPath, Uri uri,
                                           String extension, String prefix, String fileName) {
        File file = new File(parentPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        /** if prefix is null, set prefix "" */
        prefix = TextUtils.isEmpty(prefix) ? "" : prefix;
        /** reset fileName by prefix and custom file name */
        fileName = TextUtils.isEmpty(fileName) ? prefix + FileUtil.splitFileName(FileUtil.getFileName(context, uri))[0] : fileName;
        return file.getAbsolutePath() + File.separator + fileName + "." + extension;
    }
}
