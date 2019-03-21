package com.xie.framwork.bean;

/**
 * Title:
 * Description:
 *
 * @author xie
 * @create 2019/3/16
 * @update 2019/3/16
 */
public class AccessTokenDto {


    /**
     * access_token : 98cd8742-6886-4a0b-b2b1-0b2c57b3d86b
     * token_type : bearer
     * refresh_token : c5d0ac3f-86b5-4885-a9b5-97c78582cd7f
     * expires_in : 36205
     * scope : select
     * license : com.ebscn.call.license
     */

    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private String role;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}