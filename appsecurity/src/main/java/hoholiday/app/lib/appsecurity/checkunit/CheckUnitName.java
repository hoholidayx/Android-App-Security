package hoholiday.app.lib.appsecurity.checkunit;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({
        CheckUnitName.ACCESSIBILITY_SERVICE_CHECK_UNIT,
        CheckUnitName.EMULATOR_CHECK_UNIT,
        CheckUnitName.ROOT_CHECK_UNIT
})
@Retention(RetentionPolicy.SOURCE)
public @interface CheckUnitName {

    String ACCESSIBILITY_SERVICE_CHECK_UNIT = "accessibility_service";

    String EMULATOR_CHECK_UNIT = "emulator";

    String ROOT_CHECK_UNIT = "root";
}
