package net.woorisys.lighting.control3.admin.fragment;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import net.woorisys.lighting.control3.admin.R;

import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.service.scanner.ScanFilterUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeaconCheckFragment extends Fragment {

    //region UI
    @BindView(R.id.page_title)
    TextView pageTitle;

    @BindView(R.id.btn_beacon_clear)
    Button Btn_Beacon_Clear;

    @BindView(R.id.et_beacon_uuid)
    EditText ET_Beacon_Uuid;

    @BindView(R.id.txt_beacon_scan_result)
    TextView Txt_Beacon_Scan_Result;

    @BindView(R.id.Cb_beacon_1) CheckBox Cb_Beacon_1;
    @BindView(R.id.Cb_beacon_2) CheckBox Cb_Beacon_2;
    @BindView(R.id.Cb_beacon_3) CheckBox Cb_Beacon_3;
    @BindView(R.id.Cb_beacon_4) CheckBox Cb_Beacon_4;
    @BindView(R.id.Cb_beacon_5) CheckBox Cb_Beacon_5;
    @BindView(R.id.Cb_beacon_6) CheckBox Cb_Beacon_6;

    private final static String TAG="BEACON_SCAN";  //  Dimming Setting Fragment Tag

    private boolean isStartScanning=false;

    private BeaconParser beaconParser;
    private List<BeaconParser> beaconParsers;
    private ScanFilterUtils scanFilterUtils=new ScanFilterUtils();
    private ScanSettings settings;
    List<ScanFilter> filters;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner LeScanner_W;

    private BLEScanCallback mScanCallback = null;

    private BLEBroadcastReceiver mBroadcastReceiver = null;

    private String recvMsg=null;

    // 생성자
    public BeaconCheckFragment() {

    }

    public static BeaconCheckFragment newInstance() {
        return new BeaconCheckFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_beaconr_check, container, false);
        ButterKnife.bind(this, view);
        pageTitle.setText("비컨 스켄 설정");

//        sharedPreferencesSingleton=SharedPreferencesSingleton.getInstance(getApplicationContext());
//        sharedPreferencesSingleton.SharedPreferenceRead();
//
//        ServerData serverData=new ServerData(this);
//        TextView TXT_RESULT_W = null;      //  로그인시 결과
//        serverData.Login(sharedPreferencesSingleton.getID_W(),sharedPreferencesSingleton.getPASS0WORD_W()
//                ,TXT_RESULT_W,sharedPreferencesSingleton.isDRIVER_W(),sharedPreferencesSingleton.isREMEMBER_W());
//
//        sharedPreferencesSingleton.ResetUpdate(sharedPreferencesSingleton.getRESET_W()+1);

        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

        beaconParser=new BeaconParser();
        beaconParser.setBeaconLayout(getResources().getString(R.string.beacon_parser));
        beaconParsers=new ArrayList<>();
        beaconParsers.add(beaconParser);
        settings=(new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)).build();      //  RequiresApi 가 필요 - Oreo 버전에서만 사용할 예정이기 때문에 Oreo 만 잡아준다
        filters=scanFilterUtils.createScanFiltersForBeaconParsers(beaconParsers);

        LeScanner_W=mBluetoothAdapter.getBluetoothLeScanner();

        mScanCallback = new BLEScanCallback();

        IntentFilterValue();

        if (mBluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (this.isStartScanning) {
                    StopBluetoothScanning();
                }
                StartBluetoothScanning();
            }
        }

        //화면 클리어
        Btn_Beacon_Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recvMsg = null;
                Txt_Beacon_Scan_Result.setText("");
            }
        });

        return view;
    }

    public class BLEScanCallback extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            super.onScanResult(callbackType, result);

            ScanRecord scanRecord = result.getScanRecord();
            SparseArray<byte[]> sparseArray = scanRecord.getManufacturerSpecificData();
            byte[] bytevalue = sparseArray.valueAt(0);

            if(bytevalue!=null)
            {
                if(bytevalue.length>=23) {
                    //Log.d(TAG,"byteValue SIZE : "+bytevalue.length);
                    String Address= String.format("%02x", bytevalue[2] & 0xff) + String.format("%02x", bytevalue[3] & 0xff)
                            + String.format("%02x", bytevalue[4] & 0xff)+ String.format("%02x", bytevalue[5] & 0xff)
                            + String.format("%02x", bytevalue[6] & 0xff)+ String.format("%02x", bytevalue[7] & 0xff)
                            + String.format("%02x", bytevalue[8] & 0xff)+ String.format("%02x", bytevalue[9] & 0xff)
                            + String.format("%02x", bytevalue[10] & 0xff)+ String.format("%02x", bytevalue[11] & 0xff)
                            + String.format("%02x", bytevalue[12] & 0xff)+ String.format("%02x", bytevalue[13] & 0xff)
                            + String.format("%02x", bytevalue[14] & 0xff)+ String.format("%02x", bytevalue[15] & 0xff)
                            + String.format("%02x", bytevalue[16] & 0xff)+ String.format("%02x", bytevalue[17] & 0xff);

                    //if(Address==getResources().getString(R.string.beacon_id) || Address.equals(getResources().getString(R.string.beacon_id)))
                    if(Address==ET_Beacon_Uuid.getText().toString() || Address.equals(ET_Beacon_Uuid.getText().toString()))
                    {
                        final double rssi = result.getRssi();
                        String MajorValue= String.format("%02X",bytevalue[18])+ String.format("%02X",bytevalue[19]);
                        String MinorValue= String.format("%02X",bytevalue[20])+ String.format("%02X",bytevalue[21]);

                        final int major = Integer.valueOf(MajorValue,16);
                        int minor = Integer.valueOf(MinorValue,16);

                        try {
                            if (rssi >= -90) {
                                switch (major) {
                                    // 로비 비컨
                                    case 1:
                                        if (Cb_Beacon_1.isChecked()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    recvMsg += "<로비폰> ID: " + String.valueOf(minor) + ", RSSI : " + String.valueOf(rssi) + "\n";
                                                    Txt_Beacon_Scan_Result.setText(recvMsg);
                                                }
                                            });
                                        }
                                        break;
                                    case 2:
                                        if (Cb_Beacon_2.isChecked()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    recvMsg += "<시작비컨1> ID: " + String.valueOf(minor) + ", RSSI : " + String.valueOf(rssi) + "\n";
                                                    Txt_Beacon_Scan_Result.setText(recvMsg);
                                                }
                                            });
                                        }
                                        break;
                                    case 3:
                                        if (Cb_Beacon_3.isChecked()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    recvMsg += "<홀비컨> ID: " + String.valueOf(minor) + ", RSSI : " + String.valueOf(rssi) + "\n";
                                                    Txt_Beacon_Scan_Result.setText(recvMsg);
                                                }
                                            });
                                        }

                                        break;
                                    case 4:
                                        if (Cb_Beacon_4.isChecked()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    recvMsg += "<상태평시> ID: " + String.valueOf(minor) + ", RSSI : " + String.valueOf(rssi) + "\n";
                                                    Txt_Beacon_Scan_Result.setText(recvMsg);
                                                }
                                            });
                                        }

                                        break;
                                    case 5:
                                        if (Cb_Beacon_5.isChecked()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    recvMsg += "<상태변화> ID: " + String.valueOf(minor) + ", RSSI : " + String.valueOf(rssi) + "\n";
                                                    Txt_Beacon_Scan_Result.setText(recvMsg);
                                                }
                                            });
                                        }

                                        break;
                                    case 6:
                                        if (Cb_Beacon_6.isChecked()) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    recvMsg += "<시작비컨2> ID: " + String.valueOf(minor) + ", RSSI : " + String.valueOf(rssi) + "\n";
                                                    Txt_Beacon_Scan_Result.setText(recvMsg);
                                                }
                                            });
                                        }
                                        break;
                                }
                            }
                        }catch (Exception e){
                            Log.d(TAG,"Scanning Stop");
                        }
                    }
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG,"ERROR : "+errorCode);

            //LeScanner_W.stopScan(scanCallback);
            //LeScanner_W.startScan(scanCallback);
        }
    }

    // Bluetooth Low Energy Scanning Start
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void StartBluetoothScanning()
    {
        if(!isStartScanning)
        {
            if(LeScanner_W!=null)
            {
                Log.d(TAG,"START LOW ENERGY SCANNING");
                LeScanner_W.startScan(filters,settings,mScanCallback);
                isStartScanning=true;
            }
        }
    }

    // Bluetooth Low Energy Scanning Stop
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void StopBluetoothScanning()
    {
        if(isStartScanning)
        {
            if(LeScanner_W!=null)
            {
                Log.d(TAG,"STOP LOW ENERGY SCANNING");
                LeScanner_W.stopScan(mScanCallback);
                isStartScanning=false;
            }
        }
    }

    private void IntentFilterValue()
    {
        mBroadcastReceiver = new BLEBroadcastReceiver();
        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
//        registerReceiver(mBroadcastReceiver,stateFilter);
    }

    private class BLEBroadcastReceiver extends BroadcastReceiver {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onReceive(Context context, Intent intent) {

            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch(state) {
                case BluetoothAdapter.STATE_OFF:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        StopBluetoothScanning();
                    }
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:

                    break;
                case BluetoothAdapter.STATE_ON:
                    // Notification Bluetooth off

                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
            }
        }
    };
}
