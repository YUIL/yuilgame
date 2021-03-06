package com.yuil.game.server;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionJNI;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btPersistentManifold;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.yuil.game.MyGame;
import com.yuil.game.entity.attribute.Attribute;
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
import com.yuil.game.entity.physics.Behavior;
import com.yuil.game.entity.physics.BtObjectFactory;
import com.yuil.game.entity.physics.BtObjectSpawner;
import com.yuil.game.entity.physics.BtWorld;
import com.yuil.game.entity.physics.PhysicsWorldBuilder;
import com.yuil.game.entity.physics.RenderableBtObject;
import com.yuil.game.entity.volleyball.VolleyballCourt;
import com.yuil.game.input.InputDeviceControler;
import com.yuil.game.input.InputDeviceListener;
import com.yuil.game.net.MessageListener;
import com.yuil.game.net.NetSocket;
import com.yuil.game.net.Session;
import com.yuil.game.net.message.MULTI_MESSAGE;
import com.yuil.game.net.message.Message;
import com.yuil.game.net.message.MessageHandler;
import com.yuil.game.net.message.MessageType;
import com.yuil.game.net.message.MessageUtil;
import com.yuil.game.net.message.SINGLE_MESSAGE;
import com.yuil.game.net.udp.UdpSocket;
import com.yuil.game.util.Log;

import io.netty.buffer.ByteBuf;

public class VolleyballServer implements MessageListener {
	final float NO_CHANGE = 1008611;
	NetSocket netSocket;
	int port;
	BroadCastor broadCastor;
	BtWorld physicsWorld;
	PhysicsWorldBuilder physicsWorldBuilder = new PhysicsWorldBuilder(false);
	volatile Thread gameWorldThread;

	long recentPlayerObjectId = 0;

	Random random = new Random(System.currentTimeMillis());
	
	Map<Long,Player> playerMap=new HashMap<Long,Player>();
	Map<Long,MultiplayRoom> multiplayRoomMap=new HashMap<Long,MultiplayRoom>();
	
	public static Queue<Long> removeSessionQueue = new ConcurrentLinkedDeque<Long>();

	List<BtObject> obstacleBtObjectList = new LinkedList<BtObject>();

	Vector3 tempVector3 = new Vector3(0, 0, -40);
	Matrix4 tempMatrix4 = new Matrix4();

	Map<Integer, MessageHandler> messageHandlerMap = new HashMap<Integer, MessageHandler>();
	MessageProcessor messageProcessor;
	ExecutorService threadPool = Executors.newSingleThreadExecutor();

	ContactListener contactListener;
	REMOVE_BTOBJECT remove_BTOBJECT_message = new REMOVE_BTOBJECT();
	UPDATE_BTOBJECT_MOTIONSTATE update_BTRIGIDBODY = new UPDATE_BTOBJECT_MOTIONSTATE();
	UPDATE_BTOBJECT_MOTIONSTATE[] update_BTRIGIDBODY_array=new UPDATE_BTOBJECT_MOTIONSTATE[100];
	MULTI_MESSAGE multi_message=new MULTI_MESSAGE();
	
	public static Queue<BtObject> updateBtObjectMotionStateBroadCastQueue = new ConcurrentLinkedDeque<BtObject>(){
		 public boolean add(BtObject e) {
			 	if(e.transformChanged){
			 		return false;
			 	}else{
			 		e.setTransformChanged(true);
			 		return offerLast(e);
			 	}
		    }
	};
	public static Queue<BtObject> removeBtObjectQueue = new ConcurrentLinkedDeque<BtObject>();
	BtObjectSpawner obstacleBallSpawner;

	Map<Long,VolleyballCourt> volleyballCourtMap=new HashMap<Long, VolleyballCourt>();
	public static Queue<VolleyballCourt> readyVolleyballCourtQueue = new ConcurrentLinkedDeque<VolleyballCourt>();

	
	class WordChunk{
		int n=10;
		BtObject objects[][][];
		public WordChunk() {
			super();
			objects=new BtObject[n][n][n];
			// TODO Auto-generated constructor stub
		}
		
	}
	/**
	 * @author yuil
	 *
	 */
	public class MyContactListener extends ContactListener {
		Vector3 v3 = new Vector3();
		Vector3 v3_2 = new Vector3();

		REMOVE_BTOBJECT remove_BTOBJECT_message = new REMOVE_BTOBJECT();

		void explosionApplyForce(BtObject explisive,BtObject other){
			GameObjectTypeAttribute otherType = (GameObjectTypeAttribute) (other.getAttributes()
					.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
			if(otherType.getGameObjectType()==GameObjectType.OBSTACLE.ordinal()){
				other.getRigidBody().getWorldTransform().getTranslation(v3);
				v3.sub(explisive.getRigidBody().getWorldTransform().getTranslation(v3_2));
				v3.nor();
				v3.scl(((ExplosionStrength)(explisive.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()))).getStrength());
				other.getRigidBody().applyImpulse(v3, other.getRigidBody().getWorldTransform().getTranslation(v3_2));
				updateBtObjectMotionStateBroadCastQueue.add(other);
			}
		}
		void playerCollideDamage(BtObject player,BtObject other){
				HealthPoint healthPoint = ((HealthPoint) (player.getAttributes()
						.get(AttributeType.HEALTH_POINT.ordinal())));
				int demage = (int) Math
						.floor((other.getRigidBody().getCollisionShape().getLocalScaling().x * 10));
				healthPoint.setHealthPoint(healthPoint.getHealthPoint() - demage);
				//System.out.println("剩余生命：" + healthPoint.getHealthPoint());

				if (healthPoint.getHealthPoint() <= 0) {
					removeBtObjectQueue.add(player);
				}
				removeBtObjectQueue.add(other);
		}
		

		void handleBtObject(BtObject btObject,GameObjectTypeAttribute gameObjectType) {
			if (gameObjectType!= null&&gameObjectType.getType()!=GameObjectType.PLAYER.ordinal()) {
				updateBtObjectMotionStateBroadCastQueue.add(btObject);
			}
		}
		@Override
		public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
			System.out.println("onContactStarted");
			if (colObj0 instanceof btRigidBody && colObj1 instanceof btRigidBody) {
				BtObject btObject0 = (BtObject) (((btRigidBody) colObj0).userData);
				BtObject btObject1 = (BtObject) (((btRigidBody) colObj1).userData);
				GameObjectTypeAttribute gameObjectType0 = (GameObjectTypeAttribute) (btObject0.getAttributes()
						.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
				GameObjectTypeAttribute gameObjectType1 = (GameObjectTypeAttribute) (btObject1.getAttributes()
						.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
				
				if (gameObjectType0 != null && gameObjectType1 != null) {
					if (gameObjectType0.getGameObjectType() == GameObjectType.PLAYER.ordinal()
							&& gameObjectType1.getGameObjectType() == GameObjectType.OBSTACLE.ordinal()) {
						playerCollideDamage(btObject0, btObject1);
					} else if (gameObjectType0.getGameObjectType() == GameObjectType.OBSTACLE.ordinal()
							&& gameObjectType1.getGameObjectType() == GameObjectType.PLAYER.ordinal()) {
						playerCollideDamage(btObject1, btObject0);
					}
				}
				if (gameObjectType0.getGameObjectType() == GameObjectType.GROUND.ordinal()
						|| gameObjectType1.getGameObjectType() == GameObjectType.GROUND.ordinal()) {
				} else {
					if (btObject0.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()) != null) {
						explosionApplyForce(btObject0, btObject1);
					} else if (btObject1.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()) != null) {
						explosionApplyForce(btObject1, btObject0);
					}

					handleBtObject(btObject0,gameObjectType0);
					handleBtObject(btObject1,gameObjectType1);
				}
			}

		}


		public void onContactProcessed(btCollisionObject colObj0, btCollisionObject colObj1) {
/*
			if (colObj0 instanceof btRigidBody && colObj1 instanceof btRigidBody) {
				BtObject btObject0 = (BtObject) (((btRigidBody) colObj0).userData);
				BtObject btObject1 = (BtObject) (((btRigidBody) colObj1).userData);
				
				if (btObject0.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()) != null) {
					//explosionApplyForce(btObject0, btObject1);

				} else if (btObject1.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()) != null) {
					explosionApplyForce(btObject1, btObject0);
				}
			}*/
		}

		public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {

			colObj0.setIgnoreCollisionCheck(colObj1, false);
			colObj1.setIgnoreCollisionCheck(colObj0, false);
		}

		/**
		 * @param bullet
		 * @param btObject
		 * @return 是否删除
		 *//*
			 * private boolean beAttack(Bullet bullet ,BtObject btObject){
			 * AliveObject aliveObject=(AliveObject)btObject.userData;
			 * if(aliveObject.getH()>bullet.getAttack()){
			 * aliveObject.setH(aliveObject.getH()-bullet.getAttack());
			 * btObjectBroadCastQueue.add(btObject); return false; }else{
			 * REMOVE_BTOBJECT_message.setId(btObject.getId());
			 * broadCastor.broadCast_SINGLE_MESSAGE(REMOVE_BTOBJECT_message.get(
			 * ),false); removeQueue.add(btObject); return true; } }
			 */
	}

	public static void main(String[] args) {
		VolleyballServer btTestServer = new VolleyballServer();
		btTestServer.start();
	}

	public VolleyballServer() {
		GdxNativesLoader.load();
		Bullet.init();
		initConfig();
		
		for (int i = 0; i < update_BTRIGIDBODY_array.length; i++) {
			update_BTRIGIDBODY_array[i]=new UPDATE_BTOBJECT_MOTIONSTATE();
		}
		
		
		physicsWorld = new BtWorld();
		physicsWorld.addPhysicsObject(physicsWorldBuilder.createDefaultGround());

		
		contactListener = new MyContactListener();

		try {
			netSocket = new UdpSocket(port);
		} catch (BindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		netSocket.setMessageListener(this);
		broadCastor = new BroadCastor(netSocket);
		messageProcessor = new MessageProcessor();
		
		gameWorldThread = new Thread(new WorldLogic());
		
	}

	public void start() {
		Log.println("start");
		gameWorldThread.start();
		netSocket.start();
	}

	class WorldLogic implements Runnable {

		int interval = 17;// 更新间隔
		long nextUpdateTime = 0;// 下次更新的时间
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			nextUpdateTime = System.currentTimeMillis();
			while (true) {
				if (System.currentTimeMillis() >= nextUpdateTime) {// 更新世界
									
					while(!readyVolleyballCourtQueue.isEmpty()){
						VolleyballCourt volleyballCourt=readyVolleyballCourtQueue.poll();
						startGame(volleyballCourt);
					}
					
					while(!removeSessionQueue.isEmpty()){
						Player player=playerMap.get(removeSessionQueue.poll());
						if (player!=null){
							physicsWorld.removePhysicsObject(physicsWorld.getPhysicsObjects().get(player.getBtObjectId()));
							playerMap.remove(player.getSessionId());

							remove_BTOBJECT_message.setId(player.getBtObjectId());
							broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);
						}
					}
					
				//	obstacleBallSpawner.update();// 刷障碍物体
					while (!removeBtObjectQueue.isEmpty()) {
						BtObject btObject = removeBtObjectQueue.poll();
						if (btObject.getAttributes().get(AttributeType.PLAYER.ordinal()) != null) {
							System.out.println("remove a player!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						}
						physicsWorld.removePhysicsObject(btObject);
						remove_BTOBJECT_message.setId(btObject.getId());
						broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);

					}
					for (BtObject btObject : physicsWorld.getPhysicsObjects().values()) {
						// System.out.println(btObject.rigidBody.getWorldTransform());
						
						if (btObject.getAttributes().get(AttributeType.PLAYER.ordinal()) != null) {
							updateBtObjectMotionStateBroadCastQueue.add(btObject);
						}
						if (btObject.getAttributes().get(AttributeType.EXPLOSION_STRENGTH.ordinal()) != null) {
							physicsWorld.removePhysicsObject(btObject);
							remove_BTOBJECT_message.setId(btObject.getId());
							//System.out.println("remove:"+btObject.getId());

							broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);
						}
						if (!btObject.getRigidBody().isActive()) {
							btObject.getRigidBody().activate();
						}
						btObject.getRigidBody().getWorldTransform().getTranslation(tempVector3);
						if (tempVector3.y < -80) {// 所有物体的死亡高度判断

							if (btObject.getAttributes().get(AttributeType.PLAYER.ordinal()) != null) {
								System.out.println("remove a player!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							}
							physicsWorld.removePhysicsObject(btObject);
							remove_BTOBJECT_message.setId(btObject.getId());
							broadCastor.broadCast_SINGLE_MESSAGE(remove_BTOBJECT_message, false);
						} else if (((GameObjectTypeAttribute) (btObject.getAttributes()
								.get(AttributeType.GMAE_OBJECT_TYPE.ordinal())))
										.getGameObjectType() == GameObjectType.OBSTACLE.ordinal()) {
							// 检查障碍物位置,超过边界则删除
							if (tempVector3.z > 200) {
								physicsWorld.removePhysicsObject(btObject);
							}
						} else if (btObject.getAttributes().get(AttributeType.OWNER_PLAYER_ID.ordinal()) != null) {
						}
					} 
					
					nextUpdateTime += interval;
					physicsWorld.update(interval / 1000f);// 更新物理世界

					// 向连接的客户端发送btObjectMotionstate同步消息
					broadCastMotionState();
				} else {
					try {
						Thread.sleep(nextUpdateTime - System.currentTimeMillis());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		public void broadCastMotionState(){
			if (updateBtObjectMotionStateBroadCastQueue.size()>1){
				int length=updateBtObjectMotionStateBroadCastQueue.size()>update_BTRIGIDBODY_array.length?update_BTRIGIDBODY_array.length:updateBtObjectMotionStateBroadCastQueue.size();
				
				for (int i = 0; i < length; i++) {
					
					BtObject btObject = updateBtObjectMotionStateBroadCastQueue.poll();
					if (btObject.transformChanged&&btObject.getRigidBody() != null) {

						btObject.setTransformChanged(false);
						update_BTRIGIDBODY_array[i].set(btObject);
						 //System.out.println("btId:"+update_BTRIGIDBODY_array[i].getId());

					}
				}
				multi_message.set(length,update_BTRIGIDBODY_array);
				multi_message.messageNum=(byte)length;
				broadCastor.broadCast_MESSAGE_ARRAY(multi_message, false);

			}else{
				for (int i = 0; i < updateBtObjectMotionStateBroadCastQueue.size(); i++) {
					BtObject btObject = updateBtObjectMotionStateBroadCastQueue.poll();
					if (btObject.transformChanged&&btObject.getRigidBody() != null) {
						 //System.out.println("btId:"+btObject.getId());
						// System.out.println("send up
						// velocity:"+btObject.getRigidBody().getLinearVelocity());
						// System.out.println("update");
						btObject.setTransformChanged(false);
						update_BTRIGIDBODY.set(btObject);
						// System.out.println(update_BTRIGIDBODY.toString());
						broadCastor.broadCast_SINGLE_MESSAGE(update_BTRIGIDBODY, false);
					}
				}
			}
		}
		
		
		
	}

	class MessageProcessor extends com.yuil.game.net.MessageProcessor {
		public MessageProcessor() {
			messageHandlerMap.put(EntityMessageType.C2S_ADD_PLAYER.ordinal(), new MessageHandler() {
				C2S_ADD_PLAYER message = new C2S_ADD_PLAYER();
				S2C_ADD_PLAYER s2c_ADD_PLAYER_message = new S2C_ADD_PLAYER();
				Vector3 temV3=new Vector3();
				Matrix4 tempMatrix4=new Matrix4();
				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					// TODO Auto-generated method stub
					long objectId = random.nextLong();
					//System.out.println("server, playerObjectId:"+objectId);
					
					
					temV3.set(0, 20, 0);
					BtObject btObject = physicsWorldBuilder.createDefaultBall(temV3.x,temV3.y,temV3.z);
					btObject.setBehavior(new Behavior(btObject) {
						Matrix4 tempMatrix4=new Matrix4();
						@Override
						public void update(float delta) {
							// TODO Auto-generated method stub
							behaviorObject.getRigidBody().getWorldTransform(this.tempMatrix4);
							tempMatrix4.rotate(Vector3.Y, 6);
							behaviorObject.getRigidBody().setWorldTransform(tempMatrix4);
						}
					});
					btObject.setId(objectId);
					btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
							new GameObjectTypeAttribute(GameObjectType.PLAYER.ordinal()));
					btObject.getAttributes().put(AttributeType.PLAYER.ordinal(),
							new com.yuil.game.entity.attribute.Player(message.getId()));
					btObject.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(1000));
					btObject.getAttributes().put(AttributeType.MOVE_SPEED.ordinal(),new MoveSpeed(15));
					//btObject.getRigidBody().setCollisionFlags(1<<GameObjectType.PLAYER.ordinal());
				//	btObject.getRigidBody().setContactCallbackFilter((1<<GameObjectType.GROUND.ordinal())|(1<<GameObjectType.OBSTACLE.ordinal()));
					// System.out.println("asd:"+((1<<GameObjectType.GROUND.ordinal())|(1<<GameObjectType.OBSTACLE.ordinal())));
					btObject.setGroup((short)4);
					btObject.setMask((short)(Short.MAX_VALUE^4));
					
					physicsWorld.addPhysicsObject(btObject);
					
					playerMap.put(session.getId(), new Player(message.getId(), objectId, session.getId()));
				

					//tempVector3.set(btObject.getRigidBody().getLinearVelocity().x,btObject.getRigidBody().getLinearVelocity().y, -5);
					// btObject.getRigidBody().setLinearVelocity(tempVector3);
					s2c_ADD_PLAYER_message.setId(message.getId());
					s2c_ADD_PLAYER_message.setObjectId(objectId);
					s2c_ADD_PLAYER_message.setX(temV3.x);
					s2c_ADD_PLAYER_message.setY(temV3.y);
					s2c_ADD_PLAYER_message.setZ(temV3.z);
					broadCastor.broadCast_SINGLE_MESSAGE(s2c_ADD_PLAYER_message, false);
					//updateBtObjectMotionStateBroadCastQueue.add(btObject);
					recentPlayerObjectId = objectId;
					
					
					// BtTestServer2.updateBtObjectMotionStateBroadCastQueue.add(btObject);
				}
			});
			messageHandlerMap.put(EntityMessageType.ADD_BTOBJECT.ordinal(), new MessageHandler() {
				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub

				}
			});

			messageHandlerMap.put(EntityMessageType.C2S_UPDATE_BTOBJECT_MOTIONSTATE.ordinal(), new MessageHandler() {
				C2S_UPDATE_BTOBJECT_MOTIONSTATE message = new C2S_UPDATE_BTOBJECT_MOTIONSTATE();
				UPDATE_BTOBJECT_MOTIONSTATE update_BTOBJECT_MOTIONSTATE_message = new UPDATE_BTOBJECT_MOTIONSTATE();

				@Override
				public void handle(ByteBuf src) {
					// TODO Auto-generated method stub
					message.set(src);
					BtObject btObject = physicsWorld.getPhysicsObjects().get(message.getId());
					if (btObject != null) {
						System.out.println("uuuuu");
						update_BTOBJECT_MOTIONSTATE_message.set(btObject);
						netSocket.send(SINGLE_MESSAGE.get(update_BTOBJECT_MOTIONSTATE_message.get().array()), session,
								false);
					}
				}
			});
			messageHandlerMap.put(EntityMessageType.UPDATE_LINEAR_VELOCITY.ordinal(), new MessageHandler() {
				UPDATE_LINEAR_VELOCITY message = new UPDATE_LINEAR_VELOCITY();
				Vector3 v3 = new Vector3();

				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					BtObject btObject = physicsWorld.getPhysicsObjects().get(message.getId());

					if (btObject != null) {

						v3.set(btObject.getRigidBody().getLinearVelocity());
						if (message.getX() != NO_CHANGE) {
							v3.x = message.getX();
						}
						if (message.getY() != NO_CHANGE) {
							v3.y = message.getY();
						}
						if (message.getZ() != NO_CHANGE) {
							v3.z = message.getZ();
						}
						if (!btObject.getRigidBody().isActive()) {
							btObject.getRigidBody().activate();
						}

						btObject.getRigidBody().setLinearVelocity(v3);
						VolleyballServer.updateBtObjectMotionStateBroadCastQueue.add(btObject);

					}
				}
			});
			messageHandlerMap.put(EntityMessageType.MOVE_DIRECTION.ordinal(), new MessageHandler() {
				MOVE_DIRECTION message = new MOVE_DIRECTION();
				Vector3 v3 = new Vector3();

				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					BtObject btObject = physicsWorld.getPhysicsObjects().get(message.getId());

					if (btObject != null) {
						
						int moveSpeed=((MoveSpeed)(btObject.getAttributes().get(AttributeType.MOVE_SPEED.ordinal()))).getMoveSpeed();
						v3.set(message.getX(),0,message.getZ());
						v3.nor();
						if(v3.len2()==0){
							btObject.getRigidBody().setFriction(BtObjectFactory.defaultFriction);
						}else{
							btObject.getRigidBody().setFriction(0f);
						}
						v3.scl(moveSpeed);
						v3.y=btObject.getRigidBody().getLinearVelocity().y;
						if (!btObject.getRigidBody().isActive()) {
							btObject.getRigidBody().activate();
						}

						btObject.getRigidBody().setLinearVelocity(v3);
						VolleyballServer.updateBtObjectMotionStateBroadCastQueue.add(btObject);

					}
				}
			});
			messageHandlerMap.put(EntityMessageType.C2S_ENQUIRE_BTOBJECT.ordinal(), new MessageHandler() {
				C2S_ENQUIRE_BTOBJECT message = new C2S_ENQUIRE_BTOBJECT();
				S2C_ADD_PLAYER s2c_ADD_PLAYER_message = new S2C_ADD_PLAYER();

				@Override
				public void handle(ByteBuf src) {
					//System.out.println("C2S_ENQUIRE_BTOBJECT");
					message.set(src);
					BtObject btObject = physicsWorld.getPhysicsObjects().get(message.getId());
					if (btObject != null) {
						Attribute attribute = btObject.getAttributes().get(AttributeType.PLAYER.ordinal());
						if (attribute != null) {
							s2c_ADD_PLAYER_message.setId(((com.yuil.game.entity.attribute.Player) attribute).getPlayerId());
							s2c_ADD_PLAYER_message.setObjectId(message.getId());
							netSocket.send(SINGLE_MESSAGE.get(s2c_ADD_PLAYER_message.get().array()), session, false);
						}else{
							System.out.println("ASdasdasdasljalakjglajsdgljsdlkgjsldkjgslkdjglskdjglkjlk");
						}
					}
				}
			});

			messageHandlerMap.put(EntityMessageType.DO_ACTION.ordinal(), new MessageHandler() {
				DO_ACTION message = new DO_ACTION();
				Vector3 position=new Vector3();
				Vector3 v3=new Vector3();

				@Override
				public void handle(ByteBuf src) {
					message.set(src);
					System.out.println("DoAction:"+message);
					if(message.getActionId()==VollyBallAction.MATCH_GAME.ordinal()){
						System.out.println("MATCH_GAME");
						matchGame(session);
					}else if(message.getActionId()==VollyBallAction.PLAYER_JUMP.ordinal()){
						Player player=playerMap.get(session.getId());
						if(player!=null){
							BtObject btObject=physicsWorld.getPhysicsObjects().get(player.getBtObjectId());
							if(btObject!=null){
								v3.set(Vector3.Y);
								v3.scl(500);
								btObject.getRigidBody().applyForce(v3, btObject.getRigidBody().getWorldTransform().getTranslation(position));
								VolleyballServer.updateBtObjectMotionStateBroadCastQueue.add(btObject);
								System.out.println("jump");

							}
						}
					}else if(message.getActionId()==VollyBallAction.PLAYER_MAKE_EXPLOSION.ordinal()){
						Player player=playerMap.get(session.getId());
						if(player!=null){
							BtObject btObject=physicsWorld.getPhysicsObjects().get(player.getBtObjectId());
							if(btObject!=null){
								btObject.getRigidBody().getWorldTransform().getTranslation(position);
								
								BtObject explosion = physicsWorldBuilder.btObjectFactory.createBall(100, 0, position);
								explosion.getRigidBody().setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
								explosion.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
										new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
								explosion.getAttributes().put(AttributeType.OWNER_PLAYER_ID.ordinal(),new OwnerPlayerId(player.getId()));
								explosion.getAttributes().put(AttributeType.EXPLOSION_STRENGTH.ordinal(), new ExplosionStrength(500));
								//System.out.println("explosion id:"+explosion.getId());
								physicsWorld.addPhysicsObject(explosion);
								
							}
						}
					}else if(message.getActionId()==VollyBallAction.PRINT_PLAYER_INFO.ordinal()){
						Player player=playerMap.get(session.getId());
						if(player!=null){
							BtObject btObject=physicsWorld.getPhysicsObjects().get(player.getBtObjectId());
							if(btObject!=null){
								System.out.println(btObject);
								
							}else{
								System.out.println("btObject not exist!");
							}
						}else{
							System.out.println("player not exist!");
						}
					}
					//System.out.println(message.toString());
					// netSocket.send(SINGLE_MESSAGE.get(message.get().array()),
					// session, false);

				}
			});

			messageHandlerMap.put(EntityMessageType.TEST.ordinal(), new MessageHandler() {
				TEST message = new TEST();

				@Override
				public void handle(ByteBuf src) {
					message.set(src);

					netSocket.send(SINGLE_MESSAGE.get(message.get().array()), session, false);
				}
			});
		}

		@Override
		public void run() {
			ByteBuf data=dataQueue.poll();
			int typeOrdinal = MessageUtil.getType(data.array());
			// System.out.println("type:" +
			// EntityMessageType.values()[typeOrdinal]);
			messageHandlerMap.get(typeOrdinal).handle(data);
		}
	}

	@Override
	public void recvMessage(Session session, ByteBuf data) {

		if (data.array().length < Message.TYPE_LENGTH) {
			return;
		}
		int typeOrdinal = MessageUtil.getType(data.array());
		// System.out.println("type:" + MessageType.values()[typeOrdinal]);

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

	void  disposeSingleMessage(Session session, ByteBuf data1) {
		messageProcessor.setSession(session);
		messageProcessor.getDataQueue().offer(data1);
		threadPool.execute(messageProcessor);
	}

	/*
	 * public void updatePhysicsObject(UPDATE_BTOBJECT_MOTIONSTATE message) { //
	 * TODO Auto-generated method stub
	 * this.updatePhysicsObjectQueue.add(message); }
	 */
	public void updateLinearVelocity(UPDATE_LINEAR_VELOCITY message) {
		// TODO Auto-generated method stub

	}


	void initConfig() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document document = null;
		File file = new File(".//config//config.xml");
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(file);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		NodeList spawners = document.getElementsByTagName("spawner");

		for (int i = 0; i < spawners.getLength(); i++) {
			int interval = Integer.parseInt(
					spawners.item(i).getOwnerDocument().getElementsByTagName("interval").item(0).getTextContent());
			obstacleBallSpawner = createSpawner(interval);
		}
		NodeList portNode = document.getElementsByTagName("port");
		port=Integer.parseInt(portNode.item(0).getTextContent());
	}

	BtObjectSpawner createSpawner(int interval) {
		BtObjectSpawner spawner = new BtObjectSpawner(interval) {
			Vector3 v3 = new Vector3();
			Color color = new Color();
			S2C_ADD_OBSTACLE message = new S2C_ADD_OBSTACLE();

			@Override
			public void spawn() {
				// TODO Auto-generated method stub
				// physicsWorld.addPhysicsObjectQueue.
				 v3.x = -16 + random.nextInt(32);
				//v3.x = 0;
				// v3.y = 222+random.nextInt(40);
				v3.y = 11;
				v3.z = -200;
				float radius = 3;
				// float radius = 0.5f+((random.nextInt(10000) / 10000f) * 3);
				BtObject btObject = physicsWorldBuilder.createBall(radius, radius, v3);
				btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
						new GameObjectTypeAttribute(GameObjectType.OBSTACLE.ordinal()));
				btObject.getAttributes().put(AttributeType.DAMAGE_POINT.ordinal(), new DamagePoint(1));
				color.set(random.nextInt(255) / 255f, random.nextInt(255) / 255f, random.nextInt(255) / 255f, 1);
				btObject.getAttributes().put(AttributeType.COLOR.ordinal(), new com.yuil.game.entity.attribute.Color(color));
				
				//btObject.getRigidBody().setCollisionFlags(1<<GameObjectType.OBSTACLE.ordinal());
				//btObject.getRigidBody().setContactCallbackFilter((1<<GameObjectType.GROUND.ordinal())|(1<<GameObjectType.PLAYER.ordinal()));
				//btObject.getRigidBody().setContactCallbackFilter(1<<GameObjectType.GROUND.ordinal());
				v3.x = 0;
				v3.y = 0;
				v3.z = 80;
				btObject.getRigidBody().setLinearVelocity(v3);

				long id = random.nextLong();
				btObject.setId(id);
				physicsWorld.addPhysicsObject(btObject);

				message.setId(id);
				message.setRadius(radius);
				message.setR(color.r);
				message.setG(color.g);
				message.setB(color.b);
				message.setA(color.a);
				broadCastor.broadCast_SINGLE_MESSAGE(message, false);
				obstacleBtObjectList.add(btObject);
			}

		};
		return spawner;
	}

	@Override
	public void sendFailure(NetSocket netSocket, long sessionId) {
		// TODO Auto-generated method stub
		removeSessionQueue.add(sessionId);
		netSocket.removeSession(sessionId);
	}
	
	
	public synchronized void matchGame(Session session){
		Player player=playerMap.get(session.getId());
		if(player!=null&&player.getRoomId()==0){
			//TODO match game
			int addPlayerResult = 0;
			VolleyballCourt volleyballCourt=null;
			for (Iterator<VolleyballCourt> iterator = volleyballCourtMap.values().iterator(); iterator.hasNext();) {
				volleyballCourt = (VolleyballCourt) iterator.next();
				addPlayerResult=volleyballCourt.addPlayer(player);
				if(addPlayerResult!=0){
					break;
				}
			}
			
			if(addPlayerResult==0){
				VolleyballCourt vc=new VolleyballCourt(random.nextLong());
				createVolleyballCourtInstance(volleyballCourt);
				vc.addPlayer(player);
				player.setRoomId(vc.getId());
				volleyballCourtMap.put(vc.getId(), vc);
			}else if(addPlayerResult==1){
				
			}else if(addPlayerResult==2){
				readyVolleyballCourtQueue.offer(volleyballCourt);
			}
		}
		
	}
	private void createVolleyballCourtInstance(VolleyballCourt volleyballCourt) {

		
		Vector3 position=volleyballCourt.getPosition(); 
		long id=volleyballCourt.getId();
		
		BtObject btObject = null;
		btObject = physicsWorldBuilder.btObjectFactory.createCube(10f, 0f, position);
		btObject.setId(++id);
		btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
		btObject.getAttributes().put(AttributeType.OWNER_PLAYER_ID.ordinal(),new OwnerPlayerId(volleyballCourt.getPlayer1().getId()));
		btObject.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
		physicsWorld.addPhysicsObject(btObject);
		volleyballCourt.setPlayer1_side(btObject);

		position.x -= 2.5f;
		position.y += 5;
		position.z += 7.5f;
		btObject = physicsWorldBuilder.btObjectFactory.createCube(5f, 0f, position);
		btObject.setId(++id);
		btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
		btObject.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
		physicsWorld.addPhysicsObject(btObject);
		position.x += 5;
		btObject = physicsWorldBuilder.btObjectFactory.createCube(5f, 0f, position);
		btObject.setId(++id);
		btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
		btObject.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
		physicsWorld.addPhysicsObject(btObject);

		position.x -= 2.5f;
		position.y -= 5;
		position.z += 7.5f;
		btObject = physicsWorldBuilder.btObjectFactory.createCube(10f, 0f, position);
		btObject.setId(++id);
		btObject.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
				new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
		btObject.getAttributes().put(AttributeType.OWNER_PLAYER_ID.ordinal(),new OwnerPlayerId(volleyballCourt.getPlayer2().getId()));
		btObject.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
		volleyballCourt.setPlayer2_side(btObject);
		physicsWorld.addPhysicsObject(btObject);
		
	}
	private void startGame(VolleyballCourt volleyballCourt) {
		
		
	}
	
	public void createCubes(){

		Vector3 tmpV3 = new Vector3();
		Color tmpCor = new Color(55, 55, 55, 1);
		//for (int y = 1; y < 100; y++) {
			for (int z = -20; z <20; z++) {
				for (int x = -20; x < 20; x++) {
					tmpV3.set(x, 0, z);
					tmpCor.set(random.nextInt(255) / 255f, random.nextInt(255) / 255f,
							random.nextInt(255) / 255f, 1);
					 BtObject rb= physicsWorldBuilder.btObjectFactory.createCube(1f,0f,tmpV3);
					rb.getAttributes().put(AttributeType.GMAE_OBJECT_TYPE.ordinal(),
							new GameObjectTypeAttribute(GameObjectType.PLAYER_S_OBJECT.ordinal()));
					rb.getAttributes().put(AttributeType.HEALTH_POINT.ordinal(), new HealthPoint(10));
					physicsWorld.addPhysicsObject(rb);
				}
			}
	//	}
	}
}
