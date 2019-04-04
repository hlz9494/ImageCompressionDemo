package com.hlz.imagecompressordemo.Util;

import android.os.Environment;

import java.io.File;

public class FileUtil {
    /**
     * 获取首选目录
     *
     * @param dirName 目标目录
     * @return 返回目录可用路径，目录为文件或目录创建失败返回 <b>null<b>
     */
    public static String getPreferredDir(String dirName) {
        if (dirName == null) {
            dirName = "";
        }
        StringBuilder dirPath = new StringBuilder();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dirPath.append(Environment.getExternalStorageDirectory().getAbsolutePath());
            dirPath.append("/").append("innotek");
            dirPath.append("/").append("photoTest");
        } else {
//            dirPath.append(sInstance.getFilesDir().getAbsolutePath());
            dirPath.append("/").append("innotek");
            dirPath.append("/").append("photoTest");
        }
        dirPath.append("/").append(dirName);
        File dir = new File(dirPath.toString());
        if (dir.exists() && !dir.isDirectory()) {
            return null;
        } else {
            if (dir.exists()) {
                return dir.getAbsolutePath() + "/";
            } else if (dir.mkdirs()) {
                return dir.getAbsolutePath() + "/";
            } else {
                return null;
            }
        }
    }

}
