package hoholiday.app.lib.appsecurity.checkunit;

import android.content.Context;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

public class XposedCheckUnit implements ICheckUnit {

    private static final String XPOSED_BRIDGE = "de.robv.android.xposed.XposedBridge";

    @Override
    public double check(@NonNull Context context) {
        return isXposedExistByThrow() || tryShutdownXposed() ? 1 : 0;
    }

    /**
     * 通过主动抛出异常，检查堆栈信息来判断是否存在XP框架
     *
     * @return
     */
    public boolean isXposedExistByThrow() {
        try {
            throw new RuntimeException("check xposed");
        } catch (RuntimeException e) {
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                if (stackTraceElement.getClassName().contains(XPOSED_BRIDGE)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 尝试关闭XP框架
     * 先通过isXposedExistByThrow判断有没有XP框架
     * 有的话先hookXP框架的全局变量disableHooks
     *
     * @return 是否关闭成功的结果
     */
    public boolean tryShutdownXposed() {
        Field fieldDisabledHooks = null;
        try {
            fieldDisabledHooks = ClassLoader.getSystemClassLoader()
                    .loadClass(XPOSED_BRIDGE)
                    .getDeclaredField("disableHooks");
            fieldDisabledHooks.setAccessible(true);
            fieldDisabledHooks.set(null, Boolean.TRUE);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "xposed";
    }
}
