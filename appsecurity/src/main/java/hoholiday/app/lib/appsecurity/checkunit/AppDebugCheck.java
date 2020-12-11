package hoholiday.app.lib.appsecurity.checkunit;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;

/**
 * 检查是否处于Debug模式
 */
class AppDebugCheck implements ICheckUnit {

    @Override
    public double check(@NonNull Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) > 1 ? 1 : 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "app-debug";
    }
}
