package hoholiday.app.lib.appsecurity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import hoholiday.app.lib.appsecurity.conf.Configuration;

public class AppSecurityManager {

    public static final int ACCESSIBILITY_SERVICE_CHECK_UNIT = 0;

    public static final int EMULATOR_CHECK_UNIT = 1;

    static {
        System.loadLibrary("app-security");
    }

    private static AppSecurityManager instance;


    private AppSecurityManager() {
        JNI.nativeInit();
    }

    public static AppSecurityManager getInstance() {
        if (instance == null) {
            instance = new AppSecurityManager();
        }
        return instance;
    }

    public double unitCheck(Context context, int unit) {
        ICheckUnit checkUnit = null;
        switch (unit) {
            case ACCESSIBILITY_SERVICE_CHECK_UNIT:
                checkUnit = new AccessibilityServiceCheckUnit();
                break;
            case EMULATOR_CHECK_UNIT:
                checkUnit = new EmulatorCheckUnit();
                break;
            default:
                throw new IllegalArgumentException("check unit not found!");
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, String.format("Begin checking unit [%s] ... ", checkUnit));
        }
        double suspiciousDegree = checkUnit.check(context);
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, String.format("End checking unit [%s]! Result = [%.2f] ", checkUnit, suspiciousDegree));
        }
        return suspiciousDegree;
    }

    public void init(Context context) {
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "App security manager init...");
        }
        List<Integer> checkUnitList = Arrays.asList(
                ACCESSIBILITY_SERVICE_CHECK_UNIT,
                EMULATOR_CHECK_UNIT
        );
        final Context appContext = context.getApplicationContext();
        AsyncTask.execute(() -> {
            double suspiciousDegree = 0.;
            for (Integer checkUnit : checkUnitList) {
                double checkRet = unitCheck(appContext, checkUnit);
                // TODO: 2020/11/19 加权
                suspiciousDegree += checkRet;
            }
            if (Configuration.isLogEnable()) {
                Log.i(Configuration.LOG_TAG, String.format("Init check done! Suspicious degree is = [%.2f] ", suspiciousDegree));
            }
        });
    }

}
