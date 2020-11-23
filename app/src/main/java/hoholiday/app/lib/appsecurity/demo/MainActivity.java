package hoholiday.app.lib.appsecurity.demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import hoholiday.app.lib.appsecurity.AppSecurityManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_load_jni).setOnClickListener(v -> {
            AppSecurityManager.getInstance().init(getApplicationContext());
        });
        findViewById(R.id.btn_test_accessibility).setOnClickListener(v -> {
            AppSecurityManager.getInstance().unitCheck(getApplicationContext(), AppSecurityManager.ACCESSIBILITY_SERVICE_CHECK_UNIT);
        });
        findViewById(R.id.btn_test_emulator).setOnClickListener(v -> {
            AppSecurityManager.getInstance().unitCheck(getApplicationContext(), AppSecurityManager.EMULATOR_CHECK_UNIT);
        });
    }
}