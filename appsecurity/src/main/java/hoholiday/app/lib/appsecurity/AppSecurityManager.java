package hoholiday.app.lib.appsecurity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import hoholiday.app.lib.appsecurity.checkunit.CheckUnitFactory;
import hoholiday.app.lib.appsecurity.checkunit.CheckUnitName;
import hoholiday.app.lib.appsecurity.checkunit.ICheckUnit;
import hoholiday.app.lib.appsecurity.conf.Configuration;
import hoholiday.app.lib.appsecurity.exception.AppSecurityException;

public class AppSecurityManager {

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

    public double unitCheck(Context context, @CheckUnitName String name) {
        ICheckUnit checkUnit = CheckUnitFactory.getCheckUnit(name);
        if (checkUnit == null) {
            return 0;
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

    public void init(Context context) throws AppSecurityException {
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "App security manager init...");
        }
        List<String> checkUnitList = Arrays.asList(
                CheckUnitName.ACCESSIBILITY_SERVICE_CHECK_UNIT,
                CheckUnitName.EMULATOR_CHECK_UNIT
        );
        final Context appContext = context.getApplicationContext();
        AsyncTask.execute(() -> {
            double suspiciousDegree = 0.;
            for (String checkUnit : checkUnitList) {
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
