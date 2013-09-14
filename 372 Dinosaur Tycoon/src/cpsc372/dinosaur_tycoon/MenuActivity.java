// File: MenuActivity.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

//Class: MenuActiity
//Description: Provides an abstract template for menus
abstract class MenuActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    // Binds a listener that will open a new activity
    protected void createMenuButtonListenerClass(int id, final Class<?> classObject)
    {
    	final ImageButton imageButton = (ImageButton) findViewById(id);
    	imageButton.setOnTouchListener(new View.OnTouchListener()
    	{	
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					// When pressed, change to green
					imageButton.setColorFilter(Color.parseColor("#FFFFFF00"));
				else
					// Otherwise, change to white
					imageButton.setColorFilter(Color.parseColor("#00FFFFFF"));
				
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					if (classObject == null)
						finish();
					else
					{
						// Call the new activity
						Intent intent = new Intent(v.getContext(), classObject);
						startActivity(intent);
					}
				}
				return false;
			}
		});
    }
}
