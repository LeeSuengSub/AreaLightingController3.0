package net.woorisys.lighting.control3.admin.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.adapter.BeaconAdapter;
import net.woorisys.lighting.control3.admin.ble.BeaconDomain;
import net.woorisys.lighting.control3.admin.ble.BeaconSingleton;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class BleScannerFragment extends Fragment {

    private static final String TAG = "BleScannerFragment";

    /** 신호 세기 최소값 — 이 값 미만의 비콘은 무시 */
    private static final int  MIN_RSSI            = -70;
    /** 리스트 최대 항목 수 */
    private static final int  MAX_BEACONS         = 30;
    /** UI 갱신 디바운싱 간격 (ms) */
    private static final long UI_UPDATE_INTERVAL  = 500L;

    private BeaconManager beaconManager;
    private ListView listView;
    private EditText editTextSerial;
    private Button startScanBtn;
    private Button stopScanBtn;
    private Button resetListBtn;
    private Button connectBtnSerial;
    private TextView scanState;

    /** MAC → BeaconDomain 매핑 (O(1) 중복 제거, 삽입 순서 유지) */
    private final Map<String, BeaconDomain> beaconMap = new LinkedHashMap<>();

    /** 단일 어댑터 인스턴스 — 재생성 없이 데이터만 교체 */
    private BeaconAdapter beaconAdapter;

    /** UI 디바운싱용 핸들러 */
    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private Runnable uiUpdateRunnable;

    private final BeaconSingleton beaconSingleton = BeaconSingleton.getInstance();
    private boolean isScanning = false;
    private RangeNotifier rangeNotifier;

    // =====================================================
    // BeaconConsumer — Fragment 내부 익명 객체로 구현
    // =====================================================
    private final BeaconConsumer beaconConsumer = new BeaconConsumer() {

        @Override
        public void onBeaconServiceConnect() {
            beaconManager.removeRangeNotifier(rangeNotifier);

            rangeNotifier = new RangeNotifier() {
                @Override
                public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                    if (beacons.isEmpty()) return;

                    boolean changed = false;

                    for (Beacon beacon : beacons) {
                        // RSSI 필터
                        if (beacon.getRssi() < MIN_RSSI) continue;

                        String macAddress = beacon.getBluetoothAddress();
                        String uuid = (beacon.getId1() != null)
                                ? beacon.getId1().toString()
                                : "Unknown";

                        // MAC 끝 2바이트로 SerialNumber 계산
                        String[] macArray = macAddress.split(":");
                        int num1 = 0, num2 = 0;
                        try {
                            num1 = Integer.parseInt(macArray[4]);
                            num2 = Integer.parseInt(macArray[5]);
                        } catch (NumberFormatException e) {
                            try {
                                num1 = Integer.parseInt(macArray[4], 16);
                                num2 = Integer.parseInt(macArray[5], 16);
                            } catch (Exception ignored) {}
                        } catch (Exception ignored) {}
                        int serialNumber = (num1 * 100) + num2;

                        synchronized (beaconMap) {
                            // 기존 항목 업데이트 또는 최대 개수 미만이면 추가
                            if (beaconMap.containsKey(macAddress)
                                    || beaconMap.size() < MAX_BEACONS) {
                                beaconMap.put(macAddress,
                                        new BeaconDomain(macAddress, serialNumber, uuid));
                                changed = true;
                            }
                        }
                    }

                    if (changed) {
                        scheduleUiUpdate();
                    }
                }
            };

            beaconManager.addRangeNotifier(rangeNotifier);
            startBeaconScanning();
            isScanning = true;
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> updateScanState());
            }
        }

        @Override
        public Context getApplicationContext() {
            return requireContext().getApplicationContext();
        }

        @Override
        public void unbindService(ServiceConnection serviceConnection) {
            requireContext().unbindService(serviceConnection);
        }

        @Override
        public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
            return requireContext().bindService(intent, serviceConnection, i);
        }
    };

    public BleScannerFragment() {}

    public static BleScannerFragment newInstance() {
        return new BleScannerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ble_scanner, container, false);

        scanState        = view.findViewById(R.id.scanState);
        startScanBtn     = view.findViewById(R.id.startScanBtn);
        stopScanBtn      = view.findViewById(R.id.stopScanBtn);
        resetListBtn     = view.findViewById(R.id.resetListBtn);
        editTextSerial   = view.findViewById(R.id.editTextSerial);
        connectBtnSerial = view.findViewById(R.id.connectBtnSerial);
        listView         = view.findViewById(R.id.listview);

        // 어댑터 단일 인스턴스 생성 후 ListView에 한 번만 설정
        beaconAdapter = new BeaconAdapter(requireContext(), new ArrayList<>());
        listView.setAdapter(beaconAdapter);

        updateScanState();

        beaconManager = BeaconManager.getInstanceForApplication(requireContext());
        beaconManager.getBeaconParsers().add(
                new BeaconParser().setBeaconLayout(
                        "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        );
        beaconManager.bind(beaconConsumer);

        // =====================================================
        // Scan Start 버튼
        // =====================================================
        startScanBtn.setOnClickListener(v -> {
            if (!isScanning) {
                startBeaconScanning();
                isScanning = true;
                updateScanState();
                Toast.makeText(getContext(), "스캔을 시작합니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "이미 스캔 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // =====================================================
        // Scan Stop 버튼
        // =====================================================
        stopScanBtn.setOnClickListener(v -> {
            if (isScanning) {
                stopBeaconScanning();
                isScanning = false;
                updateScanState();
                Toast.makeText(getContext(), "스캔을 중지합니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "스캔이 이미 중지되어 있습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // =====================================================
        // 리스트 초기화 버튼
        // =====================================================
        resetListBtn.setOnClickListener(v -> {
            uiHandler.removeCallbacks(uiUpdateRunnable);
            synchronized (beaconMap) {
                beaconMap.clear();
            }
            beaconSingleton.resetBeaconDomainList();
            beaconAdapter.updateData(new ArrayList<>());
        });

        // =====================================================
        // Serial 직접 입력 후 연결 버튼
        // =====================================================
        connectBtnSerial.setOnClickListener(v -> {
            String inputStr = editTextSerial.getText().toString();
            if (inputStr.isEmpty()) {
                Toast.makeText(getContext(), "시리얼번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!isNumeric(inputStr)) {
                Toast.makeText(getContext(), "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            while (inputStr.length() < 4) inputStr = "0" + inputStr;

            String part1 = inputStr.substring(0, 2);
            String part2 = inputStr.substring(2);

            List<BeaconDomain> currentBeacons;
            synchronized (beaconMap) {
                currentBeacons = new ArrayList<>(beaconMap.values());
            }

            int count = 0;
            String targetMac = "";
            String targetUuid = "";
            for (BeaconDomain beacon : currentBeacons) {
                String beaconMac = beacon.getMacAddress();
                String siteBeacon = beaconMac.substring(0, beaconMac.length() - 5);
                siteBeacon += part1 + ":" + part2;
                targetMac = siteBeacon;
                if (beaconMac.equalsIgnoreCase(siteBeacon)) {
                    targetUuid = beacon.getUuid();
                }
                count++;
            }

            if (count <= 0) {
                Toast.makeText(getContext(), "통신이상\n현장을 확인후 다시 진행해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                launchDeviceControl(targetMac, targetUuid);
            }
        });

        // =====================================================
        // 리스트 아이템 클릭 → DeviceControlActivity 실행
        // =====================================================
        listView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            BeaconDomain selectedBeacon = (BeaconDomain) parent.getItemAtPosition(position);
            launchDeviceControl(selectedBeacon.getMacAddress(), selectedBeacon.getUuid());
        });

        return view;
    }

    /**
     * UI 갱신을 디바운싱 처리 — UI_UPDATE_INTERVAL 이내에 여러 번 호출되어도
     * 마지막 호출 기준으로 한 번만 갱신한다.
     */
    private void scheduleUiUpdate() {
        uiHandler.removeCallbacks(uiUpdateRunnable);
        uiUpdateRunnable = () -> {
            List<BeaconDomain> snapshot;
            synchronized (beaconMap) {
                snapshot = new ArrayList<>(beaconMap.values());
            }
            beaconAdapter.updateData(snapshot);
        };
        uiHandler.postDelayed(uiUpdateRunnable, UI_UPDATE_INTERVAL);
    }

    // DeviceControlActivity 실행
    private void launchDeviceControl(String macAddress, String uuid) {
        try {
            Class<?> cls = Class.forName("net.woorisys.lighting.control3.admin.DeviceControlActivity");
            Intent intent = new Intent(requireActivity(), cls);
            intent.putExtra("DEVICE_ADDRESS", macAddress);
            intent.putExtra("DEVICE_UUID", uuid != null ? uuid : "");
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "DeviceControlActivity를 찾을 수 없습니다.", e);
            Toast.makeText(getContext(), "DeviceControlActivity가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateScanState() {
        if (scanState != null) {
            scanState.setText(isScanning ? "Start" : "Stop");
        }
    }

    private void startBeaconScanning() {
        try {
            beaconManager.startRangingBeaconsInRegion(
                    new Region("bleScannerFragmentRegion", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stopBeaconScanning() {
        try {
            beaconManager.stopRangingBeaconsInRegion(
                    new Region("bleScannerFragmentRegion", null, null, null));
            Log.d(TAG, "Scanning stopped.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        uiHandler.removeCallbacks(uiUpdateRunnable);
        if (isScanning) {
            stopBeaconScanning();
            isScanning = false;
        }
        beaconManager.unbind(beaconConsumer);
    }

    private boolean isNumeric(String str) {
        return Pattern.matches("^[0-9]*$", str);
    }
}
