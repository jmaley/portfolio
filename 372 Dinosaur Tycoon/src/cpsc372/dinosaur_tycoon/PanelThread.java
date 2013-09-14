// File: PanelThread.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

//Class: PanelThread
//Description: Creates a thread for drawing
class PanelThread extends Thread
{
    private SurfaceHolder surfaceHolder;
    private DrawingPanel drawingPanel;
    private boolean run = false;

    public PanelThread(SurfaceHolder surfaceHolder, DrawingPanel drawingPanel)
    {
        this.surfaceHolder = surfaceHolder;
        this.drawingPanel = drawingPanel;
    }

    public void setRunning(boolean run)
    {
        this.run = run;
    }

    @Override
    public void run()
    {
        Canvas c;
        while (run)
        {
            c = null;
            try
            {
                c = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder)
                {
                	// 'Hack' to clear the screen and re-call draw
                	drawingPanel.postInvalidate();
                }
			} finally
            {
            	if (c != null)
                    surfaceHolder.unlockCanvasAndPost(c);
            }
        }
    }
}
