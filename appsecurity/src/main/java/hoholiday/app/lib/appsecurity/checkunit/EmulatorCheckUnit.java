package hoholiday.app.lib.appsecurity.checkunit;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import hoholiday.app.lib.appsecurity.conf.Configuration;
import hoholiday.app.lib.appsecurity.utils.SystemUtils;

/**
 * 模拟器检测
 */
class EmulatorCheckUnit implements ICheckUnit {

    private static final int CHECK_RESULT_FAILED = -1;
    private static final int CHECK_RESULT_NORMAL = 0;
    private static final int CHECK_RESULT_EMULATOR = 1;

    @Override
    public double check(@NonNull Context context) {

        int suspectCount = 0;
        int total = 0;

        //检查硬件名称
        int checkHardwareResult = checkHardware();
        total++;
        if (checkHardwareResult == CHECK_RESULT_EMULATOR) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "hardware=" + checkHardwareResult);
        }

        //检查设备渠道
        int checkFlavorResult = checkFlavor();
        total++;
        if (checkFlavorResult == CHECK_RESULT_EMULATOR) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "flavor=" + checkFlavorResult);
        }

        //检查制造商
        int checkManufacturerResult = checkManufacturer();
        total++;
        if (checkManufacturerResult == CHECK_RESULT_EMULATOR) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "manufacture=" + checkManufacturerResult);
        }

        //检查设备型号
        int checkModelResult = checkModel();
        total++;
        if (checkModelResult == CHECK_RESULT_EMULATOR) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "model=" + checkModelResult);
        }

        //检查主板名称
        int checkBoardResult = checkBoard();
        total++;
        if (checkBoardResult == CHECK_RESULT_EMULATOR) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "board=" + checkBoardResult);
        }

        //检查平台
        int checkPlatformResult = checkPlatform();
        total++;
        if (checkPlatformResult == CHECK_RESULT_EMULATOR) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "platform=" + checkPlatformResult);
        }

        //检查基带
        int checkBaseBandResult = checkBaseBand();
        total++;
        if (checkBaseBandResult == CHECK_RESULT_EMULATOR) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "base band=" + checkBaseBandResult);
        }

        //检查是否支持摄像头
        boolean supportCamera = supportCamera(context);
        total++;
        if (!supportCamera) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "camera=" + supportCamera);
        }

        //检查是否支持闪光灯
        boolean supportCameraFlash = supportCameraFlash(context);
        total++;
        if (!supportCameraFlash) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "camera flash=" + supportCameraFlash);
        }

        //检查是否支持蓝牙
        boolean supportBluetooth = supportBluetooth(context);
        total++;
        if (!supportBluetooth) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "bluetooth=" + supportBluetooth);
        }

        //检查是否支持光线传感器
        boolean supportLightSensor = supportLightSensor(context);
        total++;
        if (!supportLightSensor) {
            suspectCount++;
        }
        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, "light sensor=" + supportLightSensor);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            //检查传感器数量
            int sensorNum = getSensorNumber(context);
            total++;
            if (sensorNum <= 10) {
                //传感器过少可能有问题
                suspectCount++;
            }
            if (Configuration.isLogEnable()) {
                Log.i(Configuration.LOG_TAG, "sensor num=" + sensorNum);
            }
        }

        if (Configuration.isLogEnable()) {
            Log.i(Configuration.LOG_TAG, String.format("Emulator check done. Total=[%d],suspect=[%d]", total, suspectCount));
        }

        return 1. * suspectCount / total;
    }

    @NonNull
    @Override
    public String toString() {
        return "emulator";
    }


    /**
     * 特征参数-硬件名称
     *
     * @return {@link #CHECK_RESULT_FAILED:失败，{@link #CHECK_RESULT_NORMAL：真机，{@link #CHECK_RESULT_EMULATOR}：模拟器
     */
    private int checkHardware() {
        String hardware = SystemUtils.getSystemProperty("ro.hardware");
        if (TextUtils.isEmpty(hardware)) {
            return CHECK_RESULT_FAILED;
        }
        int result = CHECK_RESULT_NORMAL;
        String tempValue = hardware.toLowerCase();
        switch (tempValue) {
            case "ttvm"://天天模拟器
            case "nox"://夜神模拟器
            case "cancro"://网易MUMU模拟器
            case "intel"://逍遥模拟器
            case "vbox":
            case "vbox86"://腾讯手游助手
            case "android_x86"://雷电模拟器
                result = CHECK_RESULT_EMULATOR;
                break;
        }
        return result;
    }

    /**
     * 特征参数-渠道
     *
     * @return {@link #CHECK_RESULT_FAILED:失败，{@link #CHECK_RESULT_NORMAL：真机，{@link #CHECK_RESULT_EMULATOR}：模拟器
     */
    private int checkFlavor() {
        String flavor = SystemUtils.getSystemProperty("ro.build.flavor");
        if (TextUtils.isEmpty(flavor)) {
            return CHECK_RESULT_FAILED;
        }
        int result = CHECK_RESULT_NORMAL;
        String flavorLowercase = flavor.toLowerCase();
        if (flavorLowercase.contains("vbox") ||
                flavorLowercase.contains("sdk_gphone")) {
            result = CHECK_RESULT_EMULATOR;
        }
        return result;
    }

    /**
     * 特征参数-硬件制造商
     *
     * @return {@link #CHECK_RESULT_FAILED:失败，{@link #CHECK_RESULT_NORMAL：真机，{@link #CHECK_RESULT_EMULATOR}：模拟器
     */
    private int checkManufacturer() {
        String manufacturer = SystemUtils.getSystemProperty("ro.product.manufacturer");
        if (TextUtils.isEmpty(manufacturer)) {
            return CHECK_RESULT_FAILED;
        }
        int result = CHECK_RESULT_NORMAL;
        String manufacturerLowercase = manufacturer.toLowerCase();
        if (manufacturerLowercase.contains("genymotion") ||
                manufacturerLowercase.contains("netease") /*网易MUMU模拟器*/) {
            result = CHECK_RESULT_EMULATOR;
        }
        return result;
    }

    /**
     * 特征参数-设备型号
     *
     * @return {@link #CHECK_RESULT_FAILED:失败，{@link #CHECK_RESULT_NORMAL：真机，{@link #CHECK_RESULT_EMULATOR}：模拟器
     */
    private int checkModel() {
        String model = SystemUtils.getSystemProperty("ro.product.model");
        if (TextUtils.isEmpty(model)) {
            return CHECK_RESULT_FAILED;
        }
        int result = CHECK_RESULT_NORMAL;
        String modelLowercase = model.toLowerCase();
        if (modelLowercase.contains("emulator") ||
                modelLowercase.contains("google_sdk") ||
                modelLowercase.contains("android sdk built for x86")) {
            result = CHECK_RESULT_EMULATOR;
        }
        return result;
    }

    /**
     * 特征参数-主板名称
     *
     * @return {@link #CHECK_RESULT_FAILED:失败，{@link #CHECK_RESULT_NORMAL：真机，{@link #CHECK_RESULT_EMULATOR}：模拟器
     */
    private int checkBoard() {
        String board = SystemUtils.getSystemProperty("ro.product.board");
        if (TextUtils.isEmpty(board)) {
            return CHECK_RESULT_FAILED;
        }
        int result = CHECK_RESULT_NORMAL;
        String boardLowercase = board.toLowerCase();
        if (boardLowercase.contains("android") ||
                boardLowercase.contains("goldfish")) {
            result = CHECK_RESULT_EMULATOR;
        }
        return result;
    }

    /**
     * 特征参数-主板平台
     *
     * @return {@link #CHECK_RESULT_FAILED:失败，{@link #CHECK_RESULT_NORMAL：真机，{@link #CHECK_RESULT_EMULATOR}：模拟器
     */
    private int checkPlatform() {
        String platform = SystemUtils.getSystemProperty("ro.board.platform");
        if (TextUtils.isEmpty(platform)) {
            return CHECK_RESULT_FAILED;
        }
        int result = CHECK_RESULT_NORMAL;
        String platformLowercase = platform.toLowerCase();
        if (platformLowercase.contains("android")) {
            result = CHECK_RESULT_EMULATOR;
        }
        return result;
    }

    /**
     * 特征参数-基带信息
     *
     * @return {@link #CHECK_RESULT_FAILED:失败，{@link #CHECK_RESULT_NORMAL：真机，{@link #CHECK_RESULT_EMULATOR}：模拟器
     */
    private int checkBaseBand() {
        String baseBandVersion = SystemUtils.getSystemProperty("gsm.version.baseband");
        int result = CHECK_RESULT_NORMAL;
        String baseBandLowercase = baseBandVersion.toLowerCase();
        if (baseBandLowercase.contains("1.0.0.0")) {
            result = CHECK_RESULT_EMULATOR;
        }
        return result;
    }

    /**
     * 获取传感器数量
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private int getSensorNumber(Context context) {
        SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sm.getSensorList(Sensor.TYPE_ALL).size();
    }

    /**
     * 是否支持相机
     */
    private boolean supportCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 是否支持闪光灯
     */
    private boolean supportCameraFlash(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    /**
     * 是否支持蓝牙
     */
    private boolean supportBluetooth(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    /**
     * 判断是否存在光传感器
     */
    private boolean supportLightSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //光线传感器
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        return sensor != null;
    }

}
