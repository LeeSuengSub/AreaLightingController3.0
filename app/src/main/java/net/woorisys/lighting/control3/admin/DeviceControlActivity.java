package net.woorisys.lighting.control3.admin;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.woorisys.lighting.control3.admin.ble.BluetoothLeService;
import net.woorisys.lighting.control3.admin.ble.SampleGattAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class DeviceControlActivity extends AppCompatActivity {

    private final static String TAG = "DeviceControl";

    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_UUID    = "DEVICE_UUID";
    public static final String CUSTOM_SERVICE_UUID = "0000FFF0";
    public static final String CUSTOM_CHARACTERISTIC_UUID = "0000FFF1";

    public static final String COMMON_SETTING = "AA020000ACAB";
    public static final String DISORDER_SETTING = "AA020001ADAB";
    public static final String PINK_SETTING = "AA020002AEAB";
    public static final String YELLOW_SETTING = "AA020003AFAB";
    public static final String CYAN_SETTING = "AA020004B0AB";

    private TextView mConnectionState;
    private TextView mDataField;
    private TextView mBeaconUuid;
    private String mDeviceAddress;
    private String mBeaconUuidValue; // 재연결 후 UUID 복원용

    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private Button marginButton;
    private Button writeButton;
    private Button refreshButton;
    private Button commonButton;
    private Button disorderButton;
    private Button yellowButton;
    private Button cyanButton;
    private Button pinkButton;

    // 설정값 하단 표시 영역
    private View settingInfoLayout;
    private View marginRow;
    private TextView tvSettingHeight;
    private TextView tvMeasurementHeight;
    private TextView tvMargin;
    private TextView tvState;


    private final String LIST_UUID = "UUID";
    private final String LIST_MAC_ADDRESS = "MAC_ADDRESS";

    // onCreate가 로드되면서 bind한다.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            Log.d(TAG, "진입합니다.");
            if (!mBluetoothLeService.initialize()) {
                Log.d(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.d(TAG, "connect");

            // Android 12(API 31)+ : BLUETOOTH_CONNECT 런타임 권한 확인
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(DeviceControlActivity.this,
                        Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DeviceControlActivity.this,
                            new String[]{
                                    Manifest.permission.BLUETOOTH_CONNECT,
                                    Manifest.permission.BLUETOOTH_SCAN
                            }, 300);
                    return;
                }
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        marginButton = findViewById(R.id.marginButton);
        writeButton = findViewById(R.id.writeButton);
        refreshButton = findViewById(R.id.refreshButton);
        commonButton = findViewById(R.id.commonButton);
        disorderButton = findViewById(R.id.disorderButton);
        yellowButton = findViewById(R.id.yellowButton);
        cyanButton = findViewById(R.id.cyanButton);
        pinkButton = findViewById(R.id.pinkButton);

        settingInfoLayout  = findViewById(R.id.setting_info_layout);
        marginRow          = findViewById(R.id.margin_row);
        tvSettingHeight    = findViewById(R.id.tv_setting_height);
        tvMeasurementHeight= findViewById(R.id.tv_measurement_height);
        tvMargin           = findViewById(R.id.tv_margin);
        tvState            = findViewById(R.id.tv_state);

        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mConnectionState.setText("연결중");
        mBeaconUuid = findViewById(R.id.beacon_uuid);
        // 연결 완료 전에는 새로고침 버튼 비활성화
        refreshButton.setEnabled(false);

        // savedInstanceState가 있으면 복원, 없으면 Intent에서 가져옴
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRAS_DEVICE_ADDRESS)) {
            mDeviceAddress = savedInstanceState.getString(EXTRAS_DEVICE_ADDRESS);
        } else {
            mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);
        }

        ((TextView) findViewById(R.id.device_address)).setText(
                mDeviceAddress != null ? mDeviceAddress : "");

        mBeaconUuidValue = getIntent().getStringExtra(EXTRAS_DEVICE_UUID);
        mBeaconUuid.setText(mBeaconUuidValue != null ? mBeaconUuidValue : "");
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListener);
        mDataField = (TextView) findViewById(R.id.device_data);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // 설정값 새로고침 버튼
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothLeService == null || !mConnected) {
                    Toast.makeText(DeviceControlActivity.this, "장비가 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                refreshButton.setEnabled(false);
                refreshButton.setText("조회 중...");
                // 이미 캐싱된 characteristic이 있으면 서비스 트리 재순회 없이 바로 read
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.readCharacteristic(mNotifyCharacteristic);
                } else {
                    autoReadCharacteristic();
                }
            }
        });

        //일반
        commonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataField.length() <= 0){
                    Toast.makeText(mBluetoothLeService, "read한 다음 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] data = hexStringToByteArray(COMMON_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        //장애인
        disorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataField.length() <= 0){
                    Toast.makeText(mBluetoothLeService, "read한 다음 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] data = hexStringToByteArray(DISORDER_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        //분홍색
        pinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDataField.length() <=0) {
                    Toast.makeText(mBluetoothLeService,"read한 다음 시도해주세요.",Toast.LENGTH_SHORT);
                    return;
                }
                byte[] data = hexStringToByteArray(PINK_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        //노란색
        yellowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDataField.length() <=0) {
                    Toast.makeText(mBluetoothLeService,"read한 다음 시도해주세요.",Toast.LENGTH_SHORT);
                    return;
                }
                byte[] data = hexStringToByteArray(YELLOW_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        //시안색
        cyanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDataField.length() <=0) {
                    Toast.makeText(mBluetoothLeService,"read한 다음 시도해주세요.",Toast.LENGTH_SHORT);
                    return;
                }
                byte[] data = hexStringToByteArray(CYAN_SETTING);
                mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic, data);
            }
        });

        AlertDialog.Builder writeBuilder = new AlertDialog.Builder(DeviceControlActivity.this);

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataField.length() <= 0){
                    Toast.makeText(mBluetoothLeService, "read한 다음 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                writeBuilder.setTitle("높이 값 세팅");
                writeBuilder.setMessage("세팅 가능한 범위 : 1000 ~ 4000");

                LayoutInflater inflater = getLayoutInflater();
                View readView = inflater.inflate(R.layout.write_dialog, null);

                writeBuilder.setView(readView);

                writeBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String start = "AA";
                        String road = "01";
                        String end = "AB";

                        EditText heightSet = (EditText)((AlertDialog)dialog).findViewById(R.id.heightSet);

                        String dialogEdit = heightSet.getText().toString();

                        //내용을 적지 않으면 return;
                        if(dialogEdit.length() <= 0){
                            return;
                        }

                        // 정규표현식 숫자만!
                        if(!isNumeric(dialogEdit)){
                            Toast.makeText(mBluetoothLeService, "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int dialogEditInt = Integer.parseInt(dialogEdit);

                        String heightEditHex = Integer.toHexString(dialogEditInt);
                        while(heightEditHex.length() < 4) {
                            heightEditHex = "0"+heightEditHex;
                        }

                        String heightEditHexHeightHigh = heightEditHex.substring(0,2);
                        String heightEditHexHeightLow = heightEditHex.substring(2);

                        int startInt = Integer.parseInt(start,16);
                        int roadInt = Integer.parseInt(road,16);
                        int heightHigh = Integer.parseInt(heightEditHexHeightHigh,16);
                        int heightLow = Integer.parseInt(heightEditHexHeightLow,16);

                        int HexSum = startInt+roadInt+heightHigh+heightLow;
                        String writeSumHex = Integer.toHexString(HexSum);

                        if(writeSumHex.length() > 2) {
                            writeSumHex = writeSumHex.substring(1);
                        }

                        String hexSetting = (start+road+heightEditHex+writeSumHex+end).toUpperCase(Locale.ROOT);

                        byte[] data = hexStringToByteArray(hexSetting);
                        mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic,data);
                    }
                });
                writeBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = writeBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        /*
         * maring값 설정.
         * */
        AlertDialog.Builder marginBuilder = new AlertDialog.Builder(DeviceControlActivity.this);

        marginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataField.length() <= 0){
                    Toast.makeText(mBluetoothLeService, "read한 다음 시도해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                marginBuilder.setTitle("마진 값 세팅");
                marginBuilder.setMessage("세팅 가능한 범위 : 1000 ~ 4000");

                LayoutInflater inflater = getLayoutInflater();
                View readView = inflater.inflate(R.layout.write_dialog, null);

                marginBuilder.setView(readView);

                marginBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String start = "AA";
                        String road = "03";
                        String end = "AB";

                        EditText heightSet = (EditText)((AlertDialog)dialog).findViewById(R.id.heightSet);

                        String dialogEdit = heightSet.getText().toString();

                        //내용을 적지 않으면 return;
                        if(dialogEdit.length() <= 0){
                            return;
                        }

                        // 정규표현식 숫자만!
                        if(!isNumeric(dialogEdit)){
                            Toast.makeText(mBluetoothLeService, "숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int dialogEditInt = Integer.parseInt(dialogEdit);

                        String heightEditHex = Integer.toHexString(dialogEditInt);
                        while(heightEditHex.length() < 4) {
                            heightEditHex = "0"+heightEditHex;
                        }

                        String heightEditHexHeightHigh = heightEditHex.substring(0,2);
                        String heightEditHexHeightLow = heightEditHex.substring(2);

                        int startInt = Integer.parseInt(start,16);
                        int roadInt = Integer.parseInt(road,16);
                        int heightHigh = Integer.parseInt(heightEditHexHeightHigh,16);
                        int heightLow = Integer.parseInt(heightEditHexHeightLow,16);

                        int HexSum = startInt+roadInt+heightHigh+heightLow;
                        String writeSumHex = Integer.toHexString(HexSum);

                        if(writeSumHex.length() > 2) {
                            writeSumHex = writeSumHex.substring(1);
                        }

                        String hexSetting = (start+road+heightEditHex+writeSumHex+end).toUpperCase(Locale.ROOT);

                        byte[] data = hexStringToByteArray(hexSetting);
                        mBluetoothLeService.writeCharacteristic(mNotifyCharacteristic,data);
                    }
                });
                marginBuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = marginBuilder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mDeviceAddress != null) {
            outState.putString(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Android 13(API 33)+ : RECEIVER_NOT_EXPORTED 플래그 필수
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter(),
                    Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
        // [수정] 이미 연결됐거나 연결 중이면 재연결 시도 안 함
        if (mBluetoothLeService != null) {
            int state = mBluetoothLeService.getConnectionState();
            if (state == BluetoothLeService.STATE_DISCONNECTED) {
                final boolean result = mBluetoothLeService.connect(mDeviceAddress);
                Log.d(TAG, "Connect request result= " + result);
            } else {
                Log.d(TAG, "Already connected or connecting, state=" + state);
            }
        } else {
            Log.d(TAG, "mBluetoothLeService is null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // [수정] Activity 완전 종료 시에만 서비스 close() 호출 → 연결 완전 해제
        if (mBluetoothLeService != null) {
            mBluetoothLeService.close();
        }
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG,"Connected");
                mConnected = true;
            } else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG,"Disconnected");
                mConnected = false;
                mConnectionState.setText("연결 실패 | 다시 연결 시도중.");
                refreshButton.setEnabled(false);
                refreshButton.setText("설정값 새로고침");
                clearUI();
            } else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // displayGattServices() 제거 — ExpandableListView가 gone이므로 불필요
                // characteristic 캐싱 후 즉시 read
                autoReadCharacteristic();
            }
            else if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                mConnectionState.setText("연결됨");
                String rawData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                displayData(rawData);
                showSettingInfo(rawData);
                // 새로고침 버튼 복구
                refreshButton.setEnabled(true);
                refreshButton.setText("설정값 새로고침");
            }
        }
    };

    private final ExpandableListView.OnChildClickListener servicesListClickListener =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        //read
                        if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }

                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) == charaProp) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(characteristic , true);

                        }else{
                            mBluetoothLeService.setCharacteristicNotification(characteristic , true);
                            characteristic.setValue(new byte[] {0x24, 0x52, 0x45, 0x41, 0x44, 0x2C, 0x30, 0x0D, 0x0A});
                            mBluetoothLeService.writeCharacteristic(characteristic);
                        }

                        return true;
                    }
                    return false;
                }
            };

    private void autoReadCharacteristic() {
        if (mBluetoothLeService == null) return;
        List<BluetoothGattService> services = mBluetoothLeService.getSupportedGattServices();
        if (services == null) return;
        for (BluetoothGattService service : services) {
            String serviceUuid = service.getUuid().toString().split("-")[0].toUpperCase(Locale.ROOT);
            if (serviceUuid.equals(CUSTOM_SERVICE_UUID)) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    String charUuid = characteristic.getUuid().toString().split("-")[0].toUpperCase(Locale.ROOT);
                    if (charUuid.equals(CUSTOM_CHARACTERISTIC_UUID)) {
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.readCharacteristic(characteristic);
                        return;
                    }
                }
            }
        }
    }

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        // UUID는 Intent에서 받은 값이므로 재연결 후에도 유지
        mBeaconUuid.setText(mBeaconUuidValue != null ? mBeaconUuidValue : "");
        settingInfoLayout.setVisibility(View.GONE);
    }

    private void displayData(String data) {
        if(data != null) {
            mDataField.setText(data);
        }
    }

    private void showSettingInfo(String rawData) {
        if (rawData == null || rawData.length() < 14) return;
        try {
            String data = rawData.toUpperCase(Locale.ROOT);
            int settingHeight     = Integer.parseInt(data.substring(5, 8), 16);
            int state             = Integer.parseInt(data.substring(9, 10), 16);
            int measurementHeight = Integer.parseInt(data.substring(10, 14), 16);

            int marginSize = 0;
            try {
                marginSize = Integer.parseInt(data.substring(18, 22), 16);
            } catch (Exception e) { /* margin 없는 프로토콜 */ }

            String stateLabel;
            switch (state) {
                case 0:  stateLabel = "일반";    break;
                case 1:  stateLabel = "장애인";  break;
                case 2:  stateLabel = "Pink";    break;
                case 3:  stateLabel = "Yellow";  break;
                case 4:  stateLabel = "Cyan";    break;
                default: stateLabel = "-";       break;
            }

            tvSettingHeight.setText(settingHeight + " mm");
            tvMeasurementHeight.setText(measurementHeight + " mm");
            tvState.setText(stateLabel);

            if (marginSize != 0) {
                tvMargin.setText(marginSize + " mm");
                marginRow.setVisibility(View.VISIBLE);
            } else {
                marginRow.setVisibility(View.GONE);
            }

            settingInfoLayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "설정값 파싱 오류: " + e.getMessage());
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if(gattServices == null) return;
        String uuid = null;
        String customServiceString = "Custom SERVICE";
        String customCharaString = "Custom characteristic";

        //Service
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        for(BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            String[] uuid_split = uuid.split("-");
            String uuid_split_upper = uuid_split[0].toUpperCase(Locale.ROOT);

            if(uuid_split_upper.equals(CUSTOM_SERVICE_UUID)){
                currentServiceData.put(LIST_UUID, SampleGattAttributes.lookup(uuid, customServiceString.toUpperCase(Locale.ROOT)));
                currentServiceData.put(LIST_MAC_ADDRESS, uuid.toUpperCase(Locale.ROOT));
                gattServiceData.add(currentServiceData);

                //characteristic
                ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

                for(BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                    charas.add(gattCharacteristic);
                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();

                    String[] characteristic_uuid_split = uuid.split("-");
                    String characteristic_uuid_split_upper = characteristic_uuid_split[0].toUpperCase(Locale.ROOT);

                    if(characteristic_uuid_split_upper.equals(CUSTOM_CHARACTERISTIC_UUID)){

                        currentCharaData.put(LIST_UUID, SampleGattAttributes.lookup(uuid, customCharaString.toUpperCase(Locale.ROOT)));
                        currentCharaData.put(LIST_MAC_ADDRESS, uuid.toUpperCase(Locale.ROOT));
                        gattCharacteristicGroupData.add(currentCharaData);
                    }
                }
                mGattCharacteristics.add(charas);
                gattCharacteristicData.add(gattCharacteristicGroupData);
            }

        }//for end

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this, gattServiceData, android.R.layout.simple_expandable_list_item_2, new String[]{LIST_UUID, LIST_MAC_ADDRESS}, new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{LIST_UUID, LIST_MAC_ADDRESS},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    // hex => byteArray
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    // 숫자 정규표현식
    public boolean isNumeric(String str) {
        return Pattern.matches("^[0-9]*$", str);
    }

    // Android 12+ 블루투스 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 300) {
            boolean allGranted = grantResults.length > 0;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted && mBluetoothLeService != null) {
                mBluetoothLeService.connect(mDeviceAddress);
            } else {
                Toast.makeText(this,
                        "블루투스 권한이 거부되었습니다.\n설정에서 권한을 허용해주세요.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}