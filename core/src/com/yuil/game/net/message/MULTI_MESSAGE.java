package com.yuil.game.net.message;


import java.nio.ByteBuffer;
import java.util.Arrays;

import com.yuil.game.util.DataUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class MULTI_MESSAGE implements Message {
	public final int type=MessageType.MULTI_MESSAGE.ordinal();

	public byte messageNum;
	public int messageLength;
	public int[] messageLengths;
	public ByteBuf[] gameMessages;
	
	public MULTI_MESSAGE(){
	}
	
	public MULTI_MESSAGE(ByteBuf buf){
		this.set(buf);
	}
	
	public MULTI_MESSAGE(int length,Message... messages){
		this.set(length,messages);
	}
	
	public void set(int length,Message... messages){
		
		messageLength=0;
		if( messages.length>255){
			throw new IllegalArgumentException("message's length must <255");
		}
		
		messageNum=(byte) length;
		messageLengths=new int[messageNum];
		gameMessages=new ByteBuf[messageNum];
		for (int i = 0; i < length; i++) {
			ByteBuf src=messages[i].get();
			messageLength+=src.array().length;
			messageLengths[i]=src.array().length;
			gameMessages[i]=src;
		}
		
		
	}
	
	@Override
	public Message set(ByteBuf buf) {

		messageLength=0;
		buf.readByte();
		messageNum=buf.readByte();

		messageLengths=new int[messageNum];
		for (int i = 0; i <messageNum; i++) {
			messageLengths[i]=buf.readInt();
			messageLength+=messageLengths[i];
		}
		

		
		gameMessages=new ByteBuf[messageNum];
		for (int i = 0; i < messageNum; i++) {
			gameMessages[i]=buf.readBytes(messageLengths[i]);
		}
		return this;
	}

	@Override
	public ByteBuf get() {

		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(1+4*messageNum+messageLength+Message.TYPE_LENGTH);
		
		buf.writeByte(this.type);
		buf.writeByte(this.messageNum);
		for (int i = 0; i < messageNum; i++) {
			buf.writeInt(messageLengths[i]);
		}
		
		for (int i = 0; i < messageNum; i++) {
			gameMessages[i].resetReaderIndex();
			buf.writeBytes(gameMessages[i]);
		}
		
		return buf;
	}

	@Override
	public String toString() {
		return "MULTI_MESSAGE [type=" + type + ", messageNum=" + messageNum + ", messageLength=" + messageLength
				+ ", messageLengths=" + Arrays.toString(messageLengths) + ", gameMessages="
				+ Arrays.toString(gameMessages) + "]";
	}

/*	@Override
	public static ByteBuf get() {
		
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(1+2*messageNum+messageLength+Message.TYPE_LENGTH);
		
		buf.writeByte(MessageType.MESSAGE_ARRAY.ordinal());
		buf.writeShort(this.messageNum);
		
		
		for (int i = 0; i < messageNum; i++) {
			buf.writeInt(messageLengths[i]);
		}
		
		for (int i = 0; i < messageNum; i++) {
			buf.writeBytes(gameMessages[i]);
		}
		return buf;
	}
*/
	
}
