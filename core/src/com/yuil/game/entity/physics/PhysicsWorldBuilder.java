package com.yuil.game.entity.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.yuil.game.entity.attribute.AttributeType;
import com.yuil.game.entity.attribute.GameObjectTypeAttribute;
import com.yuil.game.entity.gameobject.GameObjectType;

public class PhysicsWorldBuilder {
	public BtObjectFactory btObjectFactory;
	Vector3 tempVector = new Vector3();
	
	public PhysicsWorldBuilder(boolean haveDefaultModel) {
		super();
		btObjectFactory=new BtObjectFactory(haveDefaultModel);
	}
	
	public BtObject createDefaultBall(float x,float y,float z){
		return btObjectFactory.createBtObject(btObjectFactory.getDefaultSphereShape(), 1, x, y, z);
	}
	public RenderableBtObject createDefaultRenderableBall(float x,float y,float z){
		return btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBallModel,btObjectFactory.getDefaultSphereShape(), 1, x, y, z);
	}
	
	public RenderableBtObject createDefaultRenderableBox(float x,float y,float z){
		RenderableBtObject renderableBtObject;
		btBoxShape collisionShape = new btBoxShape(new Vector3(0.5f,0.5f,0.5f));
		//collisionShape.setLocalScaling(new Vector3(5, 5,5));
		renderableBtObject=btObjectFactory.createRenderableBtObject(btObjectFactory.defaultBoxModel,collisionShape, 1, x, y, z);
		return renderableBtObject;
	}

	public BtObject createDefaultGround(){
		btCollisionShape collisionShape = new btBoxShape(tempVector.set(20, 0, 200));

		float r =200;
		//btCollisionShape collisionShape = new btSphereShape(r);
		BtObject btObject=new BtObject();
		btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(), new GameObjectTypeAttribute(GameObjectType.GROUND.ordinal()));
		
		btObjectFactory.initBtObject(btObject, collisionShape, 0, 0, -20, 0);
		btObject.getRigidBody().setCollisionFlags((1<<GameObjectType.GROUND.ordinal()));
		btObject.getRigidBody().setContactCallbackFilter((1<<GameObjectType.OBSTACLE.ordinal())|(1<<GameObjectType.PLAYER.ordinal()));

		return btObject;
	}
	public RenderableBtObject createDefaultRenderableGround(){

		btCollisionShape collisionShape = new btBoxShape(tempVector.set(20, 0, 200));
		float r =200;
		//btCollisionShape collisionShape = new btSphereShape(r);
		/*Model model = btObjectFactory.modelBuilder.createSphere(r*2, r*2, r*2, 100,
				100, new Material(ColorAttribute.createDiffuse(new Color(0.3f, 0.4f, 0.5f, 1)),
						ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)),
				Usage.Position | Usage.Normal);*/
		Model model = btObjectFactory.defaultGroundModel;
		RenderableBtObject btObject=btObjectFactory.createRenderableBtObject(model, collisionShape, 0, 0, -20, 0);
		btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(), new GameObjectTypeAttribute(GameObjectType.GROUND.ordinal()));
		btObject.getRigidBody().setCollisionFlags(1<<GameObjectType.GROUND.ordinal());
		//btObject.getRigidBody().setContactCallbackFilter((1<<GameObjectType.OBSTACLE.ordinal())|(1<<GameObjectType.PLAYER.ordinal()));
//		btObject.getRigidBody().setIgnoreCollisionCheck(co, ignoreCollisionCheck);
		//btObject.getRigidBody().setContactCallbackFlag(1);
		//btObject.getRigidBody().setContactCallbackFilter(8);
		//btObject.setGroup((short)4);
		//btObject.setMask((short)8);
		System.out.println(btObject.getRigidBody().getCollisionFlags());
		System.out.println(btObject.getRigidBody().getContactCallbackFilter());
		
		return btObject;
	}
	
	public BtObject createBall(float radius ,float mass,Vector3 position){
		return btObjectFactory.createBall(radius, mass, position);
	}
	public RenderableBtObject createRenderableBall(float radius ,float mass,Vector3 position,Color color){
		return btObjectFactory.createRenderableBall(radius, mass, position, color);
	}

	
	
	public BtObjectFactory getBtObjectFactory() {
		return btObjectFactory;
	}

	public void setBtObjectFactory(BtObjectFactory btObjectFactory) {
		this.btObjectFactory = btObjectFactory;
	}

}
