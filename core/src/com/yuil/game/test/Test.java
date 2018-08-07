package com.yuil.game.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.JsonReader;

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
	
		Gdx.files=new LwjglFiles();
		FileHandleResolver resolver=new InternalFileHandleResolver();
		G3dModelLoader loader=new G3dModelLoader(new JsonReader(), resolver);
		ModelData md=loader.loadModelData(Gdx.files.internal("assets/data/groundShape.g3dj"));
		System.out.println(md.nodes.first().scale);
		
		//btConvexHullShape btchs=new btConvexHullShape();
	}

}
