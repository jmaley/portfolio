// File: MainMenuActivity.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import android.os.Bundle;

//Class: MainMenuActiity
//Description: Binds listeners to the buttons on the main menu
public class MainMenuActivity extends MenuActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        
        SettingsManager settingsManager = SettingsManager.getInstance();
        
        settingsManager.setContext(this);
        
        createMenuButtonListenerClass(R.id.imageButtonMainMenuPlay, GamePlayActivity.class);
        createMenuButtonListenerClass(R.id.imageButtonMainMenuSettings, SettingsMenuActivity.class);
        createMenuButtonListenerClass(R.id.imageButtonMainMenuExit, null);
    }
}
