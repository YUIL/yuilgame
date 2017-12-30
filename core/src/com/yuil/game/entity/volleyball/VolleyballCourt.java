package com.yuil.game.entity.volleyball;

import com.badlogic.gdx.math.Vector3;
import com.yuil.game.server.Player;

public class VolleyballCourt{
	public static final Vector3 previousPosition=new Vector3(0, 0, 0);
	
	long id;
	Player player1=null;
	Player player2=null;
	boolean ready1=false;
	boolean ready2=false;
	Vector3 position=new Vector3();
	
	boolean started=false;
	
	
	public VolleyballCourt(long id) {
		super();
		getNextPosition(this.position);
		this.id = id;
	}

	
	public static final synchronized Vector3 getNextPosition(Vector3 postion){
		previousPosition.x+=100;
		postion.set(previousPosition);
		return postion;
	}
	
	/**
	 * @param player
	 * @return 1或2表示玩家1还是玩家2，返回0表示没有成功添加玩家
	 */
	public int addPlayer(Player player){
		if (started) {
			return 0;
		}else{
			if (player1==null){
				player1=player;
				return 1;
			}else if (player2==null){
				player2=player;
				return 2;
			}else{
				return 0;
			}
		}
	}
	
/*	public void ready(Long playerId){
		if(player1!=null){
			if(player1.getId()==playerId){
				ready1=true;
			}
		}else if(player2!=null){
			if(player2.getId()==playerId){
				ready1=true;
			}
		}
		if (ready1&&ready2){
			start();
		}
	}
	public void start(){
		//readyVolleyballCourtQueue.add(this);
	}
*/
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Player getPlayer1() {
		return player1;
	}

	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}

	public Player getPlayer2() {
		return player2;
	}
	
	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	public boolean isReady1() {
		return ready1;
	}

	public void setReady1(boolean ready1) {
		this.ready1 = ready1;
	}

	public boolean isReady2() {
		return ready2;
	}

	public void setReady2(boolean ready2) {
		this.ready2 = ready2;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}


	public Vector3 getPosition() {
		return position;
	}


	public void setPosition(Vector3 position) {
		this.position = position;
	}
	
	
	
}
