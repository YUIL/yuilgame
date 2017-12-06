package com.yuil.game.entity.attribute;

public class ExplosionStrength implements Attribute {

	public final Integer type=AttributeType.EXPLOSION_STRENGTH.ordinal();
	public int strength;
	
	
	
	public ExplosionStrength(int strength) {
		super();
		this.strength = strength;
	}

	public int getDamagePoint() {
		return strength;
	}

	public void setDamagePoint(int strength) {
		this.strength = strength;
	}

	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return type;
	}

}
