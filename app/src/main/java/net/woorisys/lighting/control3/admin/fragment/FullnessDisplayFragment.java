package net.woorisys.lighting.control3.admin.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.sjp.observer.BroadcastReceiverListener;
import net.woorisys.lighting.control3.admin.sjp.observer.FragmentValue;
import net.woorisys.lighting.control3.admin.sjp.observer.ResultValue;
import net.woorisys.lighting.control3.admin.sjp.usbManagement;


public class FullnessDisplayFragment extends Fragment implements BroadcastReceiverListener {

    //region UI
    TextView pageTitle;
    EditText etGatewayId;
    Button Btn_display_setting;
    Button Btn_display_getting;
    EditText first_data_et;
    EditText second_data_et;
    EditText third_data_et;
    EditText fourth_data_et;
    EditText fifth_data_et;
    EditText sixth_data_et;
    EditText seventh_data_et;
    EditText eighth_data_et;
    EditText ninth_data_et;
    EditText tenth_data_et;

    private String recvMsg = null;

    // 생성한 Broadcast Action 동작 시키기 위한 BroadcastReceiver 등록
    private usbManagement broadcastReceiver;
    IntentFilter intentFilter;
    ResultValue resultValue;

    // 생성자
    public FullnessDisplayFragment() {
    }

    public static FullnessDisplayFragment newInstance() {
        return new FullnessDisplayFragment();
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
        View view = inflater.inflate(R.layout.fragment_fullness_display, container, false);

        // 레이아웃 바인딩
        pageTitle           = view.findViewById(R.id.page_title);
        etGatewayId         = view.findViewById(R.id.et_gateway_id);
        Btn_display_setting = view.findViewById(R.id.btn_display_setting);
        Btn_display_getting = view.findViewById(R.id.btn_display_getting);
        first_data_et       = view.findViewById(R.id.first_data_et);
        second_data_et      = view.findViewById(R.id.second_data_et);
        third_data_et       = view.findViewById(R.id.third_data_et);
        fourth_data_et      = view.findViewById(R.id.fourth_data_et);
        fifth_data_et       = view.findViewById(R.id.fifth_data_et);
        sixth_data_et       = view.findViewById(R.id.sixth_data_et);
        seventh_data_et     = view.findViewById(R.id.seventh_data_et);
        eighth_data_et      = view.findViewById(R.id.eighth_data_et);
        ninth_data_et       = view.findViewById(R.id.ninth_data_et);
        tenth_data_et       = view.findViewById(R.id.tenth_data_et);

        // 폰트 크기 설정
        etGatewayId.setTextSize(20);
        first_data_et.setTextSize(20);
        second_data_et.setTextSize(20);
        third_data_et.setTextSize(20);
        fourth_data_et.setTextSize(20);
        fifth_data_et.setTextSize(20);
        sixth_data_et.setTextSize(20);
        seventh_data_et.setTextSize(20);
        eighth_data_et.setTextSize(20);
        ninth_data_et.setTextSize(20);
        tenth_data_et.setTextSize(20);

        // 기본값 설정
        etGatewayId.setText("5100");
        first_data_et.setText("500");
        second_data_et.setText("500");
        third_data_et.setText("500");
        fourth_data_et.setText("500");
        fifth_data_et.setText("500");
        sixth_data_et.setText("500");
        seventh_data_et.setText("500");
        eighth_data_et.setText("500");
        ninth_data_et.setText("500");
        tenth_data_et.setText("500");

        pageTitle.setText("표시 설정");

        // IntentFilter 등록
        intentFilter = new IntentFilter();
        intentFilter.addAction(usbManagement.getAction_Usb_Detached());
        intentFilter.addAction(usbManagement.getAction_Usb_Init());
        intentFilter.addAction(usbManagement.getAction_Fullness_Display());
        intentFilter.addAction(usbManagement.getAction_Fullness_Get_Info());
        intentFilter.addAction(usbManagement.getAction_Gateway_Check());
        intentFilter.addAction(usbManagement.getAction_Gateway_Search());
        intentFilter.addAction(usbManagement.getAction_Gateway_Setting());

        if (broadcastReceiver == null) {
            broadcastReceiver = new usbManagement();
            broadcastReceiver.setListener(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireContext().registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_EXPORTED);
            } else {
                requireContext().registerReceiver(broadcastReceiver, intentFilter);
            }
        }

        // 데이터 확인 버튼
        Btn_display_getting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pdu", "fullness get info");
                String gateway = etGatewayId.getText().toString();

                Intent intent = new Intent(usbManagement.getAction_Fullness_Get_Info());
                intent.putExtra("serial", gateway);
                getActivity().sendBroadcast(intent);
            }
        });

        // 데이터 전송 버튼
        Btn_display_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gateway = etGatewayId.getText().toString();

                if (gateway.isEmpty()) {
                    Toast.makeText(getContext(), "만차표시기(GW) ID를 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (first_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "1번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (second_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "2번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (third_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "3번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (fourth_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "4번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (fifth_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "5번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (sixth_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "6번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (seventh_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "7번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (eighth_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "8번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (ninth_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "9번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tenth_data_et.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "10번 값을 입력해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(usbManagement.getAction_Fullness_Display());
                intent.putExtra("serial",  gateway);
                intent.putExtra("first",   first_data_et.getText().toString());
                intent.putExtra("second",  second_data_et.getText().toString());
                intent.putExtra("third",   third_data_et.getText().toString());
                intent.putExtra("fourth",  fourth_data_et.getText().toString());
                intent.putExtra("fifth",   fifth_data_et.getText().toString());
                intent.putExtra("sixth",   sixth_data_et.getText().toString());
                intent.putExtra("seventh", seventh_data_et.getText().toString());
                intent.putExtra("eighth",  eighth_data_et.getText().toString());
                intent.putExtra("ninth",   ninth_data_et.getText().toString());
                intent.putExtra("tenth",   tenth_data_et.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Fragment 종료 시 BroadcastReceiver 해제 (메모리 누수 방지)
        if (broadcastReceiver != null) {
            requireContext().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
    }

    @Override
    public void Result(FragmentValue fragmentValue, boolean Result, String Message) {
        if (fragmentValue == FragmentValue.GatewaySetting) {
            Log.d("pdu", "리스너 받은곳");
            // Message를 쉼표/세미콜론으로 파싱하여 각 EditText에 값 세팅
            if (Message.split("[,;]").length > 22) {
                String[] split = Message.split("[,;]");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        first_data_et.setText(split[4]);
                        second_data_et.setText(split[6]);
                        third_data_et.setText(split[8]);
                        fourth_data_et.setText(split[10]);
                        fifth_data_et.setText(split[12]);
                        sixth_data_et.setText(split[14]);
                        seventh_data_et.setText(split[16]);
                        eighth_data_et.setText(split[18]);
                        ninth_data_et.setText(split[20]);
                        tenth_data_et.setText(split[22]);
                    }
                });
            }
        }
    }
}