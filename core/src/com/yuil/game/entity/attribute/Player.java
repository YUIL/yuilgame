package com.yuil.game.entity.attribute;

public class Player implements Attribute {
	public final Integer type=AttributeType.PLAYER.ordinal();
	public long playerId;
	
	
	public Player(long playerId) {
		super();
		this.playerId = playerId;
	}
	
	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
