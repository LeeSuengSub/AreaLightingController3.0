package net.woorisys.lighting.control3.admin.dongle;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.search.SearchActivity;

public class ConnectDongleActivity extends AppCompatActivity {

    TextView connect_dongle_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_dongle);

        connect_dongle_btn=(TextView)findViewById(R.id.connect_dongle_btn);

        connect_dongle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
