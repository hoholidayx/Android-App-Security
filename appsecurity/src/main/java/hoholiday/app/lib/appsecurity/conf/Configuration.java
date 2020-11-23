package hoholiday.app.lib.appsecurity.conf;

import hoholiday.app.lib.appsecurity.BuildConfig;

public class Configuration {

    public static final String LOG_TAG = "APP_SEC";

    private static boolean logEnable = BuildConfig.DEBUG;

    public static boolean isLogEnable() {
        return logEnable;
    }

    public static String getProtectedAppId() {
        return BuildConfig.PROTECTED_APPID;
    }
}
