package edu.clemson.cs.cybertiger;

import edu.clemson.cs.cybertiger.menu.GenericMenu;
import edu.clemson.cs.cybertiger.menu.MainMenu;
import android.os.Bundle;
import android.view.Window;
import android.app.Activity;
import android.graphics.PixelFormat;

/**
 * The entry-point for the application, as specified in the manifest.
 * @author Joe Maley
 */
public class MainActivity extends Activity {
	
	/* When this menu is set, it changes the menu controls and layout */
	private GenericMenu menu;
	
	/* Sets the menu */
	public void setMenu(GenericMenu menu) {
		this.menu = menu;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setMenu(new MainMenu(this));
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		/* Formats the android application for smooth-gradients. */
	    Window window = getWindow();
	    window.setFormat(PixelFormat.RGBA_8888);
	}
	
	@Override
	public void onBackPressed() {
		menu.onBackButton();
		return;
	}
}
