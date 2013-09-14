package edu.clemson.cs.cybertiger;

import java.util.prefs.Preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * A singleton class which provides accessors and mutators for the client's 
 * preferences.
 * @author Joe Maley
 */
public class PreferenceManager {
	private static PreferenceManager instance = null;
	
	private int preferredGPS;
	private int preferredNetwork;
	
	public void setPreferredGPS(int i, Activity activity) { 
		preferredGPS = i; 
	
		SharedPreferences sharedPreferences = activity.getPreferences(0);
		sharedPreferences.edit().putInt("preferredGPS", preferredGPS).commit();
	}
	
	public void setPreferredNetwork(int i, Activity activity) { 
		preferredNetwork = i; 
	
		SharedPreferences sharedPreferences = activity.getSharedPreferences("cybertiger_prefs", 0);
		sharedPreferences.edit().putInt("preferredNetwork", preferredNetwork).commit();
	}
	
	public int getPreferredGPS(Activity activity) { 
		SharedPreferences sharedPreferences = activity.getPreferences(0);
		
		return sharedPreferences.getInt("preferredGPS", preferredGPS);
	}
	
	public int getPreferredNetwork(Activity activity) { 
		SharedPreferences sharedPreferences = activity.getSharedPreferences("cybertiger_prefs", 0);
		
		return sharedPreferences.getInt("preferredNetwork", preferredNetwork);
	}
	
	/**
	 * Private for single-instantiation.
	 */
	private PreferenceManager() {
		preferredGPS = 0;
		preferredNetwork = 0;
	}
	
	/**
	 * Creates a new instance of the class if this is the first time being 
	 * called. Otherwise, it is returns the previously created instance.
	 * @return An instance of the preference manager
	 */
	public static PreferenceManager getInstance() {
		
		if (instance == null) {
			instance = new PreferenceManager();
		}
		
		return instance;
	}
}
