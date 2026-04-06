package net.woorisys.lighting.control3.admin.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.search.SearchActivity;
import net.woorisys.lighting.control3.admin.sjp.EditTextErrorCheck;
import net.woorisys.lighting.control3.admin.sjp.RememberData;
import net.woorisys.lighting.control3.admin.sjp.usbManagement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;


public class ScannerSettingFragment extends Fragment {

    //region UI
    TextView pageTitle;
    Button Btn_Group_Send;
    Button Btn_Group_Check;
    Button Btn_Setting;
    Button Btn_Router_Rejoin;
    Button Btn_Gateway_Rejoin;
    EditText ET_Serial;
    TextView Tv_Result_Group_Setting;
    Button Btn_csv_group_setting_select;

    private final static String TAG = "SJP_DIMMING_TAG";

    private EditTextErrorCheck errorCheck;

    private int totalDevices = 0;

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
        View view = inflater.inflate(R.layout.fragment_scanner_setting, container, false);

        // 레이아웃
        pageTitle                  = view.findViewById(R.id.page_title);
        ET_Serial                  = view.findViewById(R.id.et_Serial);
        Tv_Result_Group_Setting    = view.findViewById(R.id.txt_Result_Group_Setting);

        // 버튼
        Btn_Group_Send             = view.findViewById(R.id.btn_group_send);
        Btn_Group_Check            = view.findViewById(R.id.btn_group_check);
        Btn_Setting                = view.findViewById(R.id.btn_setting_confirm);
        Btn_Router_Rejoin          = view.findViewById(R.id.btn_Router_Rejoin);
        Btn_Gateway_Rejoin         = view.findViewById(R.id.btn_Gateway_Rejoin);
        Btn_csv_group_setting_select = view.findViewById(R.id.btn_csv_group_setting_select);

        // =====================================================
        // Pill Tab: 비컨 확인 탭 클릭 시 BeaconCheckFragment 로 전환
        // =====================================================
        TextView tabBeacon = view.findViewById(R.id.tab_beacon);
        tabBeacon.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, BeaconCheckFragment.newInstance())
                        .commit()
        );

        pageTitle.setText("구역등 설정");

        // CSV 조회
        Btn_csv_group_setting_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ET_Serial.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "시리얼을 입력하여 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String selAreaId = String.valueOf(ET_Serial.getText());
                Log.d(TAG, "조회 클릭 " + ET_Serial.getText());

                File path = RememberData.getInstance().getSavefilepath();
                Log.d(TAG, "path : " + path);

                if (path.toString() == "NULL") {
                    Toast.makeText(getContext(), "파일이 선택되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    totalDevices = countLines(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "total Device count : " + totalDevices);

                try {
                    InputStream inputStream = requireContext().getContentResolver().openInputStream(SearchActivity.DefaultUri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            int cnt = 0;
                            while (true) {
                                String line;
                                try {
                                    line = reader.readLine();
                                    Log.d("ss1234", "line  : " + line);
                                    if (line == null) break;
                                    String[] columns = line.split(",");
                                    if (columns.length < 2) continue;

                                    String id = columns[1].trim();
                                    String device_list_line = line;

                                    cnt++;
                                    if (id.equals(selAreaId) || id == selAreaId) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            public void run() {
                                                Tv_Result_Group_Setting.setText(device_list_line);
                                            }
                                        });
                                        break;
                                    }

                                    if (totalDevices == cnt) {
                                        Looper.prepare();
                                        Handler handler = new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(), "일치하는 시리얼 번호가 없습니다. CSV 파일을 확인하여 주세요.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Looper.loop();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 그룹 전송
        Btn_Group_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ET_Serial.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "시리얼을 입력하여 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(usbManagement.getAction_Group_Setting());
                intent.putExtra("serial", ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        // 그룹 확인
        Btn_Group_Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ET_Serial.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "시리얼을 입력하여 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(usbManagement.getAction_Group_Check());
                intent.putExtra("serial", ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        // 적용
        Btn_Setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ET_Serial.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "시리얼을 입력하여 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(usbManagement.getAction_Setting_Confirm());
                intent.putExtra("serial", ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        Btn_Router_Rejoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(usbManagement.getAction_Router_Rejoin());
                intent.putExtra("serial", ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        Btn_Gateway_Rejoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(usbManagement.getAction_Gateway_Rejoin());
                intent.putExtra("serial", ET_Serial.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        return view;
    }

    public static int countLines(File aFile) throws IOException {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(aFile));
            while ((reader.readLine()) != null) ;
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}