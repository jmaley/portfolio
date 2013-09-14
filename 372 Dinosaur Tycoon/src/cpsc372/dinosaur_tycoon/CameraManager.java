// File: CameraManager.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

//Class: CameraManager
//Description: Manages the position of the camera
public class CameraManager
{
	private float x = 0, y = 0;
	private int tileCountX, tileCountY;
	private GamePlayActivity game;
	
	public float getX() { return x; }
	public float getY() { return y; }
	
	public CameraManager(GamePlayActivity game, float x, float y, int tileCountX, int tileCountY)
	{
		this.game = game;
		this.x = x;
		this.y = y;
		this.tileCountX = tileCountX;
		this.tileCountY = tileCountY;
		
		this.move(x, y);
	}
	
	// Move the camera by the passed x and y offsets
	public void move(float offsetX, float offsetY)
	{
		float tempX = x + offsetX;
		float tempY = y + offsetY;
		
		tempX = tempX > -1 ? -1 : tempX;
		tempY = tempY > -0.5f ? -0.5f : tempY;
		
		tempX = tempX < -tileCountX + 1 ? -tileCountX + 1 : tempX;
		tempY = tempY < -tileCountY + 0.5f ? -tileCountY +0.5f : tempY;

		x = tempX;
		y = tempY;
	}
	
	// Center the camera in the middle of the map
	public void center()
	{
		// We use the 'grass' tile as the size of all other tiles
		int TILE_WIDTH = game.getBitmapManager().getBitmap(R.drawable.terrain_grass).getWidth();
		int TILE_HEIGHT = game.getBitmapManager().getBitmap(R.drawable.terrain_grass).getHeight();
		
		x = -((tileCountX / 2) + 0.5f - ((game.getScreenWidth()/TILE_WIDTH)));
		y = -((tileCountY / 2) + 0.5f - ((game.getScreenHeight()/TILE_HEIGHT)/2));
	
	}
}
