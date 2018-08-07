package com.yuil.game.entity.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.ModelLoader.ModelParameters;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.JsonReader;
import com.yuil.game.entity.attribute.AttributeType;
import com.yuil.game.entity.attribute.GameObjectTypeAttribute;
import com.yuil.game.entity.gameobject.GameObjectType;

public class PhysicsWorldBuilder {
	public BtObjectFactory btObjectFactory;
	G3dModelLoader loader;
	Vector3 tempVector = new Vector3();

	
	FileHandleResolver resolver=new InternalFileHandleResolver();
	
	public PhysicsWorldBuilder(boolean haveDefaultModel) {
		super();
		Gdx.files=new LwjglFiles();
		loader=new G3dModelLoader(new JsonReader(), resolver);
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

	void addPoint(btConvexHullShape collisionShape,float[] vertices){
		int pointNum=vertices.length/3;
		int offset=0;
		for (int i = 0; i < pointNum; i++) {
			collisionShape.addPoint(tempVector.set(vertices[offset],vertices[offset+1],vertices[offset+2]));
			offset+=3;
		}
	}
	
	public btConvexHullShape makeHullShapeFromFile(String fileName){
		btConvexHullShape collisionShape=new btConvexHullShape();
		ModelData md=loader.loadModelData(Gdx.files.internal(fileName));
		//ModelData md=loader.loadModelData(Gdx.files.internal("assets/data/cube_half.g3dj"));
		System.out.println(md.meshes.first().vertices);
		addPoint(collisionShape, md.meshes.first().vertices);
		collisionShape.setLocalScaling(md.nodes.first().scale);
		//collisionShape.setLocalScaling(tempVector.set(100,100,100));

		collisionShape.recalcLocalAabb();
		collisionShape.calculateLocalInertia(1, tempVector.set(0, 0, 0));
		
		
		return collisionShape;
	}
	
	public btConvexHullShape makeGround(){
		btConvexHullShape collisionShape=new btConvexHullShape();
		int width=40;
		int length=400;
		
		collisionShape.addPoint(tempVector.set(-1*(width/2), 0, -1*(length/2)));
		collisionShape.addPoint(tempVector.set(-1*(width/2), 0, length/2));
		collisionShape.addPoint(tempVector.set((width/2), 0, length/2));
		collisionShape.addPoint(tempVector.set((width/2), 0, -1*(length/2)));
		collisionShape.recalcLocalAabb();
		collisionShape.calculateLocalInertia(1, tempVector.set(0, 0, 0));
		return collisionShape;
	}
	public BtObject createDefaultGround(){
		
		/*Model model = loader.loadModel(new FileHandle("assets/data/ship.obj"));
		
		btConvexHullShape chs=new btConvexHullShape(model.nodes.get(0).parts.first().meshPart.mesh.getVerticesBuffer());
		chs.recalcLocalAabb();
		btCollisionShape collisionShape = chs;*/
		
		btConvexHullShape collisionShape=makeHullShapeFromFile("assets/data/groundShape.g3dj");
		
		//btCollisionShape collisionShape = new btBoxShape(tempVector.set(20, 0, 200));

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
		/*Model model = loader.loadModel(Gdx.files.internal("data/ship.obj"));
		
		btConvexHullShape chs=new btConvexHullShape(model.nodes.get(0).parts.first().meshPart.mesh.getVerticesBuffer());
		chs.recalcLocalAabb();
		btCollisionShape collisionShape = chs;*/
		btCollisionShape collisionShape = new btBoxShape(tempVector.set(20, 0, 200));
		float r =200;
		//btCollisionShape collisionShape = new btSphereShape(r);
		/*Model model = btObjectFactory.modelBuilder.createSphere(r*2, r*2, r*2, 100,
				100, new Material(ColorAttribute.createDiffuse(new Color(0.3f, 0.4f, 0.5f, 1)),
						ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)),
				Usage.Position | Usage.Normal);*/
		//Model model = btObjectFactory.defaultGroundModel;
		Model model=loader.loadModel(Gdx.files.internal("data/groundShape.g3dj"));
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
