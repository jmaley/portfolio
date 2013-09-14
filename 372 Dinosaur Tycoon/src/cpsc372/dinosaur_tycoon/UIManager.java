// File: UIManager.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.TextView;

//Class: UIManager
//Description: Provides management for buttons on the action bar
public class UIManager
{	
	private ButtonList buttonsDefault;
	private ButtonList buttonsTerrain;
	private ButtonList buttonsDinosaurs;
	private ButtonList buttonsStructures;
	private ButtonList buttonsFoliage;
	
	public ButtonList getButtonsDefault()    { return buttonsDefault; }
	public ButtonList getButtonsTerrain()    { return buttonsTerrain; }
	public ButtonList getButtonsDinosaurs()  { return buttonsDinosaurs; }
	public ButtonList getButtonsStructures() { return buttonsStructures; }
	public ButtonList getButtonsFoliage()    { return buttonsFoliage; }
	
	private GamePlayActivity game;
	
	public UIManager(GamePlayActivity game)
	{
		this.game = game;
		
		buttonsDefault    = new ButtonList(false);
		buttonsTerrain    = new ButtonList(false);
		buttonsDinosaurs  = new ButtonList(false);
		buttonsStructures = new ButtonList(true);
		buttonsFoliage    = new ButtonList(false);
		
		initButtonLists();
	}
	
	// Creates an action-bar with left and right arrows, and a 'rotate' button if neccessary
	public void setUIScrolling(ButtonList buttonList)
	{	
		ButtonList buttonListTemp = new ButtonList(buttonList.getRotation());
		
		buttonListTemp.addButton("button_arrow_left");
		
		for (int i = 0; i < (buttonList.getRotate() ? 4 : 5); i++)
			if (buttonList.getSize() > i)
				buttonListTemp.addButton(buttonList.getTag(i));
		
		buttonListTemp.addButton("button_arrow_right");
		if (buttonList.getRotate()) buttonListTemp.addButton("button_rotate");
		
		this.setUI(buttonListTemp);
	}
	
	// Creates an action bar from the given button list
	public void setUI(ButtonList buttonList)
	{
		ImageButton[] imageButton = new ImageButton[7];
		imageButton[0] = (ImageButton) game.findViewById(R.id.imageButton7);
		imageButton[1] = (ImageButton) game.findViewById(R.id.imageButton6);
		imageButton[2] = (ImageButton) game.findViewById(R.id.imageButton5);
		imageButton[3] = (ImageButton) game.findViewById(R.id.imageButton4);
		imageButton[4] = (ImageButton) game.findViewById(R.id.imageButton3);
		imageButton[5] = (ImageButton) game.findViewById(R.id.imageButton2);
		imageButton[6] = (ImageButton) game.findViewById(R.id.imageButton1);
		
		TextView[] textView = new TextView[5];
		textView[0] = (TextView) game.findViewById(R.id.Price6);
		textView[1] = (TextView) game.findViewById(R.id.Price5);
		textView[2] = (TextView) game.findViewById(R.id.Price4);
		textView[3] = (TextView) game.findViewById(R.id.Price3);
		textView[4] = (TextView) game.findViewById(R.id.Price2);

		for (int i = 0; i < 7; i++)
		{
			if (buttonList.getSize() > i)
			{
				int k = i - 1;
				if (k >= 0 && k < 5)
				{
					int value = game.getEntityManager().getEntityValue(buttonList.getImage(i));
					if (value > 0)
						textView[k].setText(Integer.toString(value));
					else
						textView[k].setText(" ");
				}
				
				imageButton[i].setImageResource(game.getResources().getIdentifier(buttonList.getImage(i), "drawable", "cpsc372.dinosaur_tycoon"));
				imageButton[i].setTag(buttonList.getTag(i));
			}
			else
			{
				imageButton[i].setImageResource(R.drawable.button_blank);
				imageButton[i].setTag("button_blank");
			}
		}
	}
	
	// Updates a button to 'red' if non-affordable
	public void updateUIColors(int money)
	{
		ImageButton[] imageButton = new ImageButton[7];
		imageButton[0] = (ImageButton) game.findViewById(R.id.imageButton7);
		imageButton[1] = (ImageButton) game.findViewById(R.id.imageButton6);
		imageButton[2] = (ImageButton) game.findViewById(R.id.imageButton5);
		imageButton[3] = (ImageButton) game.findViewById(R.id.imageButton4);
		imageButton[4] = (ImageButton) game.findViewById(R.id.imageButton3);
		imageButton[5] = (ImageButton) game.findViewById(R.id.imageButton2);
		imageButton[6] = (ImageButton) game.findViewById(R.id.imageButton1);
		
		for (int i = 0; i < 7; i++)
			{
			if (money < game.getEntityManager().getEntityValue(imageButton[i].getTag().toString()))
				imageButton[i].setColorFilter(Color.parseColor("#96FF0000"));
			else
				imageButton[i].setColorFilter(Color.parseColor("#00000000"));	
		}
	}
	
	// Given a state, return an appropriate button list
	public ButtonList getButtonListFromState(String state)
	{
		ButtonList buttonList = buttonsDefault;
		
		if (state.equals("terrain"))
			buttonList = buttonsTerrain;
		else if (state.equals("dinosaur"))
			buttonList = buttonsDinosaurs;
		else if (state.equals("structures"))
			buttonList = buttonsStructures;
		else if (state.equals("foliage"))
			buttonList = buttonsFoliage;
		
		return buttonList;
	}
	
	private void initButtonLists()
	{
		initDefaultButtons();
		initTerrainButtons();
		initDinosaurButtons();
		initStructureButtons();
		initFoliageButtons();
	}
	
	private void initDefaultButtons()
	{
		buttonsDefault.addButton("button_terrain");
		buttonsDefault.addButton("button_dinosaur");
		buttonsDefault.addButton("button_structures");
		buttonsDefault.addButton("button_foliage");
		buttonsDefault.addButton("button_delete");
		buttonsDefault.addButton("button_blank");
		buttonsDefault.addButton("button_menu");
	}
	
	private void initTerrainButtons()
	{
		buttonsTerrain.addButton("button_terrain_concrete");
		buttonsTerrain.addButton("button_terrain_dirt");
		buttonsTerrain.addButton("button_terrain_grass");
		buttonsTerrain.addButton("button_terrain_stone");
		buttonsTerrain.addButton("button_terrain_sand");
		buttonsTerrain.addButton("button_terrain_water");
		buttonsTerrain.addButton("button_terrain_snow");
		buttonsTerrain.addButton("button_terrain_ice");
	}
	
	private void initDinosaurButtons()
	{
		buttonsDinosaurs.addButton("button_dinosaur_raptor");
		buttonsDinosaurs.addButton("button_dinosaur_trex");
		buttonsDinosaurs.addButton("button_dinosaur_spinosaurus");
		buttonsDinosaurs.addButton("button_dinosaur_stegosaurus");
		buttonsDinosaurs.addButton("button_dinosaur_triceratops");
	}
	
	private void initStructureButtons()
	{
		buttonsStructures.addButton("button_structures_fence1");
		buttonsStructures.addButton("button_structures_fence2");
		buttonsStructures.addButton("button_structures_fence3");
		buttonsStructures.addButton("button_structures_bathroom1");
		buttonsStructures.addButton("button_structures_vendor1");
	}
	
	private void initFoliageButtons()
	{
		buttonsFoliage.addButton("button_foliage_tree1");
		buttonsFoliage.addButton("button_foliage_tree2");
		buttonsFoliage.addButton("button_foliage_tree3");
		buttonsFoliage.addButton("button_foliage_bush1");
		buttonsFoliage.addButton("button_foliage_bush2");
		buttonsFoliage.addButton("button_foliage_bush3");
	}
}
