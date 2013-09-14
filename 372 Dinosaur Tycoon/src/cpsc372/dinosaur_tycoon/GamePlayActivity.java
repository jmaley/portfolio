// File: GamePlayActivity.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;

//Class: GamePlayActivity
//Description: Holds all managers, acts as a controller for the views
public class GamePlayActivity extends Activity implements Handler.Callback, OnTouchListener
{
	private float x, y;
	
	private TerrainManager terrainManager = null;
	private EntityManager entityManager = null;
	private BitmapManager bitmapManager = null;
	private CameraManager cameraManager = null;
	private UIManager uiManager = null;
	private ParkManager parkManager = null;
	
	public TerrainManager getTerrainManager() { return terrainManager; }
	public EntityManager getEntityManager() { return entityManager; }
	public BitmapManager getBitmapManager() { return bitmapManager; }
	public CameraManager getCameraManager() { return cameraManager; }
	public UIManager getUIManager() { return uiManager; }
	public ParkManager getParkManager() { return parkManager; }
	
	private String state = "default";
	private ImageButton imageButtonReset = null;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        bitmapManager = new BitmapManager(getResources());
        cameraManager = new CameraManager(this, 0, 0, 40, 40);
        terrainManager = new TerrainManager(this, 40, 40);
        entityManager = new EntityManager(this);

        SurfaceView surfaceView = new DrawingPanel(this, this);
        surfaceView.setOnTouchListener(this);
        addContentView(surfaceView, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
        
        LayoutInflater inflater = getLayoutInflater();
        getWindow().addContentView(inflater.inflate(R.layout.vignette, null), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        getWindow().addContentView(inflater.inflate(R.layout.activity_game_play, null), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
        
        uiManager = new UIManager(this);
        
        ((ImageButton) findViewById(R.id.imageButton7)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.imageButton6)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.imageButton5)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.imageButton4)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.imageButton3)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.imageButton2)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.imageButton1)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.ticket_left)).setOnTouchListener(this);
        ((ImageButton) findViewById(R.id.ticket_right)).setOnTouchListener(this);
        
        TextView tv;
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Base02.ttf");

        tv = (TextView) findViewById(R.id.Price2);
        tv.setTypeface(tf);
        tv.setTextColor(Color.parseColor("#969600"));
        tv.setText(" ");
        
        tv = (TextView) findViewById(R.id.Price3);
        tv.setTypeface(tf);
        tv.setTextColor(Color.parseColor("#969600"));
        tv.setText(" ");
        
        tv = (TextView) findViewById(R.id.Price4);
        tv.setTypeface(tf);
        tv.setTextColor(Color.parseColor("#969600"));
        tv.setText(" ");
        
        tv = (TextView) findViewById(R.id.Price5);
        tv.setTypeface(tf);
        tv.setTextColor(Color.parseColor("#969600"));
        tv.setText(" ");
        
        tv = (TextView) findViewById(R.id.Price6);
        tv.setTypeface(tf);
        tv.setTextColor(Color.parseColor("#969600"));
        tv.setText(" ");
        
        parkManager = new ParkManager(this, entityManager);
        
        tv = (TextView) findViewById(R.id.Money);
        tv.setTypeface(tf);
        tv.setTextColor(Color.WHITE);
        
        tv = (TextView) findViewById(R.id.Ticket);
        tv.setTypeface(tf);
        tv.setTextColor(Color.WHITE);

        cameraManager.center();
        
        setPaused(false);
    }
	
//	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		String hoverImage;
		
		// refresh the 'hover' image
		if (state.contains("buy") || state.contains("delete"))
		{
			if (state.contains("terrain"))
				hoverImage = terrainManager.getHoverImage();
			else
				hoverImage = entityManager.getHoverImage();
		}
		else
			hoverImage = "terrain_fakewater";

		int hoverX = 0;
		int hoverY = 0;
		
		// reset colored buttons
		if (imageButtonReset != null)
			imageButtonReset.setColorFilter(Color.parseColor("#00000000"));
		imageButtonReset = null;
		
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			// pause menu exit
			if(v.getId() == R.id.game_menu_yes)
			{
				finish();
			}
			
			// pause menu continue
			else if (v.getId() == R.id.game_menu_no)
			{
				setPaused(false);
			}
			
			// ignore all other buttons if paused
			if (state.contains("paused"))
				return true;
			
			// decrement ticket price
			if (v.getId() == R.id.ticket_left)
			{
				ImageButton imageButton = (ImageButton) findViewById(v.getId());
				parkManager.setTicketPrice(parkManager.getTicketPrice() - 1);
				imageButton.setColorFilter(Color.parseColor("#FFFFFF00"));
				imageButtonReset = imageButton;
			}
			
			// increment ticket price
			else if (v.getId() == R.id.ticket_right)
			{
				ImageButton imageButton = (ImageButton) findViewById(v.getId());
				parkManager.setTicketPrice(parkManager.getTicketPrice() + 1);
				imageButton.setColorFilter(Color.parseColor("#FFFFFF00"));
				imageButtonReset = imageButton;
			}
			
			// any of the action buttons
			else if (v.getId() == R.id.imageButton1 ||
				v.getId() == R.id.imageButton2 ||
				v.getId() == R.id.imageButton3 ||
				v.getId() == R.id.imageButton4 ||
				v.getId() == R.id.imageButton5 ||
				v.getId() == R.id.imageButton6 ||
				v.getId() == R.id.imageButton7)
			{
				ImageButton imageButton = (ImageButton) findViewById(v.getId());
					
				String tag = imageButton.getTag().toString();
				
				hoverX = (int) event.getRawX();
				hoverY = (int) event.getRawY();
				
				// handle sub-menus
				if (tag.equals("button_terrain") ||
					tag.equals("button_dinosaur") ||
					tag.equals("button_structures") ||
					tag.equals("button_foliage"))
				{	
					state = tag.substring(7);
					uiManager.setUIScrolling(uiManager.getButtonListFromState(state));
				}
				
				// loop the ui buttons backward
				else if (tag.equals("button_arrow_left"))
				{
					uiManager.getButtonListFromState(state).backward();
					uiManager.setUIScrolling(uiManager.getButtonListFromState(state));
					imageButton.setColorFilter(Color.parseColor("#FFFFFF00"));
					imageButtonReset = imageButton;
				}
				
				// loop the ui buttons forward
				else if (tag.equals("button_arrow_right"))
				{
					uiManager.getButtonListFromState(state).forward();
					uiManager.setUIScrolling(uiManager.getButtonListFromState(state));
					imageButton.setColorFilter(Color.parseColor("#FFFFFF00"));
					imageButtonReset = imageButton;
				}
				
				// rotate ui buttons
				else if (tag.equals("button_rotate"))
				{
					uiManager.getButtonListFromState(state).rotate();
					uiManager.setUIScrolling(uiManager.getButtonListFromState(state));
					imageButton.setColorFilter(Color.parseColor("#FFFFFF00"));
					imageButtonReset = imageButton;
				}
				
				// attempt to delete objects
				else if (tag.equals("button_delete"))
				{
					state = "delete";
					hoverImage = "delete";
				}
				
				// pause menu
				else if (tag.equals("button_menu"))
				{
					setPaused(true);
				}
				
				// individual terrain/dinosaur/structure/foliage entities
				else if (tag.contains("button_terrain_")    ||
						 tag.contains("button_dinosaur_")   ||
						 tag.contains("button_structures_") ||
						 tag.contains("button_foliage_"))
					
				{
					String image = tag.substring(7);
					
					if (!parkManager.canAfford(image))
						return true;
					
					int rotation = uiManager.getButtonListFromState(state).getRotation();
					
					if (rotation != 0)
						image = image + "_r" + rotation;
					
					state = tag.substring(7, tag.indexOf('_', 8)) + "_buy";
					
					hoverImage = image;
				}
			}
			else
			{
				x = event.getX();
				y = event.getY();
			}
		}
		else if (event.getAction() == MotionEvent.ACTION_MOVE)
		{
			// no movement if paused
			if (state.contains("paused"))
				return true;
			
			// move the hovering image
			if (state.contains("buy") || state.contains("delete"))
			{
				hoverX = (int) event.getRawX();
				hoverY = (int) event.getRawY();
			}
			
			// otherwise, move the camera
			else
			{
				if (v.getId() == -1)
				{
					float dX = event.getX() - x;
					float dY = event.getY() - y;
						
					x = event.getX();
					y = event.getY();
					cameraManager.move(0.0142f * dX, 0.0142f * dY);
				}
			}
		}
		else if (event.getAction() == MotionEvent.ACTION_UP)
		{
			// attempt to buy an entity
			if (state.contains("buy"))
			{
				hoverImage = hoverImage.equals("terrain_water") ? "terrain_fakewater" : hoverImage;
				
				if (state.contains("terrain"))
					terrainManager.changeTile((int)event.getRawX(), (int)event.getRawY(), hoverImage);
				else
					entityManager.addEntity((int)event.getRawX(), (int)event.getRawY(), hoverImage);
				
				hoverImage = "terrain_fakewater";
				state = state.substring(0, state.length() - 4);
			}
			
			// attempt to delete an entity
			else if (state.contains("delete"))
			{
				entityManager.addEntity((int)event.getRawX(), (int)event.getRawY(), hoverImage);
				hoverImage = "terrain_fakewater";
				state = "default";
				entityManager.setHover(hoverImage, hoverX, hoverY);
			}
		}
			
		if (state.contains("terrain"))
			terrainManager.setHover(hoverImage, hoverX, hoverY);
		else if (state.contains("dinosaur")   || 
				 state.contains("structures") ||
				 state.contains("foliage") ||
				 state.contains("delete"))
		{
			entityManager.setHover(hoverImage, hoverX, hoverY);
		}
		
		return true;
	}
	
	@Override
	public void onBackPressed()
	{
		if (state.equals("default"))
		{	
			setPaused(true);
		}
		else
		{
			state = "default";
			uiManager.setUI(uiManager.getButtonsDefault());
		}
	}
	
	public int getScreenWidth()
	{
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int width = dm.widthPixels;
		
		return width;
	}
	
	public int getScreenHeight()
	{
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int height = dm.heightPixels;
		
		return height;
	}
	
//	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private void setPaused(boolean paused)
	{
		ImageView iv_game_menu = (ImageView) this.findViewById(R.id.game_menu);
		ImageButton ib_yes = (ImageButton) this.findViewById(R.id.game_menu_yes);
		ImageButton ib_no = (ImageButton) this.findViewById(R.id.game_menu_no);
		
		if (paused)
		{
			state = "paused";
			iv_game_menu.setAlpha(255);
			ib_yes.setAlpha(255);
			ib_no.setAlpha(255);
			
			ib_yes.setOnTouchListener(this);
			ib_no.setOnTouchListener(this);
		}
		else
		{
			state = "default";
			iv_game_menu.setAlpha(0);
			ib_yes.setAlpha(0);
			ib_no.setAlpha(0);
			
			ib_yes.setOnTouchListener(null);
			ib_no.setOnTouchListener(null);
		}
	}
}
