// File: TerrainManager.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import java.util.ArrayList;

import cpsc372.dinosaur_tycoon.EntityManager.Entity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

//Class: TerrainManager
//Description: Manages Tiles
public class TerrainManager
{	
	//Class: Tile
	//Description: Inner class that describes a tile in terms of position and image
	public class Tile
	{
		public int x, y;
		public String image;
		private int id;
		
		public Tile(int x, int y, String image)
		{
			this.x = x;
			this.y = y;
			this.image = image;
			id = game.getResources().getIdentifier(image, "drawable", "cpsc372.dinosaur_tycoon");
		}
		
		public void setImage(String image)
		{
			this.image = image;
			this.id = game.getResources().getIdentifier(image, "drawable", "cpsc372.dinosaur_tycoon");
		}
		
		public int getX() { return x; }
		public int getY() { return y; }
		public String getImage() { return image; }
		public int getImageId() { return id; }
	}
	
	private int mapWidth, mapHeight;
	private int TILE_WIDTH, TILE_HEIGHT;
	
	private ArrayList<Tile> tiles = null;
	private ArrayList<Tile> water = null;
	
	private GamePlayActivity game;
	
	private boolean initialized = false;
	private long gameTime;
	private float waterOffset = 0;
	
	private String hoverImage = "terrain_fakewater";
	private int hoverX = 0;
	private int hoverY = 0;
	
	public int getTileWidth() { return TILE_WIDTH; }
	public int getTileHeight() { return TILE_HEIGHT; }
	
	public int getMapWidth() { return mapWidth; }
	public int getMapHeight() { return mapHeight; }
	
	public boolean getInitialized() { return initialized; }
	
	public String getHoverImage() { return hoverImage; }
	
	public TerrainManager(GamePlayActivity game, int mapWidth, int mapHeight)
	{
		this.game = game;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		
		gameTime = System.currentTimeMillis();
	}
	
	private void generateWater(ArrayList<Tile> water)
	{
		for (int x = 0; x < 2 * ((game.getScreenWidth() / TILE_WIDTH) + 6); x++)
			for (int y = 0;  y < (game.getScreenHeight() / TILE_HEIGHT) + 6; y++)
				water.add(new Tile(x, y, "terrain_water"));
	}
	
	private void generateTerrain(ArrayList<Tile> tiles)
	{
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				String image = "terrain_grass";
				
				if (y < 6 || x < 6)
					image = "terrain_fakewater";
				
				if (x < (mapWidth / 2) + 4 && x > (mapWidth / 2) - 4 &&
						y < (mapHeight / 2) + 2 && y > (mapHeight / 2) - 2)
					image = "terrain_concrete";
				
				tiles.add(new Tile(x, y, image));
			}
		}
	}
	
	public void setHover(String image, int x, int y)
	{
		hoverImage = image;
		hoverX = x;
		hoverY = y;
	}
	
	public void Draw(Canvas canvas)
	{
		if (!initialized)
		{
			tiles = new ArrayList<Tile>();
			water = new ArrayList<Tile>();
			
			TILE_WIDTH = game.getBitmapManager().getBitmap(R.drawable.terrain_grass).getWidth();
			TILE_HEIGHT = game.getBitmapManager().getBitmap(R.drawable.terrain_grass).getHeight();
			
			generateTerrain(tiles);
			generateWater(water);
			
			initialized = true;
		}
		
		float tempWaterOffset = waterOffset;
		tempWaterOffset = (float) Math.round(waterOffset);
		tempWaterOffset = tempWaterOffset % 2 == 0 ? tempWaterOffset : tempWaterOffset + 1;
		
		for (int i = 0; i < water.size(); i++)
		{
			float offset_x = (float)(water.get(i).x) * TILE_WIDTH/2;
			float offset_y = (float)(water.get(i).y)* TILE_HEIGHT;
			
			if (water.get(i).x % 2 == 1)
				offset_y += TILE_HEIGHT/2;
			
			offset_x += tempWaterOffset;
			offset_y += tempWaterOffset/2;
			
			offset_x -= 2*TILE_WIDTH;
			offset_y -= 2*TILE_HEIGHT;
			
			offset_x += (game.getCameraManager().getX() % 2) * TILE_WIDTH/2;
			offset_y += (game.getCameraManager().getY() % 1) * TILE_HEIGHT;
		
			
			Bitmap bmp = game.getBitmapManager().getBitmap(water.get(i).getImageId());
			canvas.drawBitmap(bmp, offset_x, offset_y, null);
		}
		
		float hoverOffsetX = 100000;
		float hoverOffsetY = 100000;
		int tempHoverX = hoverX - TILE_WIDTH / 2;
		int tempHoverY = hoverY - TILE_HEIGHT;
		Tile temp = tiles.get(0);
		
		for (int i = 0; i < tiles.size(); i++)
		{
			Bitmap bmp = game.getBitmapManager().getBitmap(tiles.get(i).getImageId());

			float offset_x = (float)(tiles.get(i).x + game.getCameraManager().getX()) * TILE_WIDTH/2;
			float offset_y = (float)(tiles.get(i).y + game.getCameraManager().getY())* TILE_HEIGHT;
			
			if (tiles.get(i).x % 2 == 1)
				offset_y += TILE_HEIGHT/2;
			
			if (Math.sqrt(Math.pow(offset_x - tempHoverX, 2) + Math.pow(offset_y - tempHoverY, 2)) < Math.sqrt(Math.pow(hoverOffsetX - tempHoverX, 2) + Math.pow(hoverOffsetY - tempHoverY, 2)))
			{
				hoverOffsetX = offset_x;
				hoverOffsetY = offset_y;
				temp = tiles.get(i);
			}
		
			if (offset_x < (canvas.getWidth() + TILE_WIDTH) && offset_y < (canvas.getHeight() + TILE_HEIGHT) && offset_x > -TILE_WIDTH && offset_y > -TILE_HEIGHT) 
				canvas.drawBitmap(bmp, offset_x, offset_y, null);
		}

		int color = Color.parseColor("#96FFFF00");
		
		Entity ent = game.getEntityManager().getEntity(temp.getX(), temp.getY());
		
		if (ent != null)
		{
			if (hoverImage.contains("concrete"))
				if (ent.getImage().contains("dinosaur") || ent.getImage().contains("fence"))
					color = Color.parseColor("#96FF0000");
		}
		
		if (game.getEntityManager().entityAt(temp.getX(), temp.getY()) && hoverImage.contains("water"))
			color = Color.parseColor("#96FF0000");
		
		Paint paint = new Paint(color);
		ColorFilter filter = new LightingColorFilter(color, 1);
		paint.setColorFilter(filter);
		canvas.drawBitmap(game.getBitmapManager().getBitmap(game.getResources().getIdentifier(hoverImage, "drawable", "cpsc372.dinosaur_tycoon")), hoverOffsetX, hoverOffsetY, paint);
		
long delta = (System.currentTimeMillis() - gameTime);
		
		Update(delta);
		gameTime = System.currentTimeMillis();
	}
	
	private void Update(long delta)
	{
		waterOffset += 0.02 * delta;
		waterOffset = waterOffset > TILE_HEIGHT ? waterOffset % TILE_HEIGHT : waterOffset;
		
		ParkManager parkManager = game.getParkManager();
		if (parkManager != null)
			parkManager.updatePark(delta);
	}
	
	public void waterOffsetDelta(long offset)
	{
		waterOffset += offset;
	}
	
	public Tile getTile(int x, int y)
	{
		Tile temp = null;
		
		float tempOffsetX = 100000;
		float tempOffsetY = 100000;
		int tempX = x - TILE_WIDTH / 2;
		int tempY = y - TILE_HEIGHT;
		
		for (int i = 0; i < tiles.size(); i++)
		{
			float offset_x = (float)(tiles.get(i).x + game.getCameraManager().getX()) * TILE_WIDTH/2;
			float offset_y = (float)(tiles.get(i).y + game.getCameraManager().getY())* TILE_HEIGHT;
			
			if (tiles.get(i).x % 2 == 1)
				offset_y += TILE_HEIGHT/2;
			
			if (Math.sqrt(Math.pow(offset_x - tempX, 2) + Math.pow(offset_y - tempY, 2)) < Math.sqrt(Math.pow(tempOffsetX - tempX, 2) + Math.pow(tempOffsetY - tempY, 2)))
			{
				temp = tiles.get(i);
				tempOffsetX = offset_x;
				tempOffsetY = offset_y;
			}
		}
		
		return temp;
	}
	
	public Tile getTileExact(int x, int y)
	{
		for (int i = 0; i < tiles.size(); i++)
		{
			if (tiles.get(i).getX() == x && tiles.get(i).getY() == y)
				return tiles.get(i);
		}
		
		return null;
	}
	
	public boolean changeTile(int x, int y, String image)
	{
		Tile temp = getTile(x, y);
		
		Entity ent = game.getEntityManager().getEntity(temp.getX(), temp.getY());
		
		if (ent != null)
		{
			if (image.contains("concrete"))
				if (ent.getImage().contains("dinosaur") || ent.getImage().contains("fence"))
					return false;
		}
		
		if (game.getEntityManager().entityAt(temp.getX(), temp.getY()) && image.contains("water"))
			return false;
		
		if (game.getParkManager().purchaseEntity(image))
			temp.setImage(image);
			
		return true;
	}
}