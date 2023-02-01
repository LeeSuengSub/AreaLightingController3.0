package net.woorisys.lighting.control3.admin.sjp.sharedpreference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SharedPreferenceManage {

    String TAG="SJP_SharedPreference_Tag";

    String SharedPreferenceName="UserData"; //  SharedPreference Login 정보 기억하는 Object 이름
    String UserId="UserId";
    String UserPassword="UserPassword";

    Context context;

    private String Id;              //  사용자가 가장 마지막에 입력 하였던 Id
    private String Password;        //  사용자가 가장 마지막에 입력 하였던 Password

    SharedPreferences sharedPreferences;

    public SharedPreferenceManage(Context context)
    {
        this.context=context;
    }

    public void WriteSharedPreference(UserData userData)
    {
        Id=userData.getId();
        Password=userData.getPassword();

        sharedPreferences=context.getSharedPreferences(SharedPreferenceName,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(UserId,userData.getId());
        editor.putString(UserPassword,userData.getPassword());
        editor.commit();

        Log.d(TAG,"Write SharedPreference Login : "+Id+" / "+Password);
    }

    public UserData ReadSharedPreference()
    {
        UserData userData=new UserData();

        sharedPreferences=context.getSharedPreferences(SharedPreferenceName,Context.MODE_PRIVATE);
        Id=sharedPreferences.getString(UserId,"");
        Password=sharedPreferences.getString(UserPassword,"");

        userData.setId(Id);
        userData.setPassword(Password);

        Log.d(TAG,"Write SharedPreference Login : "+Id+" / "+Password);

        return userData;
    }
}
