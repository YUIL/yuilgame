package com.yuil.game.server;

import java.util.HashMap;

import com.yuil.game.entity.attribute.Attribute;

public class Player {
	long id;
	long btObjectId;
	long roomId;
	long sessionId;
	public HashMap<String, Object> Attributes=new HashMap<String, Object>();

	
	public Player() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Player(long id, long btObjectId,long sessionId) {
		super();
		this.id = id;
		this.btObjectId = btObjectId;
		this.sessionId= sessionId;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getBtObjectId() {
		return btObjectId;
	}
	public void setBtObjectId(long btObjectId) {
		this.btObjectId = btObjectId;
	}
	public long getSessionId() {
		return sessionId;
	}
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
	public HashMap<String, Object> getAttributes() {
		return Attributes;
	}
	public void setAttributes(HashMap<String, Object> attributes) {
		Attributes = attributes;
	}
	public long getRoomId() {
		return roomId;
	}
	public void setRoomId(long roomId) {
		this.roomId = roomId;
	}
	
	
	
}
