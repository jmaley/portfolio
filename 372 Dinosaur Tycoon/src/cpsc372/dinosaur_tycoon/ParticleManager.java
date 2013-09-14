// File: ParticleManager.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

//Class: ParticleManager
//Description: Creates, draws, updates, and destroys particles
public class ParticleManager
{
	//Class: ParticleManager
	//Description: Inner class that represents a particle
	private class Particle
	{
		private int id;
		private int x, y;
		private double rotation;
		private long lifeTime;
		
		public int getId() { return id; }
		public int getX()  { return x; }
		public int getY()  { return y; }
		public double getRotation() { return rotation; }
		public long getLifeTime() { return lifeTime; }
		
		public Particle(int id, int x, int y, double rotation)
		{
			this.id = id;
			this.x = x;
			this.y = y;
			this.rotation = rotation;
			
			lifeTime = 0;
			
			System.out.println("Rotation: " + rotation);
		}
		
		public void incLifeTime(long delta)
		{
			lifeTime += delta;
		}
	}
	
	private ArrayList<Particle> particles;
	
	private long gameTime;
	
	private static ParticleManager instance;
	
	private ParticleManager()
	{
		particles = new ArrayList<Particle>();
	}
	
	public static ParticleManager getInstance()
	{
		if (instance == null)
			instance = new ParticleManager();
		
     return instance;
	}
	
	public void explosion(int x, int y, int[] particleIds)
	{
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		
		for (int i = 0; i < particleIds.length; i++)
			particles.add(new Particle(particleIds[i], x, y, Math.toRadians(random.nextInt(360))));
	}
	
	@SuppressLint("FloatMath")
	public void draw(Canvas canvas, TerrainManager tm, BitmapManager bm, CameraManager cm)
	{
		if (!tm.getInitialized())
			return;
		
		this.update((System.currentTimeMillis() - gameTime));
		gameTime = System.currentTimeMillis();
		
		int x, y;
		Bitmap bmp;
		float offsetX, offsetY;
		
		for (int i = 0; i < particles.size(); i++)
		{
			Particle particle = particles.get(i);
			
			bmp = bm.getBitmap(particle.getId());
			x = particle.getX();
			y = particle.getY();
			
			offsetX = (float)(x + cm.getX()) * tm.getTileWidth() / 2;
			offsetY = (float)(y + cm.getY()) * tm.getTileHeight();
			
			offsetX = offsetX - bmp.getWidth()/2 + tm.getTileWidth() / 2;
			offsetX = offsetX - bmp.getHeight() + tm.getTileHeight();
			
			if (x % 2 == 1)
				offsetY +=  tm.getTileHeight()/2;
			
			offsetX = offsetX + ((float)particle.getLifeTime() / 40.0f * (float)Math.cos(particle.getRotation()));
			offsetY = offsetY + ((float)particle.getLifeTime() / 40.0f * (float)Math.sin(particle.getRotation())) - tm.getTileHeight()/2;

			Paint paint = new Paint();
			paint.setAlpha((int) (200 - (100 * particle.getLifeTime() / 3000.0f)));
			
			canvas.drawBitmap(bmp, offsetX, offsetY, paint);
		}
	}
	
	private void update(long delta)
	{
		for (int i = 0; i < particles.size(); i++)
		{
			Particle particle = particles.get(i);
			
			particle.incLifeTime(delta);
			
			if (particle.getLifeTime() > 3000)
			{
				particles.remove(i);
				i--;
				continue;
			}
		}
	}
}
