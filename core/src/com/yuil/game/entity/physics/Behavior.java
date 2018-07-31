package com.yuil.game.entity.physics;

public abstract class Behavior{
	public BtObject behaviorObject;
	public Behavior( BtObject btObject){
		this.behaviorObject=btObject;
	}
	public abstract  void update(float delta);
}
