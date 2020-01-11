package com.star.video.helper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity  {

    private Button mBtnHelper;
    private Button mBtnHelperWS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnHelper = findViewById(R.id.btn_helper);
        mBtnHelperWS = findViewById(R.id.btn_helper_ws);
        mBtnHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShuaBaoService.isStart()) {
                    Toast.makeText(MainActivity.this, "已经开启啦", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }

            }
        });
        mBtnHelperWS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WeiShiService.isStart()) {
                    Toast.makeText(MainActivity.this, "已经开启啦", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }

            }
        });
        findViewById(R.id.btn_helper_dy).setOnClickListener(v->{
            if (DouYinService.isStart()) {
                Toast.makeText(MainActivity.this, "已经开启啦", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
    }
}
