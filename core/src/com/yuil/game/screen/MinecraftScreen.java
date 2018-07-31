package com.yuil.game.screen;

import com.yuil.game.MyGame;
import com.yuil.game.net.MessageListener;
import com.yuil.game.net.Session;

import io.netty.buffer.ByteBuf;

public class MinecraftScreen extends Screen2D implements MessageListener {

	public MinecraftScreen(MyGame game) {
		super(game);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void recvMessage(Session session, ByteBuf buf) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSession(long sessionId) {
		// TODO Auto-generated method stub

	}

}
