package net.woorisys.lighting.control3.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.woorisys.lighting.control3.admin.fragment.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAndRequestPermissions();
    }

    /** Android 버전별 필요 권한 목록 반환 */
    private String[] getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ : 블루투스 런타임 권한
            permissions.add(Manifest.permission.BLUETOOTH_SCAN);
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE);
        }

        // BLE 스캔에 위치 권한 필요 (Android 6~11)
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        return permissions.toArray(new String[0]);
    }

    /** 미허용 권한을 추려서 요청, 모두 허용됐으면 바로 진행 */
    private void checkAndRequestPermissions() {
        List<String> denied = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                denied.add(permission);
            }
        }

        if (denied.isEmpty()) {
            goToBaseActivity();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    denied.toArray(new String[0]),
                    REQUEST_CODE_PERMISSIONS
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE_PERMISSIONS) return;

        List<String> permanentlyDenied = new ArrayList<>();
        List<String> justDenied       = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                // shouldShowRationale == false + 거부 → 영구 거부(다시 묻지 않음 체크)
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    permanentlyDenied.add(permissions[i]);
                } else {
                    justDenied.add(permissions[i]);
                }
            }
        }

        if (permanentlyDenied.isEmpty() && justDenied.isEmpty()) {
            // 모두 허용
            goToBaseActivity();
        } else if (!permanentlyDenied.isEmpty()) {
            // 영구 거부 → 설정 화면으로 안내
            showGoToSettingsDialog();
        } else {
            // 일반 거부 → 권한이 왜 필요한지 설명 후 재요청
            showRationaleDialog();
        }
    }

    private void goToBaseActivity() {
        startActivity(new Intent(this, BaseActivity.class));
        finish();
    }

    /** 권한 거부 시 — 이유 설명 후 재요청 */
    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 허용 필요")
                .setMessage("블루투스 스캔 및 위치 권한이 없으면\n비콘을 검색할 수 없습니다.\n\n권한을 허용해주세요.")
                .setPositiveButton("다시 요청", (dialog, which) -> checkAndRequestPermissions())
                .setNegativeButton("종료", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    /** 영구 거부 시 — 앱 설정 화면으로 이동 안내 */
    private void showGoToSettingsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("권한 설정 필요")
                .setMessage("권한이 영구적으로 거부되었습니다.\n\n설정 → 앱 → 권한에서\n블루투스 및 위치 권한을 직접 허용해주세요.")
                .setPositiveButton("설정으로 이동", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("종료", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
