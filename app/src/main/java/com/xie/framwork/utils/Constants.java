package com.xie.framwork.utils;

/**
 * Title:
 * Description:
 *
 * @author xie
 * @create 2019/3/16
 * @update 2019/3/16
 */
public interface Constants {

    /**
     * t通知
     */
    String NOTIFICATION_CHANNELID = "channel";
    int PAGESIZE = 10;
    String PREF_FILE_NAME = "ca_prefs";

    /**
     * 认证相关
     */
    String TOKEN_AUTH_URL = "/oauth/token";
    String TOKEN_SCOPE = "select";
    String TOKEN_GRANT_TYPE = "password";
    String TOKEN_REFRESH_GRANT_TYPE = "refresh_token";
    String TOKEN_CLIENT_ID = "Android";
    String TOKEN_CLIENT_SECRET = "112233";


    String ROLE_BOSS = "1";
    String ROLE_SECRET = "2";

    String KEY_AUTH_INFO = "authInfo";
    String DATE_TIME_FORMAT = "MM/dd HH:mm";
}
