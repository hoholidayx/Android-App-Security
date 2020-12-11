package hoholiday.app.lib.appsecurity.checkunit;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
        CheckUnitName.ACCESSIBILITY_SERVICE,
        CheckUnitName.EMULATOR,
        CheckUnitName.ROOT,
        CheckUnitName.APP_DEBUG,
})
@Retention(RetentionPolicy.SOURCE)
public @interface CheckUnitName {

    String ACCESSIBILITY_SERVICE = "accessibility_service";

    String EMULATOR = "emulator";

    String ROOT = "root";

    String APP_DEBUG = "app-debug";
}
