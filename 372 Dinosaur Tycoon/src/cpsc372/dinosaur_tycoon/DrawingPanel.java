// File: DrawingPanel.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//Class: DrawingPanel
//Description: A SurfaceView that draws from the entity and terrain managers
public class DrawingPanel extends SurfaceView implements SurfaceHolder.Callback
{	
	PanelThread thread;
	EntityManager entityManager;
	TerrainManager terrainManager;
	BitmapManager bitmapManager;
	CameraManager cameraManager;
	ParticleManager particleManager;
	
	public DrawingPanel(Context context, GamePlayActivity game)
	{ 
        super(context); 
        getHolder().addCallback(this);
        
        this.terrainManager = game.getTerrainManager();
        this.entityManager = game.getEntityManager();
        this.bitmapManager = game.getBitmapManager();
    	this.cameraManager = game.getCameraManager();
    	this.particleManager = ParticleManager.getInstance();
    }

    @Override 
    public void onDraw(Canvas canvas)
    {
    	canvas.drawRGB(0, 0, 0);
    	terrainManager.Draw(canvas);
    	entityManager.draw(canvas);
    	particleManager.draw(canvas, terrainManager, bitmapManager, cameraManager);
    } 

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    public void surfaceCreated(SurfaceHolder holder)
    {
    	setWillNotDraw(false);

    	thread = new PanelThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
    	try
    	{
            thread.setRunning(false);
            thread.join();
    	} catch (InterruptedException e) {}
	}
}
