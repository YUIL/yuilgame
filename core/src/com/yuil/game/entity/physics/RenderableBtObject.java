package com.yuil.game.entity.physics;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

public class RenderableBtObject extends BtObject {
	ModelInstance instance;
	AnimationController animationController;
	
	public RenderableBtObject() {
		super();
	}

	public RenderableBtObject(ModelInstance instance,AnimationController animationController) {
		super();
		this.instance=instance;
		this.animationController=animationController;
	}
	public RenderableBtObject(long id,ModelInstance instance,btRigidBody rigidBody, btDefaultMotionState motionState,
			btCollisionShape collisionShape, btRigidBodyConstructionInfo rigidBodyConstructionInfo) {
		super(id, rigidBody,  motionState, collisionShape,  rigidBodyConstructionInfo);
	
	}

	public ModelInstance getInstance() {
		return instance;
	}

	public void setInstance(ModelInstance instance) {
		this.instance = instance;
	}

	public AnimationController getAnimationController() {
		return animationController;
	}

	public void setAnimationController(AnimationController animationController) {
		this.animationController = animationController;
	}
	
	
}
