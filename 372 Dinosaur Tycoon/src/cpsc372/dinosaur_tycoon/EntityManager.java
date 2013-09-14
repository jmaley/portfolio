// File: EntityManager.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import java.util.ArrayList;
import java.util.Random;

import cpsc372.dinosaur_tycoon.TerrainManager.Tile;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

//Class: EntityManager
//Description: Manages Entities (Price, animation, rotating, drawing, placement)
public class EntityManager
{
	public final int PRICE_RAPTOR = 150;
	public final int PRICE_TREX = 2500;
	public final int PRICE_STEGOSAURUS = 500;
	public final int PRICE_TRICERATOPS = 1500;
	public final int PRICE_SPINOSAURUS = 3500;
	
	public final int PRICE_FENCE = 10;
	public final int PRICE_BATHROOM1 = 50;
	public final int PRICE_VENDOR1 = 75;
	
	public final int PRICE_TREE1 = 25;
	public final int PRICE_TREE2 = 50;
	public final int PRICE_TREE3 = 75;
	public final int PRICE_BUSH1 = 15;
	public final int PRICE_BUSH2 = 30;
	public final int PRICE_BUSH3 = 45;
	
	public final int PRICE_GRASS = 5;
	public final int PRICE_STONE = 10;
	public final int PRICE_DIRT = 5;
	public final int PRICE_SAND = 15;
	public final int PRICE_CONCRETE = 10;
	public final int PRICE_SNOW = 25;
	public final int PRICE_ICE = 25;
	public final int PRICE_WATER = 15;
	
	//Class: EntityManager
	//Description: An object with an image, location, rotation, and animation
	public class Entity
	{
		protected int x, y;
		protected String image;
		protected int animation = 0;
		protected int rotation = 0;

		protected int oldX, oldY;
		protected float magOffset = 0;
		protected boolean isAnimating = false;
		
		public int getX() { return x; }
		public int getY() { return y; }
		public String getImage()  { return image; }
		public int getRotation()  { return rotation; }
		public int getAnimation() { return animation; }
		
		public int getOldX() { return oldX; }
		public int getOldY() { return oldY; }
		public float getMagOffset() { return magOffset; }
		public boolean getIsAnimating() { return isAnimating; }
		
		public void setRotation(int i) { this.rotation = i; }
		public void setAnimation(int animation) { this.animation = animation; }

		public void setIsAnimating(boolean state) { isAnimating = state; }
		
		public Entity(int x, int y, String image)
		{
			this.x = x;
			this.y = y;
			this.image = image;
			
			oldX = x;
			oldY = y;
			
			parseRotation();
			
			// treat animation = 0 as non animating (everything but dinosaurs)
			// treat rotation = 0 as non rotating object (foliage, terrain)
			if (image.contains("dinosaur"))
			{
				rotation = 1;
				animation = 2;
			}
			else
				animation = 0;
		}
		
		public Entity(int x, int y, String image, int rotation, int animation,
				int oldX, int oldY, boolean isAnimating, float magOffset)
		{
			this(x, y, image);
			this.rotation = rotation;
			this.animation = animation;
			this.oldX = oldX;
			this.oldY = oldY;
			this.isAnimating = isAnimating;
			this.magOffset = magOffset;
		}
		
		// format the image string, return image id
		public int getImageId()
		{
			if (rotation == 0)
				return game.getResources().getIdentifier(image, "drawable", "cpsc372.dinosaur_tycoon");
			else
			{
				String tmpImg = image + "_r" + rotation;
				
				if (animation != 0)
					tmpImg = tmpImg + "_a" + animation;
				
				return game.getResources().getIdentifier(tmpImg, "drawable", "cpsc372.dinosaur_tycoon");
			}
		}
		
		// used for swapping
		public void replace(Entity temp) 
		{
			this.x = temp.getX();
			this.y = temp.getY();
			this.image = temp.getImage();
			this.rotation = temp.getRotation();
			this.animation = temp.getAnimation();
			this.oldX = temp.getOldX();
			this.oldY = temp.getOldY();
			this.isAnimating = temp.getIsAnimating();
			this.magOffset = temp.getMagOffset();
		}
		
		// get rotation from image string
		private void parseRotation()
		{
			int i = image.indexOf('_', image.indexOf('_') + 1);
			if (i != -1)
			{
				rotation = Integer.parseInt(image.substring(i+2));
				image = image.substring(0, i);
			}
		}
		
		// increment to the next animation (1, 2, 3)
		private void incAnimation()
		{
			if (animation >= 1 && isAnimating)
			{
				if (animation == 1)
					animation = 3;
				else
					animation--;
			}
		}
		
		// increase the offset magnitude while animating
		private void incMag(float delta)
		{
			if (animation >= 1)
			{
				magOffset += delta;
				
				if (magOffset > 1.0)
				{
					magOffset = 0;
					oldX = x;
					oldY = y;
					isAnimating = false;
				}
			}
		}
		
		// attempt to move to a new tile
		private void moveTo(int tx, int ty)
		{
			for (int i = 0; i < entities.size(); i++)
			{
				// entities at the move-location
				if (entities.get(i).getX() == tx && entities.get(i).getY() == ty)
				{
					// if there is a non-fence/dinosaur entity, reject movement
					if (!entities.get(i).getImage().contains("fence") && !entities.get(i).getImage().contains("dinosaur"))
					{
						return;
					}
					else if (entities.get(i).getImage().contains("fence"))
					{
						// a fence on the move tile, facing the current tile
						int inverse_rotation = rotation;
						for (int j = 0; j < 2; j++)
						{
							inverse_rotation++;
							if (inverse_rotation > 4)
								inverse_rotation = 1;
						}
						
						if (entities.get(i).getRotation() == inverse_rotation)
							return;
					}
				}
				else if (entities.get(i).getX() == x && entities.get(i).getY() == y)
				{
					// if they are in a tile with a fence in between the move location, reject
					if (entities.get(i).getImage().contains("fence"))
					{
						if (entities.get(i).getRotation() == rotation)
							return;
					}
				}
				
				if (entities.get(i).getImage().contains("dinosaur"))
					if (entities.get(i).getX() == tx && entities.get(i).getY() == ty)
						if (getEntityValue(this.image) > getEntityValue(entities.get(i).getImage()))
							killEntity(i);
						else
							return;
			}
			
			// don't let them move onto water
			Tile temp = game.getTerrainManager().getTileExact(tx, ty);
			if (temp != null)
				if (temp.getImage().contains("water"))
					return;
			
			isAnimating = true;
			
			oldX = x;
			oldY = y;
			
			x = tx;
			y = ty;
		}
		
		// decide if the entity wants to move
		private void think(Random random)
		{
			if (animation >= 1)
			{
				// if currently moving, stop thinking
				if (oldX != x || oldY != y)
					return;
				
				if (random.nextInt(6) == 0)
				{
					int roll = random.nextInt(4);
					if (roll == 0) // down, right
					{
						rotation = 1;
						
						if (x % 2 == 0)
						{
							moveTo(x + 1, y);
						}
						else
						{
							moveTo(x + 1, y + 1);
						}
					}
					else if (roll == 1) // up, right
					{
						rotation = 4;
						
						if (x % 2 == 0)
						{
							moveTo(x + 1, y - 1);
						}
						else
						{
							moveTo(x + 1, y);
						}
					}
					else if (roll == 2) // down, left
					{
						rotation = 2;
						
						if (x % 2 == 0)
						{
							moveTo(x - 1, y);
						}
						else
						{
							moveTo(x - 1, y + 1);
						}
					}
					else if (roll == 3) // up, left
					{
						rotation = 3;
						
						if (x % 2 == 0)
						{
							moveTo(x - 1, y - 1);
						}
						else
						{
							moveTo(x - 1, y);
						}
					}
				}
			}
		}
	}
	
	private ArrayList<Entity> entities;
	private GamePlayActivity game;
	
	private int hoverX = 0;
	private int hoverY = 0;
	private String hoverImage = "terrain_fakewater";
	
	private long gameTime;
	private long totalDelta;
	private long sortTime;
	
	public String getHoverImage() { return hoverImage; }
	
	public EntityManager(GamePlayActivity game)
	{
		this.game = game;
		
		entities = new ArrayList<Entity>();
	}
	
	// image over touch when buying
	public void setHover(String image, int x, int y)
	{
		hoverImage = image;
		hoverX = x;
		hoverY = y;
	}
	
	public void draw(Canvas canvas)
	{
		if (!game.getTerrainManager().getInitialized())
			return;
		
		long delta = (System.currentTimeMillis() - gameTime);
		this.update((System.currentTimeMillis() - gameTime));
		gameTime = System.currentTimeMillis();
		
		int x, y, snapX, snapY;
		Paint paint;
		Bitmap bmp;
		
		Tile snap = game.getTerrainManager().getTile(hoverX, hoverY);
		
		snapX = snap.getX();
		snapY = snap.getY();
		
		paint = new Paint(Color.parseColor("#96FF0000"));
		ColorFilter filter = new LightingColorFilter(Color.parseColor("#96FF0000"), 1);
		paint.setColorFilter(filter);
		
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i).getOldX() != entities.get(i).getX()) // animating
			{
				x = entities.get(i).getOldX();
				y = entities.get(i).getOldY();
			}
			else
			{
				x = entities.get(i).getX();
				y = entities.get(i).getY();
			}
			
			/*
			 * Draw the entity in the list
			 */
			
			bmp = game.getBitmapManager().getBitmap(entities.get(i).getImageId());
			
			// snap to isometric tile
			float offset_x = (float)(x + game.getCameraManager().getX()) * game.getTerrainManager().getTileWidth() / 2;
			float offset_y = (float)(y + game.getCameraManager().getY()) * game.getTerrainManager().getTileHeight();
			
			offset_x = offset_x - bmp.getWidth()/2 + game.getTerrainManager().getTileWidth() / 2;
			offset_y = offset_y - bmp.getHeight() + game.getTerrainManager().getTileHeight();
			
			if (x % 2 == 1)
				offset_y +=  game.getTerrainManager().getTileHeight()/2;
			
			boolean toDelete = false;
			if (hoverImage.contains("delete") && snapX == x && snapY == y)
				toDelete = true;
			
			// draw on a magnitude offset if animating
			if (entities.get(i).getOldX() != entities.get(i).getX()) // animating
			{	
				float anim_offset_x;
				float anim_offset_y;
				
				anim_offset_x = entities.get(i).getMagOffset() * game.getTerrainManager().getTileWidth() / 2;
				anim_offset_y = entities.get(i).getMagOffset() * game.getTerrainManager().getTileHeight() / 2;
				
				if (entities.get(i).getRotation() == 2)
				{
					anim_offset_x = -anim_offset_x;
				}
				else if (entities.get(i).getRotation() == 3)
				{
					anim_offset_x = -anim_offset_x;
					anim_offset_y = -anim_offset_y;
				}
				else if (entities.get(i).getRotation() == 4)
				{
					anim_offset_y = -anim_offset_y;
				}
				
				canvas.drawBitmap(bmp, offset_x + anim_offset_x, offset_y + anim_offset_y, toDelete ? paint : null);
				
				entities.get(i).incMag((float)delta / 2000.0f);
			}
			else
				canvas.drawBitmap(bmp, offset_x, offset_y, toDelete ? paint : null);
		}
		
		/*
		 * Draw the 'hover' entity
		 */
		
		// color red if on top of water
		int color = snap.getImageId() == R.drawable.terrain_fakewater ? Color.parseColor("#96FF0000") : Color.parseColor("#96FFFF00");
		
		// color red if placing a dinosaur or fence on concrete
		if (snap.getImageId() == R.drawable.terrain_concrete && (hoverImage.contains("fence") || hoverImage.contains("dinosaur")))
			color = Color.parseColor("#96FF0000");

		// color red if placing a fence on top of an identical fence
		// or, color red if placing anything on another entity
		for (int i = 0; i < entities.size(); i++)
		{
			if (entities.get(i).getX() == snapX && entities.get(i).getY() == snapY)
			{
				if (hoverImage.contains("fence"))
				{
					int z = hoverImage.indexOf('_', hoverImage.indexOf('_') + 1);
					if (i != -1 && entities.get(i).getImage().contains("fence"))
						if (entities.get(i).getRotation() == Integer.parseInt(hoverImage.substring(z+2)))
							color = Color.parseColor("#96FF0000");
				}
				else
				{
					if (!entities.get(i).getImage().contains("fence"))
						color = Color.parseColor("#96FF0000");
				}	
			}
		}
		
		paint = new Paint(color);
		filter = new LightingColorFilter(color, 1);
		paint.setColorFilter(filter);
		
		bmp = game.getBitmapManager().getBitmap(game.getResources().getIdentifier(hoverImage, "drawable", "cpsc372.dinosaur_tycoon"));
		
		/*
		 * Draw a plane if placing a fence
		 */
		
		float offset_x = (float)(snapX + game.getCameraManager().getX()) * game.getTerrainManager().getTileWidth() / 2;
		float offset_y = (float)(snapY + game.getCameraManager().getY()) * game.getTerrainManager().getTileHeight();
		
		offset_x = offset_x - bmp.getWidth()/2 + game.getTerrainManager().getTileWidth() / 2;
		offset_y = offset_y - bmp.getHeight() + game.getTerrainManager().getTileHeight();
		
		if (snapX % 2 == 1)
			offset_y +=  game.getTerrainManager().getTileHeight()/2;
		
		if (hoverImage.contains("fence"))
			canvas.drawBitmap(game.getBitmapManager().getBitmap(R.drawable.place), offset_x, offset_y + game.getTerrainManager().getTileHeight(), paint);
		
		canvas.drawBitmap(bmp, offset_x, offset_y, paint);
	}
	
	private void update(long delta)
	{
		totalDelta += delta;
		
		if (totalDelta > 333)
		{
			totalDelta = 0;
			
			Random random = new Random(System.currentTimeMillis());
			
			for (int i = 0; i < entities.size(); i++)
			{
				entities.get(i).incAnimation();
				entities.get(i).think(random);
			}
			
			sortEntities(delta);
		}
	}
	
	public void addEntity(int hoverX, int hoverY, String image)
	{
		Tile snap = game.getTerrainManager().getTile(hoverX, hoverY);
		
		// do not place an entity over water
		if (snap.getImageId() == R.drawable.terrain_fakewater)
			return;
		
		// do not place dinosaurs or fence on top of concrete
		if (snap.getImageId() == R.drawable.terrain_concrete && (image.contains("fence") || image.contains("dinosaur")))
			return;
		
		int x, y;
		x = snap.getX();
		y = snap.getY();
		
		// if placing a 'delete' image, delete all entities instead of placing one
		if (image.contains("delete"))
		{
			for (int i = 0; i < entities.size(); i++)
			{
				if (entities.get(i).getX() == x && entities.get(i).getY() == y)
				{
					game.getParkManager().deleteEntity(entities.get(i).getImage());
					entities.remove(i);
					i--;
				}
			}
			return;
		}
		
		for (int i = 0; i < entities.size(); i++)
			if (entities.get(i).getX() == x && entities.get(i).getY() == y)
			{
				if (image.contains("fence"))
				{
					int z = image.indexOf('_', image.indexOf('_') + 1);
					if (i != -1 && entities.get(i).getImage().contains("fence"))
						if (entities.get(i).getRotation() == Integer.parseInt(image.substring(z+2)))
							return;
				}
				else
				{
					if (!entities.get(i).getImage().contains("fence"))
						return;
				}	
			}
		
		if (game.getParkManager().purchaseEntity(hoverImage))
			entities.add(new Entity(x, y, hoverImage));
		
		sortEntities(1000);
	}
	
	// is there an entity at this location?
	public boolean entityAt(int x, int y)
	{
		for (int i = 0; i < entities.size(); i++)
			if (entities.get(i).getX() == x && entities.get(i).getY() == y)
				return true;
		
		return false;
	}
	
	// get the entity at the location
	public Entity getEntity(int x, int y)
	{
		for (int i = 0; i < entities.size(); i++)
			if (entities.get(i).getX() == x && entities.get(i).getY() == y)
				return (entities.get(i));
		
		return null;
	}
	
	// sort entities using a simple bubble sort
	private void sortEntities(long delta)
	{
		sortTime += delta;
		
		if (sortTime < 1000)
			return;
		else
			sortTime = 0;
		
		for (int i = 0; i < entities.size(); i++)
		{
			for (int j = 0; j < entities.size() - 1; j++)
			{
				if (entities.get(j).getY() > entities.get(j+1).getY())
				{
					Entity temp = new Entity(entities.get(j).getX(), entities.get(j).getY(), entities.get(j).getImage(), entities.get(j).getRotation(), entities.get(j).getAnimation(),
							entities.get(j).getOldX(), entities.get(j).getOldY(), entities.get(j).getIsAnimating(), entities.get(j).getMagOffset());
					entities.get(j).replace(entities.get(j+1));
					entities.get(j+1).replace(temp);
				}
				else if (entities.get(j).getY() == entities.get(j+1).getY())
				{
					if (entities.get(j).getX() % 2 == 1 && entities.get(j+1).getX() % 2 == 0)
					{
						Entity temp = new Entity(entities.get(j).getX(), entities.get(j).getY(), entities.get(j).getImage(), entities.get(j).getRotation(), entities.get(j).getAnimation(),
								entities.get(j).getOldX(), entities.get(j).getOldY(), entities.get(j).getIsAnimating(), entities.get(j).getMagOffset());
						entities.get(j).replace(entities.get(j+1));
						entities.get(j+1).replace(temp);
					}
					else if (entities.get(j).getImage().contains("fence") && !entities.get(j+1).getImage().contains("fence"))
					{
						if (entities.get(j).getRotation() == 1 || entities.get(j).getRotation() == 2)
						{
							Entity temp = new Entity(entities.get(j).getX(), entities.get(j).getY(), entities.get(j).getImage(), entities.get(j).getRotation(), entities.get(j).getAnimation(),
									entities.get(j).getOldX(), entities.get(j).getOldY(), entities.get(j).getIsAnimating(), entities.get(j).getMagOffset());
							entities.get(j).replace(entities.get(j+1));
							entities.get(j+1).replace(temp);
						}
					}
					else if (!entities.get(j).getImage().contains("fence") && entities.get(j+1).getImage().contains("fence"))
					{
						if (entities.get(j+1).getRotation() == 3 || entities.get(j+1).getRotation() == 4)
						{
							Entity temp = new Entity(entities.get(j).getX(), entities.get(j).getY(), entities.get(j).getImage(), entities.get(j).getRotation(), entities.get(j).getAnimation(),
									entities.get(j).getOldX(), entities.get(j).getOldY(), entities.get(j).getIsAnimating(), entities.get(j).getMagOffset());
							entities.get(j).replace(entities.get(j+1));
							entities.get(j+1).replace(temp);
						}
					}
					else if (entities.get(j).getImage().contains("fence") && entities.get(j+1).getImage().contains("fence"))
					{
						if (entities.get(j+1).getRotation() == 3 || entities.get(j+1).getRotation() == 4)
						{
							if (entities.get(j).getRotation() == 1 || entities.get(j).getRotation() == 2)
							{
								Entity temp = new Entity(entities.get(j).getX(), entities.get(j).getY(), entities.get(j).getImage(), entities.get(j).getRotation(), entities.get(j).getAnimation(),
										entities.get(j).getOldX(), entities.get(j).getOldY(), entities.get(j).getIsAnimating(), entities.get(j).getMagOffset());
								entities.get(j).replace(entities.get(j+1));
								entities.get(j+1).replace(temp);
							}
						}
					}
				}
			}
		}
	}
	
	public int getEntityValue(String image) 
	{ 
		if (image.contains("dinosaur_raptor"))
			return PRICE_RAPTOR;
		if (image.contains("dinosaur_trex"))
			return PRICE_TREX;
		if (image.contains("dinosaur_stegosaurus"))
			return PRICE_STEGOSAURUS;
		if (image.contains("dinosaur_spinosaurus"))
			return PRICE_SPINOSAURUS;
		if (image.contains("dinosaur_triceratops"))
			return PRICE_TRICERATOPS;
		if (image.contains("structures_fence"))
			return PRICE_FENCE;
		if (image.contains("structures_bathroom1"))
			return PRICE_BATHROOM1;
		if (image.contains("structures_vendor1"))
			return PRICE_VENDOR1;
		if (image.contains("foliage_bush1"))
			return PRICE_BUSH1;
		if (image.contains("foliage_bush2"))
			return PRICE_BUSH2;
		if (image.contains("foliage_bush3"))
			return PRICE_BUSH3;
		if (image.contains("foliage_tree1"))
			return PRICE_TREE1;
		if (image.contains("foliage_tree2"))
			return PRICE_TREE2;
		if (image.contains("foliage_tree3"))
			return PRICE_TREE3;
		if (image.contains("terrain_grass"))
			return PRICE_GRASS;
		if (image.contains("terrain_dirt"))
			return PRICE_DIRT;
		if (image.contains("terrain_stone"))
			return PRICE_STONE;
		if (image.contains("terrain_snow"))
			return PRICE_SNOW;
		if (image.contains("terrain_ice"))
			return PRICE_ICE;
		if (image.contains("terrain_water") || image.contains("terrain_fakewater"))
			return PRICE_WATER;
		if (image.contains("terrain_sand"))
			return PRICE_SAND;
		if (image.contains("terrain_concrete"))
			return PRICE_CONCRETE;
		else
			return 0;
	}
	
	public int getEntityCountByImage(String image)
	{
		int count = 0;
		
		for (int i = 0; i < entities.size(); i++) 
			if (entities.get(i).getImage().contains(image))
				count++;
		
		return count;
	}
	
	private void killEntity(int ent)
	{	
		if (SettingsManager.getInstance().getViolence())
		{
			Random random = new Random();
			random.setSeed(System.currentTimeMillis());
			
			int[] particleIds = new int[10];
			
			for (int i = 0; i < 10; i++)
			{
				int roll = random.nextInt(8);
				switch(roll)
				{
				case 0:
					particleIds[i] = R.drawable.gore1;
					break;
				case 1:
					particleIds[i] = R.drawable.gore2;
					break;
				case 2:
					particleIds[i] = R.drawable.gore3;
					break;
				case 3:
					particleIds[i] = R.drawable.gore4;
					break;
				case 4:
					particleIds[i] = R.drawable.gore5;
					break;
				case 5:
					particleIds[i] = R.drawable.gore6;
					break;
				case 6:
					particleIds[i] = R.drawable.gore7;
					break;
				case 7:
					particleIds[i] = R.drawable.gore8;
					break;
				}
			}
			
			ParticleManager.getInstance().explosion(entities.get(ent).getX(), entities.get(ent).getY(), particleIds);
		}
		
		entities.remove(ent);
	}
}
