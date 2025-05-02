package net.woorisys.lighting.control3.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import net.woorisys.lighting.control3.admin.fragment.BaseActivity;

//import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_USB_PERMISSION="com.android.example.USB_PERMISSION";

//    @BindView(R.id.light)
//    ImageView lightImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(MainActivity.this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }

//        new Handler().postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    lightImage.setImageDrawable(getResources().getDrawable(R.drawable.bulb_on_set_with_app_name, getApplicationContext().getTheme()));
//                } else {
//                    lightImage.setImageDrawable(getResources().getDrawable(R.drawable.bulb_on_set_with_app_name));
//                }
//            }
//        }, 3000);// 0.5초 정도 딜레이를 준 후 시작

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                Intent intent=new Intent(getApplicationContext(), BaseActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);// 0.5초 정도 딜레이를 준 후 시작
    }
}
