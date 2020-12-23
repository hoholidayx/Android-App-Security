package hoholiday.app.lib.appsecurity.checkunit;

import android.content.Context;

import androidx.annotation.NonNull;

// TODO: 2020/12/23
class VirtualApkCheckUnit implements ICheckUnit {

    @Override
    public double check(@NonNull Context context) {
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "virtual-apk";
    }
}
