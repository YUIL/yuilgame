package com.yuil.game.net.udp;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import com.yuil.game.net.Session;
import com.yuil.game.net.udp.UdpSocket.SendServicer;

import io.netty.buffer.ByteBuf;


public class UdpSession extends Session{
	
	volatile boolean isSending=false;
	int timeOut=5;//millisecond
	int maxUnusedTime=30000;//millisecond
	int maxResendTimes=10;
	SendServicer sendThread;
	volatile short resendTimes=0;
	volatile long lastSendTime;
	volatile long lastReceiveTime;
	InetSocketAddress contactorAddress;
	volatile UdpMessage currentSendMessage;
	public   int lastSendSequenceId;
	public  int lastRecvSequenceId;
	volatile Queue<ByteBuf> sendMessageBuffer = new LinkedBlockingQueue<ByteBuf>();
	public volatile int sendMessageBufferMaxSize=100;
	
	public UdpSession(){
		init(new Random(System.currentTimeMillis()).nextLong());
	}
	
	public UdpSession(long id){
		init(id);
	}
	private void init(long id){
		//System.out.println("new session,id:"+id);
		this.id=id;
		lastSendSequenceId=-1;
		lastRecvSequenceId=-1;
	}

	public void config(UdpSessionConfiguration config){
		this.setTimeOut(config.getTimeOut());
		this.setMaxUnusedTime(config.getMaxUnusedTime());
		this.setMaxResendTimes(config.getMaxResendTimes());
		this.setSendMessageBufferMaxSize(config.getSendMessageBufferMaxSize());
		
	}
	
	public SendServicer getSendThread() {
		return sendThread;
	}

	public void setSendThread(SendServicer sendThread) {
		this.sendThread = sendThread;
	}

	public short getTimeOutMultiple() {
		return resendTimes;
	}

	public void setTimeOutMultiple(short timeOutMultiple) {
		this.resendTimes = timeOutMultiple;
	}

	
	public int getSendMessageBufferMaxSize() {
		return sendMessageBufferMaxSize;
	}

	public void setSendMessageBufferMaxSize(int sendMessageBufferMaxSize) {
		this.sendMessageBufferMaxSize = sendMessageBufferMaxSize;
	}

	public Queue<ByteBuf> getSendMessageBuffer() {
		return sendMessageBuffer;
	}

	public void setSendMessageBuffer(Queue<ByteBuf> sendMessageBuffer) {
		this.sendMessageBuffer = sendMessageBuffer;
	}

	public int getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	public InetSocketAddress getContactorAddress() {
		return contactorAddress;
	}
	public void setContactorAddress(InetSocketAddress contactorAddress) {
		this.contactorAddress = contactorAddress;
	}

	public long getLastSendTime() {
		return lastSendTime;
	}
	public void setLastSendTime(long lastSendTime) {
		this.lastSendTime = lastSendTime;
	}

	
	public long getLastReceiveTime() {
		return lastReceiveTime;
	}

	public void setLastReceiveTime(long lastReceiveTime) {
		this.lastReceiveTime = lastReceiveTime;
	}
	
	
	

	public boolean isSending() {
		return isSending;
	}

	public void setSending(boolean isSending) {
		this.isSending = isSending;
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

	public short getResendTimes() {
		return resendTimes;
	}

	public void setResendTimes(short resendTimes) {
		this.resendTimes = resendTimes;
	}

	public UdpMessage getCurrentSendMessage() {
		return currentSendMessage;
	}

	public void setCurrentSendMessage(UdpMessage currentSendMessage) {
		this.currentSendMessage = currentSendMessage;
	}

	public int getLastSendSequenceId() {
		return lastSendSequenceId;
	}

	public void setLastSendSequenceId(int lastSendSequenceId) {
		this.lastSendSequenceId = lastSendSequenceId;
	}

	public int getLastRecvSequenceId() {
		return lastRecvSequenceId;
	}

	public void setLastRecvSequenceId(int lastRecvSequenceId) {
		this.lastRecvSequenceId = lastRecvSequenceId;
	}

	@Override
	public String toString() {
		return "Session [id=" + id + ", timeOut=" + timeOut + ", maxResendTimes=" + maxResendTimes + ", sendThread="
				+ sendThread + ", timeOutMultiple=" + resendTimes + ", lastSendTime=" + lastSendTime
				+ ", contactorAddress=" + contactorAddress + ", currentSendMessage=" + currentSendMessage
				+ ", lastSendSequenceId=" + lastSendSequenceId + ", lastRecvSequenceId=" + lastRecvSequenceId + "]";
	}

}
