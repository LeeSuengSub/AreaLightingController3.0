package net.woorisys.lighting.control3.admin.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

public class ScannerSettingIndiFragment extends Fragment {

    //region UI
    TextView pageTitle;
    TextView tvAreaId;
    EditText etGatewayId;
    EditText etGroupCount;
    EditText etAreaId;
    EditText etDeviceLine;
    Button btnCsvGroupCheck;
    Button btnGroupSend;
    Button btnGroupCheck;
    Button btnSetting;
    Button btnUpdate;


//    @BindView(R.id.et_Serial)
//    EditText ET_Serial;

    private final static String TAG="JHLEE";  //

    private int totalDevices = 0;

    private EditTextErrorCheck errorCheck;

    // 생성자
    public ScannerSettingIndiFragment() {

    }

    public static ScannerSettingIndiFragment newInstance() {
        return new ScannerSettingIndiFragment();
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
        View view = inflater.inflate(R.layout.fragment_scanner_indi_setting, container, false);
//        ButterKnife.bind(this, view);

        //레이아웃
        pageTitle=view.findViewById(R.id.page_title);
        tvAreaId=view.findViewById(R.id.tv_area_id);
        etAreaId=view.findViewById(R.id.et_area_id);
        etGatewayId=view.findViewById(R.id.et_gateway_id);
        etGroupCount=view.findViewById(R.id.et_group_count);
        etDeviceLine=view.findViewById(R.id.et_device_line);

        //버튼
        btnCsvGroupCheck=view.findViewById(R.id.btn_csv_group_check);
        btnGroupSend=view.findViewById(R.id.btn_group_send);
        btnGroupCheck=view.findViewById(R.id.btn_group_check);
        btnSetting=view.findViewById(R.id.btn_setting_confirm);
        btnUpdate=view.findViewById(R.id.btn_update);

        pageTitle.setText("개별 구역등 설정");

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
        btnGroupSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etAreaId.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(usbManagement.getAction_Group_Setting());
                intent.putExtra("serial",etAreaId.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        // 그룹 확인
        btnGroupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etAreaId.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(usbManagement.getAction_Group_Check());
                intent.putExtra("serial",etAreaId.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });

        // 적용
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etAreaId.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(usbManagement.getAction_Setting_Confirm());
                intent.putExtra("serial",etAreaId.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });


        //csv에서 정보 가져오기
        btnCsvGroupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etAreaId.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                String selAreaId = String.valueOf(etAreaId.getText());
                Log.d(TAG,"조회 클릭 " + etAreaId.getText());

                File path= RememberData.getInstance().getSavefilepath();
                Log.d(TAG,"path : "+path);

                if(path.toString()=="NULL")
                {
                    Toast.makeText(getContext(),"파일이 선택되어 있지 않습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    totalDevices = countLines(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.d(TAG,"total Device count : "+totalDevices);

                try {
//                    InputStream inputStream = requireContext().getContentResolver().openInputStream(SearchActivity.DefaultUri);
//                    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));

//                    FileInputStream in=new FileInputStream(path);
                    Log.d("디버그", "DefaultUri: " + SearchActivity.DefaultUri.toString());
                    InputStream inputStream = requireContext().getContentResolver().openInputStream(SearchActivity.DefaultUri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            int cnt=0;

                            while (true)
                            {
                                String line;
                                try {
                                    line=reader.readLine();
                                    Log.d("ss1234","line  : "+line);
                                    if (line == null)
                                        break;
                                    String[] columns = line.split(",");
                                    if (columns.length < 2)
                                        continue;

                                    String gateway=columns[0].trim();
                                    String id = columns[1].trim();
                                    String device_count = columns[2].trim();
                                    String device_list_line = line;

                                    //Log.d(TAG,"id : "+id);

                                    cnt++;
                                    // id 가 일치하는 것이 존재 할 경우
                                    if(id.equals(selAreaId) || id == selAreaId )
                                    {

                                        getActivity().runOnUiThread(new Runnable() {
                                            public void run() {
                                                etGatewayId.setText(gateway);
                                                etGroupCount.setText(device_count);
                                                etDeviceLine.setText(device_list_line);
                                            }
                                        });
                                         break;
                                    }

                                    // 일치하는 Serial ID 가 없을 경우
                                    if(totalDevices==cnt)
                                    {
                                        Looper.prepare();
                                        Handler handler=new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getContext(),"일치하는 시리얼 번호가 없습니다. CSV 파일을 확인하여 주세요.",Toast.LENGTH_SHORT).show();
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

//                Intent intent=new Intent(usbManagement.getAction_Group_Check());
//                intent.putExtra("serial",ET_Serial.getText().toString());
//                getActivity().sendBroadcast(intent);
            }
        });


        //csv 수정
        btnUpdate.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(etDeviceLine.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"수정할 내용이 없습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                File path = RememberData.getInstance().getSavefilepath();
                File newFile = new File(Environment.getExternalStorageDirectory(),"Area_Group/new1.csv");

//                Log.d(TAG,"path : "+path);
//                Log.d(TAG,"path1 : "+newFile);

                if(path.toString()=="NULL")
                {
                    Toast.makeText(getContext(),"수정할 파일이 선택되어 있지 않습니다.",Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    FileInputStream in = new FileInputStream(path);
                    FileOutputStream out = new FileOutputStream(newFile, false);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            int cnt=1;

                            while (true)
                            {
                                String line;

                                try {
                                    line=reader.readLine();
                                    if (line == null)
                                        break;
                                    String[] columns = line.split(",");
                                    if (columns.length < 2)
                                        continue;

                                    String gateway=columns[0].trim();
                                    String id = columns[1].trim();
                                    String device_count = columns[2].trim();
                                    String device_list_line = line;


                                    String selId = String.valueOf(etAreaId.getText());
                                    String updateLine = String.valueOf(etDeviceLine.getText());

                                    String[] upDateColumns = updateLine.split(",");
                                    String updateId = upDateColumns[1].trim();

//                                    Log.d(TAG," sel id : "+id);
//                                    Log.d(TAG," updateId id : "+updateId);

                                    // id 가 일치하는 것이 존재 할 경우
                                    if(id.equals(updateId) || id == updateId )
                                    {
                                        Log.d(TAG," 일치!!!!! " + updateLine);
                                        writer.write(updateLine + "\n");
                                        cnt = 0;
                                    }else{
                                        writer.write(line + "\n");
                                        writer.flush();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            // 일치하는 Serial ID 가 없을 경우
                            if(cnt == 1 )
                            {
                                Looper.prepare();
                                Handler handler=new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(),"일치하는 시리얼 번호가 없습니다. 수정에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Looper.loop();
                            }else{
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getContext(),"번호가 수정되었습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            try {
                                writer.close();
                                out.close();
                                reader.close();
                                in.close();
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getContext(),"저장 되었습니다.",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                //파일 덮어쓰기
                                if(newFile.renameTo(path)){
                                    Log.d(TAG," 파일 이름변경 성공");
                                }else{
                                    Log.d(TAG," 파일 이름변경 실패");
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public static int countLines(File aFile) throws IOException {
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(aFile));
            while ((reader.readLine()) != null);
            return reader.getLineNumber();
        } catch (Exception ex) {
            return -1;
        } finally {
            if(reader != null)
                reader.close();
        }
    }
}
