package com.yuil.game.entity.attribute;

public class MoveSpeed implements Attribute {

	public final Integer type=AttributeType.MOVE_SPEED.ordinal();
	public int moveSpeed;
	
	
	
	public MoveSpeed(int moveSpeed) {
		super();
		this.moveSpeed = moveSpeed;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
