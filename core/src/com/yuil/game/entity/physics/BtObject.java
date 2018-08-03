package com.yuil.game.entity.physics;

import java.util.HashMap;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.utils.Disposable;
import com.yuil.game.entity.attribute.Attribute;

public class BtObject extends PhysicsObject implements Disposable{
	//ModelInstance instance;
	btRigidBody rigidBody;
	btCollisionShape collisionShape;
	btRigidBodyConstructionInfo  rigidBodyConstructionInfo;
	public Object userData;
	private HashMap<Integer, Attribute> attributes=null;
	public short group=1;
	public short mask=(short) 65535;
	Behavior behavior;
	public boolean transformChanged;
	
	private Vector3 position=new Vector3();

	public BtObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BtObject(long id,btRigidBody rigidBody, btDefaultMotionState motionState,
			btCollisionShape collisionShape, btRigidBodyConstructionInfo rigidBodyConstructionInfo) {
		super();
		this.id=id;
		this.rigidBody = rigidBody;
		this.collisionShape = collisionShape;
		this.rigidBodyConstructionInfo = rigidBodyConstructionInfo;
		
	}


	public void update(float delta){
		if(behavior!=null){
			behavior.update(delta);
		}
	}
	public  Matrix4 getTransform(){
		return this.rigidBody.getWorldTransform();
	}

	
	
	public Behavior getBehavior() {
		return behavior;
	}

	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
	}

	public Vector3 getPosition(Matrix4 tm4) {
		rigidBody.getMotionState().getWorldTransform(tm4);
		return tm4.getTranslation(position);
	}

	public void setPosition(Vector3 position) {
		this.rigidBody.getWorldTransform().setTranslation(position);
	}




	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}

	public HashMap<Integer, Attribute> getAttributes() {
		if (this.attributes==null){
			this.attributes=new HashMap<Integer, Attribute>();
		}
		return this.attributes;
	}

	public void setAttributes(HashMap<Integer, Attribute> attributes) {
		this.attributes = attributes;
	}

	public short getGroup() {
		return group;
	}

	public void setGroup(short group) {
		this.group = group;
	}

	public short getMask() {
		return mask;
	}

	public void setMask(short mask) {
		this.mask = mask;
	}

	public btRigidBodyConstructionInfo getRigidBodyConstructionInfo() {
		return rigidBodyConstructionInfo;
	}




	public void setRigidBodyConstructionInfo(btRigidBodyConstructionInfo rigidBodyConstructionInfo) {
		this.rigidBodyConstructionInfo = rigidBodyConstructionInfo;
	}




	public btCollisionShape getCollisionShape() {
		return collisionShape;
	}




	public void setCollisionShape(btCollisionShape collisionShape) {
		this.collisionShape = collisionShape;
	}



	public btRigidBody getRigidBody() {
		return rigidBody;
	}




	public void setRigidBody(btRigidBody rigidBody) {
		this.rigidBody = rigidBody;
	}



	

	public boolean isTransformChanged() {
		return transformChanged;
	}

	public void setTransformChanged(boolean transformChanged) {
		this.transformChanged = transformChanged;
	}

	@Override
	public  void dispose() {
		// TODO Auto-generated method stub
		synchronized (this) {
			rigidBody.getMotionState().dispose();
			rigidBodyConstructionInfo.dispose();
			collisionShape.dispose();
			rigidBody.dispose();
			this.rigidBodyConstructionInfo=null;
			this.collisionShape=null;
			this.rigidBody=null;
		}
	}

	@Override
	public String toString() {
		return "BtObject [Id"+id+", rigidBody=" + rigidBody + ", collisionShape="
				+ collisionShape + ", rigidBodyConstructionInfo=" + rigidBodyConstructionInfo + ", userData=" + userData
				+ ", position=" + position + "]";
	}

	
	
}
