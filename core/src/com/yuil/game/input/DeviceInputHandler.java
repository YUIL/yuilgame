package com.yuil.game.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

public class DeviceInputHandler {
	public KeyboardStatus keyboardStatus;
	public DeviceInputListener deviceInputListener;
	public DeviceInputHandler(KeyboardStatus keyboardStatus,DeviceInputListener deviceInputListener){
		this.keyboardStatus=keyboardStatus;
		this.deviceInputListener=deviceInputListener;
	}
	public void checkDeviceInput(){
		
		if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			if(!keyboardStatus.isMouseLeftJustPressed()){
				deviceInputListener.mouseLeftJustPressedAction();
				keyboardStatus.setMouseLeftJustPressed(true);
			}
		}else if(keyboardStatus.isMouseLeftJustPressed()){
			deviceInputListener.mouseLeftJustUppedAction();
			keyboardStatus.setMouseLeftJustPressed(false);
		}
		
		if(Gdx.input.isButtonPressed(Buttons.RIGHT)){
			if(!keyboardStatus.isMouseRightJustPressed()){
				deviceInputListener.mouseRightJustPressedAction();
				keyboardStatus.setMouseRightJustPressed(true);
			}
		}else if(keyboardStatus.isMouseRightJustPressed()){
			deviceInputListener.mouseRightJustUppedAction();
			keyboardStatus.setMouseRightJustPressed(false);
		}
		
		if(Gdx.input.isButtonPressed(Buttons.MIDDLE)){
			if(!keyboardStatus.isMouseMiddleJustPressed()){
				deviceInputListener.mouseMiddleJustPressedAction();
				keyboardStatus.setMouseMiddleJustPressed(true);
			}
		}else if(keyboardStatus.isMouseMiddleJustPressed()){
			deviceInputListener.mouseMiddleJustUppedAction();
			keyboardStatus.setMouseMiddleJustPressed(false);
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.Q)) {
			keyboardStatus.setqJustPressed(true);
		}else if (Gdx.input.isKeyPressed(Keys.Q)==false&& keyboardStatus.isqJustPressed()) {
			keyboardStatus.setqJustPressed(false);
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.A)) {
			// game.getScreen().dispose();
			keyboardStatus.setaJustPressed(true);
			deviceInputListener.aJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.A)==false&& keyboardStatus.isaJustPressed()) {
			keyboardStatus.setaJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.D)){
				dJustPressedAction();
			}else{
				aJustUppedAction();
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.D)) {
			// game.getScreen().dispose();
			keyboardStatus.setdJustPressed(true);
			dJustPressedAction();
		}else if (Gdx.input.isKeyPressed(Keys.D)==false&& keyboardStatus.isdJustPressed()) {
			keyboardStatus.setdJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.A)){
				aJustPressedAction();
			}else{
				dJustUppedAction();
			}
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.W)) {
			// game.getScreen().dispose();
			keyboardStatus.setwJustPressed(true);
			wJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.W)==false&& keyboardStatus.iswJustPressed()) {
			keyboardStatus.setwJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.S)){
				sJustPressedAction();
			}else{
				wJustUppedAction();
			}
		}
		if (Gdx.input.isKeyJustPressed(Keys.S)) {
			// game.getScreen().dispose();
			keyboardStatus.setsJustPressed(true);
			sJustPressedAction();
		}else if (Gdx.input.isKeyPressed(Keys.S)==false&& keyboardStatus.issJustPressed()) {
			keyboardStatus.setsJustPressed(false);
			if(Gdx.input.isKeyPressed(Keys.W)){
				wJustPressedAction();
			}else{
				sJustUppedAction();
			}
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			// game.getScreen().dispose();
			keyboardStatus.setSpaceJustPressed(true);
			spaceJustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.SPACE)==false&& keyboardStatus.isSpaceJustPressed()) {
			keyboardStatus.setSpaceJustPressed(false);
			spaceJustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_0)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum0JustPressed(true);
			Num0JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_0)==false&& keyboardStatus.isNum0JustPressed()) {
			keyboardStatus.setNum0JustPressed(false);
			Num0JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum1JustPressed(true);
			Num1JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_1)==false&& keyboardStatus.isNum1JustPressed()) {
			keyboardStatus.setNum1JustPressed(false);
			Num1JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum2JustPressed(true);
			Num2JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_2)==false&& keyboardStatus.isNum2JustPressed()) {
			keyboardStatus.setNum2JustPressed(false);
			Num2JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum3JustPressed(true);
			Num3JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_3)==false&& keyboardStatus.isNum3JustPressed()) {
			keyboardStatus.setNum3JustPressed(false);
			Num3JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum4JustPressed(true);
			Num4JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_4)==false&& keyboardStatus.isNum4JustPressed()) {
			keyboardStatus.setNum4JustPressed(false);
			Num4JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum5JustPressed(true);
			Num5JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_5)==false&& keyboardStatus.isNum5JustPressed()) {
			keyboardStatus.setNum5JustPressed(false);
			Num5JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_6)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum6JustPressed(true);
			Num6JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_6)==false&& keyboardStatus.isNum6JustPressed()) {
			keyboardStatus.setNum6JustPressed(false);
			Num6JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_7)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum7JustPressed(true);
			Num7JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_7)==false&& keyboardStatus.isNum7JustPressed()) {
			keyboardStatus.setNum7JustPressed(false);
			Num7JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_8)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum8JustPressed(true);
			Num8JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_8)==false&& keyboardStatus.isNum8JustPressed()) {
			keyboardStatus.setNum8JustPressed(false);
			Num8JustUppedAction();
		}
		
		if (Gdx.input.isKeyJustPressed(Keys.NUM_9)) {
			// game.getScreen().dispose();
			keyboardStatus.setNum9JustPressed(true);
			Num9JustPressedAction();

		}else if (Gdx.input.isKeyPressed(Keys.NUM_9)==false&& keyboardStatus.isNum9JustPressed()) {
			keyboardStatus.setNum9JustPressed(false);
			Num9JustUppedAction();
		}
		
	}
	
	public void Num9JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num9JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num8JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num8JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num7JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num7JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num6JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num6JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num5JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num5JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num4JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num4JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num3JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num3JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num2JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num2JustPressedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num1JustUppedAction() {
		// TODO Auto-generated method stub
		
	}

	public void Num1JustPressedAction() {
	}

	public void Num0JustUppedAction() {
	}

	public void Num0JustPressedAction() {
	}

	
	public void aJustPressedAction() {
	}
	public void aJustUppedAction() {
	}
	
	public void bJustPressedAction() {
	}
	public void bJustUppedAction() {
	}
	
	public void cJustPressedAction() {
	}
	public void cJustUppedAction() {
	}
	
	public void dJustPressedAction() {
	}
	public void dJustUppedAction() {
	}
	
	public void eJustPressedAction() {
	}
	public void eJustUppedAction() {
	}
	
	public void fJustPressedAction() {
	}
	public void fJustUppedAction() {
	}
	
	public void gJustPressedAction() {
	}
	public void gJustUppedAction() {
	}
	
	public void hJustPressedAction() {
	}
	public void hJustUppedAction() {
	}
	
	public void iJustPressedAction() {
	}
	public void iJustUppedAction() {
	}
	
	public void jJustPressedAction() {
	}
	public void jJustUppedAction() {
	}
	
	public void kJustPressedAction() {
	}
	public void kJustUppedAction() {
	}
	
	public void lJustPressedAction() {
	}
	public void lJustUppedAction() {
	}
	
	public void mJustPressedAction() {
	}
	public void mJustUppedAction() {
	}
	
	public void nJustPressedAction() {
	}
	public void nJustUppedAction() {
	}
	
	public void oJustPressedAction() {
	}
	public void oJustUppedAction() {
	}
	
	public void pJustPressedAction() {
	}
	public void pJustUppedAction() {
	}
	
	public void qJustPressedAction() {
	}
	public void qJustUppedAction() {
	}
	
	public void rJustPressedAction() {
	}
	public void rJustUppedAction() {
	}
	
	public void sJustPressedAction() {
	}
	public void sJustUppedAction() {
	}
	
	public void tJustPressedAction() {
	}
	public void tJustUppedAction() {
	}
	
	public void uJustPressedAction() {
	}
	public void uJustUppedAction() {
	}
	
	public void vJustPressedAction() {
	}
	public void vJustUppedAction() {
	}
	
	public void wJustPressedAction() {
	}
	public void wJustUppedAction() {
	}
	
	public void xJustPressedAction() {
	}
	public void xJustUppedAction() {
	}
	
	public void yJustPressedAction() {
	}
	public void yJustUppedAction() {
	}
	
	public void zJustPressedAction() {
	}
	public void zJustUppedAction() {
	}
	
	
	
	public void spaceJustPressedAction() {
	}

	public void spaceJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	public void delJustPressedAction() {
		
	}

	public void delJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	public void mouseLeftJustPressedAction() {
		
	}
	public void mouseLeftJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	public void mouseRightJustPressedAction() {
		
	}
	public void mouseRightJustUppedAction() {
		// TODO Auto-generated method stub
		
	}
	
	public void mouseMiddleJustPressedAction() {
		
	}
	public void mouseMiddleJustUppedAction() {
		// TODO Auto-generated method stub
		
	}

}
