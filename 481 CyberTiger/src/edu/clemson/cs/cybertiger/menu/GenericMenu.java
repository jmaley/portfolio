package edu.clemson.cs.cybertiger.menu;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.R;
import edu.clemson.cs.cybertiger.menu.TestMenu.TestID;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Provides a template view and controller for menus.
 * @author Joe Maley
 */
public abstract class GenericMenu {
	
	protected enum MenuID {
		MAIN, PREFERENCES, DIAGNOSTICS, TEST;
	}
	
	protected MainActivity activity;
	
	private int layoutResID;
	private int viewID;
	
	/**
	 * Constructs a new menu.
	 * @param activity The main activity
	 * @param layoutResID resource ID of the layout
	 * @param viewID viewID resource ID of the view
	 */
	public GenericMenu(MainActivity activity, int layoutResID, int viewID) {
		this.activity = activity;
		this.layoutResID = layoutResID;
		this.viewID = viewID;
		
		activity.setContentView(layoutResID);
		formatMenu(viewID);
	}
	
	/**
	 * Updates the screen to the layout.
	 */
	public void updateContentView() {
		activity.setContentView(layoutResID);
		formatMenu(viewID);
	}
	
	/**
	 * Provides functionallity for the hardware back-button.
	 */
	public void onBackButton() {
		activity.finish();
	}
	
	/**
	 * Formats the menu's font and boldness.
	 * @param viewID resource ID of the view
	 */
	private void formatMenu(int viewID) {
		RelativeLayout layout = (RelativeLayout) activity.findViewById(viewID);
		Typeface tf = Typeface.createFromAsset(activity.getAssets(), "verdana.ttf");
		
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (layout.getChildAt(i) instanceof TextView) {
				TextView textView = (TextView) layout.getChildAt(i);
				
				if (textView.getId() == R.id.textViewLogo) {
					textView.setTypeface(tf, Typeface.BOLD);
					textView.setShadowLayer(2, 0, 0, Color.BLACK);
				} else {
					textView.setTypeface(tf);
					textView.setShadowLayer(1, 1, 1, Color.BLACK);
				}    	
			}
		}
	}
	
	/**
	 * Launches a new menu on button click. Must be followed by updateContentView().
	 * @param buttonID resource ID of the button
	 * @param menuID Menu to call on click
	 * @param testID If the test menu has been specificed, this specifies which test to perform
	 */
	protected void createMenuButtonListener(int buttonID, final MenuID menuID, final TestID testID) {
		final Button button = (Button) activity.findViewById(buttonID);

		button.setOnTouchListener(new View.OnTouchListener() {	

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
					
					if (menuID == null) {
						activity.finish();
					} else {
						switch (menuID) {
						case MAIN:
							activity.setMenu(new MainMenu(activity));
							break;
						case PREFERENCES:
							activity.setMenu(new PreferencesMenu(activity));
							break;
						case DIAGNOSTICS:
							activity.setMenu(new DiagnosticsMenu(activity));
							break;
						case TEST:	
							activity.setMenu(new TestMenu(activity, testID));
							break;
						}
					}
				}
				
				return false;
			}
		});
	}
}
