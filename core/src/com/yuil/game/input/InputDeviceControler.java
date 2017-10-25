package com.yuil.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

public class InputDeviceControler {
	public InputDeviceStatus inputDeviceStatus;
	public InputDeviceListener deviceInputListener;
	public InputDeviceControler(InputDeviceStatus inputDeviceStatus,InputDeviceListener deviceInputListener){
		this.inputDeviceStatus=inputDeviceStatus;
		this.deviceInputListener=deviceInputListener;
	}
	public void checkDeviceInput(){
		
		if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			if(!inputDeviceStatus.isMouseLeftJustPressed()){
				inputDeviceStatus.setMouseLeftJustPressed(true);
				deviceInputListener.mouseLeftJustPressedAction();
			}
		}else if(inputDeviceStatus.isMouseLeftJustPressed()){
			inputDeviceStatus.setMouseLeftJustPressed(false);
			deviceInputListener.mouseLeftJustUppedAction();
		}
		
		if(Gdx.input.isButtonPressed(Buttons.RIGHT)){
			if(!inputDeviceStatus.isMouseRightJustPressed()){
				inputDeviceStatus.setMouseRightJustPressed(true);
				deviceInputListener.mouseRightJustPressedAction();
			}
		}else if(inputDeviceStatus.isMouseRightJustPressed()){
			inputDeviceStatus.setMouseRightJustPressed(false);
			deviceInputListener.mouseRightJustUppedAction();
		}
		
		if(Gdx.input.isButtonPressed(Buttons.MIDDLE)){
			if(!inputDeviceStatus.isMouseMiddleJustPressed()){
				inputDeviceStatus.setMouseMiddleJustPressed(true);
				deviceInputListener.mouseMiddleJustPressedAction();
			}
		}else if(inputDeviceStatus.isMouseMiddleJustPressed()){
			inputDeviceStatus.setMouseMiddleJustPressed(false);
			deviceInputListener.mouseMiddleJustUppedAction();
		}
		

		
		if (Gdx.input.isKeyJustPressed(Keys.A)) {
			inputDeviceStatus.setaJustPressed(true);
			deviceInputListener.aJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.A)==false&& inputDeviceStatus.isaJustPressed()) {
			inputDeviceStatus.setaJustPressed(false);
			deviceInputListener.aJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.B)) {
			inputDeviceStatus.setbJustPressed(true);
			deviceInputListener.bJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.B)==false&& inputDeviceStatus.isbJustPressed()) {
			inputDeviceStatus.setbJustPressed(false);
			deviceInputListener.bJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.C)) {
			inputDeviceStatus.setcJustPressed(true);
			deviceInputListener.cJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.C)==false&& inputDeviceStatus.iscJustPressed()) {
			inputDeviceStatus.setcJustPressed(false);
			deviceInputListener.cJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			inputDeviceStatus.setdJustPressed(true);
			deviceInputListener.dJustPressedAction();
		}else if (Gdx.input.isKeyPressed(Keys.D)==false&& inputDeviceStatus.isdJustPressed()) {
			inputDeviceStatus.setdJustPressed(false);
			deviceInputListener.dJustUppedAction();
		
		}
		if (Gdx.input.isKeyJustPressed(Keys.E)) {
			inputDeviceStatus.seteJustPressed(true);
			deviceInputListener.eJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.E)==false&& inputDeviceStatus.iseJustPressed()) {
			inputDeviceStatus.seteJustPressed(false);
			deviceInputListener.eJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.F)) {
			inputDeviceStatus.setfJustPressed(true);
			deviceInputListener.fJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.F)==false&& inputDeviceStatus.isfJustPressed()) {
			inputDeviceStatus.setfJustPressed(false);
			deviceInputListener.fJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.G)) {
			inputDeviceStatus.setgJustPressed(true);
			deviceInputListener.gJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.G)==false&& inputDeviceStatus.isgJustPressed()) {
			inputDeviceStatus.setgJustPressed(false);
			deviceInputListener.gJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.H)) {
			inputDeviceStatus.sethJustPressed(true);
			deviceInputListener.hJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.H)==false&& inputDeviceStatus.ishJustPressed()) {
			inputDeviceStatus.sethJustPressed(false);
			deviceInputListener.hJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.I)) {
			inputDeviceStatus.setiJustPressed(true);
			deviceInputListener.iJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.I)==false&& inputDeviceStatus.isiJustPressed()) {
			inputDeviceStatus.setiJustPressed(false);
			deviceInputListener.iJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.J)) {
			inputDeviceStatus.setjJustPressed(true);
			deviceInputListener.jJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.J)==false&& inputDeviceStatus.isjJustPressed()) {
			inputDeviceStatus.setjJustPressed(false);
			deviceInputListener.jJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.K)) {
			inputDeviceStatus.setkJustPressed(true);
			deviceInputListener.kJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.K)==false&& inputDeviceStatus.iskJustPressed()) {
			inputDeviceStatus.setkJustPressed(false);
			deviceInputListener.kJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.L)) {
			inputDeviceStatus.setlJustPressed(true);
			deviceInputListener.lJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.L)==false&& inputDeviceStatus.islJustPressed()) {
			inputDeviceStatus.setlJustPressed(false);
			deviceInputListener.lJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.M)) {
			inputDeviceStatus.setmJustPressed(true);
			deviceInputListener.mJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.M)==false&& inputDeviceStatus.ismJustPressed()) {
			inputDeviceStatus.setmJustPressed(false);
			deviceInputListener.mJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.N)) {
			inputDeviceStatus.setnJustPressed(true);
			deviceInputListener.nJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.N)==false&& inputDeviceStatus.isnJustPressed()) {
			inputDeviceStatus.setnJustPressed(false);
			deviceInputListener.nJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.O)) {
			inputDeviceStatus.setoJustPressed(true);
			deviceInputListener.oJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.O)==false&& inputDeviceStatus.isoJustPressed()) {
			inputDeviceStatus.setoJustPressed(false);
			deviceInputListener.oJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.P)) {
			inputDeviceStatus.setpJustPressed(true);
			deviceInputListener.pJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.P)==false&& inputDeviceStatus.ispJustPressed()) {
			inputDeviceStatus.setpJustPressed(false);
			deviceInputListener.pJustUppedAction();
		}
	
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			inputDeviceStatus.setqJustPressed(true);
			deviceInputListener.qJustPressedAction();
		}else if (Gdx.input.isKeyPressed(Keys.Q)==false&& inputDeviceStatus.isqJustPressed()) {
			inputDeviceStatus.setqJustPressed(false);
			deviceInputListener.qJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.R)) {
			inputDeviceStatus.setrJustPressed(true);
			deviceInputListener.rJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.R)==false&& inputDeviceStatus.isrJustPressed()) {
			inputDeviceStatus.setrJustPressed(false);
			deviceInputListener.rJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.S)) {
			inputDeviceStatus.setsJustPressed(true);
			deviceInputListener.sJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.S)==false&& inputDeviceStatus.issJustPressed()) {
			inputDeviceStatus.setsJustPressed(false);
			deviceInputListener.sJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.T)) {
			inputDeviceStatus.settJustPressed(true);
			deviceInputListener.tJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.T)==false&& inputDeviceStatus.istJustPressed()) {
			inputDeviceStatus.settJustPressed(false);
			deviceInputListener.tJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.U)) {
			inputDeviceStatus.setuJustPressed(true);
			deviceInputListener.uJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.U)==false&& inputDeviceStatus.isuJustPressed()) {
			inputDeviceStatus.setuJustPressed(false);
			deviceInputListener.uJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.V)) {
			inputDeviceStatus.setvJustPressed(true);
			deviceInputListener.vJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.V)==false&& inputDeviceStatus.isvJustPressed()) {
			inputDeviceStatus.setvJustPressed(false);
			deviceInputListener.vJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.W)) {
			inputDeviceStatus.setwJustPressed(true);
			deviceInputListener.wJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.W)==false&& inputDeviceStatus.iswJustPressed()) {
			inputDeviceStatus.setwJustPressed(false);
			deviceInputListener.wJustUppedAction();
		}
		if (Gdx.input.isKeyJustPressed(Keys.X)) {
			inputDeviceStatus.setxJustPressed(true);
			deviceInputListener.xJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.X)==false&& inputDeviceStatus.isxJustPressed()) {
			inputDeviceStatus.setxJustPressed(false);
			deviceInputListener.xJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.Y)) {
			inputDeviceStatus.setyJustPressed(true);
			deviceInputListener.yJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.Y)==false&& inputDeviceStatus.isyJustPressed()) {
			inputDeviceStatus.setyJustPressed(false);
			deviceInputListener.yJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.Z)) {
			inputDeviceStatus.setzJustPressed(true);
			deviceInputListener.zJustPressedAction();
		}else if (Gdx.input.isKeyPressed(Keys.Z)==false&& inputDeviceStatus.iszJustPressed()) {
			inputDeviceStatus.setzJustPressed(false);
			deviceInputListener.zJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			inputDeviceStatus.setSpaceJustPressed(true);
			deviceInputListener.spaceJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.SPACE)==false&& inputDeviceStatus.isSpaceJustPressed()) {
			inputDeviceStatus.setSpaceJustPressed(false);
			deviceInputListener.spaceJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_0)) {
			inputDeviceStatus.setNum0JustPressed(true);
			deviceInputListener.Num0JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_0)==false&& inputDeviceStatus.isNum0JustPressed()) {
			inputDeviceStatus.setNum0JustPressed(false);
			deviceInputListener.Num0JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
			inputDeviceStatus.setNum1JustPressed(true);
			deviceInputListener.Num1JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_1)==false&& inputDeviceStatus.isNum1JustPressed()) {
			inputDeviceStatus.setNum1JustPressed(false);
			deviceInputListener.Num1JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
			inputDeviceStatus.setNum2JustPressed(true);
			deviceInputListener.Num2JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_2)==false&& inputDeviceStatus.isNum2JustPressed()) {
			inputDeviceStatus.setNum2JustPressed(false);
			deviceInputListener.Num2JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3)) {
			inputDeviceStatus.setNum3JustPressed(true);
			deviceInputListener.Num3JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_3)==false&& inputDeviceStatus.isNum3JustPressed()) {
			inputDeviceStatus.setNum3JustPressed(false);
			deviceInputListener.Num3JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4)) {
			inputDeviceStatus.setNum4JustPressed(true);
			deviceInputListener.Num4JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_4)==false&& inputDeviceStatus.isNum4JustPressed()) {
			inputDeviceStatus.setNum4JustPressed(false);
			deviceInputListener.Num4JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5)) {
			inputDeviceStatus.setNum5JustPressed(true);
			deviceInputListener.Num5JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_5)==false&& inputDeviceStatus.isNum5JustPressed()) {
			inputDeviceStatus.setNum5JustPressed(false);
			deviceInputListener.Num5JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_6)) {
			inputDeviceStatus.setNum6JustPressed(true);
			deviceInputListener.Num6JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_6)==false&& inputDeviceStatus.isNum6JustPressed()) {
			inputDeviceStatus.setNum6JustPressed(false);
			deviceInputListener.Num6JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_7)) {
			inputDeviceStatus.setNum7JustPressed(true);
			deviceInputListener.Num7JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_7)==false&& inputDeviceStatus.isNum7JustPressed()) {
			inputDeviceStatus.setNum7JustPressed(false);
			deviceInputListener.Num7JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_8)) {
			inputDeviceStatus.setNum8JustPressed(true);
			deviceInputListener.Num8JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_8)==false&& inputDeviceStatus.isNum8JustPressed()) {
			inputDeviceStatus.setNum8JustPressed(false);
			deviceInputListener.Num8JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) {
			inputDeviceStatus.setNum9JustPressed(true);
			deviceInputListener.Num9JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_9)==false&& inputDeviceStatus.isNum9JustPressed()) {
			inputDeviceStatus.setNum9JustPressed(false);
			deviceInputListener.Num9JustUppedAction();
		}
		
	}
}
