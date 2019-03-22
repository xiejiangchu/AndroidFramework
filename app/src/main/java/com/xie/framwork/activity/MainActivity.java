package com.xie.framwork.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.xie.framwork.R;
import com.xie.framwork.request.ApiService;
import com.xie.framwork.utils.RetrofitHelper;

import es.dmoral.toasty.Toasty;

/**
 * A login screen that offers login via email/password.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ApiService apiService;
    protected boolean useThemestatusBarColor = false;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBar();
        apiService = RetrofitHelper.get(ApiService.class);
        findViewById(R.id.btn_scan).setOnClickListener(this);
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

//    private void initEyeIcon() {
//        final Drawable[] drawables = password.getCompoundDrawables();
//        final int eyeWidth = drawables[2].getBounds().width();// 眼睛图标的宽度
//        final Drawable drawableEyeOpen = getResources().getDrawable(R.drawable.login_icon_click_xsmm, this.getTheme());
//        final Drawable drawableEyeClose = getResources().getDrawable(R.drawable.login_icon_click_ycmm, this.getTheme());
//        drawableEyeOpen.setBounds(drawables[2].getBounds());
//        drawableEyeClose.setBounds(drawables[2].getBounds());
//        password.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    float et_pwdMinX = v.getWidth() - eyeWidth - password.getPaddingRight();
//                    float et_pwdMaxX = v.getWidth();
//                    float et_pwdMinY = 0;
//                    float et_pwdMaxY = v.getHeight();
//                    float x = event.getX();
//                    float y = event.getY();
//                    if (x < et_pwdMaxX && x > et_pwdMinX && y > et_pwdMinY && y < et_pwdMaxY) {
//                        isHidePwd = !isHidePwd;
//                        if (isHidePwd) {
//                            password.setCompoundDrawables(drawables[0], drawables[1], drawableEyeClose, drawables[3]);
//                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                        } else {
//                            password.setCompoundDrawables(drawables[0], drawables[1], drawableEyeOpen, drawables[3]);
//                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                        }
//                    }
//                }
//                return false;
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                IntentIntegrator integrator = null;
                Activity activity = MainActivity.this;

                integrator = new IntentIntegrator(activity);

                if (integrator != null) {
                    integrator.setCaptureActivity(ScanCaptureActivity.class);
                    integrator.initiateScan();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IntentIntegrator.REQUEST_CODE) {
                // 扫描二维码回传值
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                String ewmString = result.getContents();
                Toasty.info(this, ewmString).show();
            }
        }
    }

    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(apiService);
    }
}

