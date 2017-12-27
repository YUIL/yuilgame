package com.yuil.game.net.udp;


public class UdpSessionConfiguration {
	int timeOut=2;
	int maxUnusedTime=30000;
	int maxResendTimes=10;
	int sendMessageBufferMaxSize=100;
	
	
	public UdpSessionConfiguration() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UdpSessionConfiguration(int timeOut, int maxUnusedTime, int maxResendTimes, int sendMessageBufferMaxSize) {
		super();
		this.timeOut = timeOut;
		this.maxUnusedTime = maxUnusedTime;
		this.maxResendTimes = maxResendTimes;
		this.sendMessageBufferMaxSize = sendMessageBufferMaxSize;
	}
	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public int getMaxUnusedTime() {
		return maxUnusedTime;
	}
	public void setMaxUnusedTime(int maxUnusedTime) {
		this.maxUnusedTime = maxUnusedTime;
	}
	public int getMaxResendTimes() {
		return maxResendTimes;
	}
	public void setMaxResendTimes(int maxResendTimes) {
		this.maxResendTimes = maxResendTimes;
	}
	public int getSendMessageBufferMaxSize() {
		return sendMessageBufferMaxSize;
	}
	public void setSendMessageBufferMaxSize(int sendMessageBufferMaxSize) {
		this.sendMessageBufferMaxSize = sendMessageBufferMaxSize;
	}
	
	
}
