package com.yuil.game.screen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.yuil.game.MyGame;
import com.yuil.game.entity.attribute.AttributeType;
import com.yuil.game.entity.attribute.DamagePoint;
import com.yuil.game.entity.attribute.ExplosionStrength;
import com.yuil.game.entity.attribute.GameObjectTypeAttribute;
import com.yuil.game.entity.attribute.HealthPoint;
import com.yuil.game.entity.attribute.MoveSpeed;
import com.yuil.game.entity.attribute.OwnerPlayerId;
import com.yuil.game.entity.gameobject.GameObjectType;
import com.yuil.game.entity.message.*;
import com.yuil.game.entity.message.action.VollyBallAction;
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
import com.yuil.game.net.MessageListener;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.MULTI_MESSAGE;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageHandler;
import com.yuil.game.net.message.MessageType;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.net.message.SINGLE_MESSAGE;
import com.yuil.game.net.udp.ClientSocket;
import com.yuil.game.util.Log;

import io.netty.buffer.ByteBuf;

public class VolleyballScreen extends Screen2D implements MessageListener {

	Queue<S2C_ADD_OBSTACLE> createObstacleQueue = new ConcurrentLinkedQueue<S2C_ADD_OBSTACLE>();

	boolean turnLeft = true;
	long nextTurnTime = 0;

	final float NO_CHANGE = 1008611;// 代表无效参数的一个值

	ClientSocket clientSocket;
	Map<Integer, MessageHandler> messageHandlerMap = new HashMap<Integer, MessageHandler>();

	PhysicsWorldBuilder physicsWorldBuilder;
	PhysicsWorld physicsWorld;
	Environment lights;

	ContactListener contactListener;

	InputDeviceStatus inputDeviceStatus = new InputDeviceStatus();
	InputDeviceControler deviceInputHandler;

	public PerspectiveCamera camera;
	Vector3 previousCameraDirection=new Vector3();
	CameraInputController camController;

	ModelBatch modelBatch = new ModelBatch();

	long interval = 100;
	long nextTime = 0;

	Random random = new Random(System.currentTimeMillis());

	long playerId;
	BtObject playerObject;
	Vector3 playerPrePosition = new Vector3();
	Vector3 moveDirection=new Vector3();
	boolean playerForward=false;
	boolean playerBack=false;
	boolean playerLeft=false;
	boolean playerRight=false;
	boolean playerDirectionChanged=false;

	Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound/bee.wav"));

	Matrix4 tempMatrix4 = new Matrix4();
	Vector3 tempVector3 = new Vector3();

	UPDATE_BTOBJECT_MOTIONSTATE message_update_rigidbody;
	UPDATE_LINEAR_VELOCITY message_update_liner_velocity = new UPDATE_LINEAR_VELOCITY();
	DO_ACTION message_do_action = new DO_ACTION();
	MOVE_DIRECTION message_move_direction=new MOVE_DIRECTION();
	
	boolean isLogin = false;

	public VolleyballScreen(MyGame game) {
		super(game);
		Bullet.init();
		deviceInputHandler = new InputDeviceControler(inputDeviceStatus, createDeviceInputListener());

		//clientSocket = new ClientSocket(9092, "39.106.33.9", 9091, this);
		clientSocket = new ClientSocket(9092, "127.0.0.1", 9091, this);

		initMessageHandle();

		GuiFactory guiFactory = new GuiFactory();
		String guiXmlPath = "gui/VolleyballScreen.xml";
		guiFactory.setStage(stage, guiXmlPath);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1.f));
		lights.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1f, -0.7f));

		physicsWorldBuilder = new PhysicsWorldBuilder(true);
		physicsWorld = new BtWorld();
		physicsWorld.addPhysicsObject(physicsWorldBuilder.btObjectFactory.createRenderableGround());

		contactListener = new MyContactListener();

		// Set up the camera
		final float width = Gdx.graphics.getWidth();
		final float height = Gdx.graphics.getHeight();
		if (width > height)
			camera = new PerspectiveCamera(45, 3f * width / height, 3f);
		else
			camera = new PerspectiveCamera(45, 3f, 3f * height / width);
		camera.far = 200;
		camera.position.set(10f, 10f, 10f);
		camera.lookAt(0, 0, 0);
		camera.update();
		camController = new CameraInputController(camera);

		setupActorInput();
		InputManager.setInputProcessor(stage, camController);

		nextTime = System.currentTimeMillis();
	}

	public class MyContactListener extends ContactListener {
		Vector3 v3 = new Vector3();

		@Override
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			if (colObj0 instanceof btRigidBody && colObj1 instanceof btRigidBody) {
				BtObject btObject0 = (BtObject) (((btRigidBody) colObj0).userData);
				BtObject btObject1 = (BtObject) (((btRigidBody) colObj1).userData);
				handleBtObject(btObject0);
				handleBtObject(btObject1);

				if (btObject0.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()) != null) {
					System.out.println("explosion!!!!!!!!!!!!!!!!!!!!!!!");
					physicsWorld.removePhysicsObject(btObject1);
				} else if (btObject1.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()) != null) {
					System.out.println("explosion!!!!!!!!!!!!!!!!!!!!!!!");
					physicsWorld.removePhysicsObject(btObject0);

				}

				GameObjectTypeAttribute gameObjectType0 = (GameObjectTypeAttribute) (btObject0.getAttributes()
						.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
				GameObjectTypeAttribute gameObjectType1 = (GameObjectTypeAttribute) (btObject1.getAttributes()
						.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));

				if (gameObjectType0 != null && gameObjectType1 != null) {
					if (gameObjectType0.getGameObjectType() == GameObjectType.PLAYER.ordinal()
							&& gameObjectType1.getGameObjectType() == GameObjectType.OBSTACLE.ordinal()) {
						// System.out.println("coll");
						// sound.play();
					} else if (gameObjectType0.getGameObjectType() == GameObjectType.OBSTACLE.ordinal()
							&& gameObjectType1.getGameObjectType() == GameObjectType.PLAYER.ordinal()) {
						// System.out.println("coll");

						// sound.play();

					}

				}

				/*
				 * if(gameObjectType != null){
				 * if(gameObjectType.getGameObjectType()==com.yuil.game.entity.
				 * gameobject.GameObjectType.OBSTACLE.ordinal()){
				 * System.out.println("asdasds");
				 * physicsWorld.removePhysicsObject(btObject);
				 * remove_BTOBJECT_message.setId(btObject.getId());
				 * broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message,
				 * false); } }
				 */

			}
		}

		void handleBtObject(BtObject btObject) {
			if (btObject.getAttributes().get(AttributeType.OWNER_PLAYER_ID.ordinal()) != null) {
				v3.set(0, btObject.getRigidBody().getLinearVelocity().y, 0);
				btObject.getRigidBody().setLinearVelocity(v3);
			}
		}

		@Override
		public void onContactEnded(int userValue0, boolean match0, int userValue1, boolean match1) {
			if (match0) {
				// collision object 0 (userValue0) matches
			}
			if (match1) {
				// collision object 1 (userValue1) matches
			}
			// System.out.println("onContactEnded()");
		}
	}

	@Override
	public void render(float delta) {
		// checkKeyBoardStatus();
		deviceInputHandler.checkDeviceInput();
		if(!previousCameraDirection.equals(camera.direction)){
			if(Gdx.input.isKeyPressed(Keys.A)||Gdx.input.isKeyPressed(Keys.D)||Gdx.input.isKeyPressed(Keys.S)||Gdx.input.isKeyPressed(Keys.W)){
				playerDirectionChanged=true;
			}
		}
		previousCameraDirection.set(camera.direction);
		sendMessage_MOVE_DIRECTION();
		while (!createObstacleQueue.isEmpty()) {
			S2C_ADD_OBSTACLE message = createObstacleQueue.poll();
			C2S_UPDATE_BTOBJECT_MOTIONSTATE c2s_UPDATE_BTOBJECT_MOTIONSTATE_message = new C2S_UPDATE_BTOBJECT_MOTIONSTATE();
			Color color = new Color();
			Vector3 v3 = new Vector3();
			if (physicsWorld.getPhysicsObjects().get(message.getId()) == null) {
				v3.x = 0;
				v3.y = -100;
				v3.z = -100;
				color.set(message.getR(), message.getG(), message.getB(), message.getA());
				// System.out.println("color.g:"+message.getG());
				BtObject btObject = physicsWorldBuilder.createRenderableBall(message.getRadius(), message.getRadius(),
						v3, color);
				btObject.setId(message.getId());
				btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
						new GameObjectTypeAttribute(GameObjectType.OBSTACLE.ordinal()));
				btObject.getAttributes().put(AttributeType.DAMAGE_POINT.ordinal(), new DamagePoint(1));
				btObject.getAttributes().put(AttributeType.COLOR.ordinal(),
						new com.yuil.game.entity.attribute.Color(color));
				// btObject.getRigidBody().setContactCallbackFilter(GameObjectType.PLAYER.ordinal());
				btObject.getRigidBody().setContactCallbackFilter(1 << GameObjectType.GROUND.ordinal());

				physicsWorld.addPhysicsObject(btObject);
				c2s_UPDATE_BTOBJECT_MOTIONSTATE_message.setId(message.getId());
				sendSingleMessage(c2s_UPDATE_BTOBJECT_MOTIONSTATE_message);
			}

		}
		/*
		 * if(System.currentTimeMillis()>nextTurnTime){ aJustUppedAction();
		 * nextTurnTime=System.currentTimeMillis()+100; if (turnLeft) {
		 * aJustPressedAction(); }else{ dJustPressedAction(); }
		 * turnLeft=!turnLeft; }
		 */

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1.f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (System.currentTimeMillis() >= nextTime) {
			nextTime += interval;
			// physicsWorld.update(interval);
			/* physicsWorld.addPhysicsObject(btObjectFactory.createBall()); */
			// zJustPressAction();
		}
		physicsWorld.update(delta);

		if (playerObject == null) {
			if (playerId != 0) {
				playerObject = (BtObject) physicsWorld.getPhysicsObjects().get(playerId);
			}
		} else {
			// System.out.println("x:"+playerObject.getPosition().x);
			try {
				// camera.translate(playerObject.getPosition(tempMatrix4).sub(playerPrePosition));
				camera.translate(playerObject.getRigidBody().getWorldTransform().getTranslation(tempVector3)
						.sub(playerPrePosition));
				// camera.lookAt(playerObject.getPosition().x,playerObject.getPosition().y,
				// playerObject.getPosition().z);
				// playerPrePosition.set(playerObject.getPosition(tempMatrix4));
				playerPrePosition.set(playerObject.getRigidBody().getWorldTransform().getTranslation(tempVector3));
				camController.target.set(playerObject.getRigidBody().getWorldTransform().getTranslation(tempVector3));

				camera.update();
			} catch (Exception e) {
				// System.out.println("object已被刪除");
			}
		}
		modelBatch.begin(camera);

		for (PhysicsObject physicsObject : physicsWorld.getPhysicsObjects().values()) {
			if (physicsObject instanceof RenderableBtObject) {

				BtObject btObject = (BtObject) physicsObject;
				if (btObject.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()) != null) {
					physicsWorld.removePhysicsObject(physicsObject);
				}

				ModelInstance modelInstance = ((RenderableBtObject) physicsObject).getInstance();
				((BtObject) physicsObject).getRigidBody().getWorldTransform(modelInstance.transform);
				//GameObjectTypeAttribute gameObjectType = (GameObjectTypeAttribute) (((BtObject) physicsObject).getAttributes().get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
				/*
				 * if (gameObjectType!=null) { if
				 * (gameObjectType.getGameObjectType()==GameObjectType.GROUND.
				 * ordinal()){
				 * 
				 * }else{
				 * System.out.println(((BtObject)physicsObject).getRigidBody().
				 * getCollisionShape().getLocalScaling()); } }
				 */
				modelInstance.transform
						.scl(((BtObject) physicsObject).getRigidBody().getCollisionShape().getLocalScaling());
				// modelInstance.nodes.first().localTransform.scl(((BtObject)physicsObject).getRigidBody().getCollisionShape().getLocalScaling());

				if (((GameObjectTypeAttribute) (((BtObject) physicsObject).getAttributes()
						.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()))).getGameObjectType() == GameObjectType.OBSTACLE
								.ordinal()) {
					// 检查障碍物位置,超过边界则删除
					// System.out.println("asdasd");
					if (((BtObject) physicsObject).getPosition(tempMatrix4).z > 200) {
						physicsWorld.removePhysicsObject(physicsObject);
						// remove_BTOBJECT_message.setId(btObject.getId());
						// broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message,
						// false);
					}
				}
				// modelInstance.transform.scl(2);
				// System.out.println(modelInstance);
				modelBatch.render(modelInstance, lights);
			}
		}
		modelBatch.end();
		super.render(delta);
	}

	@Override
	public void recvMessage(Session session, ByteBuf data) {
		/*
		 * try { Runtime.getRuntime().exec(
		 * "cmd /k start /MIN d:\\bat\\test1.bat" ); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */
		// TODO Auto-generated method stub
		if (data.array().length < Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data.array());
		// System.out.println("type:" + GameMessageType.values()[typeOrdinal]);

		switch (MessageType.values()[typeOrdinal]) {
		case MULTI_MESSAGE:
			MULTI_MESSAGE message_ARRAY = new MULTI_MESSAGE(data);
			for (ByteBuf data1 : message_ARRAY.gameMessages) {
				disposeSingleMessage(session, data1);
			}
			break;
		case SINGLE_MESSAGE:
			data.skipBytes(Message.TYPE_LENGTH);
			data.discardReadBytes();
			disposeSingleMessage(session, data);
			break;
		default:
			break;
		}
	}

	void disposeSingleMessage(Session session, ByteBuf data) {
		if (data.array().length < Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data.array());
		// System.out.println("typeOrdinal"+typeOrdinal);
		messageHandlerMap.get(typeOrdinal).handle(data);
	}

	public long getObjectId(int screenX, int screenY) {
		return getObject(screenX, screenY).getId();
	}

	public PhysicsObject getObject(int screenX, int screenY) {
		Ray ray = camera.getPickRay(screenX, screenY);
		Vector3 position = new Vector3();
		float dst = 0;
		float dst2 = Float.MAX_VALUE;
		PhysicsObject result = null;
		for (PhysicsObject physicsObject : physicsWorld.getPhysicsObjects().values()) {

			((BtObject)physicsObject).getRigidBody().getWorldTransform().getTranslation(position);
			dst = position.dst2(camera.position);
			if (Intersector.intersectRaySphere(ray, position, 0.6f, null)) {
				if (dst < dst2) {
					dst2 = dst;
					result = physicsObject;
				}

			}
		}
		return result;
	}

	public void createCubes() {

		Vector3 tmpV3 = new Vector3();
		Color tmpCor = new Color(55, 55, 55, 1);
		// for (int y = 1; y < 100; y++) {
		for (int z = -20; z < 20; z++) {
			for (int x = -20; x < 20; x++) {
				tmpV3.set(x, 0, z);
				tmpCor.set(random.nextInt(255) / 255f, random.nextInt(255) / 255f, random.nextInt(255) / 255f, 1);
				// System.out.println(tmpCor.toString());
				RenderableBtObject rb = physicsWorldBuilder.btObjectFactory.createRenderableCube(1f, 0f, tmpV3, tmpCor);
				// BtObject rb=
				// physicsWorldBuilder.btObjectFactory.createCube(1f,0f,tmpV3);
				rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
						new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
				rb.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
				physicsWorld.addPhysicsObject(rb);
			}
		}
		// }
	}

	public void makeExplosion(Vector3 position) {
		RenderableBtObject rb = physicsWorldBuilder.btObjectFactory.createRenderableBall(10, 0, position,
				new Color(55 / 255f, 55 / 255f, 55 / 255f, 1));
		rb.getRigidBody().setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
		rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));

		rb.getAttributes().put(AttributeType.EXPLOSION_STRENGTH.ordinal(), new ExplosionStrength(50));
		physicsWorld.addPhysicsObject(rb);
	}

	public void createVolleyballCourt() {

		Vector3 position = new Vector3();
		Color tmpCor = new Color(55, 55, 55, 1);
		tmpCor.set(0.4f, 0.5f, 0.6f, 1);

		RenderableBtObject rb = null;
		rb = physicsWorldBuilder.btObjectFactory.createRenderableCube(10f, 0f, position, tmpCor);
		rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
		rb.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
		physicsWorld.addPhysicsObject(rb);

		position.x -= 2.5f;
		position.y += 5;
		position.z += 7.5f;
		rb = physicsWorldBuilder.btObjectFactory.createRenderableCube(5f, 0f, position, tmpCor);
		rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
		rb.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
		physicsWorld.addPhysicsObject(rb);
		position.x += 5;
		rb = physicsWorldBuilder.btObjectFactory.createRenderableCube(5f, 0f, position, tmpCor);
		rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
		rb.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
		physicsWorld.addPhysicsObject(rb);

		position.x -= 2.5f;
		position.y -= 5;
		position.z += 7.5f;
		rb = physicsWorldBuilder.btObjectFactory.createRenderableCube(10f, 0f, position, tmpCor);
		rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
		rb.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
		physicsWorld.addPhysicsObject(rb);
	}
	
	public void sendMessage_ADD_PLAYER(){
		if (isLogin) {
			// sound.play();
			if (playerId == 0) {
				playerId = random.nextLong();
				System.out.println("new id:" + playerId);
				C2S_ADD_PLAYER add_player = new C2S_ADD_PLAYER();
				add_player.setId(playerId);
				sendSingleMessage(add_player);
			}
		} else {
			TEST message = new TEST();
			sendSingleMessage(message);
		}

	}
	public void sendMessage_MOVE_DIRECTION(){
		if(playerDirectionChanged&&playerObject!=null){
			tempVector3.set(camera.direction);
			if(playerForward){
				if(playerLeft){
					tempVector3.rotate(Vector3.Y, 45);
				}else if(playerRight){
					tempVector3.rotate(Vector3.Y, -45);
				}
			}else if(playerBack){
				if(playerLeft){
					tempVector3.rotate(Vector3.Y, 135);
				}else if(playerRight){
					tempVector3.rotate(Vector3.Y, -135);
				}else{
					tempVector3.rotate(Vector3.Y, -180);
				}
			}else if (playerLeft){
				tempVector3.rotate(Vector3.Y, 90);
			}else if(playerRight){
				tempVector3.rotate(Vector3.Y, -90);
			}else{
				tempVector3.setZero();
			}
			tempVector3.y = 0;
			tempVector3.nor();
			
			message_move_direction.setId(playerObject.getId());
			message_move_direction.setX(tempVector3.x);
			message_move_direction.setY(tempVector3.y);
			message_move_direction.setZ(tempVector3.z);

			if (isLogin&&playerId!=0&&playerObject!=null) {
				sendSingleMessage(message_move_direction,false);
			} else {
				TEST message = new TEST();
				sendSingleMessage(message);
			}
			
			playerDirectionChanged=false;
		}
	}
	
	void sendSingleMessage(Message message) {
		sendSingleMessage(message,false);
	}
	void sendSingleMessage(Message message,boolean isImmediately) {
		clientSocket.send(SINGLE_MESSAGE.get(message.get().array()).array(), isImmediately);
	}
	void sendSingleMessage(byte[] data) {
		sendSingleMessage(data,false);
	}
	void sendSingleMessage(byte[] data,boolean isImmediately) {
		clientSocket.send(SINGLE_MESSAGE.get(data).array(), isImmediately);
	}
	void setupActorInput() {
		stage.getRoot().findActor("A").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				deviceInputHandler.deviceInputListener.aJustUppedAction();
			}
	
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				deviceInputHandler.deviceInputListener.aJustPressedAction();
				return true;
			}
		});
		stage.getRoot().findActor("D").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				deviceInputHandler.deviceInputListener.dJustUppedAction();
			}
	
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				deviceInputHandler.deviceInputListener.dJustPressedAction();
				return true;
			}
		});
		stage.getRoot().findActor("ADD").addListener(new ActorInputListenner() {
	
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				deviceInputHandler.deviceInputListener.zJustUppedAction();
			}
		});
		stage.getRoot().findActor("X").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				deviceInputHandler.deviceInputListener.delJustPressedAction();
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
				System.out.println("uiuuS");
				deviceInputHandler.deviceInputListener.wJustPressedAction();
			}
		});
	
		stage.getRoot().findActor("S").addListener(new ActorInputListenner() {
	
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				deviceInputHandler.deviceInputListener.sJustPressedAction();
			}
		});
	
		stage.getRoot().findActor("login").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			}
		});
	
		stage.getRoot().findActor("doAction").addListener(new ActorInputListenner() {
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
	
				message_do_action
						.setActionId(Long.parseLong(((TextArea) stage.getRoot().findActor("actionId")).getText()));
				sendSingleMessage(message_do_action);
	
			}
	
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
	
				return true;
			}
		});
	}

	void initMessageHandle() {
		messageHandlerMap.put(EntityMessageType.S2C_ADD_PLAYER.ordinal(), new MessageHandler() {
			S2C_ADD_PLAYER message = new S2C_ADD_PLAYER();

			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);
				hideGameOver();
				System.out.println("recv addplayer");
				if (physicsWorld.getPhysicsObjects().get(message.getObjectId()) == null) {
					RenderableBtObject btObject = physicsWorldBuilder.createDefaultRenderableBall(19, 10, random.nextInt(30));
					ColorAttribute ca = (ColorAttribute) (((RenderableBtObject) btObject).getInstance().nodes
							.get(0).parts.get(0).material.get(ColorAttribute.Diffuse));
					ca.color.set(0.9f, 0.2f, 0.1f, 1);
					btObject.setId(message.getObjectId());
					btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
							new GameObjectTypeAttribute(GameObjectType.PLAYER.ordinal()));
					btObject.getAttributes().put(AttributeType.OWNER_PLAYER_ID.ordinal(),
							new OwnerPlayerId(message.getId()));
					btObject.getAttributes().put(AttributeType.MOVE_SPEED.ordinal(),new MoveSpeed(20));
					
					btObject.getRigidBody().setContactCallbackFilter(1 << GameObjectType.GROUND.ordinal());
					// System.out.println(1<<GameObjectType.GROUND.ordinal());
					physicsWorld.addPhysicsObject(btObject);
					if (message.getId() == playerId) {
						playerObject = btObject;
					}
				}
			}
		});
		messageHandlerMap.put(EntityMessageType.ADD_BTOBJECT.ordinal(), new MessageHandler() {

			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub

			}
		});

		messageHandlerMap.put(EntityMessageType.S2C_ADD_OBSTACLE.ordinal(), new MessageHandler() {
			S2C_ADD_OBSTACLE message = new S2C_ADD_OBSTACLE();

			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub

				message.set(src);
				if (physicsWorld.getPhysicsObjects().get(message.getId()) == null) {
					createObstacleQueue.add(message);
				}

			}
		});

		messageHandlerMap.put(EntityMessageType.APPLY_FORCE.ordinal(), new MessageHandler() {
			APPLY_FORCE message = new APPLY_FORCE();

			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				message.set(src);

			}
		});

		messageHandlerMap.put(EntityMessageType.REMOVE_BTOBJECT.ordinal(), new MessageHandler() {
			REMOVE_BTOBJECT message = new REMOVE_BTOBJECT();

			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub

				message.set(src);
				BtObject btObject = (BtObject) physicsWorld.getPhysicsObjects().get(message.getId());
				if (btObject != null) {

					OwnerPlayerId ownerPlayerId = (OwnerPlayerId) btObject.getAttributes()
							.get(AttributeType.OWNER_PLAYER_ID.ordinal());
					if (ownerPlayerId != null && ownerPlayerId.getPlayerId() == playerId) {
						// System.out.println("remove myself");
						showGameOver();
						sound.play(1, 0.5f, 0);

						playerId = 0;
						playerObject = null;
					}
					System.out.println("remove position:" + btObject.getPosition(tempMatrix4));
					physicsWorld.removePhysicsObject(physicsWorld.getPhysicsObjects().get(message.getId()));
				}
			}
		});

		messageHandlerMap.put(EntityMessageType.UPDATE_BTOBJECT_MOTIONSTATE.ordinal(), new MessageHandler() {
			UPDATE_BTOBJECT_MOTIONSTATE message = new UPDATE_BTOBJECT_MOTIONSTATE();
			C2S_ENQUIRE_BTOBJECT c2s_ENQUIRE_BTOBJECT_message = new C2S_ENQUIRE_BTOBJECT();

			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				//System.out.println("UPDATE_BTOBJECT_MOTIONSTATE");
				message.set(src);
				BtObject btObject = (BtObject) physicsWorld.getPhysicsObjects().get(message.getId());
				if (btObject == null) {
					c2s_ENQUIRE_BTOBJECT_message.setId(message.getId());
					sendSingleMessage(c2s_ENQUIRE_BTOBJECT_message);
				} else {
					if (!btObject.getRigidBody().isActive()) {
						btObject.getRigidBody().activate();
					}
					if (playerObject != null) {
						if (message.getId() == playerObject.getId()) {
							// System.out.println(message.getLinearVelocityX());
						}
					}
					// System.out.println("mmm:"+message.getLinearVelocityZ());
					updatePhysicsObject(btObject, message);
					// System.out.println("nnn:"+btObject.getRigidBody().getLinearVelocity().z);
				}
			}
		});

		messageHandlerMap.put(EntityMessageType.TEST.ordinal(), new MessageHandler() {
			@Override
			public void handle(ByteBuf src) {
				// TODO Auto-generated method stub
				isLogin = true;
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

	void showGameOver() {
		Label console = stage.getRoot().findActor("console");
		console.setPosition(320, 230);
		console.setText("Game Over");
	}

	void hideGameOver() {
		Label console = stage.getRoot().findActor("console");
		console.setPosition(-300, 200);
	}

	@Override
	public void removeSession(long sessionId) {
		// TODO Auto-generated method stub

	}

	public InputDeviceListener createDeviceInputListener() {
		return new InputDeviceListener() {

			@Override
			public void zJustUppedAction() {
				// TODO Auto-generated method stub
				System.out.println("zup");
				//createVolleyballCourt();
				sendMessage_ADD_PLAYER();
			}

			@Override
			public void zJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void yJustUppedAction() {
				// TODO Auto-generated method stub
				RenderableBtObject rb = physicsWorldBuilder.btObjectFactory.createRenderableCube(1f, 1f,
						new Vector3(1, 1, 1), new Color(1, 55, 55, 55));
				rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
						new GameObjectTypeAttribute(GameObjectType.GROUND.ordinal()));

				physicsWorld.addPhysicsObject(rb);
			}

			@Override
			public void yJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void xJustUppedAction() {
				// TODO Auto-generated method stub
				Vector3 tmpV3 = new Vector3();
				Color tmpCor = new Color(55, 55, 55, 1);
				// for (int y = 1; y < 100; y++) {
				for (int z = -20; z < 20; z++) {
					for (int x = -20; x < 20; x++) {
						tmpV3.set(x, 0, z);
						tmpCor.set(random.nextInt(255) / 255f, random.nextInt(255) / 255f, random.nextInt(255) / 255f,
								1);
						// System.out.println(tmpCor.toString());
						RenderableBtObject rb = physicsWorldBuilder.btObjectFactory.createRenderableCube(1f, 0f, tmpV3,
								tmpCor);
						// BtObject rb=
						// physicsWorldBuilder.btObjectFactory.createCube(1f,0f,tmpV3);
						rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
								new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
						rb.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
						physicsWorld.addPhysicsObject(rb);
					}
				}
				// }
			}

			@Override
			public void xJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void wJustUppedAction() {
				playerForward=false;
				if(!Gdx.input.isKeyPressed(Keys.S)){
					playerDirectionChanged=true;
				}
			}

			@Override
			public void wJustPressedAction() {
				playerForward=true;
				playerBack=false;
				playerDirectionChanged=true;

			}

			@Override
			public void vJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void vJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void uJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void uJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void tJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void tJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void spaceJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void spaceJustPressedAction() {
				// TODO Auto-generated method stub
				if(playerId!=0&&playerObject!=null){
					message_do_action.setActionId(VollyBallAction.PLAYER_JUMP.ordinal());
					message_do_action.setPlayerId(playerId);
					sendSingleMessage(message_do_action, true);
				}
			}

			@Override
			public void sJustUppedAction() {
				playerBack=false;
				if(!Gdx.input.isKeyPressed(Keys.W)){
					playerDirectionChanged=true;

				}
			}

			@Override
			public void sJustPressedAction() {
				playerBack=true;
				playerForward=false;
				playerDirectionChanged=true;


			}

			@Override
			public void rJustUppedAction() {
				// TODO Auto-generated method stub
				createCubes();
			}

			@Override
			public void rJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void qJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void qJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void pJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void pJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void oJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void oJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void nJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void nJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseRightJustUppedAction() {
				// TODO Auto-generated method stub
				PhysicsObject obj = getObject(Gdx.input.getX(), Gdx.input.getY());
				if (obj != null)
					makeExplosion(obj.getPosition(tempMatrix4));

			}

			@Override
			public void mouseRightJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseMiddleJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseMiddleJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseLeftJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseLeftJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void mJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void mJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void lJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void lJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void kJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void kJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void jJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void jJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void iJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void iJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void hJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void hJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void gJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void gJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void fJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void fJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void eJustUppedAction() {
				// TODO Auto-generated method stub
			}

			@Override
			public void eJustPressedAction() {
				// TODO Auto-generated method stub

				if(playerId!=0&&playerObject!=null){
					message_do_action.setActionId(VollyBallAction.PLAYER_MAKE_EXPLOSION.ordinal());
					message_do_action.setPlayerId(playerId);
					sendSingleMessage(message_do_action, true);
				}
			}

			@Override
			public void delJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void delJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void dJustUppedAction() {
				playerRight=false;
				if(!Gdx.input.isKeyPressed(Keys.A)){
					playerDirectionChanged=true;

				}
			}

			@Override
			public void dJustPressedAction() {
				playerRight=true;
				playerLeft=false;
				playerDirectionChanged=true;
			}

			@Override
			public void cJustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void cJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void bJustUppedAction() {
				// TODO Auto-generated method stub
				long id = random.nextLong();
				RenderableBtObject btObject = physicsWorldBuilder.createDefaultRenderableBall(0, 10, 0);
				ColorAttribute ca = (ColorAttribute) (((RenderableBtObject) btObject).getInstance().nodes.get(0).parts
						.get(0).material.get(ColorAttribute.Diffuse));
				ca.color.set(0.9f, 0.2f, 0.1f, 1);
				btObject.setId(id);
				btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
						new GameObjectTypeAttribute(GameObjectType.PLAYER.ordinal()));
				btObject.getAttributes().put(AttributeType.OWNER_PLAYER_ID.ordinal(), new OwnerPlayerId(id));
				btObject.getRigidBody().setContactCallbackFilter(1 << GameObjectType.GROUND.ordinal());
				// System.out.println(1<<GameObjectType.GROUND.ordinal());
				physicsWorld.addPhysicsObject(btObject);
				playerPrePosition.set(btObject.getPosition(tempMatrix4));
				playerId = id;
				playerObject = btObject;

				camera.position.set(playerObject.getPosition(tempMatrix4).x, playerObject.getPosition(tempMatrix4).y,
						playerObject.getPosition(tempMatrix4).z);
				// camera.lookAt(playerObject.getPosition(tempMatrix4).x,playerObject.getPosition(tempMatrix4).y,playerObject.getPosition(tempMatrix4).z);

			}

			@Override
			public void bJustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void aJustUppedAction() {
				playerLeft=false;
				if(!Gdx.input.isKeyPressed(Keys.D)){
					playerDirectionChanged=true;

				}
			}

			@Override
			public void aJustPressedAction() {
				playerLeft=true;
				playerRight=false;
				playerDirectionChanged=true;

			}

			@Override
			public void Num9JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num9JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num8JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num8JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num7JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num7JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num6JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num6JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num5JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num5JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num4JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num4JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num3JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num3JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num2JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num2JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num1JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num1JustPressedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num0JustUppedAction() {
				// TODO Auto-generated method stub

			}

			@Override
			public void Num0JustPressedAction() {
				// TODO Auto-generated method stub

			}
		};
	}

}
