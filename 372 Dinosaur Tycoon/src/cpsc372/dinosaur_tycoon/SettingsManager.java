// File: SettingsManager.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import android.content.Context;

//Class: ParkManager
//Description: Provides logic and saving for the settings menu
public class SettingsManager implements Serializable
{
	private static final long serialVersionUID = 978270229241283440L;
	
	private boolean violence;
    private boolean audio;
    Context context;

    public boolean getViolence() { return !violence; }
    public boolean getAudio()    { return !audio; }
   
    public void setViolence(boolean settingViolence) { violence = settingViolence; }
    public void setAudio(boolean settingAudio)       { audio = settingAudio; }
   
    public void setContext(Context context) { this.context = context; }
   
    private static SettingsManager instance = null;
    
    // Private constructor
    private SettingsManager()
    {
    	try
    	{
	    	FileInputStream fileInput = new FileInputStream("settings.ser");
	    	ObjectInputStream objectInput = new ObjectInputStream(fileInput);
	    	
	    	violence = objectInput.readBoolean();
	        audio = objectInput.readBoolean();
	        
	        objectInput.close();
	        fileInput.close();
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    // Singleton instance
    public static SettingsManager getInstance()
    {
        if (instance == null)
            instance = new SettingsManager();
       
        return instance;
    }
    
    public void saveSettings()
    {
    	try {
	    	FileOutputStream fileOutput = new FileOutputStream("settings.ser");
	    	ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
	    	
	    	objectOutput.writeBoolean(violence);
	    	objectOutput.writeBoolean(audio);
	        
	    	objectOutput.close();
	    	fileOutput.close();
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}