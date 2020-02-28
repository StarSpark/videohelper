package com.star.video.helper;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.service.restrictions.RestrictionsReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity  {
    private IntentFilter intentFilter;

    private BroadcastReceiver localReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pkName = intent.getStringExtra(Helper.pkName);
            String lclassName = intent.getStringExtra(Helper.lclassName);
            boolean startApp = intent.getBooleanExtra(Helper.startApp, false);
            Log.e("localReceiver","startApp======>"+startApp);
            Log.e("localReceiver","pkName======>"+pkName);
            Log.e("localReceiver","lclassName======>"+lclassName);
            if (startApp&& !TextUtils.isEmpty(pkName)) {
                if (!TextUtils.isEmpty(lclassName)) {
                    startApp(pkName,lclassName);
                }else {
                    startApp(pkName);
                }
            }
        }
    };;
    private LocalBroadcastManager localBroadcastManager;
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
                    ShuaBaoService.openApp();
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
                    WeiShiService.openApp();
                    Toast.makeText(MainActivity.this, "已经开启啦", Toast.LENGTH_LONG).show();
                } else {
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                }

            }
        });
        findViewById(R.id.btn_helper_dy).setOnClickListener(v->{
            if (DouYinService.isStart()) {
                DouYinService.openApp();
                Toast.makeText(MainActivity.this, "已经开启啦", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        findViewById(R.id.btn_helper_ks).setOnClickListener(v->{
            if (KuaiShouService.isStart()) {
                KuaiShouService.openApp();
                Toast.makeText(MainActivity.this, "已经开启啦", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        //注册本地广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Helper.MY_BROADCAST);
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void startApp(String pkgName) {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pkgName);
        if (intent!=null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            Log.e("startApp","intent===null");
        }
    }
    private void startApp(String pkgName,String launcherClassName) {
        ComponentName com = new ComponentName(pkgName, launcherClassName); //package;class
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(com);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
}
