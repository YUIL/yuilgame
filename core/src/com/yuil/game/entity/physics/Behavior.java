package com.yuil.game.entity.physics;

import com.badlogic.gdx.math.Matrix4;

public abstract class Behavior{
	


	public BtObject behaviorObject;
	public Behavior( BtObject btObject){
		this.behaviorObject=btObject;
	}
	public abstract  void update(float delta);
}
