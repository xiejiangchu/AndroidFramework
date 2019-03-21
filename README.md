# Android基本框架

- okhttp3
- gson
- retrofit-lifecycle
- retrofit2
- FlycoTabLayout_Lib
- glide
- bugly
- BaseRecyclerViewAdapterHelper
- SmartRefreshHeader
- xxpermissions
- leakcanary
- Toasty
- circleimageview
- badgeview

## 统一认证（oauth 2）
oauth2根据使用场景不同，分成了4种模式

* 授权码模式（authorization code）
* 简化模式（implicit）
* 密码模式（resource owner password credentials）
* 客户端模式（client credentials）

### 获取token
服务器有以下endpoints：

	{[/oauth/authorize]}
	{[/oauth/authorize],methods=[POST]
	{[/oauth/token],methods=[GET POST]}
	{[/oauth/check_token]}
	{[/oauth/error]}
	
```
http://127.0.0.1/oauth/token?username=user&password=123456&grant_type=password&scope=select&client_id=Android&client_secret=ebscn
```
其中 client_id 分 `Android`和`iOS`,用户名、密码为用户输入，其他为常量
返回
```json
{
    "access_token": "c7856e5b-60d1-4607-b619-9bc0c1464d03",
    "token_type": "bearer",
    "refresh_token": "3392482d-7246-4837-892a-1160838296f5",
    "expires_in": 3599,
    "scope": "select",
    "license": "EBSCN GROUP"
}
```

### 刷新access_token
使用上述接口返回的refresh_token进行刷新
```http
http://127.0.0.1/oauth/token?grant_type=refresh_token&scope=select&client_id=Android&client_secret=ebscn&refresh_token=51d5c79d-78d3-4268-96e7-9c69c9339666
```
```json
{
    "access_token": "1cf775a5-380c-4a04-8558-a4620ae2a8aa",
    "token_type": "bearer",
    "refresh_token": "759d988f-69f7-495e-bb5a-bd29df9ff01d",
    "expires_in": 3599,
    "scope": "select",
    "license": "com.ebscn.call.license"
}
```

#### Q: 我该如何刷新我的token？
A：在access_token里加入refresh_token标识，给access_token设置短时间的期限（例如一天），给refresh_token设置长时间的期限（例如七天）。当活动用户（拥有access_token）发起request时，在权限验证里，对于requeset的header包含的access_token、refresh_token分别进行验证：
  1、access_token没过期，即通过权限验证；
  2、access_token过期,refresh_token没过期，则返回权限验证失败，并在返回的response的header中加入标识状态的key，在request方法的catch中通过webException来获取标识的key，获取新的token（包含新的access_token和refresh_token），再次发起请求，并返回给客户端请求结果以及新的token，再在客户端更新公共静态token模型；
  3、access_token过期,refresh_token过期即权限验证失败。


### 访问API，有两种方案
+ 拼接在URL中
```http
http://localhost:8080/qq/info/250577914?access_token=9f54d26f-5545-4eba-a124-54e6355dbe69
```
+ 放置在Header中，注意Bearer后面有空格
```
Authorization:Bearer 1cf775a5-380c-4a04-8558-a4620ae2a8ab
```