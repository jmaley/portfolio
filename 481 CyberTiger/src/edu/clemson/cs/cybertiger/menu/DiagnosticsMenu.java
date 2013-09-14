package edu.clemson.cs.cybertiger.menu;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.R;
import edu.clemson.cs.cybertiger.menu.TestMenu.TestID;

/**
 * This menu provides handling for the diagnostics XML menu.
 * @author Joe Maley
 */
public class DiagnosticsMenu extends GenericMenu {

	public DiagnosticsMenu(MainActivity activity) {
		super(activity, R.layout.activity_diagnostics_menu, R.id.relativeLayoutDiagnosticsMenuActivity);
		
		createMenuButtonListener(R.id.buttonLatency, MenuID.TEST, TestID.LATENCY);
		createMenuButtonListener(R.id.buttonUDP, MenuID.TEST, TestID.UDP);
		createMenuButtonListener(R.id.buttonTCP, MenuID.TEST, TestID.TCP);
		createMenuButtonListener(R.id.buttonStreaming, MenuID.TEST, TestID.STREAMING);
	}
	
	@Override
	public void onBackButton() {
		activity.setMenu(new MainMenu(activity));
	}
}
