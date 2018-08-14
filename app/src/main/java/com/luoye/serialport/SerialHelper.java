package com.luoye.serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * ���ڸ���������
 * 
 * @author LUOYE
 * @data 2015-07-05 01:13:50
 */
public  class SerialHelper {
	/** ���ڶ��� */
	private SerialPort mSerialPort;
	/** ����������������ֽ����� */
	private OutputStream mOutputStream;
	/** �����������������ֽ����� */
	private InputStream   mInputStream;
	/** �����߳� */
	private ReadThread mReadThread;
	/** �����߳� */
	private SendThread mSendThread;
	/** ���ں� */
	private String mPort = "/dev/s3c2410_serial0";
	/** ������ */
	private int mBaudRate = 9600;
	/** �Ƿ��Ѵ򿪴��� */
	private boolean mbIsOpen = false;
	/** ����ѭ���������� */
	private byte[] mLoopData = new byte[] { 0x30 };
	/** ��ʱ����ʱ�� */
	private int iDelay = 500;

	/**
	 * ���캯��
	 * 
	 * @param port ������
	 * @param baudRate ������
	 */
	public SerialHelper(String port, int baudRate) {
		mPort = port;
		mBaudRate = baudRate;
	}

	/**
	 * ���캯����Ĭ�϶˿ں�/dev/s3c2410_serial0��Ĭ�ϲ�����9600
	 */
	public SerialHelper() {
		this("/dev/s3c2410_serial0", 9600);
	}

	/**
	 * ���캯����Ĭ�ϲ�����9600
	 * 
	 * @param port ������
	 */
	public SerialHelper(String port) {
		this(port, 9600);
	}

	/**
	 * ���캯��
	 * 
	 * @param port ������
	 * @param baudRate ������
	 * @throws NumberFormatException �������ַ������������ַ���ʱ�׳�
	 */
	public SerialHelper(String port, String baudRate) throws NumberFormatException {
		this(port, Integer.parseInt(baudRate));
	}

	/**
	 * �򿪴���
	 * 
	 * @throws SecurityException �򿪴���ʧ��ʱ���׳�
	 * @throws IOException ��ȡ�������������ʧ��ʱ���׳�
	 * @throws InvalidParameterException ��Ч�����쳣���˿ںŻ�������Чʱ�׳�
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
	 * �رմ���
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
	 * ���ʹ�������
	 * 
	 * @param data �ֽ�����
	 * @throws IOException ���ʹ�������ʱ���׳�IO�쳣
	 */
	public void send(byte[] data) throws IOException {
		mOutputStream.write(data);
	}

	/**
	 * ����ʮ�������ַ�������
	 * 
	 * @param hex ʮ�������ַ���
	 * @throws IOException ����ʮ�������ַ���ʧ��ʱ���׳�IO�쳣
	 */
	public void sendHex(String hex) throws IOException {
		byte[] bOutArray = MyFunc.hexToByteArr(hex);
		send(bOutArray);
	}

	/**
	 * �����ַ���
	 * 
	 * @param text �ַ���
	 * @throws IOException ��������ʧ��ʱ���׳�IO�쳣
	 */
	public void sendTxt(String text) throws IOException {
		byte[] bOutArray = text.getBytes();
		send(bOutArray);
	}

	/**
	 * ���ڽ����߳�
	 * 
	 * @author LUOYE
	 * @data 2015-07-05 1:20:32
	 */
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			//�жϽ����Ƿ������У�����ȫ�Ľ�������
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
						Thread.sleep(50);// ��ʱ50ms
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
	 * ���ڷ����߳�
	 * 
	 * @author LUOYE
	 * @data 2015-07-05 01:52:14
	 */
	private class SendThread extends Thread {
		public boolean suspendFlag = true;// �����̵߳�ִ��

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
		 * ��ͣ�߳�
		 */
		public void setSuspendFlag() {
			suspendFlag = true;
		}

		/**
		 * �����߳�
		 */
		public synchronized void setResume() {
			suspendFlag = false;
			notify();
		}
	}

	/**
	 * ��ȡ������
	 * 
	 * @return ������
	 */
	public int getBaudRate() {
		return mBaudRate;
	}

	/**
	 * ���ò�����
	 * 
	 * @param baudRate ������
	 * @return true ���óɹ���false �����ѿ������޸���Ч
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
	 * ���ò�����
	 * 
	 * @param baudRate ������
	 * @return true ���óɹ���false �����ѿ������޸���Ч
	 */
	public boolean setBaudRate(String baudRate) {
		int iBaud = Integer.parseInt(baudRate);
		return setBaudRate(iBaud);
	}

	/**
	 * ��ȡ������
	 * 
	 * @return ������
	 */
	public String getPort() {
		return mPort;
	}

	/**
	 * ���ô�����
	 * 
	 * @param port ������
	 * @return true ���óɹ���false �����ѿ������޸���Ч
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
	 * �����Ƿ��ѿ���
	 * 
	 * @return true �����ѿ���, false �����ѹر�
	 */
	public boolean isOpen() {
		return mbIsOpen;
	}

	/**
	 * ��ȡѭ�����͵����ݣ���δ���ã�����ֵΪ0x30
	 * 
	 * @return �ֽ�����
	 */
	public byte[] getLoopData() {
		return mLoopData;
	}

	/**
	 * ����ѭ����������
	 * 
	 * @param loopData �ֽ�����
	 */
	public void setbLoopData(byte[] loopData) {
		mLoopData = loopData;
	}

	/**
	 * ����ѭ�������ı�
	 * 
	 * @param text �ı��ַ���
	 */
	public void setTxtLoopData(String text) {
		mLoopData = text.getBytes();
	}

	/**
	 * ����ѭ������ʮ�������ı�
	 * 
	 * @param hex ʮ�������ı�
	 */
	public void setHexLoopData(String hex) {
		mLoopData = MyFunc.hexToByteArr(hex);
	}

	/**
	 * ��ȡ��ʱʱ��
	 * 
	 * @return ��ʱʱ��
	 */
	public int getiDelay() {
		return iDelay;
	}

	/**
	 * ������ʱʱ��
	 * 
	 * @param delay ��ʱʱ��
	 */
	public void setiDelay(int delay) {
		this.iDelay = delay;
	}

	/**
	 * �ָ�ѭ�������߳�
	 */
	public void resumeSend() {
		if (mSendThread != null) {
			mSendThread.setResume();
		}
	}

	/**
	 * ��ͣѭ�������߳�
	 */
	public void pauseSend() {
		if (mSendThread != null) {
			mSendThread.setSuspendFlag();
		}
	}

	/**
	 * �������ݽ��ջص�����
	 * 
	 * @param btData ���յ����ֽ�����
	 */
	//protected abstract void onReceive(byte[] btData);

	/**-----------------------------------------���ݻص��ӿ�---------------------------------------*/
	public receiveListener mReceiveListener;
	public interface receiveListener{
		void onReceive(byte[] data,int size);
	}
	public void setOnReceiveListener(receiveListener receiveListener){
		this.mReceiveListener = receiveListener;
	}
}