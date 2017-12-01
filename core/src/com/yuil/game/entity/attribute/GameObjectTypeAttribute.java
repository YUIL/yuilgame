package com.yuil.game.entity.attribute;

import com.yuil.game.entity.physics.BtObject;

public class GameObjectTypeAttribute implements Attribute {
	public final Integer type=AttributeType.GMAE_OBJECT_TYPE.ordinal();
	public int gameObjectType;
	
	
	public GameObjectTypeAttribute(int gameObjectType) {
		super();
		this.gameObjectType=gameObjectType;
	}
	
	
	public int getGameObjectType() {
		return gameObjectType;
	}


	public void setGameObjectType(int gameObjectType) {
		this.gameObjectType = gameObjectType;
	}


	@Override
	public Integer getType() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static int getGameObjectType(BtObject btObject){
		GameObjectTypeAttribute gameObjectType = (GameObjectTypeAttribute) (btObject.getAttributes()
				.get(AttributeType.GMAE_OBJECT_TYPE.ordinal()));
		if (gameObjectType!=null){
			return gameObjectType.getGameObjectType();
		}else{
			return -1;
		}
	}
}
