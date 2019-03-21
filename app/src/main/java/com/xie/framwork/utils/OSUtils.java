package com.xie.framwork.utils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Title: 系统判断
 * Description: 获取手机系统类型（小米MIUI、华为EMUI、魅族FLYME）
 *
 * @author xie
 * @create 2018/3/21
 * @update 2018/3/21
 */
public class OSUtils {

    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

    //EMUI标识
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";

    //Flyme标识
    private static final String KEY_FLYME_ID_FLAG_KEY = "ro.build.display.id";
    private static final String KEY_FLYME_ID_FLAG_VALUE_KEYWORD = "Flyme";
    private static final String KEY_FLYME_ICON_FLAG = "persist.sys.use.flyme.icon";
    private static final String KEY_FLYME_SETUP_FLAG = "ro.meizu.setupwizard.flyme";
    private static final String KEY_FLYME_PUBLISH_FLAG = "ro.flyme.published";

    /**
     * @param
     * @return ROM_TYPE ROM类型的枚举
     * @description获取ROM类型: MIUI_ROM, FLYME_ROM, EMUI_ROM, OTHER_ROM
     */

    public static ROM_TYPE getRomType() {
        ROM_TYPE rom_type = ROM_TYPE.OTHER;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            if (!TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_CODE, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_VERSION_NAME, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_MIUI_INTERNAL_STORAGE, ""))) {
                return ROM_TYPE.MIUI;
            } else if (!TextUtils.isEmpty(getSystemProperty(KEY_EMUI_API_LEVEL, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_EMUI_VERSION_CODE, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_EMUI_CONFIG_HW_SYS_VERSION, ""))) {
                return ROM_TYPE.EMUI;
            } else if (!TextUtils.isEmpty(getSystemProperty(KEY_FLYME_ICON_FLAG, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_FLYME_SETUP_FLAG, ""))
                    || !TextUtils.isEmpty(getSystemProperty(KEY_FLYME_PUBLISH_FLAG, ""))) {
                return ROM_TYPE.FLYME;
            }
            return rom_type;
        } else {
            try {
                Properties buildProperties = new Properties();
                buildProperties.load(new FileInputStream(new File(Environment.getRootDirectory(), "build.prop")));

                if (buildProperties.containsKey(KEY_EMUI_VERSION_CODE) || buildProperties.containsKey(KEY_EMUI_API_LEVEL) || buildProperties.containsKey(KEY_EMUI_CONFIG_HW_SYS_VERSION)) {
                    return ROM_TYPE.EMUI;
                }
                if (buildProperties.containsKey(KEY_MIUI_VERSION_CODE) || buildProperties.containsKey(KEY_MIUI_VERSION_NAME) || buildProperties.containsKey(KEY_MIUI_INTERNAL_STORAGE)) {
                    return ROM_TYPE.MIUI;
                }
                if (buildProperties.containsKey(KEY_FLYME_ICON_FLAG) || buildProperties.containsKey(KEY_FLYME_SETUP_FLAG) || buildProperties.containsKey(KEY_FLYME_PUBLISH_FLAG)) {
                    return ROM_TYPE.FLYME;
                }
                if (buildProperties.containsKey(KEY_FLYME_ID_FLAG_KEY)) {
                    String romName = buildProperties.getProperty(KEY_FLYME_ID_FLAG_KEY);
                    if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FLAG_VALUE_KEYWORD)) {
                        return ROM_TYPE.FLYME;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return rom_type;
    }

    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (Exception e) {
        }
        return defaultValue;
    }

    public enum ROM_TYPE {
        MIUI,
        FLYME,
        EMUI,
        OTHER
    }
}
