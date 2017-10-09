package com.yuil.game.util;

/**
 * @author dj-004
 * @changedby dj-004
 */
public class DataUtil {
	/**
	 * @param src
	 *            {byte[]:{"length":4}}
	 * @return {int}
	 */
	public static int bytesToInt(byte[] src) {
		int value = 0;
		for (int i = 0; i < src.length; i++) {
			value = value | ((src[i] & 0xFF) << (i * 8));
		}
		return value;
	}

	/**
	 * @param src
	 *            {int}
	 * @return {byte[]:{"length":4}}
	 */
	public static byte[] intToBytes(int src) {

		byte[] bytes = new byte[4];
		for (int b = 0; b < bytes.length; b++) {
			bytes[b] = (byte) (src >> (b * 8));
		}
		return bytes;
	}

	public static void subByte(byte[] src, byte[] dst, int offset) {
		for (int i = 0; i < dst.length; i++) {
			dst[i] = src[i + offset];
		}
	}

	public static byte[] subByte(byte[] src, int length, int offset) {
		byte[] dst = new byte[length];
		for (int i = 0; i < dst.length; i++) {
			dst[i] = src[i + offset];
		}
		return dst;
	}

	public static byte[] longToBytes(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ++ix) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}

	public static long bytesToLong(byte[] byteNum) {
		long num = 0;
		for (int ix = 0; ix < 8; ++ix) {
			num <<= 8;
			num |= (byteNum[ix] & 0xff);
		}
		return num;
	}

	public static byte[] floatToBytes(float f) {

		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (fbit >> (24 - i * 8));
		}


		int len = b.length;

		byte[] dest = new byte[len];

		System.arraycopy(b, 0, dest, 0, len);
		byte temp;

		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}

		return dest;

	}

	public static float bytesToFloat(byte[] b) {
		int l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		return Float.intBitsToFloat(l);
	}

	public static byte[] shortToBytes(short s) {
		byte[] dest = new byte[2];
		dest[1] = (byte) (s >> 8);
		dest[0] = (byte) (s >> 0);
		return dest;
	}

	public static short bytesToShort(byte[] src) {
		return (short) (((src[1] << 8) | src[0] & 0xff));
	}

	public static int getUnsignedNum(byte data) { 
		return data & 0x0FF;
	}

	public static int getUnsignedNum(short data) { 
		return data & 0x0FFFF;
	}

	public static long getUnsignedNum(int data) {
		return data & 0x0FFFFFFFFl;
	}

	public static int appendBytes(byte[] dest,float f,int offset){
		System.arraycopy(floatToBytes(f), 0, dest, offset, 4);;
		return offset+4;
	}
	
	public static int appendBytes(byte[] dest,int i,int offset){
		System.arraycopy(intToBytes(i), 0, dest, offset, 4);
		return offset+4;
	}
	
	public static int appendBytes(byte[] dest,long l,int offset){
		System.arraycopy(longToBytes(l), 0, dest, offset, 8);
		return offset+8;
	}
	
}