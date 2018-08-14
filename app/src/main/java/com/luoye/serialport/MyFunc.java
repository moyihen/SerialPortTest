package com.luoye.serialport;

import java.util.Locale;

/**
 * ����ת������
 * 
 * @author LUOYE
 * @data 2015-07-05 02:13:26
 */
public class MyFunc {
	/**
	 * �ж�������ż����λ���㣬���һλ��1��Ϊ������Ϊ0��ż��
	 * 
	 * @param num ��������ż������
	 * @return λ�����������һλ��1��Ϊ������Ϊ0��ż��
	 */
	static public int isOdd(int num) {
		return num & 0x1;
	}

	/**
	 * Hex�ַ���תint
	 * 
	 * @param hex ʮ�������ַ�
	 * @return ת���������
	 */
	static public int hexToInt(String hex) {
		return Integer.parseInt(hex, 16);
	}

	/**
	 * Hex�ַ���תbyte
	 * 
	 * @param hex ʮ�������ַ�
	 * @return ת���������
	 */
	static public byte hexToByte(String hex) {
		return (byte) Integer.parseInt(hex, 16);
	}

	/**
	 * 1�ֽ�ת2��Hex�ַ�
	 * 
	 * @param bt һ���ֽ�
	 * @return �ֽڵ�ʮ�������ַ�����ʾ����bt = 255��ת����ʮ�������ַ�����ʾΪFF
	 */
	static public String byte2Hex(Byte bt) {
		return String.format("%02x", bt).toUpperCase(Locale.getDefault());
	}

	/**
	 * �ֽ�����תתhex�ַ���
	 * 
	 * @param btArr �ֽ�����תʮ�������ַ���
	 * @return �����ֽڵ�ʮ�������ַ���
	 */
	static public String byteArrToHex(byte[] btArr) {
		StringBuilder strBuilder = new StringBuilder();
		int j = btArr.length;
		for (int i = 0; i < j; i++) {
			strBuilder.append(byte2Hex(btArr[i]));
			strBuilder.append(" ");
		}
		return strBuilder.toString();
	}

	/**
	 * �ֽ�����תתhex�ַ�������ѡ����
	 * 
	 * @param btArr ����ת�����ֽ�����
	 * @param offset ��ʼת�����ֽ�λ�ã���ʼλ����0��ʼ����
	 * @param count �ӿ�ʼλ����ת�����ֽڸ�������offset + count > btArr.length����ֻ����btArrĩβ
	 * @return ת�����ʮ�������ַ���
	 */
	static public String byteArrToHex(byte[] btArr, int offset, int count) {
		StringBuilder strBuilder = new StringBuilder();
		int j = offset + count > btArr.length ? btArr.length : offset + count;
		for (int i = offset; i < j; i++) {
			strBuilder.append(byte2Hex(btArr[i]));
		}
		return strBuilder.toString();
	}

	/**
	 * hex�ַ���ת�ֽ�����
	 * 
	 * @param hex ʮ�������ַ���
	 * @return ת������ֽ�����
	 */
	static public byte[] hexToByteArr(String hex) {
		int nLen = hex.length();
		byte[] result;
		if (isOdd(nLen) == 1) {// ����
			nLen++;
			result = new byte[(nLen / 2)];
			hex = "0" + hex;
		} else {// ż��
			result = new byte[(nLen / 2)];
		}
		int j = 0;
		for (int i = 0; i < nLen; i += 2) {
			result[j] = hexToByte(hex.substring(i, i + 2));
			j++;
		}
		return result;
	}
}