package net.woorisys.lighting.control3.admin.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.search.SearchActivity;
import net.woorisys.lighting.control3.admin.sjp.EditTextErrorCheck;
import net.woorisys.lighting.control3.admin.sjp.RememberData;
import net.woorisys.lighting.control3.admin.sjp.observer.BroadcastReceiverListener;
import net.woorisys.lighting.control3.admin.sjp.observer.FragmentValue;
import net.woorisys.lighting.control3.admin.sjp.observer.ResultValue;
import net.woorisys.lighting.control3.admin.sjp.usbManagement;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity implements BroadcastReceiverListener {

    /** JHLEE **/

    //Log Tag 구분 하기 위한 String
    private final static String TAG="BASE_ACTIVITY";

    // 생성한 Broadcast Action 동작 시키기 위한 BroadcastReceiver 등록
    private usbManagement broadcastReceiver;
    IntentFilter intentFilter;
    ResultValue resultValue;

    /** ---------------------------------------------- **/
    private long backPressedTime = 0;

    private final long FINISH_INTERVAL_TIME = 2000;

    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.btn_Search)
    Button btnSearch;
    @BindView(R.id.txt_Path)
    TextView txt_FilePath_Whole;
    @BindView(R.id.txt_Path_)
    TextView getTxt_FilePath_Whole;
    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.tab1:
                    ScannerSettingFragment scannerSettingFragment = ScannerSettingFragment.newInstance();
                    replaceFragment(scannerSettingFragment);
                    pageTitle.setText("CSV 그룹 설정");
                    return true;
                case R.id.tab2:
                    ScannerSettingIndiFragment scannerSettingIndiFragment = ScannerSettingIndiFragment.newInstance();
                    replaceFragment(scannerSettingIndiFragment);
                    pageTitle.setText("개별 그룹 설정");
                    return true;
                case R.id.tab3:
                    GatewaySettingFragment gatewaySettingFragment = GatewaySettingFragment.newInstance();
                    replaceFragment(gatewaySettingFragment);
                    pageTitle.setText("게이트웨이 설정");
                    return true;
                case R.id.tab4:
                    BeaconCheckFragment beaconCheckFragment = BeaconCheckFragment.newInstance();
                    replaceFragment(beaconCheckFragment);
                    pageTitle.setText("비컨 확인");
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        ButterKnife.bind(this);

        /** PSJ **/
        //region IntentFilter
        // USB 동작 관련 BroadcastReceiver
        intentFilter=new IntentFilter();

        // 기타
        intentFilter.addAction(usbManagement.getAction_Usb_Detached());                 //  Usb 분리
        intentFilter.addAction(usbManagement.getAction_Usb_Init());                     //  Usb Initialize
//        intentFilter.addAction(usbManagement.getAction_Group_DImming_Enable_B());
//        intentFilter.addAction(usbManagement.getAction_Group_Dimming_Disable_B());
        //endregion

        intentFilter.addAction(usbManagement.getAction_Channel_Change());
        intentFilter.addAction(usbManagement.getAction_Group_Setting());
        intentFilter.addAction(usbManagement.getAction_Group_Check());
        intentFilter.addAction(usbManagement.getAction_Setting_Confirm());
        //intentFilter.addAction(usbManagement.getAction_Setting());

        //intentFilter.addAction(usbManagement.getAction_Group_Toggle());
        intentFilter.addAction(usbManagement.getAction_Router_Rejoin());

        intentFilter.addAction(usbManagement.getAction_Gateway_Check());
        intentFilter.addAction(usbManagement.getAction_Gateway_Search());
        intentFilter.addAction(usbManagement.getAction_Gateway_Setting());
        intentFilter.addAction(usbManagement.getAction_Gateway_Rejoin());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        BottomNavigationView navigation =findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fragmentTransaction.add(R.id.fragment_container, ScannerSettingFragment.newInstance()).commit();
        pageTitle.setText("구역 설정");

        String RememberPath=RememberData.getInstance().getSavefilepath().toString();

        if(RememberPath=="NULL" || RememberPath.equals("NULL"))
        {
            txt_FilePath_Whole.setText("");
        }

        // CSV 선택 버튼 Event
        btnSearch.setVisibility(View.VISIBLE);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intent,100);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions( BaseActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG,"REQUEST : "+requestCode+"/"+permissions+"/"+grantResults);

        switch (requestCode) {
            case 1:

                Log.d(TAG,"PERMISSION FIND");
                if(requestCode==-1)
                    android.os.Process.killProcess(android.os.Process.myPid());
                break;

        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            finishAffinity();
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), R.string.press_back_message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(broadcastReceiver==null)
        {
            broadcastReceiver=new usbManagement();
            broadcastReceiver.setListener(this);
            registerReceiver(broadcastReceiver,intentFilter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null)
        {
            Log.d(TAG,"End Broadcast Receiver In BaseActivity, UnregisterReceiver");
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode == 100)
        {
            getTxt_FilePath_Whole.setVisibility(View.VISIBLE);
            txt_FilePath_Whole.setVisibility(View.VISIBLE);
            txt_FilePath_Whole.setText(RememberData.getInstance().getSavefilepath().getName());
        }
    }


    @Override
    public void Result(FragmentValue fragmentValue, boolean Result , String Message) {
        Log.d(TAG,"FRAGMENT : "+fragmentValue+" / Result : "+Result+" / MESSAGE : "+Message);
        if(!Result)
        {
            EditTextErrorCheck editTextErrorCheck=new EditTextErrorCheck();
            editTextErrorCheck.ErrorAlertDialog(BaseActivity.this,fragmentValue+" Error",Message);
        }
        else
        {
            EditTextErrorCheck editTextErrorCheck=new EditTextErrorCheck();
            editTextErrorCheck.ErrorAlertDialog(BaseActivity.this,fragmentValue+" Success",Message);
        }
    }

}
