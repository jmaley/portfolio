package edu.clemson.cs.cybertiger.menu;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.R;

/**
 * This menu provides handling for the main XML menu.
 * @author Joe Maley
 */
public class MainMenu extends GenericMenu {
	
	public MainMenu(final MainActivity activity) {
		super(activity, R.layout.activity_main_menu, R.id.relativeLayoutMainMenuActivity);
		
		createMenuButtonListener(R.id.buttonDiagnostics, MenuID.DIAGNOSTICS, null);
		createMenuButtonListener(R.id.buttonPreferences, MenuID.PREFERENCES, null);
		createMenuButtonListener(R.id.buttonExit, null, null);
	}
	
	@Override
	public void onBackButton() {
		super.onBackButton();
	}
}
