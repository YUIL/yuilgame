package com.yuil.game.server;

import com.yuil.game.net.MessageListener;
import com.yuil.game.net.NetSocket;
import com.yuil.game.net.Session;

import io.netty.buffer.ByteBuf;

public class MinecraftServer implements MessageListener {

	@Override
	public void recvMessage(Session session, ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendFailure(NetSocket netSocket, long sessionId) {
		// TODO Auto-generated method stub
		
	}

}
