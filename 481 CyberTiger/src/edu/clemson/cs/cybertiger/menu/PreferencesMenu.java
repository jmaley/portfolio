package edu.clemson.cs.cybertiger.menu;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.PreferenceManager;
import edu.clemson.cs.cybertiger.R;
import edu.clemson.cs.cybertiger.menu.GenericMenu.MenuID;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * This menu provides handling for the preferences XML menu.
 * @author Joe Maley
 */
public class PreferencesMenu extends GenericMenu {
	
	private class GPSOnItemSelectedListener implements OnItemSelectedListener {
		 
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			preferenceManager.setPreferredGPS(pos, activity);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) { }
	}
	
	private class NetworkOnItemSelectedListener implements OnItemSelectedListener {
		 
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			preferenceManager.setPreferredNetwork(pos, activity);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) { }
	}
	
	PreferenceManager preferenceManager;
	
	public PreferencesMenu(MainActivity activity) {
		super(activity, R.layout.activity_preferences_menu, R.id.relativeLayoutPreferencesMenuActivity);
		
		preferenceManager = PreferenceManager.getInstance();
		
		Spinner spinnerGPS = (Spinner) activity.findViewById(R.id.spinnerGPS);
		spinnerGPS.setSelection(preferenceManager.getPreferredGPS(activity));
		spinnerGPS.setOnItemSelectedListener(new GPSOnItemSelectedListener());
		
		Spinner spinnerNetwork = (Spinner) activity.findViewById(R.id.spinnerNetwork);
		spinnerNetwork.setSelection(preferenceManager.getPreferredNetwork(activity));
		spinnerNetwork.setOnItemSelectedListener(new NetworkOnItemSelectedListener());

		createMenuButtonListener(R.id.buttonBack, MenuID.MAIN, null);
	}
	
	@Override
	public void onBackButton() {
		activity.setMenu(new MainMenu(activity));
	}
}
