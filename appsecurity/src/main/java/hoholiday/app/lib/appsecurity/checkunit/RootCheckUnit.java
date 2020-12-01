package hoholiday.app.lib.appsecurity.checkunit;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.File;

import hoholiday.app.lib.appsecurity.utils.SystemUtils;

class RootCheckUnit implements ICheckUnit {

    @Override
    public double check(@NonNull Context context) {
        return isRoot() ? 1 : 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "root";
    }

    /**
     * 检查root权限
     *
     * @return
     */
    private boolean isRoot() {
        //eng/userdebug版本，自带root权限
        if (hasRoSecureProp()) {
            return true;
        } else {
            //user版本，继续查su文件
            return isSUExist();
        }
    }

    private boolean hasRoSecureProp() {
        boolean secureProp;
        String roSecureObj = SystemUtils.getSystemProperty("ro.secure");
        if (roSecureObj == null) {
            secureProp = false;
        } else {
            secureProp = "0".equals(roSecureObj);
        }
        return secureProp;
    }

    private boolean isSUExist() {
        String[] paths = {"/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }
}
