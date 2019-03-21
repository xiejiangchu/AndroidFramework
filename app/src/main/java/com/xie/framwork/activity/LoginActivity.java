package com.xie.framwork.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xie.framwork.R;
import com.xie.framwork.bean.AccessTokenDto;
import com.xie.framwork.request.ApiService;
import com.xie.framwork.utils.Constants;
import com.xie.framwork.utils.OkHttpUtils;
import com.xie.framwork.utils.RetrofitHelper;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;
    private EditText username;
    private EditText password;
    private Button login;
    private ApiService apiService;

    private boolean isHidePwd = true;
    protected boolean useThemestatusBarColor = false;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBar();
        initEyeIcon();
        apiService = RetrofitHelper.get(ApiService.class);
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

    private void initEyeIcon() {
        final Drawable[] drawables = password.getCompoundDrawables();
        final int eyeWidth = drawables[2].getBounds().width();// 眼睛图标的宽度
        final Drawable drawableEyeOpen = getResources().getDrawable(R.drawable.login_icon_click_xsmm, this.getTheme());
        final Drawable drawableEyeClose = getResources().getDrawable(R.drawable.login_icon_click_ycmm, this.getTheme());
        drawableEyeOpen.setBounds(drawables[2].getBounds());
        drawableEyeClose.setBounds(drawables[2].getBounds());
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float et_pwdMinX = v.getWidth() - eyeWidth - password.getPaddingRight();
                    float et_pwdMaxX = v.getWidth();
                    float et_pwdMinY = 0;
                    float et_pwdMaxY = v.getHeight();
                    float x = event.getX();
                    float y = event.getY();
                    if (x < et_pwdMaxX && x > et_pwdMinX && y > et_pwdMinY && y < et_pwdMaxY) {
                        isHidePwd = !isHidePwd;
                        if (isHidePwd) {
                            password.setCompoundDrawables(drawables[0], drawables[1], drawableEyeClose, drawables[3]);
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        } else {
                            password.setCompoundDrawables(drawables[0], drawables[1], drawableEyeOpen, drawables[3]);
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                String name = username.getText().toString();
                String pass = password.getText().toString();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pass)) {
                    Toasty.warning(this, "用户名或密码不能为空").show();
                } else {
                    closeKeyboard();
                    apiService.getAccessToken(name, pass, Constants.TOKEN_GRANT_TYPE, Constants.TOKEN_SCOPE, Constants.TOKEN_CLIENT_ID, Constants.TOKEN_CLIENT_SECRET).enqueue(new Callback<String>() {

                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.code() == 401) {
                                Toasty.error(LoginActivity.this, "用户名或者密码错误！").show();
                            } else if (response.code() == 426) {
                                Toasty.error(LoginActivity.this, "密码错误！").show();
                            } else if (response.code() == 200) {
                                try {
                                    AccessTokenDto accessTokenDto = new Gson().fromJson(response.body(), AccessTokenDto.class);
                                    if (accessTokenDto != null) {
                                        OkHttpUtils.setToken(accessTokenDto.getAccess_token(), accessTokenDto.getRefresh_token());
                                        Toasty.info(LoginActivity.this, "登录成功!").show();
                                        if (accessTokenDto.getRole().equals(Constants.ROLE_BOSS)) {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            LoginActivity.this.startActivity(intent);
                                            LoginActivity.this.finish();
                                        }
                                    } else {
                                        Toasty.error(LoginActivity.this, "服务器返回数据错误!").show();
                                    }
                                } catch (JsonSyntaxException json) {
                                    Toasty.error(LoginActivity.this, "登录异常").show();
                                }
                            } else {
                                String errMsg = null;
                                try {
                                    errMsg = response.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (!TextUtils.isEmpty(errMsg)) {
                                    Toasty.warning(LoginActivity.this, errMsg).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toasty.warning(LoginActivity.this, t.getMessage()).show();
                        }
                    });
                }
                break;
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

