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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hlz.imagecompressordemo.Util.FileUtil;
import com.nanchen.compresshelper.CompressHelper;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    private final static int SIZE = 50;
    private final static int WIDTH = 480;
    private final static int HEIGHT = 720;
    private final static int QUALITY = 80;

    private void compress() {
        compresshelperPic();
        compresshelperPic2WEBP();
        Toast.makeText(this.getApplicationContext(), "成功", Toast.LENGTH_SHORT).show();
    }

    //第一种压缩方法
    private void compresshelperPic() {
        File file = new CompressHelper.Builder(this).setMaxHeight(HEIGHT).setMaxWidth(WIDTH).setQuality(QUALITY).setMaxSize(SIZE)
                .setCompressFormat(Bitmap.CompressFormat.JPEG).setFileName(mCurrentPicPath + "-compresshelper").setDestinationDirectoryPath(FileUtil.getPreferredDir("images")).build().compressToFile(new File(mCurrentPicPath));
    }

    //第二种压缩方法
    private void compresshelperPic2WEBP() {
        File file = new CompressHelper.Builder(this).setMaxHeight(HEIGHT).setMaxWidth(WIDTH).setQuality(QUALITY).setMaxSize(SIZE)
                .setCompressFormat(Bitmap.CompressFormat.WEBP).setFileName(mCurrentPicPath + "-compresshelperWebp").setDestinationDirectoryPath(FileUtil.getPreferredDir("images")).build().compressToFile(new File(mCurrentPicPath));
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
