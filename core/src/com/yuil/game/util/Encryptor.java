package com.yuil.game.util;

import java.util.Random;

public class Encryptor {
	Random random=new Random();
	long seed;
	
	public Encryptor() {
		random.setSeed(0x9e370001);
		seed=random.nextLong();
	}
	public void test(){
		System.out.println(seed);
	}
	public void test(String string){
		long result=seed;
		byte[] bytes=string.getBytes();
		boolean isAdd=false;
		for (int i = 0; i < bytes.length; i++) {
			if(isAdd){
				result=(result+bytes[i])^2;
			}else{
				result=(result*bytes[i])^2;
			}
		}
		System.out.println(result);
	}
	public long stringToLong(String string){
		long result=seed;
		byte[] bytes=string.getBytes();
		boolean isAdd=false;
		for (int i = 0; i < bytes.length; i++) {
			if(isAdd){
				result=(result+bytes[i])^2;
			}else{
				result=(result*bytes[i])^2;
			}
		}
		return result;
	}
}
