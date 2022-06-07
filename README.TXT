#####引入方法####

/******初始化******/
在宿主应用的application中加入如下代码:
public class MyApp extends Application {
++    private static InmoAppKit appKit;
    @Override
    public void onCreate() {
        super.onCreate();
++        appKit = new InmoAppKit();
++        appKit.init(this);
    }

++    public static InmoAppKit getAppKit() {
++        return appKit;
++    }
}
/******初始化******/

/******启动组件*****/
在需要启动的activity的oncreate中加入如下代码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ...
++        MyApp.getAppKit().startAppKit();

    }
/******启动组件*****/

/******停止组件*****/
在需要停止组件的地方加入如下代码:

++       MyApp.getAppKit().stopAppKit();

/******停止组件*****/

/******app需要配置的地方*****/
1.AndroidManifest.xml中加入android:sharedUserId="android.uid.system"并具备系统签名

2.AndroidManifest.xml中加入两个权限
    <uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS"
        tools:ignore="ProtectedPermissions" />
    <uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE"
        tools:ignore="ProtectedPermissions" />
/******app需要配置的地方*****/