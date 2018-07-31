package com.yuil.game.test;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Matrix4 rotationMatrix = new Matrix4().rotate(Vector3.Y, 1);
		System.out.println(rotationMatrix);
	}

}
