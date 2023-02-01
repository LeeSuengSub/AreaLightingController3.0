package net.woorisys.lighting.control3.admin.sjp.usb.pdu.response;

import android.util.Log;

public class ResponseInterruptState extends ResponsePDUBase {


    private final static String TAG="SJP_DECODE_ERROR";

    String enable;
    @Override
    public boolean decode(String message) {

        Log.d(TAG,"MESSAGE : "+message);
        if(decodeInternal(message))
        {
            if(result)
            {
                String[] fields=rawContent.split(SEPARATOR);

                if(fields.length!=2)
                {
                    Log.d(TAG,"Invalid number of fields.(number of fields = " + fields.length + ")");
                    return false;
                }

                enable=fields[1];

                Log.d(TAG,"ENABLE : "+enable);
            }
            return true;
        }

        return false;
    }


    @Override
    public String toString() {

        String Message="";

        if(enable=="0" || enable.equals("0"))
            Message="Disable";
        else
            Message="Enable";

        return "동작 : "+Message;
    }
}
