package com.yuil.game.test;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxNativesLoader;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Quaternion rotation=new Quaternion();
		GdxNativesLoader.load();
		Matrix4 rotationMatrix = new Matrix4();
			
		//rotationMatrix.rotate(Vector3.Y, 180);
		//rotationMatrix.rotate(Vector3.Z, 90);
		rotationMatrix.setToLookAt(Vector3.Z, Vector3.Y);
		System.out.println(rotationMatrix.getRotation(rotation));	
		System.out.println(rotationMatrix.getRotation(rotation).getAngleAround(Vector3.Y));
		System.out.println(rotationMatrix.getRotation(rotation).getAngleAround(Vector3.X));
		System.out.println(rotationMatrix.getRotation(rotation).getAngleAround(Vector3.Z));
	}

}
