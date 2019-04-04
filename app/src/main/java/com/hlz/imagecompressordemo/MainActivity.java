package com.hlz.imagecompressordemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hlz.imagecompressordemo.Util.FileUtil;
import com.hlz.imagecompressordemo.Util.ToastUtil;
import com.nanchen.compresshelper.CompressHelper;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/***
 * 照片压缩的demo
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.sample_text)
    TextView sampleText;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.imageView)
    ImageView imageView;

    String mCurrentPicPath;

    public final static int REQUEST_CODE_TAKE_PHOTO = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//    TextView tv = (TextView) findViewById(R.id.sample_text);
//    tv.setText(stringFromJNI());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermission(new String[]{Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE, Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_FINE_LOCATION, Permission.CAMERA});
    }

    private void requestPermission(String[]... permissions) {
        AndPermission.with(this)
                .permission(permissions)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(getApplicationContext(), "获取权限成功!", Toast.LENGTH_LONG).show();
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(getApplicationContext(), permissions)) {
                            Toast.makeText(getApplicationContext(), "获取权限失败,请重新设置!", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .start();

    }

    private void takePhoto() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(FileUtil.getPreferredDir("images"),
                    String.format("%d.jpg", System.currentTimeMillis()));
            mCurrentPicPath = file.getAbsolutePath();
            Uri uri = FileProvider.getUriForFile(this, "com.hlz.imagecompressordemo.hlz", file);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (mCurrentPicPath != null && mCurrentPicPath.length() != 0) {
                Glide.with(this).load(Uri.fromFile(new File(mCurrentPicPath))).into(imageView);
            }
        }
    }

    @OnClick({R.id.button, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                compress();
                break;
            case R.id.button2:
                takePhoto();
                break;
        }
    }

    private final static int SIZE = 150;
    private final static int WIDTH = 480;
    private final static int HEIGHT = 720;
    private final static int QUALITY = 90;

    private void compress() {
        compresshelperPic();
        compresshelperPic2WEBP();
        compressHelpPicByLuBan();
        compressHelpbyTiny();
    }

    //第一种压缩方法
    private void compresshelperPic() {
        File backFile = new File(mCurrentPicPath + "-compresshelper");

        File file = new CompressHelper.Builder(this).setMaxHeight(HEIGHT).setMaxWidth(WIDTH).setQuality(QUALITY).setMaxSize(SIZE)
                .setCompressFormat(Bitmap.CompressFormat.JPEG).setFileName(backFile.getName()).setDestinationDirectoryPath(FileUtil.getPreferredDir("images")).build().compressToFile(new File(mCurrentPicPath));
        if (!file.exists()) {
            ToastUtil.showShortText("miss");
        }
    }

    //第二种压缩方法
    private void compresshelperPic2WEBP() {
        File backFile = new File(mCurrentPicPath + "-compresshelper");
        File file = new CompressHelper.Builder(this).setMaxHeight(HEIGHT).setMaxWidth(WIDTH).setQuality(QUALITY).setMaxSize(SIZE)
                .setCompressFormat(Bitmap.CompressFormat.WEBP).setFileName(backFile.getName()).setDestinationDirectoryPath(FileUtil.getPreferredDir("images")).build().compressToFile(new File(mCurrentPicPath));
//        if (!file.exists()) {
//            ToastUtil.showShortText("miss");
//        }
    }

    private void compressHelpPicByLuBan() {
        final File backFile = new File(mCurrentPicPath + "-luban.jpeg");
        Luban.with(this)
                .load(new File(mCurrentPicPath))
                .setTargetDir(FileUtil.getPreferredDir("images"))
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        Log.i("Luban", file.getAbsolutePath());
                        try {
                            copyFileUsingFileStreams(file, backFile);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showShortText(e.getMessage());
                        Log.i("Luban", e.toString());
                    }
                }).launch();    //启动压缩
    }

    private void compressHelpbyTiny() {
        final File backFile = new File(mCurrentPicPath + "-Tiny.jpeg");
        Tiny.getInstance().init(this.getApplication());
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        options.height = HEIGHT;
        options.width = WIDTH;
        options.size = SIZE;
        options.quality=QUALITY;
        options.outfile = backFile.getAbsolutePath();
        Tiny.getInstance().source(mCurrentPicPath).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                ToastUtil.showShortText(isSuccess + "");
            }
        });

//        FileResult result = Tiny.getInstance().source("").asFile().withOptions(options).compressSync();
    }


    private static void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input =
                null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }
//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    public native String stringFromJNI();
//
//    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }
}
