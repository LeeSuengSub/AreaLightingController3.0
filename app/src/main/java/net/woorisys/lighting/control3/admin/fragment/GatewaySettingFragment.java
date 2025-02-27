package net.woorisys.lighting.control3.admin.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.woorisys.lighting.control3.admin.R;
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
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;


public class GatewaySettingFragment extends Fragment {

    private final static String TAG="GATEWAY";  //

    //region UI
    TextView pageTitle;
    EditText etGatewayId;
    EditText etGatewayMac;
    EditText etGatewayIp;
    EditText etServerIp;
    EditText etServerPort;
    EditText etSubnetIp;
    EditText etServerGatewayIp;
    EditText etDeviceLine;

    Button btnCsvDeviceCheck;
    Button btnGwSettingSend;
    Button btnGwSettingCheck;
    Button btnUpdate;

    private int totalDevices = 0;

    // 생성자
    public GatewaySettingFragment() {
    }

    public static GatewaySettingFragment newInstance() {
        return new GatewaySettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_gateway_setting, container, false);
//        ButterKnife.bind(this, view);

        //레이아웃
        pageTitle=view.findViewById(R.id.page_title);
        etGatewayId=view.findViewById(R.id.et_device_id);
        etGatewayMac=view.findViewById(R.id.et_gateway_mac);
        etGatewayIp=view.findViewById(R.id.et_gateway_ip);
        etServerIp=view.findViewById(R.id.et_server_ip);
        etServerPort=view.findViewById(R.id.et_server_port);
        etSubnetIp=view.findViewById(R.id.et_subnet_ip);
        etServerGatewayIp=view.findViewById(R.id.et_server_gateway_ip);
        etDeviceLine=view.findViewById(R.id.et_device_line);

        //버튼
        btnCsvDeviceCheck=view.findViewById(R.id.btn_csv_device_check);
        btnGwSettingSend=view.findViewById(R.id.btn_gw_setting_Send);
        btnGwSettingCheck=view.findViewById(R.id.btn_gw_setting_check);
        btnUpdate=view.findViewById(R.id.btn_update);


        pageTitle.setText("게이트웨이 설정");

        // 게이트웨이 셋팅 전송
        btnGwSettingSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etGatewayId.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(usbManagement.getAction_Gateway_Setting());
                intent.putExtra("serial",etGatewayId.getText().toString());
                intent.putExtra("send_line",etDeviceLine.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });
        
        //게이트웨이 정보 확인
        btnGwSettingCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etGatewayId.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(usbManagement.getAction_Gateway_Check());
                intent.putExtra("serial",etGatewayId.getText().toString());
                getActivity().sendBroadcast(intent);
            }
        });


        //csv에서 게이트웨이 정보 가져오기
        btnCsvDeviceCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etGatewayId.getText().toString().isEmpty())
                {
                    Toast.makeText(getContext(),"시리얼을 입력하여 주세요",Toast.LENGTH_SHORT).show();
                    return;
                }
                String selAreaId = String.valueOf(etGatewayId.getText());
                Log.d(TAG,"조회 클릭 " + etGatewayId.getText());

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
                    FileInputStream in=new FileInputStream(path);
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            int cnt=0;

                            while (true)
                            {
                                String line;
                                int index = 4;

                                try {
                                    line=reader.readLine();
                                    if (line == null)
                                        break;
                                    String[] columns = line.split(",");
                                    if (columns.length < 2)
                                        continue;

                                    String id=columns[0].trim();
                                    String mac_id = columns[index++].trim() +"."+ columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim()+ columns[index++].trim()+"."+columns[index++].trim();
                                    String device_ip = columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim();
                                    String server_ip = columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim();
                                    String server_port = columns[index++].trim();
                                    String net_mask = columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim();
                                    String gateway_ip = columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim()+"."+columns[index++].trim();
                                    String msgLine = line;

                                    //Log.d(TAG,"id : "+id);

                                    cnt++;
                                    // id 가 일치하는 것이 존재 할 경우
                                    if(id.equals(selAreaId) || id == selAreaId )
                                    {
                                        getActivity().runOnUiThread(new Runnable() {
                                            public void run() {
                                                etGatewayId.setText(id);
                                                etGatewayMac.setText(mac_id);
                                                etGatewayIp.setText(device_ip);
                                                etServerIp.setText(server_ip);
                                                etServerPort.setText(server_port);
                                                etSubnetIp.setText(net_mask);
                                                etServerGatewayIp.setText(gateway_ip);
                                                etDeviceLine.setText(msgLine);
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

                } catch (FileNotFoundException e) {
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
                                int index = 4;

                                try {
                                    line=reader.readLine();
                                    if (line == null)
                                        break;
                                    String[] columns = line.split(",");
                                    if (columns.length < 2)
                                        continue;

                                    String id=columns[0].trim();

                                    String selId = String.valueOf(etGatewayId.getText());
                                    String updateLine = String.valueOf(etDeviceLine.getText());

                                    String[] upDateColumns = updateLine.split(",");
                                    String updateId = upDateColumns[0].trim();

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
