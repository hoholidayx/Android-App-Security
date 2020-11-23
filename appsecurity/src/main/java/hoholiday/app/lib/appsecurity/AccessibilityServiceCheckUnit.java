package hoholiday.app.lib.appsecurity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.view.accessibility.AccessibilityManager;

import androidx.annotation.NonNull;

import java.util.List;

import hoholiday.app.lib.appsecurity.conf.Configuration;

/**
 * 检查辅助功能列表是否有注册service。
 * <p>
 * 一般在沙箱运行是无法注册辅助功能service。
 */
class AccessibilityServiceCheckUnit implements ICheckUnit {

    @Override
    public double check(@NonNull Context context) {
        final String protectedAppId = Configuration.getProtectedAppId();
        boolean hasService = false;
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        //查找所有已安装的辅助功能service
        List<AccessibilityServiceInfo> installedAccessibilityServiceList = am.getInstalledAccessibilityServiceList();
        for (AccessibilityServiceInfo accessibilityServiceInfo : installedAccessibilityServiceList) {
            ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
            if (serviceInfo.packageName.equals(protectedAppId))
                hasService = true;
        }

        return hasService ? 0 : 0.9;
    }

    @NonNull
    @Override
    public String toString() {
        return "AcesSvc";
    }
}
