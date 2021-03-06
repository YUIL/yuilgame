package com.yuil.game.screen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.naming.ldap.ExtendedRequest;
import javax.print.attribute.TextSyntax;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionJNI;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btAxisSweep3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexShape;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.collision.btOverlapFilterCallback;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btCharacterControllerInterface;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.yuil.game.MyGame;
import com.yuil.game.entity.attribute.Attribute;
import com.yuil.game.entity.attribute.AttributeType;
import com.yuil.game.entity.attribute.DamagePoint;
import com.yuil.game.entity.attribute.GameObjectTypeAttribute;
import com.yuil.game.entity.attribute.HealthPoint;
import com.yuil.game.entity.gameobject.GameObjectType;
import com.yuil.game.entity.message.*;
import com.yuil.game.entity.message.UPDATE_BTOBJECT_MOTIONSTATE;
import com.yuil.game.entity.physics.BtObject;
import com.yuil.game.entity.physics.BtObjectFactory;
import com.yuil.game.entity.physics.BtWorld;
import com.yuil.game.entity.physics.PhysicsObject;
import com.yuil.game.entity.physics.PhysicsWorld;
import com.yuil.game.entity.physics.PhysicsWorldBuilder;
import com.yuil.game.entity.physics.RenderableBtObject;
import com.yuil.game.gui.GuiFactory;
import com.yuil.game.input.ActorInputListenner;
import com.yuil.game.input.InputDeviceControler;
import com.yuil.game.input.InputDeviceListener;
import com.yuil.game.input.InputManager;
import com.yuil.game.input.InputDeviceStatus;

public class BulletEngineTestScreen extends Screen2D {

	boolean turnLeft = true;
	long nextTurnTime = 0;

	AssetManager assets=new AssetManager();
	ModelInstance charactor;
	ModelInstance charactor2;

	AnimationController animationController;
	
	public HashMap<String, Object> testVariables = new HashMap<String, Object>();

	ModelBuilder modelBuilder = new ModelBuilder();
	PhysicsWorldBuilder physicsWorldBuilder;
	BtWorld physicsWorld;
	ContactListener contactListener;
	Environment lights;

	InputDeviceStatus inputDeviceStatus = new InputDeviceStatus();

	InputDeviceControler inputControler = new InputDeviceControler(inputDeviceStatus, createInputDeviceListener());
	RenderableBtObject testBtObject;

	public PerspectiveCamera camera;
	CameraInputController camController;

	ModelBatch modelBatch = new ModelBatch();

	Random random = new Random();

	Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/bee.wav"));

	Matrix4 tempMatrix4 = new Matrix4();
	Vector3 tempVector3 = new Vector3();
    Vector3 bulletPosition =new Vector3();

	UPDATE_BTOBJECT_MOTIONSTATE temp_update_rigidbody_message;
	UPDATE_LINEAR_VELOCITY temp_update_liner_velocity_message = new UPDATE_LINEAR_VELOCITY();
	boolean isLogin = false;

	btGhostPairCallback ghostPairCallback;
	
	public BulletEngineTestScreen(MyGame game) {
		super(game);
		
		Bullet.init();

		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/RigidBodyTestScreen.xml";
		guiFactory.setStage(stage, guiXmlPath);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.7f));

		physicsWorldBuilder = new PhysicsWorldBuilder(true);
		physicsWorld = createBtWorld();
		physicsWorld.addPhysicsObject(physicsWorldBuilder.btObjectFactory.createRenderableGround());
		// physicsWorld.addPhysicsObject(createGround());

		MyFilterCallback myFilterCallback = new MyFilterCallback();
		// ((BtWorld)physicsWorld).getCollisionWorld().getPairCache().setOverlapFilterCallback(myFilterCallback);

		contactListener = new MyContactListener();
		// Set up the camera
		final float width = Gdx.graphics.getWidth();
		final float height = Gdx.graphics.getHeight();
		if (width > height)
			camera = new PerspectiveCamera(67f, 3f * width / height, 3f);
		else
			camera = new PerspectiveCamera(67f, 3f, 3f * height / width);
		camera.position.set(10f, 10f, 10f);
		camera.lookAt(0, 0, 0);
		camera.update();
		camController = new CameraInputController(camera);

		setupActorInput();
		InputManager.setInputProcessor(stage, camController);

		
		assets.load("data/cube.g3dj", Model.class);
		assets.finishLoading();
		charactor=new ModelInstance(assets.get("data/cube.g3dj",Model.class));
		charactor2=new ModelInstance(assets.get("data/cube.g3dj",Model.class));
		charactor2.transform.translate(10, 0, 0);
		charactor.transform.translate(0, 0, 0);
		animationController=new AnimationController(charactor);
		
	//	animationController.setAnimation("Cube|CubeAction");
	}

	@Override
	public void render(float delta) {
		animationController.update(delta);
		inputControler.checkDeviceInput();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		physicsWorld.update(delta);
		modelBatch.begin(camera);

		for (PhysicsObject physicsObject : physicsWorld.getPhysicsObjects().values()) {
			ModelInstance modelInstance = ((RenderableBtObject) physicsObject).getInstance();
			((BtObject) physicsObject).getRigidBody().getWorldTransform(modelInstance.transform);
			// System.out.println("!!!!!!!!!!!!!");
			// System.out.println(modelInstance.transform);
			// System.out.println(((BtObject)physicsObject).getRigidBody().getWorldTransform());

			// System.out.println("!!!!!!!!");
			// System.out.println(modelInstance.transform);
			// modelInstance.transform.scl(new Vector3(5, 5, 5));
			GameObjectTypeAttribute gameObjectType = (GameObjectTypeAttribute) (((BtObject) physicsObject).getAttributes()
					.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));

			if (gameObjectType != null) {
				if (gameObjectType.getGameObjectType() == GameObjectType.GROUND.ordinal()) {
					// modelInstance.transform.translate(tempVector3.set(0,-0.1f,0));
					// modelInstance.transform.scale(2, 2, 2);
				}
			} else {
				// System.out.println("---------((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling()----"+((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling());
				// modelInstance.nodes.first().localTransform.scl(((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling());
				// modelInstance.nodes.first().calculateLocalTransform();
				// System.out.println(modelInstance.nodes.first().scale);

				modelInstance.transform
						.scl(((BtObject) physicsObject).getRigidBody().getCollisionShape().getLocalScaling());
				// System.out.println(((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling().set(2,
				// 4, 4));

			}
			// modelInstance.transform.scl(((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling());
			// modelInstance.transform.translate(translation)
			// System.out.println(((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling());

			modelBatch.render(modelInstance, lights);
		}
		modelBatch.render(charactor, lights);
		modelBatch.render(charactor2, lights);

		//System.out.println(charactor.getNode("Cube").translation);
		
		modelBatch.end();
		super.render(delta);
	}

	public class MyContactListener extends ContactListener {
		@Override
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			 System.out.println("onContactStarted");
			if (colObj0 instanceof btRigidBody && colObj1 instanceof btRigidBody) {
				BtObject btObject0 = (BtObject) (((btRigidBody) colObj0).userData);
				BtObject btObject1 = (BtObject) (((btRigidBody) colObj1).userData);

				int type0=GameObjectTypeAttribute.getGameObjectType(btObject0);
				int type1=GameObjectTypeAttribute.getGameObjectType(btObject1);

				if (type0 == GameObjectType.PLAYER.ordinal() && type1== GameObjectType.PLAYER.ordinal()) {
					System.out.println("pcp");
				}else if(type0==GameObjectType.PLAYER.ordinal()&& type1==GameObjectType.BULLET.ordinal()){
					HealthPoint hp=((HealthPoint)(btObject0.getAttributes().get(AttributeType.HEALTH_POINT.ordinal())));
					DamagePoint dp=((DamagePoint)(btObject1.getAttributes().get(AttributeType.DAMAGE_POINT.ordinal())));
					hp.setHealthPoint(hp.getHealthPoint()-dp.getDamagePoint());
					if (hp.getHealthPoint()<=0){
						physicsWorld.removePhysicsObject(btObject0);
					}
					physicsWorld.removePhysicsObject(btObject1);
				}else if(type0==GameObjectType.BULLET.ordinal()&& type1==GameObjectType.PLAYER.ordinal()){
					HealthPoint hp=((HealthPoint)(btObject1.getAttributes().get(AttributeType.HEALTH_POINT.ordinal())));
					DamagePoint dp=((DamagePoint)(btObject0.getAttributes().get(AttributeType.DAMAGE_POINT.ordinal())));
					hp.setHealthPoint(hp.getHealthPoint()-dp.getDamagePoint());
					if (hp.getHealthPoint()<=0){
						physicsWorld.removePhysicsObject(btObject1);
					}
					physicsWorld.removePhysicsObject(btObject0);
				}
			}
		}
	
		@Override
		public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {
		
			System.out.println("onContactEnded()");
			
			if (colObj0 instanceof btRigidBody && colObj1 instanceof btRigidBody) {
				BtObject btObject0 = (BtObject) (((btRigidBody) colObj0).userData);
				BtObject btObject1 = (BtObject) (((btRigidBody) colObj1).userData);

				int type0=GameObjectTypeAttribute.getGameObjectType(btObject0);
				int type1=GameObjectTypeAttribute.getGameObjectType(btObject1);

				if (type0 == GameObjectType.PLAYER.ordinal() && type1== GameObjectType.PLAYER.ordinal()) {
					btObject0.getRigidBody().activate();
					btObject1.getRigidBody().activate();
			
				}
			}
		}
		
	}

	public class MyFilterCallback extends btOverlapFilterCallback {
		public boolean needBroadphaseCollision(btBroadphaseProxy proxy0, btBroadphaseProxy proxy1) {
			boolean collides = false;

			System.out.println("filter!");
			return collides;
		}

	}

	void setupActorInput() {
		stage.getRoot().findActor("A").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				inputControler.deviceInputListener.aJustUppedAction();
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				inputControler.deviceInputListener.aJustPressedAction();
				return true;
			}
		});
		stage.getRoot().findActor("D").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				inputControler.deviceInputListener.dJustUppedAction();
			}

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				inputControler.deviceInputListener.dJustPressedAction();
				return true;
			}
		});
		stage.getRoot().findActor("Z").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				inputControler.deviceInputListener.zJustPressedAction();
			}
		});
		stage.getRoot().findActor("X").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				inputControler.deviceInputListener.delJustPressedAction();
			}
		});
		stage.getRoot().findActor("G").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				// gameObjectId = Long.parseLong(((TextArea)
				// stage.getRoot().findActor("userName")).getText());

			}
		});

		stage.getRoot().findActor("W").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				inputControler.deviceInputListener.wJustPressedAction();
			}
		});

		stage.getRoot().findActor("S").addListener(new ActorInputListenner() {

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				inputControler.deviceInputListener.sJustPressedAction();
			}
		});

		stage.getRoot().findActor("login").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
	}

	public void updatePhysicsObject(BtObject btObject, UPDATE_BTOBJECT_MOTIONSTATE message) {
		// btMotionState tempMotionState =new btMotionState();

		// btObject.getRigidBody().setMotionState(tempMotionState);
		tempMatrix4.set(message.getTransformVal());
		btObject.getRigidBody().setWorldTransform(tempMatrix4);
		// btObject.getMotionState().setWorldTransform(tempMatrix4);

		tempVector3.x = message.getLinearVelocityX();
		tempVector3.y = message.getLinearVelocityY();
		tempVector3.z = message.getLinearVelocityZ();
		btObject.getRigidBody().setLinearVelocity(tempVector3);

		tempVector3.x = message.getAngularVelocityX();
		tempVector3.y = message.getAngularVelocityY();
		tempVector3.z = message.getAngularVelocityZ();
		btObject.getRigidBody().setAngularVelocity(tempVector3);
	}

	BtWorld createBtWorld(){
		btAxisSweep3 sweep = new btAxisSweep3(new Vector3(-1000, -1000, -1000), new Vector3(1000, 1000, 1000));
		BtWorld world=new BtWorld(new Vector3(0, -9.81f, 0), sweep); 
		ghostPairCallback = new btGhostPairCallback();
		sweep.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);

		return world;
	}
	
	RenderableBtObject createGround() {
		RenderableBtObject ground;
		Model model = modelBuilder.createRect(1f, 0f, -1f, -1f, 0f, -1f, -1f, 0f, 1f, 1f, 0f, 1f, 0, 1, 0,
				new Material(ColorAttribute.createDiffuse(new Color(0.2f, 0.4f, 0.6f, 1)),
						ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(16f)),
				Usage.Position | Usage.Normal);
		Vector3 tempVector = new Vector3();
		btCollisionShape collisionShape = new btBoxShape(tempVector.set(1, 0, 1));
		ground = physicsWorldBuilder.btObjectFactory.createRenderableBtObject(model, collisionShape, 0, 0, 0, 0);
		ground.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.GROUND.ordinal()));
		//ground.getRigidBody().setContactCallbackFlag(8);
		ground.getRigidBody().setContactCallbackFilter(16);
		return ground;
	}

	RenderableBtObject createTestObject() {
		System.out.println("createTestObject");
		Vector3 tempVector = new Vector3();

		RenderableBtObject testObject;
		// Model model=modelBuilder.createBox(2, 2, 2, new
		// Material(ColorAttribute.createDiffuse(new Color(0.7f, 0.1f, 0.1f,
		// 1)),
		// ColorAttribute.createSpecular(Color.WHITE),
		// FloatAttribute.createShininess(64f)),
		// Usage.Position | Usage.Normal);
		/*
		 * Vector3 tempVector = new Vector3(); btCollisionShape collisionShape =
		 * new btBoxShape(tempVector.set(1,1,1));
		 * testObject=physicsWorldBuilder.btObjectFactory.
		 * createRenderableBtObject(model, collisionShape, 1, 0, 10, 0);
		 */
		// testObject.Attributes.put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
		// new GameObjectTypeAttribute(GameObjectType.GROUND.ordinal()));

		Model model = modelBuilder.createSphere(1, 1, 1, 10, 10,
				new Material(ColorAttribute.createDiffuse(new Color(0.7f, 0.1f, 0.1f, 1)),
						ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)),
				Usage.Position | Usage.Normal);
		btSphereShape collisionShape = new btSphereShape(0.5f);
		testObject = physicsWorldBuilder.btObjectFactory.createRenderableBtObject(model, collisionShape, 1, 0, 10, 0);
		//testObject.getRigidBody().setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
		testObject.getRigidBody().getCollisionShape().setLocalScaling(new Vector3(1f, 1f, 1f));
		testObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER.ordinal()));
		testObject.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(100));

		//testObject.setMask((short)(Short.MAX_VALUE^2));
		testObject.getRigidBody().setContactCallbackFlag(2);
		testObject.getRigidBody().setContactCallbackFilter(2);
		System.out.println("asdasd:"+testObject.getRigidBody().getContactCallbackFilter());

/*
		btGhostObject ghostObject = new btGhostObject();
		ghostObject.setCollisionShape(new btSphereShape(3));
		ghostObject.userData = testObject;
		((BtWorld) physicsWorld).getCollisionWorld().addCollisionObject(ghostObject);

		System.out.println("asd:" + testObject.getRigidBody().getCollisionFlags());*/
		// testObject.getRigidBody().setContactCallbackFilter((1<<GameObjectType.GROUND.ordinal())|(1<<GameObjectType.OBSTACLE.ordinal()));
		return testObject;
	}
	
	RenderableBtObject createBullet() {
		System.out.println("createBullet");
		RenderableBtObject bullet;

		Model model = modelBuilder.createSphere(1, 1, 1, 10, 10,
				new Material(ColorAttribute.createDiffuse(new Color(0.1f, 0.1f, 0.1f, 1)),
						ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)),
				Usage.Position | Usage.Normal);
		btSphereShape collisionShape = new btSphereShape(0.5f);
		bullet = physicsWorldBuilder.btObjectFactory.createRenderableBtObject(model, collisionShape, 1, 0, 10, 0);
		//testObject.getRigidBody().setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
		bullet.getRigidBody().getCollisionShape().setLocalScaling(new Vector3(1f, 1f, 1f));
		bullet.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.BULLET.ordinal()));
		bullet.getAttributes().put(AttributeType.DAMAGE_POINT.ordinal(), new DamagePoint(20));
		
		bullet.getRigidBody().setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
		
		return bullet;
	}
	void characterControllerTest(){
	
		
		btPairCachingGhostObject ghostObject =new btPairCachingGhostObject();;
		testVariables.put("ghostObject", ghostObject);
		
		btConvexShape ghostShape = new btSphereShape(0.5f);
		testVariables.put("ghostShape", ghostShape);
		
		btKinematicCharacterController characterController=new btKinematicCharacterController(ghostObject, ghostShape,0.35f, Vector3.Y);
		testVariables.put("characterController", characterController);
		

		ghostObject.setCollisionShape(ghostShape);
		ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);

		physicsWorld.getCollisionWorld().addCollisionObject(ghostObject, 
				(short)btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
				(short)((btBroadphaseProxy.CollisionFilterGroups.DefaultFilter)|btBroadphaseProxy.CollisionFilterGroups.StaticFilter));

		
		physicsWorld.getCollisionWorld().addAction(characterController);

	}

	InputDeviceListener createInputDeviceListener() {
		return new InputDeviceListener() {

			@Override
			public void zJustUppedAction() {
				
				btCharacterControllerInterface characterController=(btCharacterControllerInterface)(testVariables.get("characterController"));
				characterController.jump(Vector3.Y.scl(100));
				
			}

			@Override
			public void zJustPressedAction() {
				

			}

			@Override
			public void yJustUppedAction() {
				
				characterControllerTest();

			}

			@Override
			public void yJustPressedAction() {
				

			}

			@Override
			public void xJustUppedAction() {
				
				btCharacterControllerInterface characterController=(btCharacterControllerInterface)(testVariables.get("characterController"));

				btPairCachingGhostObject ghostObject=(btPairCachingGhostObject)(testVariables.get("ghostObject"));
				Vector3 tempv3=new Vector3();
				ghostObject.getWorldTransform().getTranslation(tempv3);
				System.out.println("canjump:"+characterController.canJump());
				System.out.println("v3.y:"+Vector3.Y);
				System.out.println("OverlappingNum:"+ghostObject.getNumOverlappingObjects());
				System.out.println("position:"+tempv3);
			}

			@Override
			public void xJustPressedAction() {
				

			}

			@Override
			public void wJustUppedAction() {
				System.out.println("wwww");
				//testBtObject=physicsWorldBuilder.createObstacleRenderableBall(1, 1, new Vector3(0,0,0),new Color(0f,1f,0f,1f));
				if(testBtObject==null){

					testBtObject=createTestObject();
					
					//btObject.getRigidBody().setContactCallbackFilter((1<<GameObjectType.GROUND.ordinal())|(1<<GameObjectType.OBSTACLE.ordinal()));

					//testBtObject.getRigidBody().getWorldTransform(tempMatrix4);
					//tempMatrix4.scale(5, 5, 5);
					//testBtObject.getRigidBody().setWorldTransform(tempMatrix4);
					System.out.println(testBtObject.getRigidBody().getWorldTransform());
					physicsWorld.addPhysicsObject(testBtObject);
					testBtObject.getRigidBody().setMotionState(new btDefaultMotionState(){
						  public void setWorldTransform(Matrix4 worldTrans) {
							  System.out.println("setWorldTransform");
							  super.setWorldTransform(worldTrans);
						  }
					});
					testBtObject.getRigidBody().setWorldTransform(new Matrix4(new Vector3(0, 10, 0), new Quaternion(), new Vector3(1,1,1)));

					//((BtWorld)physicsWorld).getCollisionWorld().getDebugDrawer();
					//testBtObject.getRigidBody().setLinearVelocity(new Vector3(0,0,2));

				}else{
					BtObject bo=createTestObject();
					
					//bo.getRigidBody().setIgnoreCollisionCheck(testBtObject.getRigidBody(), true);
					physicsWorld.addPhysicsObject(bo);
				}
			}

			@Override
			public void wJustPressedAction() {
				

			}

			@Override
			public void vJustUppedAction() {
				
			}

			@Override
			public void vJustPressedAction() {
				
			}

			@Override
			public void uJustUppedAction() {
				

			}

			@Override
			public void uJustPressedAction() {
				

			}

			@Override
			public void tJustUppedAction() {
				
				Vector3 v3=new Vector3(1,1,1);
				v3.rotate(180f, 0, 1, 0);
				System.out.println(v3);
			}

			@Override
			public void tJustPressedAction() {
				

			}

			@Override
			public void spaceJustUppedAction() {
				

			}

			@Override
			public void spaceJustPressedAction() {
				

			}

			@Override
			public void sJustUppedAction() {
				

			}

			@Override
			public void sJustPressedAction() {
				

			}

			@Override
			public void rJustUppedAction() {
				

			}

			@Override
			public void rJustPressedAction() {
				

			}

			@Override
			public void qJustUppedAction() {
				

			}

			@Override
			public void qJustPressedAction() {
				

			}

			@Override
			public void pJustUppedAction() {
				

			}

			@Override
			public void pJustPressedAction() {
				

			}

			@Override
			public void oJustUppedAction() {
				

			}

			@Override
			public void oJustPressedAction() {
				

			}

			@Override
			public void nJustUppedAction() {
				

			}

			@Override
			public void nJustPressedAction() {
				

			}

			@Override
			public void mouseRightJustUppedAction() {
				Matrix4 tm4=new Matrix4();
		       // Ray ray = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
				tempVector3.set(camera.position);
				//tempVector3.y=testBtObject.getPosition().y;
				 Ray ray = new Ray(camera.position, testBtObject.getPosition(tm4).sub(tempVector3).nor());
		        tempVector3=ray.direction;
		        
		        BtObject bullet=createBullet();
		        
		        bullet.getRigidBody().getWorldTransform(tempMatrix4);
		        tempMatrix4.setTranslation(testBtObject.getPosition(tm4).x,testBtObject.getPosition(tm4).y,testBtObject.getPosition(tm4).z-2);
		        bullet.getTransform().getTranslation(bulletPosition);
		        bullet.getRigidBody().setWorldTransform(tempMatrix4);
		        bullet.getRigidBody().applyForce(tempVector3.scl(1000),bulletPosition);
		        
		        physicsWorld.addPhysicsObject(bullet);		
		    }

			@Override
			public void mouseRightJustPressedAction() {
				

			}

			@Override
			public void mouseMiddleJustUppedAction() {
				

			}

			@Override
			public void mouseMiddleJustPressedAction() {
				

			}

			@Override
			public void mouseLeftJustUppedAction() {
				

			}

			@Override
			public void mouseLeftJustPressedAction() {
				

			}

			@Override
			public void mJustUppedAction() {
				
				testBtObject.getRigidBody().getMotionState().setWorldTransform(new Matrix4(new Vector3(0, 10, 0), new Quaternion(), new Vector3(1,1,1)));
			}

			@Override
			public void mJustPressedAction() {
				

			}

			@Override
			public void lJustUppedAction() {
				

			}

			@Override
			public void lJustPressedAction() {
				

			}

			@Override
			public void kJustUppedAction() {
				

			}

			@Override
			public void kJustPressedAction() {
				

			}

			@Override
			public void jJustUppedAction() {
				

			}

			@Override
			public void jJustPressedAction() {
				

			}

			@Override
			public void iJustUppedAction() {
				

			}

			@Override
			public void iJustPressedAction() {
				

			}

			@Override
			public void hJustUppedAction() {
				

			}

			@Override
			public void hJustPressedAction() {
				

			}

			@Override
			public void gJustUppedAction() {
				

			}

			@Override
			public void gJustPressedAction() {
				

			}

			@Override
			public void fJustUppedAction() {
				

			}

			@Override
			public void fJustPressedAction() {
				

			}

			@Override
			public void eJustUppedAction() {
				

			}

			@Override
			public void eJustPressedAction() {
				

			}

			@Override
			public void delJustUppedAction() {
				

			}

			@Override
			public void delJustPressedAction() {
				

			}

			@Override
			public void dJustUppedAction() {
				

			}

			@Override
			public void dJustPressedAction() {
				

			}

			@Override
			public void cJustUppedAction() {
				

			}

			@Override
			public void cJustPressedAction() {
				

			}

			@Override
			public void bJustUppedAction() {
				
			}

			@Override
			public void bJustPressedAction() {
				

			}

			@Override
			public void aJustUppedAction() {
				

			}

			@Override
			public void aJustPressedAction() {
				

			}

			@Override
			public void Num9JustUppedAction() {
				

			}

			@Override
			public void Num9JustPressedAction() {
				

			}

			@Override
			public void Num8JustUppedAction() {
				

			}

			@Override
			public void Num8JustPressedAction() {
				

			}

			@Override
			public void Num7JustUppedAction() {
				

			}

			@Override
			public void Num7JustPressedAction() {
				

			}

			@Override
			public void Num6JustUppedAction() {
				

			}

			@Override
			public void Num6JustPressedAction() {
				

			}

			@Override
			public void Num5JustUppedAction() {
				

			}

			@Override
			public void Num5JustPressedAction() {
				

			}

			@Override
			public void Num4JustUppedAction() {
				

			}

			@Override
			public void Num4JustPressedAction() {
				

			}

			@Override
			public void Num3JustUppedAction() {
				

			}

			@Override
			public void Num3JustPressedAction() {
				

			}

			@Override
			public void Num2JustUppedAction() {
				

				if (animationController.current!=null) {
					System.out.println("at:"+animationController.current.time);
					animationController.current.time=0;
				}
				animationController.setAnimation("Cube|CubeAction.002");
			}

			@Override
			public void Num2JustPressedAction() {
				

			}

			@Override
			public void Num1JustUppedAction() {

				if (animationController.current!=null) {
					System.out.println("at:"+animationController.current.time);
					animationController.current.time=0;
				}
				animationController.setAnimation("Cube|CubeAction");
				
			}

			@Override
			public void Num1JustPressedAction() {


			}

			@Override
			public void Num0JustUppedAction() {
				

			}

			@Override
			public void Num0JustPressedAction() {
				

			}
		};
	}
}
