package com.luoye.serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * 串口辅助工具类
 * 
 * @author LUOYE
 * @data 2015-07-05 01:13:50
 */
public  class SerialHelper {
	/** 串口对象 */
	private SerialPort mSerialPort;
	/** 输出流，用来发送字节数据 */
	private OutputStream mOutputStream;
	/** 输入流，用来接收字节数据 */
	private InputStream   mInputStream;
	/** 接收线程 */
	private ReadThread mReadThread;
	/** 发送线程 */
	private SendThread mSendThread;
	/** 串口号 */
	private String mPort = "/dev/s3c2410_serial0";
	/** 波特率 */
	private int mBaudRate = 9600;
	/** 是否已打开串口 */
	private boolean mbIsOpen = false;
	/** 串口循环发送数据 */
	private byte[] mLoopData = new byte[] { 0x30 };
	/** 延时发送时间 */
	private int iDelay = 500;

	/**
	 * 构造函数
	 * 
	 * @param port 串口名
	 * @param baudRate 波特率
	 */
	public SerialHelper(String port, int baudRate) {
		mPort = port;
		mBaudRate = baudRate;
	}

	/**
	 * 构造函数，默认端口号/dev/s3c2410_serial0，默认波特率9600
	 */
	public SerialHelper() {
		this("/dev/s3c2410_serial0", 9600);
	}

	/**
	 * 构造函数，默认波特率9600
	 * 
	 * @param port 串口名
	 */
	public SerialHelper(String port) {
		this(port, 9600);
	}

	/**
	 * 构造函数
	 * 
	 * @param port 串口名
	 * @param baudRate 波特率
	 * @throws NumberFormatException 波特率字符串不是数字字符串时抛出
	 */
	public SerialHelper(String port, String baudRate) throws NumberFormatException {
		this(port, Integer.parseInt(baudRate));
	}

	/**
	 * 打开串口
	 * 
	 * @throws SecurityException 打开串口失败时将抛出
	 * @throws IOException 获取串口输入输出流失败时将抛出
	 * @throws InvalidParameterException 无效参数异常，端口号或波特率无效时抛出
	 */
	public void open() throws SecurityException, IOException, InvalidParameterException {
		mSerialPort = new SerialPort(new File(mPort), mBaudRate, 0);
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		mReadThread = new ReadThread();
		mReadThread.start();
		mSendThread = new SendThread();
		mSendThread.setSuspendFlag();
		mSendThread.start();
		mbIsOpen = true;
	}

	/**
	 * 关闭串口
	 */
	public void close() {
		if (mReadThread != null)
			mReadThread.interrupt();
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
		mbIsOpen = false;
	}

	/**
	 * 发送串口数据
	 * 
	 * @param data 字节数组
	 * @throws IOException 发送串口数据时将抛出IO异常
	 */
	public void send(byte[] data) throws IOException {
		mOutputStream.write(data);
	}

	/**
	 * 发送十六进制字符串数据
	 * 
	 * @param hex 十六进制字符串
	 * @throws IOException 发送十六进制字符串失败时将抛出IO异常
	 */
	public void sendHex(String hex) throws IOException {
		byte[] bOutArray = MyFunc.hexToByteArr(hex);
		send(bOutArray);
	}

	/**
	 * 发送字符串
	 * 
	 * @param text 字符串
	 * @throws IOException 发送数据失败时将抛出IO异常
	 */
	public void sendTxt(String text) throws IOException {
		byte[] bOutArray = text.getBytes();
		send(bOutArray);
	}

	/**
	 * 串口接收线程
	 * 
	 * @author LUOYE
	 * @data 2015-07-05 1:20:32
	 */
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			//判断进程是否在运行，更安全的结束进程
			while (!isInterrupted()) {
				try {
					if (mInputStream == null)
						break;
					byte[] buffer = new byte[50];

					int size = mInputStream.read(buffer);
					if (size > 0) {
						//onReceive(buffer);
						mReceiveListener.onReceive(buffer,size);
					}
					try {
						Thread.sleep(50);// 延时50ms
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Throwable e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * 串口发送线程
	 * 
	 * @author LUOYE
	 * @data 2015-07-05 01:52:14
	 */
	private class SendThread extends Thread {
		public boolean suspendFlag = true;// 控制线程的执行

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				synchronized (this) {
					while (suspendFlag) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					send(getLoopData());
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(iDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * 暂停线程
		 */
		public void setSuspendFlag() {
			suspendFlag = true;
		}

		/**
		 * 唤醒线程
		 */
		public synchronized void setResume() {
			suspendFlag = false;
			notify();
		}
	}

	/**
	 * 获取波特率
	 * 
	 * @return 波特率
	 */
	public int getBaudRate() {
		return mBaudRate;
	}

	/**
	 * 设置波特率
	 * 
	 * @param baudRate 波特率
	 * @return true 设置成功，false 串口已开启，修改无效
	 */
	public boolean setBaudRate(int baudRate) {
		if (mbIsOpen) {
			return false;
		} else {
			mBaudRate = baudRate;
			return true;
		}
	}

	/**
	 * 设置波特率
	 * 
	 * @param baudRate 波特率
	 * @return true 设置成功，false 串口已开启，修改无效
	 */
	public boolean setBaudRate(String baudRate) {
		int iBaud = Integer.parseInt(baudRate);
		return setBaudRate(iBaud);
	}

	/**
	 * 获取串口名
	 * 
	 * @return 串口名
	 */
	public String getPort() {
		return mPort;
	}

	/**
	 * 设置串口名
	 * 
	 * @param port 串口名
	 * @return true 设置成功，false 串口已开启，修改无效
	 */
	public boolean setPort(String port) {
		if (mbIsOpen) {
			return false;
		} else {
			mPort = port;
			return true;
		}
	}

	/**
	 * 串口是否已开启
	 * 
	 * @return true 串口已开启, false 串口已关闭
	 */
	public boolean isOpen() {
		return mbIsOpen;
	}

	/**
	 * 获取循环发送的数据，若未设置，返回值为0x30
	 * 
	 * @return 字节数组
	 */
	public byte[] getLoopData() {
		return mLoopData;
	}

	/**
	 * 设置循环发送数据
	 * 
	 * @param loopData 字节数组
	 */
	public void setbLoopData(byte[] loopData) {
		mLoopData = loopData;
	}

	/**
	 * 设置循环发送文本
	 * 
	 * @param text 文本字符串
	 */
	public void setTxtLoopData(String text) {
		mLoopData = text.getBytes();
	}

	/**
	 * 设置循环发送十六进制文本
	 * 
	 * @param hex 十六进制文本
	 */
	public void setHexLoopData(String hex) {
		mLoopData = MyFunc.hexToByteArr(hex);
	}

	/**
	 * 获取延时时长
	 * 
	 * @return 延时时长
	 */
	public int getiDelay() {
		return iDelay;
	}

	/**
	 * 设置延时时长
	 * 
	 * @param delay 延时时长
	 */
	public void setiDelay(int delay) {
		this.iDelay = delay;
	}

	/**
	 * 恢复循环发送线程
	 */
	public void resumeSend() {
		if (mSendThread != null) {
			mSendThread.setResume();
		}
	}

	/**
	 * 暂停循环发送线程
	 */
	public void pauseSend() {
		if (mSendThread != null) {
			mSendThread.setSuspendFlag();
		}
	}

	/**
	 * 串口数据接收回调函数
	 * 
	 * @param btData 接收到的字节数组
	 */
	//protected abstract void onReceive(byte[] btData);

	/**-----------------------------------------数据回调接口---------------------------------------*/
	public receiveListener mReceiveListener;
	public interface receiveListener{
		void onReceive(byte[] data,int size);
	}
	public void setOnReceiveListener(receiveListener receiveListener){
		this.mReceiveListener = receiveListener;
	}
}