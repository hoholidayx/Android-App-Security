package hoholiday.app.lib.appsecurity.checkunit;

import android.content.Context;

import androidx.annotation.NonNull;

public interface ICheckUnit {

    /**
     * 进行单元检测
     *
     * @param context
     * @return [0.0 ,1.0],数值越高表示"非法app"的怀疑度越高
     */
    double check(@NonNull Context context);

}
