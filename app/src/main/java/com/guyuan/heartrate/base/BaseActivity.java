package com.guyuan.heartrate.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * created by tl
 * created at 2020/8/18
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 9527;
    private AlertDialog loadingDialog;
    protected FragmentManager fragmentManager;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        fragmentManager = getSupportFragmentManager();
        init(savedInstanceState);
    }


    protected View getRootView() {
        return ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    protected abstract int getLayoutID();

    protected abstract void init(Bundle savedInstanceState);


    /**
     * app字体不跟随系统设置改变而改变
     *
     * @param newConfig config
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
        {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    /**
     * 去掉手机状态栏
     */
    protected void setFullScreen() {
        getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    /**
     * 某些功能需要确保先已经获得用户授权才可以实现}
     * 然后才在这个回调中启动这些功能。
     */
    public void onAllPermissionsGranted() {

    }

    public void checkPermissions(String... permissions) {
        ArrayList<String> request = new ArrayList<>();
        for (String per : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int result = checkSelfPermission(per);
                if (PackageManager.PERMISSION_GRANTED != result) {
                    request.add(per);
                }
            }
        }
        if (!request.isEmpty()) {
            String[] list = new String[request.size()];
            for (int i = 0; i < request.size(); i++) {
                list[i] = request.get(i);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(list, REQUEST_CODE_PERMISSION);
            }
        } else {
            onAllPermissionsGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isGranted = true;
        final String[] rational = new String[grantResults.length];
        int index = 0;
        if (requestCode == REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissions[i])) {
                            rational[index++] = permissions[i];
                        } else {
                            showToastTip("权限被限制，请到APP设置中授权。");
                            onBackPressed();
                            return;
                        }
                    }
                }
            }
        }
        if (isGranted) {
            onAllPermissionsGranted();
        } else {
            if (index > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("请授权");
                StringBuilder sb = new StringBuilder();
                sb.append("为了使用此界面的功能，需要获得以下授权：\n");
                for (String temp : rational) {
                    sb.append(temp).append("\n");
                }
                sb.append("点击确定重新授权，点击取消退出此界面。");
                builder.setMessage(sb.toString());
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(rational, REQUEST_CODE_PERMISSION);
                                }
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                                onBackPressed();
                            }
                        });
                builder.setCancelable(false);
                alertDialog = builder.create();
                alertDialog.show();
            } else {
                showToastTip("权限被限制，请到APP设置中授权。");
                onBackPressed();
            }
        }
    }

    //当键盘弹出来的时候，点击屏幕空白处，将受收起键盘 by leo
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isToHide(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    private boolean isToHide(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText || v instanceof AppCompatEditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void showToastTip(int resId) {
        String txt = getString(resId);
        showToastTip(txt);
    }

    public void showToastTip(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(message);
        toast.show();
    }

    public void showSnackBarTip(String message) {
        Snackbar.make(getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnackBarTip(int resId) {
        Snackbar.make(getRootView(), resId, Snackbar.LENGTH_SHORT).show();
    }

    public void showLoading(FragmentManager manager) {
        hideLoading();
        //    loadingDialog = AlertDialogUtils.showLoading(this, null, null);
    }

    public void showLoadingWithStatus(FragmentManager manager, String status) {
        hideLoading();
        //    loadingDialog = AlertDialogUtils.showLoading(this, null, status);
    }

    public void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }
}
