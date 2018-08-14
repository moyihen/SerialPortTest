package com.tangfeng.serialporttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.luoye.serialport.MyFunc;
import com.luoye.serialport.SerialHelper;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SerialHelper.receiveListener {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_send = (Button) findViewById(R.id.bt_send);
        final SerialHelper serialHelper = new SerialHelper("/dev/ttyUSB0", 9600);
       // final SerialHelper serialHelper = new SerialHelper("/dev/ttyUSB5", 9600);
        serialHelper.setOnReceiveListener(this);
        try {
            serialHelper.open();
            //serialHelper.sendTxt("F2 00 00 03 43 30 34 03 B5");
            Log.i(TAG, "onCreate: 打开成功----------------------------------");
           //  serialHelper.send(MyFunc.hexToByteArr("F200000343313003B0"));
            Log.i(TAG, "onCreate: 配置串口成功----------------------------------");
        } catch (IOException e) {
            Log.i(TAG, "onCreate: 打开出错----------------------------------");
            e.printStackTrace();
        }
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //serialHelper.send(MyFunc.hexToByteArr("F200000343323903BA"));
                   // serialHelper.send(MyFunc.hexToByteArr("FC05112756"));
                    serialHelper.send(MyFunc.hexToByteArr("F200000343303403B5"));
                    Log.i(TAG, "onClick:发送数据");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(TAG, "onCreate: 发送异常----------------------------------");
                }
            }
        });

    }

    /**
     * 接收的数据
     *
     * @param data
     */
    @Override
    public void onReceive(byte[] data,int size) {
        Log.i(TAG, "onReceive: 接收的数据" + MyFunc.byteArrToHex(data) + "data长度:" +size);
    }
}
