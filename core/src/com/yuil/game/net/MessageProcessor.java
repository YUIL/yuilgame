package com.yuil.game.net;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;


import io.netty.buffer.ByteBuf;

public abstract class MessageProcessor implements Runnable {

	public Session session=null;
	public Queue<ByteBuf> dataQueue = new ConcurrentLinkedDeque<ByteBuf>();

	public Queue<ByteBuf> getDataQueue() {
		return dataQueue;
	}

	public void setDataQueue(Queue<ByteBuf> dataQueue) {
		this.dataQueue = dataQueue;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	
	




	public abstract void run() ;
}