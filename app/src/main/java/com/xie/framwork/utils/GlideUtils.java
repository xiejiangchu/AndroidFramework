package com.xie.framwork.utils;


import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.xie.framwork.R;

import java.io.InputStream;

/**
 * Title:  Glide 4.8配置
 * Description: Glide 4.8集成现有OkHttpClient并加载https图片
 *
 * @author xie
 * @create 2019/3/7
 * @update 2019/3/7
 */
@GlideModule
public class GlideUtils extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(OkHttpUtils.glideBuilder().build()));
    }

    public static RequestOptions getRequestOptions() {
        RequestOptions options = new RequestOptions()
//                .placeholder(R.drawable.default_img)//加载成功之前占位图
//                .error(R.drawable.default_img)//加载错误之后的错误图
                .fitCenter()//指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
//                .centerCrop()//指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
//                .circleCrop()//指定图片的缩放类型为centerCrop （圆形）
//                .skipMemoryCache(true)//跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存所有版本的图像
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//跳过磁盘缓存
//                .diskCacheStrategy(DiskCacheStrategy.DATA)//只缓存原来分辨率的图片
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)//只缓存最终的图片
                ;

        return options;
    }
}
