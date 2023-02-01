package net.woorisys.lighting.control3.admin.sjp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import net.woorisys.lighting.control3.admin.sjp.observer.BroadcastReceiverListener;
import net.woorisys.lighting.control3.admin.sjp.observer.FragmentValue;
import net.woorisys.lighting.control3.admin.sjp.usb.DefaultUSBDeviceManager;
import net.woorisys.lighting.control3.admin.sjp.usb.SerialSettings;
import net.woorisys.lighting.control3.admin.sjp.usb.USBTerminalException;
import net.woorisys.lighting.control3.admin.sjp.usb.pdu.response.GroupResponsePDU2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class usbManagement extends BroadcastReceiver {

    private final static String TAG="JH_USB_DDD";

    /** 장비 일괄 설정 전체 대상 장비 개수 */
    private int totalDevices = 0;
    /** Progress 진행을 메인 쓰레드에 알리기위하여 사용하는 상수 값 */
    private static final int WHAT_LOAD_GROUP_CSV_INC = 20;
    /** Progress 종료를 메인 쓰레드에 알리기위하여 사용하는 상수 값 */
    private static final int WHAT_LOAD_GROUP_CSV_END = 21;


    /** 동글 채널 변경 **/
    @Getter private final static String Action_Channel_Change="dongle.chanel.change";
    /** 구역등 그룹 설정 **/
    @Getter private final static String Action_Group_Setting="area.group.setting";
    /** 구역등 그룹 확인 **/
    @Getter private final static String Action_Group_Check="area.group.check";
    /** 구역등 그룹 적용 **/
    @Getter private final static String Action_Setting_Confirm="area.setting.confirm";
    /** 라우터 리조인 **/
    @Getter private final static String Action_Router_Rejoin="router.rejoin";

    /** 게이트웨이 셋팅 **/
    @Getter private final static String Action_Gateway_Search = "gateway.search";
    @Getter private final static String Action_Gateway_Setting = "gateway.setting";
    @Getter private final static String Action_Gateway_Check = "gateway.check";

    //게이트웨이 초기화 하여 라우터가 다시 조인할 수 있도록 한다.
    @Getter private final static String Action_Gateway_Rejoin = "gateway.rejoin";

    // USB 분리
    @Getter private final static String Action_Usb_Detached=UsbManager.ACTION_USB_DEVICE_DETACHED;
    // USB 연결
    @Getter private final static String Action_Usb_Init="com.android.psj.usb.init";

    /** 공통 **/
    @Getter private final static String MacID="MACID";

    // USB 통신에 필요한 Class
    private static DefaultUSBDeviceManager usbDeviceManager;
    private UsbManager usbManager;
    private SerialSettings serialSettings;


    @Override
    public void onReceive(final Context context, final Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String Action = intent.getAction();

        String gateway = null;

        File path=null;

        USBInit(context);

        switch (Action)
        {
            //region 채널 변경
            case Action_Channel_Change:
                try
                {
                    if(usbDeviceManager.ChannelChange(intent.getExtras().getString("channel")))
                    {
                        listener.Result(FragmentValue.ScannerSetting,true,"요청이 정상적으로 처리되었습니다.");
                    }
                    else
                    {
                        listener.Result(FragmentValue.ScannerSetting,false,"요청이 정상적으로 처리되지 않았습니다.");
                    }
                }
                catch (NullPointerException e)
                {
                    listener.Result(FragmentValue.ScannerSetting,false,e.getMessage());
                }
                break;
            //endregion

            //region 그룹 전송
            case Action_Group_Setting:

                String Serial_Send=intent.getExtras().getString("serial");

                // 받은 값이 없을 때 처리해주면 X
                if(Serial_Send.isEmpty() )
                {
                    listener.Result(FragmentValue.ScannerSetting,false,"입력되지 않은 값이 있습니다. 확인하여 주세요.");
                    return;
                }

                path=RememberData.getInstance().getSavefilepath();
                Log.d(TAG,"path : "+path);
                if(path.toString()=="NULL")
                {
                    listener.Result(FragmentValue.ScannerSetting,false,"파일이 선택되어 있지 않습니다.");
                    return;
                }
                try {
                    FileInputStream in=new FileInputStream(path);
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));

                    new Thread(){
                        @Override
                        public void run() {
                            super.run();

                            int Count=0;

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

                                    String id = columns[1].trim(); //구역등
                                    String gateway=columns[0].trim(); //게이트웨이
                                    int length =Integer.valueOf(columns[2].trim()); //data length
                                    int total_len =length * 2; //data length
                                    String[] memberIds = new String[length]; //주차면 ID
                                    String[] memberTypes = new String[length]; //주차면 type

                                    Log.d(TAG,"ID : "+id+" / SIZE : "+(total_len));
                                    Count++;
                                    // id 가 일치하는 것이 존재 할 경우
                                    if(id.equals(Serial_Send) || id==Serial_Send)
                                    {
                                        int temp = 0;
                                        for(int i=3; i<total_len+3;i=i+2)
                                        {
                                            String member = columns[i].trim();
                                            String type = columns[i+1].trim();

                                            memberIds[temp] = member;
                                            memberTypes[temp] = type;
                                            temp++;
                                        }

                                        setDongleChannel(gateway);


                                        if(usbDeviceManager.GroupSendCheck(gateway,Serial_Send,memberIds,memberTypes))
                                        {
                                            Looper.prepare();
                                            Handler handler=new Handler();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.Result(FragmentValue.ScannerSetting,true,"요청이 정상적으로 처리되었습니다."+gateway+","+Serial_Send);
                                                }
                                            });
                                            Looper.loop();
                                        }
                                        else
                                        {
                                            Looper.prepare();
                                            Handler handler=new Handler();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.Result(FragmentValue.ScannerSetting,false,"요청이 정상적으로 처리되지 않았습니다.");
                                                }
                                            });
                                            Looper.loop();
                                        }
                                        break;
                                    }

                                    // 일치하는 Serial ID 가 없을 경우
                                    if(totalDevices==Count)
                                    {
                                        Looper.prepare();
                                        Handler handler=new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.Result(FragmentValue.ScannerSetting,false,"일치하는 시리얼 번호가 없습니다. CSV 파일을 확인하여 주세요.");
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
                break;
            //endregion

            //region 그룹 확인
            case Action_Group_Check:

                String Serial_Check=intent.getExtras().getString("serial");

                path=RememberData.getInstance().getSavefilepath();
                Log.d(TAG,"path : "+path);
                if(path.toString()=="NULL")
                {
                    listener.Result(FragmentValue.ScannerSetting,false,"파일이 선택되어 있지 않습니다.");
                    return;
                }
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

                                try {
                                    line=reader.readLine();
                                    if (line == null)
                                        break;
                                    String[] columns = line.split(",");
                                    if (columns.length < 2)
                                        continue;

                                    String gateway=columns[0].trim();
                                    String id = columns[1].trim();

                                    cnt++;
                                    // id 가 일치하는 것이 존재 할 경우

                                    if(id.equals(Serial_Check) || id==Serial_Check)
                                    {
                                        setDongleChannel(gateway);
                                        GroupResponsePDU2 groupRes=usbDeviceManager.sendGroupRequest2(gateway,Serial_Check);
                                        Log.d(TAG,"PDU 2 Result : "+groupRes.getRawContent());

                                        try
                                        {
                                            if(groupRes.getRawContent()!=null)
                                            {
                                                String [] parsing=groupRes.getRawContent().split(",");
                                                String Count=parsing[1];
                                                String Group="그룹 : ";
                                                for(int i=2; i<Integer.valueOf(Count)*2+2;i++)
                                                {
                                                    Log.d(TAG,"PDU ITEM : "+parsing[i]);
                                                    Group+=parsing[i];

                                                    if(i!=Integer.valueOf(Count)*2+1)
                                                    {
                                                        Group+=",";
                                                    }
                                                }

                                                String Result=" 전체 그룹 갯수 : "+Count+"\n"+Group;
                                                Log.d(TAG,"PDU RESULT : "+Result);

                                                Looper.prepare();
                                                Handler handler=new Handler();
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.Result(FragmentValue.ScannerSetting,true,Result);
                                                    }
                                                });
                                                Looper.loop();
                                            }
                                            else
                                            {
                                                Looper.prepare();
                                                Handler handler=new Handler();
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        listener.Result(FragmentValue.ScannerSetting,false,"수집에 실패하였습니다.");
                                                    }
                                                });
                                                Looper.loop();

                                                return;
                                            }
                                        }
                                        catch (NullPointerException e)
                                        {
                                            Looper.prepare();
                                            Handler handler=new Handler();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.Result(FragmentValue.ScannerSetting,false,e.getMessage());
                                                }
                                            });
                                            Looper.loop();
                                        }
                                    }

                                    // 일치하는 Serial ID 가 없을 경우
                                    if(totalDevices==cnt)
                                    {
                                        Looper.prepare();
                                        Handler handler=new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.Result(FragmentValue.ScannerSetting,false,"일치하는 시리얼 번호가 없습니다. CSV 파일을 확인하여 주세요.");
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
                break;
            //endregion

            // region 그룹 적용
            case Action_Setting_Confirm:

                String serial=intent.getExtras().getString("serial");

                Log.d(TAG, " Serial_Setting : "+intent.getExtras().getString("serial"));

                path=RememberData.getInstance().getSavefilepath();
                Log.d(TAG,"path : "+path);
                if(path.toString()=="NULL")
                {
                    listener.Result(FragmentValue.ScannerSetting,false,"파일이 선택되어 있지 않습니다.");
                    return;
                }
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

                                try {
                                    line=reader.readLine();
                                    if (line == null)
                                        break;
                                    String[] columns = line.split(",");
                                    if (columns.length < 2)
                                        continue;

                                    String id = columns[1].trim();
                                    String gateway=columns[0].trim();

                                    cnt++;
                                    // id 가 일치하는 것이 존재 할 경우
                                    if(id.equals(serial) || id==serial)
                                    {
                                        setDongleChannel(gateway);
                                        usbDeviceManager.Setting(gateway, serial);
                                    }

                                    // 일치하는 Serial ID 가 없을 경우
                                    if(totalDevices==cnt)
                                    {
                                        Looper.prepare();
                                        Handler handler=new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.Result(FragmentValue.ScannerSetting,true,"일치하는 시리얼 번호가 없습니다. CSV 파일을 확인하여 주세요.");
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

                break;
            //endregion

            case Action_Gateway_Rejoin:
                Log.d(TAG, "GW Serial_Setting : "+intent.getExtras().getString("serial"));
                gateway=intent.getExtras().getString("serial");
                String sendRejoinMsg = gateway+",0000,"+gateway+",REJOIN,1;";
                setDongleChannel(gateway);
                String recvRejoinMsg_check=usbDeviceManager.sendCommand(sendRejoinMsg);
                Log.d(TAG,"Gateway SendMsg : "+sendRejoinMsg);
                listener.Result(FragmentValue.ScannerSetting,true,gateway+" 초기화가 요청이 정상적으로 처리되었습니다.");
                break;
            
            case Action_Gateway_Setting:
                gateway=intent.getExtras().getString("serial");
                String sendLine=intent.getExtras().getString("send_line");
                setDongleChannel(gateway);
                String recvMsg_setting=usbDeviceManager.sendCommand(sendLine);

                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        while (true)
                        {
                            if(recvMsg_setting!=null)
                            {

                                Log.d(TAG,"PDU RESULT : "+recvMsg_setting);

                                Looper.prepare();
                                Handler handler=new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.Result(FragmentValue.GatewaySetting,true,recvMsg_setting);
                                    }
                                });
                                Looper.loop();
                            }
                            else
                            {
                                Looper.prepare();
                                Handler handler=new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.Result(FragmentValue.GatewaySetting,false,"수집에 실패하였습니다.");
                                    }
                                });
                                Looper.loop();
                            }
                        }

                    }
                }.start();

                break;

            case Action_Gateway_Check:
                gateway=intent.getExtras().getString("serial");
                String sendMsg = gateway+",0000,"+gateway+",GET_ETH;";
                setDongleChannel(gateway);
                String recvMsg_check=usbDeviceManager.sendCommand(sendMsg);

                Log.d(TAG,"Gateway SendMsg : "+sendMsg);
                Log.d(TAG,"Gateway Recv Result : "+recvMsg_check);

                new Thread(){
                    @Override
                    public void run() {
                        super.run();

                        while (true)
                        {
                            if(recvMsg_check!=null)
                            {

                                Log.d(TAG,"PDU RESULT : "+recvMsg_check);

                                Looper.prepare();
                                Handler handler=new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.Result(FragmentValue.GatewaySetting,true,recvMsg_check);
                                    }
                                });
                                Looper.loop();
                            }
                            else
                            {
                                Looper.prepare();
                                Handler handler=new Handler();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.Result(FragmentValue.GatewaySetting,false,"수집에 실패하였습니다.");
                                    }
                                });
                                Looper.loop();
                            }
                        }
                    }
                }.start();
                break;

            // USB Device 와 분리
            case Action_Usb_Detached:

                try
                {
                    System.runFinalization();
                    System.exit(0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                catch (WindowManager.BadTokenException e)
                {
                    Log.e(TAG,"BadTokenException Error Detached USB : "+e.getMessage());
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                break;

            case Action_Usb_Init:
                USBInit(context);
                break;

            case Action_Router_Rejoin:

                Log.d(TAG, " Serial_Setting : "+intent.getExtras().getString("serial"));

                serial=intent.getExtras().getString("serial");

                path=RememberData.getInstance().getSavefilepath();
                Log.d(TAG,"path : "+path);
                if(path.toString()=="NULL")
                {
                    listener.Result(FragmentValue.ScannerSetting,false,"파일이 선택되어 있지 않습니다.");
                    return;
                }
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

                                try {
                                    line=reader.readLine();
                                    if (line == null)
                                        break;
                                    String[] columns = line.split(",");
                                    if (columns.length < 2)
                                        continue;

                                    String id = columns[1].trim();
                                    String gateway=columns[0].trim();

                                    cnt++;
                                    // id 가 일치하는 것이 존재 할 경우
                                    if(id.equals(serial) || id==serial)
                                    {
                                        setDongleChannel(gateway);
                                        if(usbDeviceManager.sendRouterRejoin(id))
                                        {
                                            Looper.prepare();
                                            Handler handler=new Handler();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.Result(FragmentValue.ScannerSetting,true,"요청이 정상적으로 처리되었습니다.");
                                                }
                                            });
                                            Looper.loop();
                                        }
                                        else
                                        {
                                            Looper.prepare();
                                            Handler handler=new Handler();
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    listener.Result(FragmentValue.ScannerSetting,false,"요청이 정상적으로 처리되지 않았습니다.");
                                                }
                                            });
                                            Looper.loop();
                                        }
                                        //Router_Rejoin(serial);
                                    }

                                    // 일치하는 Serial ID 가 없을 경우
                                    if(totalDevices==cnt)
                                    {
                                        Looper.prepare();
                                        Handler handler=new Handler();
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                listener.Result(FragmentValue.ScannerSetting,true,"일치하는 시리얼 번호가 없습니다. CSV 파일을 확인하여 주세요.");
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
                break;
        }
    }

    // USB 관련 명령어들
    private void USBInit(Context context)
    {
        usbManager=(UsbManager)context.getSystemService(Context.USB_SERVICE);


        if(usbManager!=null && usbDeviceManager==null)
        {
            usbDeviceManager=new DefaultUSBDeviceManager(context,handler,usbManager);
            HashMap<String, UsbDevice> deviceList=usbManager.getDeviceList();
            Set key = deviceList.keySet();

            try
            {
                for (Iterator iterator = key.iterator(); iterator.hasNext();) {
                    String keyName = (String) iterator.next();
                    UsbDevice valueName = deviceList.get(keyName);
                    prepareDevice(valueName,context);
                }
            }
            catch (NullPointerException e)
            {
                Toast.makeText(context,"USB 연결을 확인하여 주세요.",Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
        }
    }

    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private boolean prepareDevice(UsbDevice device,Context context) {
        serialSettings = new SerialSettings();
        serialSettings.baudRate = 115200;
        serialSettings.dataBits = 8;
        serialSettings.handShaking = "None";
        serialSettings.parity = "None";
        serialSettings.stopBits = 1;

        try {

            usbDeviceManager.initDevice(context, usbManager, device, serialSettings);
        } catch (USBTerminalException e) {
            Log.e(TAG,"ERROR : "+e.getMessage());
        }
        catch (NullPointerException e)
        {
            Log.e(TAG,"ERROR : "+e.getMessage());
        }
        return true;
    }

    private void Router_Rejoin(String id)
    {
        try
        {
            if (usbDeviceManager.sendRouterRejoin(id)) {
                listener.Result(FragmentValue.ScannerSetting,true,"요청이 정상적으로 처리되었습니다.");
            } else {
                listener.Result(FragmentValue.ScannerSetting,false,"요청이 정상적으로 처리되지 않았습니다.");
            }
        }
        catch (NullPointerException e)
        {
            Log.d(TAG,"NULL : "+e.getMessage());
        }
    }

    public void setDongleChannel(String id)
    {
        //채널을 먼저 변경한다.
        //광교에서 잘못된거 5001 채널이 17이다
        if(id.equals("5001") ||  id.equals("5017") ||  id.equals("5033")){
            usbDeviceManager.ChannelChange("11");
        }else if(id.equals("5002") ||  id.equals("5018") ||  id.equals("5034")){
            usbDeviceManager.ChannelChange("12");
        }else if(id.equals("5003") ||  id.equals("5019") ||  id.equals("5035")){
            usbDeviceManager.ChannelChange("13");
        }else if(id.equals("5004") ||  id.equals("5020") ||  id.equals("5036")){
            usbDeviceManager.ChannelChange("14");
        }else if(id.equals("5005") ||  id.equals("5021") ||  id.equals("5037")){
            usbDeviceManager.ChannelChange("15");
        }else if(id.equals("5006") ||  id.equals("5022") ||  id.equals("5038")){
            usbDeviceManager.ChannelChange("16");
        }else if(id.equals("5007") ||  id.equals("5023") ||  id.equals("5039")){
            usbDeviceManager.ChannelChange("17");
        }else if(id.equals("5008") ||  id.equals("5024") ||  id.equals("5040")){
            usbDeviceManager.ChannelChange("18");
        }else if(id.equals("5009") ||  id.equals("5025") ||  id.equals("5041")){
            usbDeviceManager.ChannelChange("19");
        }else if(id.equals("5010") ||  id.equals("5026") ||  id.equals("5042")){
            usbDeviceManager.ChannelChange("20");
        }else if(id.equals("5011") ||  id.equals("5027") ||  id.equals("5043")){
            usbDeviceManager.ChannelChange("21");
        }else if(id.equals("5012") ||  id.equals("5028") ||  id.equals("5044")){
            usbDeviceManager.ChannelChange("22");
        }else if(id.equals("5013") ||  id.equals("5029") ||  id.equals("5045")){
            usbDeviceManager.ChannelChange("23");
        }else if(id.equals("5014") ||  id.equals("5030") ||  id.equals("5046")){
            usbDeviceManager.ChannelChange("24");
        }else if(id.equals("5015") ||  id.equals("5031") ||  id.equals("5047")){
            usbDeviceManager.ChannelChange("25");
        }else if(id.equals("5016") ||  id.equals("5032") ||  id.equals("5048")){
            usbDeviceManager.ChannelChange("26");
        }
    }

    @Setter
    BroadcastReceiverListener listener;
}
