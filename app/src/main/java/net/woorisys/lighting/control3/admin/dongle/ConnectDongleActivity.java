package net.woorisys.lighting.control3.admin.dongle;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.search.SearchActivity;

public class ConnectDongleActivity extends AppCompatActivity {

//    @BindView(R.id.connect_dongle_btn)
    TextView connect_dongle_btn;

   // @BindView(R.id.progressBar)
   // CircleProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_dongle);
//        ButterKnife.bind(ConnectDongleActivity.this);
        connect_dongle_btn = findViewById(R.id.connect_dongle_btn);
        //progressBar.setVisibility(View.GONE);


        connect_dongle_btn.setOnClickListener(new View.OnClickListener() {;
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
