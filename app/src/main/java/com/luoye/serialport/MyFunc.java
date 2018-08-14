package com.luoye.serialport;

import java.util.Locale;

/**
 * 数据转换工具
 * 
 * @author LUOYE
 * @data 2015-07-05 02:13:26
 */
public class MyFunc {
	/**
	 * 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
	 * 
	 * @param num 用于判奇偶的数字
	 * @return 位运算结果，最后一位是1则为奇数，为0是偶数
	 */
	static public int isOdd(int num) {
		return num & 0x1;
	}

	/**
	 * Hex字符串转int
	 * 
	 * @param hex 十六进制字符
	 * @return 转换后的整数
	 */
	static public int hexToInt(String hex) {
		return Integer.parseInt(hex, 16);
	}

	/**
	 * Hex字符串转byte
	 * 
	 * @param hex 十六进制字符
	 * @return 转换后的整数
	 */
	static public byte hexToByte(String hex) {
		return (byte) Integer.parseInt(hex, 16);
	}

	/**
	 * 1字节转2个Hex字符
	 * 
	 * @param bt 一个字节
	 * @return 字节的十六进制字符串表示，如bt = 255，转换后十六进制字符串表示为FF
	 */
	static public String byte2Hex(Byte bt) {
		return String.format("%02x", bt).toUpperCase(Locale.getDefault());
	}

	/**
	 * 字节数组转转hex字符串
	 * 
	 * @param btArr 字节数组转十六进制字符串
	 * @return 所有字节的十六进制字符串
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
	 * 字节数组转转hex字符串，可选长度
	 * 
	 * @param btArr 用于转换的字节数组
	 * @param offset 开始转换的字节位置，起始位置以0开始计数
	 * @param count 从开始位置起转换的字节个数，若offset + count > btArr.length，则只处理到btArr末尾
	 * @return 转换后的十六进制字符串
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
	 * hex字符串转字节数组
	 * 
	 * @param hex 十六进制字符串
	 * @return 转换后的字节数组
	 */
	static public byte[] hexToByteArr(String hex) {
		int nLen = hex.length();
		byte[] result;
		if (isOdd(nLen) == 1) {// 奇数
			nLen++;
			result = new byte[(nLen / 2)];
			hex = "0" + hex;
		} else {// 偶数
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