package hoholiday.app.lib.appsecurity.utils;

public class SystemUtils {

    public static String getSystemProperty(String propName) {
        String value = null;
        try {
            Object roSecureObj = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class)
                    .invoke(null, propName);
            if (roSecureObj != null) {
                value = (String) roSecureObj;
            }
        } catch (Exception e) {
            value = null;
        }
        return value;
    }

}
