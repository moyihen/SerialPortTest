  简单的串口通讯 发送和接收数据.
  
1.配置参数 SerialHelper serialHelper = new SerialHelper("/dev/ttyUSB0", 9600);

2.打开连接串口 serialHelper.open();

3.发送数据 serialHelper.send(MyFunc.hexToByteArr("F200000343303403B5"));

4 接收数据 serialHelper.setOnReceiveListener(this);

    @Override
    public void onReceive(byte[] data,int size) {
        //这边可根据协议 做具体的解析.
        Log.i(TAG, "onReceive: 接收的数据" + MyFunc.byteArrToHex(data) + "data长度:" +size);
    }
    
    
    
    
    --------------------------此为记录学习串口通讯---------------------------------------------------
