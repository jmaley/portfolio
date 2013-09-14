// File: SettingsMenuActivity.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

//Class: SettingsMenuActivity
//Description: Binds listeners to buttons in the settings menu
public class SettingsMenuActivity extends MenuActivity implements OnClickListener
{
    private ImageButton imageButtonAudio;
    private ImageButton imageButtonViolence;
   
    private SettingsManager settingsManager;
   
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);
       
        settingsManager = SettingsManager.getInstance();
       
        createMenuButtonListenerClass(R.id.imageButtonSettingsMenuBack, null);
       
        imageButtonAudio = (ImageButton) findViewById(R.id.imageButtonSettingsMenuAudio);
        imageButtonAudio.setOnClickListener(this);
       
        imageButtonViolence = (ImageButton) findViewById(R.id.imageButtonSettingsMenuViolence);
        imageButtonViolence.setOnClickListener(this);

        toggleAudio();
        toggleViolence();
    }

    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imageButtonSettingsMenuAudio:
                settingsManager.setAudio(!settingsManager.getAudio());
                toggleAudio();
                break;
               
            case R.id.imageButtonSettingsMenuViolence:
                settingsManager.setViolence(!settingsManager.getViolence());
                toggleViolence();
                break;
        }
        settingsManager.saveSettings();
    }
   
    private void toggleAudio()
    {
        if (settingsManager.getAudio())
            imageButtonAudio.setImageResource(R.drawable.settings_menu_off);
        else
            imageButtonAudio.setImageResource(R.drawable.settings_menu_on);
    }
   
    private void toggleViolence()
    {
        if (settingsManager.getViolence())
            imageButtonViolence.setImageResource(R.drawable.settings_menu_off);
        else
            imageButtonViolence.setImageResource(R.drawable.settings_menu_on);
    }
}