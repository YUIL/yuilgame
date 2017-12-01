package com.yuil.game.entity.physics;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public abstract class PhysicsObject {
	long id;
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public abstract Matrix4 getTransform();
	public abstract Vector3 getPosition(Matrix4 tm4);
	public abstract void setPosition(Vector3 position);
	public abstract Object getUserData() ;
	public abstract void setUserData(Object userData) ;
}
