package hoholiday.app.lib.appsecurity.checkunit;

import android.util.Log;

import androidx.annotation.Nullable;

import hoholiday.app.lib.appsecurity.conf.Configuration;

public class CheckUnitFactory {

    @Nullable
    public static ICheckUnit getCheckUnit(@CheckUnitName String name) {
        ICheckUnit checkUnit = null;
        switch (name) {
            case CheckUnitName.ACCESSIBILITY_SERVICE:
                checkUnit = new AccessibilityServiceCheckUnit();
                break;
            case CheckUnitName.EMULATOR:
                checkUnit = new EmulatorCheckUnit();
                break;
            case CheckUnitName.ROOT:
                checkUnit = new RootCheckUnit();
                break;
            case CheckUnitName.APP_DEBUG:
                checkUnit = new AppDebugCheckUnit();
                break;
            case CheckUnitName.XPOSED:
                checkUnit = new XposedCheckUnit();
                break;
            default:
                if (Configuration.isLogEnable()) {
                    Log.e(Configuration.LOG_TAG, String.format("check [%s] unit not found!", name));
                }
        }
        return checkUnit;
    }

}
