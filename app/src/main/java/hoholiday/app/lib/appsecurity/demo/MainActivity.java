package hoholiday.app.lib.appsecurity.demo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hoholiday.app.lib.appsecurity.AppSecurityManager;
import hoholiday.app.lib.appsecurity.checkunit.CheckUnitName;
import hoholiday.app.lib.appsecurity.exception.AppSecurityException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_load_jni).setOnClickListener(v -> {
            try {
                AppSecurityManager.getInstance().init(getApplicationContext());
            } catch (AppSecurityException e) {
                Toast.makeText(getApplicationContext(), String.format("App安全异常!%s", e), Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.btn_test_accessibility).setOnClickListener(v -> {
            AppSecurityManager.getInstance().unitCheck(getApplicationContext(), CheckUnitName.ACCESSIBILITY_SERVICE);
        });
        findViewById(R.id.btn_test_emulator).setOnClickListener(v -> {
            AppSecurityManager.getInstance().unitCheck(getApplicationContext(), CheckUnitName.EMULATOR);
        });
        findViewById(R.id.btn_test_root).setOnClickListener(v -> {
            AppSecurityManager.getInstance().unitCheck(getApplicationContext(), CheckUnitName.ROOT);
        });
        findViewById(R.id.btn_test_app_debug).setOnClickListener(v -> {
            AppSecurityManager.getInstance().unitCheck(getApplicationContext(), CheckUnitName.APP_DEBUG);
        });
    }
}