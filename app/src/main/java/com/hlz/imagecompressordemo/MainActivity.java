package com.hlz.imagecompressordemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

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



    @OnClick({R.id.button, R.id.button2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button:
                break;
            case R.id.button2:
                break;
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
