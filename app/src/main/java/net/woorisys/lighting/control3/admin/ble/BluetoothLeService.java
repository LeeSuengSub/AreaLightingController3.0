package net.woorisys.lighting.control3.admin.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class BluetoothLeService extends Service {

    private final static String TAG = "BluetoothLeService";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private final Handler mHandler = new Handler();
    private boolean mIsConnecting = false;

    // =====================================================
    // [수정1] 무한 재연결: MAX 제거, 재시도 간격만 유지
    // =====================================================
    private static final long CONNECT_TIMEOUT  = 10000; // 10초 연결 타임아웃
    private static final long RECONNECT_DELAY  = 2000;  // 2초 후 재시도
    private String mDeviceAddress;

    // 재연결 활성 여부 플래그 (명시적 disconnect 시 false로 설정)
    private boolean mAutoReconnect = true;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING   = 1;
    public static final int STATE_CONNECTED    = 2;

    public final static String ACTION_GATT_CONNECTED           = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED        = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE           = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA                      = "com.example.bluetooth.le.EXTRA_DATA";

    // =====================================================
    // GATT 콜백
    // =====================================================
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange: status=" + status + " newState=" + newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // ── 연결 성공 ──────────────────────────────
                mConnectionState = STATE_CONNECTED;
                mIsConnecting    = false;
                mHandler.removeCallbacks(mConnectTimeoutRunnable);
                mHandler.removeCallbacks(mReconnectRunnable);
                // 고속 연결 간격 요청 (7.5ms) → service discovery 속도 향상
                // 기본 BALANCED(30~50ms) 대비 최대 5배 빠름
                gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                // mBluetoothGatt 대신 콜백 파라미터 gatt 사용 (타임아웃 race condition 방지)
                gatt.discoverServices();
                broadcastUpdate(ACTION_GATT_CONNECTED);
                Log.d(TAG, "Connected to GATT server.");

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                gatt.close();

                // gatt가 현재 mBluetoothGatt와 다르면 이전 장비의 stale 콜백
                // → broadcast/재연결 무시 (다른 장비 연결에 간섭하지 않음)
                if (gatt != mBluetoothGatt) {
                    Log.d(TAG, "Stale disconnect callback — ignoring.");
                    return;
                }

                mBluetoothGatt   = null;
                mConnectionState = STATE_DISCONNECTED;
                mIsConnecting    = false;
                mHandler.removeCallbacks(mConnectTimeoutRunnable);
                mHandler.removeCallbacks(mReconnectRunnable);
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                Log.d(TAG, "Disconnected from GATT server. autoReconnect=" + mAutoReconnect);

                if (mAutoReconnect) {
                    Log.d(TAG, "Scheduling reconnect in " + RECONNECT_DELAY + "ms...");
                    mHandler.postDelayed(mReconnectRunnable, RECONNECT_DELAY);
                }
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d(TAG, "onServicesDiscovered: status=" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.d(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead: status=" + status + " uuid=" + characteristic.getUuid());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                // read 완료 후 BALANCED로 복귀 — 일부 펌웨어가 HIGH 유지 시 연결을 끊는 문제 방지
                gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_BALANCED);
            } else {
                Log.e(TAG, "Characteristic read failed, status: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicWrite: status=" + status + " uuid=" + characteristic.getUuid());
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic written successfully");
            } else {
                Log.e(TAG, "Characteristic write failed, status: " + status);
                // [수정3] write 실패 시 즉시 disconnect 하지 않음 → 연결 유지
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged: uuid=" + characteristic.getUuid());
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    // =====================================================
    // 타임아웃: 연결 시도가 CONNECT_TIMEOUT 내 완료되지 않으면
    // GATT를 닫고 재시도
    // =====================================================
    private final Runnable mConnectTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsConnecting) {
                Log.d(TAG, "Connection timeout — retrying...");
                closeGatt();
                if (mAutoReconnect) {
                    // 중복 방지: 기존 pending 제거 후 등록
                    mHandler.removeCallbacks(mReconnectRunnable);
                    mHandler.postDelayed(mReconnectRunnable, RECONNECT_DELAY);
                }
            }
        }
    };

    // =====================================================
    // 재연결 Runnable — 무한 반복
    // =====================================================
    private final Runnable mReconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAutoReconnect && mDeviceAddress != null) {
                Log.d(TAG, "Reconnecting to " + mDeviceAddress);
                connect(mDeviceAddress);
            }
        }
    };

    // =====================================================
    // Broadcast
    // Android 14+에서 RECEIVER_NOT_EXPORTED 리시버가 수신하려면
    // setPackage()로 명시적으로 앱 내부 브로드캐스트임을 지정해야 함
    // =====================================================
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        intent.setPackage(getPackageName());
        sendBroadcast(intent);
    }

    public void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        intent.setPackage(getPackageName());
        Log.d(TAG, characteristic.getUuid().toString());

        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder sb = new StringBuilder(data.length);
            for (byte b : data) sb.append(String.format("%02X", b));

            if (SampleGattAttributes.WOORI_NOTI_UUID.equals(characteristic.getUuid().toString())) {
                intent.putExtra(EXTRA_DATA, sb.toString());
            } else {
                Log.d(TAG, sb.toString());
                intent.putExtra(EXTRA_DATA, sb.toString());
            }
        }
        sendBroadcast(intent);
    }

    // =====================================================
    // Binder
    // =====================================================
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // [수정4] onUnbind 에서 close() 제거 → Activity 재개 시 연결 유지
    //         (명시적 disconnect/close 는 DeviceControlActivity.onDestroy 에서만 호출)
    @Override
    public boolean onUnbind(Intent intent) {
        // close() 를 여기서 호출하지 않음으로써 연결 상태 유지
        return super.onUnbind(intent);
    }

    // =====================================================
    // 현재 연결 상태 조회 (DeviceControlActivity에서 사용)
    // =====================================================
    public int getConnectionState() {
        return mConnectionState;
    }

    // =====================================================
    // 초기화
    // =====================================================
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.d(TAG, "Unable to initialize BluetoothManager");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    // =====================================================
    // 연결
    // =====================================================
    @SuppressLint("MissingPermission")
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.d(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        mDeviceAddress  = address;
        mAutoReconnect  = true;

        // [수정5] 이미 연결된 상태면 재연결 시도 안 함
        if (mConnectionState == STATE_CONNECTED) {
            Log.d(TAG, "Already connected — skip reconnect.");
            return true;
        }

        // 기존 GATT 재사용 시도
        if (mBluetoothGatt != null && address.equals(mBluetoothDeviceAddress)) {
            Log.d(TAG, "Reusing existing mBluetoothGatt.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                mIsConnecting    = true;
                mHandler.postDelayed(mConnectTimeoutRunnable, CONNECT_TIMEOUT);
                return true;
            } else {
                // 재사용 실패 → GATT 닫고 새로 연결
                closeGatt();
            }
        }

        // 새 GATT 연결
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.d(TAG, "Device not found. Unable to connect.");
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        }

        Log.d(TAG, "Creating new GATT connection to " + address);
        mBluetoothDeviceAddress = address;
        mConnectionState        = STATE_CONNECTING;
        mIsConnecting           = true;
        mHandler.postDelayed(mConnectTimeoutRunnable, CONNECT_TIMEOUT);
        return true;
    }

    // =====================================================
    // 명시적 연결 해제 (사용자가 직접 끊을 때만 호출)
    // mAutoReconnect = false 로 설정해 재연결 루프 중단
    // =====================================================
    @SuppressLint("MissingPermission")
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mAutoReconnect = false; // 재연결 중단
        mHandler.removeCallbacks(mReconnectRunnable);
        mHandler.removeCallbacks(mConnectTimeoutRunnable);
        Log.d(TAG, "Disconnecting (mAutoReconnect=false)...");
        mBluetoothGatt.disconnect();
    }

    // =====================================================
    // GATT 닫기 (내부 정리용)
    // disconnect()만 호출하고 close()는 onConnectionStateChange 콜백에서 처리.
    // disconnect() 직후 close()를 바로 호출하면 콜백이 실행되지 않아
    // broadcastUpdate(DISCONNECTED)가 전달되지 않고 UI가 "연결중"에 고정됨.
    // =====================================================
    @SuppressLint("MissingPermission")
    private void closeGatt() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            // close()는 onConnectionStateChange(STATE_DISCONNECTED) 콜백에서 호출됨
        }
        mIsConnecting = false;
    }

    @SuppressLint("MissingPermission")
    public void close() {
        mAutoReconnect = false;
        mHandler.removeCallbacks(mReconnectRunnable);
        mHandler.removeCallbacks(mConnectTimeoutRunnable);
        // Activity 종료 시 강제 정리: disconnect + close 즉시 호출
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        mIsConnecting = false;
    }

    // =====================================================
    // Characteristic 읽기 / 쓰기 / Notification
    // =====================================================
    @SuppressLint("MissingPermission")
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean status = mBluetoothGatt.readCharacteristic(characteristic);
        Log.d(TAG, "Reading characteristic, status: " + status);
    }

    @SuppressLint("MissingPermission")
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        Log.d(TAG, "Setting notification for: " + characteristic.getUuid());
        if (SampleGattAttributes.WOORI_NOTI_UUID.equals(characteristic.getUuid().toString())) {
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                }
            }
        }
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    @SuppressLint("MissingPermission")
    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    @SuppressLint("MissingPermission")
    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] data) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.d(TAG, "BluetoothAdapter not initialized");
            return false;
        }
        characteristic.setValue(data);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        Log.d(TAG, "writeCharacteristic data: " + Arrays.toString(data));
        return mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b & 0xff));
        return sb.toString();
    }

}