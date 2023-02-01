package net.woorisys.lighting.control3.admin.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.sjp.EditTextErrorCheck;
import net.woorisys.lighting.control3.admin.sjp.usbManagement;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScannerSettingFragment extends Fragment {

    //region UI
    @BindView(R.id.page_title)
    TextView pageTitle;


    @BindView(R.id.btn_group_send)
    Button Btn_Group_Send;
    @BindView(R.id.btn_group_check)
    Button Btn_Group_Check;
    @BindView(R.id.btn_setting_confirm)
    Button Btn_Setting;

//    @BindView(R.id.btn_Group_Togle)
//    Button Btn_Group_Togle;
    @BindView(R.id.btn_Router_Rejoin)
    Button Btn_Router_Rejoin;

    @BindView(R.id.btn_Gateway_Rejoin)
    Button Btn_Gateway_Rejoin;

    @BindView(R.id.et_Serial)
    EditText ET_Serial;

    private final static String TAG="SJP_DIMMING_TAG";  //  Dimming Setting Fragment Tag

    private EditTextErrorCheck errorCheck;

    // 생성자
    public ScannerSettingFragment() {

    }

    public static ScannerSettingFragment newInstance() {
        return new ScannerSettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_scanner_setting, container, false);
        ButterKnife.bind(this, view);
        pageTitle.setText("구역등 설정");

        // 채널 변경
        // 채널 설정
//        Btn_ChannelSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(ET_ChannelSetting.getText().toString().isEmpty())
//                {
//                    Toast.makeText(getContext(),"채널을 입력하여 주세요~",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                Intent intent=new Intent(usbManagement.getAction_Channel_Change());
//                intent.putExtra("channel",ET_ChannelSetting.getText().toString());
//                getActivity().sendBroadcast(intent);
//            }
//        });

        // 그룹 전송
        Btn_Group_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ET_Serial.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(usbManagement.getAction_Group_Setting());
                intent.putExtra("serial",ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        // 그룹 확인
        Btn_Group_Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ET_Serial.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(usbManagement.getAction_Group_Check());
                intent.putExtra("serial",ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        // 적용
        Btn_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ET_Serial.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(usbManagement.getAction_Setting_Confirm());
                intent.putExtra("serial",ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

//        Btn_Group_Togle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(usbManagement.getAction_Group_Toggle());
//                getActivity().sendBroadcast(intent);
//            }
//        });

        Btn_Router_Rejoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(usbManagement.getAction_Router_Rejoin());
                intent.putExtra("serial",ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        Btn_Gateway_Rejoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(usbManagement.getAction_Gateway_Rejoin());
                intent.putExtra("serial",ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        return view;
    }
}
