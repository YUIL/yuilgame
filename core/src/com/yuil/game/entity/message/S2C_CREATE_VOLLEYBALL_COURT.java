package com.yuil.game.entity.message;



import com.yuil.game.net.message.Message;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class S2C_CREATE_VOLLEYBALL_COURT implements Message {
	public final int type=EntityMessageType.S2C_CREATE_VOLLEYBALL_COURT.ordinal();
	long id;
	float x;
	float y;
	float z;
	
	long time=0;
	
	
	public S2C_CREATE_VOLLEYBALL_COURT() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public S2C_CREATE_VOLLEYBALL_COURT(ByteBuf src) {
		super();
		this.set(src);
		// TODO Auto-generated constructor stub
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	@Override
	public Message set(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.readByte();
		this.setId(buf.readLong());
		this.setX(buf.readFloat());
		this.setY(buf.readFloat());
		this.setZ(buf.readFloat());
		return this;
	}

	@Override
	public ByteBuf get() {
		
		ByteBuf buf=UnpooledByteBufAllocator.DEFAULT.heapBuffer(8+4+4+4+Message.TYPE_LENGTH);
		buf.writeByte(this.type);
		buf.writeLong(this.id);
		buf.writeFloat(this.x);
		buf.writeFloat(this.y);
		buf.writeFloat(this.z);
	
		return buf;
	}

	@Override
	public String toString() {
		return "S2C_CREATE_VOLLEYBALL_COURT [type=" + type + ", id=" + id + ", x=" + x + ", y=" + y + ", z=" + z + ", time=" + time + "]";
	}
	
	

}
