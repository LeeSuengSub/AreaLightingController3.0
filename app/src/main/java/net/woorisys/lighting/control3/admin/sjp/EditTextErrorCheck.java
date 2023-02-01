package net.woorisys.lighting.control3.admin.sjp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;


public class EditTextErrorCheck {

    public String NullCheck(String MAX,String MIN , String ON , String OFF ,  String MainTain)
    {
        String result="";

        boolean MaxCheck    =   TextUtils.isEmpty(MAX);
        boolean MINCheck    =   TextUtils.isEmpty(MIN);
        boolean ONCheck     =   TextUtils.isEmpty(ON);
        boolean OFFCheck    =   TextUtils.isEmpty(OFF);
        boolean MainTainCheck=  TextUtils.isEmpty(MainTain);

        if(MaxCheck)
            result+="최대밝기,";
        if(MINCheck)
            result+="최소밝기,";
        if(ONCheck)
            result+="On 디밍,";
        if(OFFCheck)
            result+="Off 디밍,";
        if(MainTainCheck)
            result+="디밍유지,";

        int endIndex=result.lastIndexOf(",");
        if(endIndex!=-1)
            result=result.substring(0,endIndex);

        return result;
    }

    public String MaxValue(String MAX,String MIN , String ON , String OFF ,  String MainTain)
    {
        String result="";
        int MAX100=100;
        int MAX99=99;
        int MAX60=60;

        if(!MAX.isEmpty())
        {
            if(Integer.valueOf(MAX)>MAX100)
            {
                result+="최대 밝기,";
            }
        }

        if(!MIN.isEmpty())
        {
            if(Integer.valueOf(MIN)>MAX99)
            {
                result+="최소 밝기,";
            }
        }

        if(!ON.isEmpty())
        {
            if(Integer.valueOf(ON)>MAX60)
            {
                result+="ON 디밍,";
            }
        }

        if(!OFF.isEmpty())
        {
            if(Integer.valueOf(OFF)>MAX60)
                result+="OFF 디밍,";
        }

        if(!MainTain.isEmpty())
        {
            if(Integer.valueOf(MainTain)>MAX60)
                result+="디밍 유지";
        }

        // 마지막 문장 끝에 , 를 제거해준다.
        int endIndex=result.lastIndexOf(",");
        if(endIndex!=-1)
            result=result.substring(0,endIndex);
        return result;
    }

    public void ErrorAlertDialog(Context context,String Title,String Message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(Title);
        builder.setMessage(Message);
        builder.setCancelable(false);
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
