// File: ButtonList.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import java.util.ArrayList;

//Class: ButtonList
//Description: An ArrayList that holds 'tags' and 'rotations' of UI buttons 
public class ButtonList
{
	private int rotation = 0;
	private ArrayList<String> tags = new ArrayList<String>();
	
	public String  getTag(int i) { return tags.get(i); }
	public int     getSize()     { return tags.size(); }
	public int     getRotation() { return rotation; }
	public boolean getRotate()   { return (rotation == 0 ? false : true); }
	
	public ButtonList(boolean rotate) { if (rotate) rotation = 1; }
	public ButtonList(int rotation)   { this.rotation = rotation; }
	
	public void addButton(String tag) { tags.add(tag); }
	
	// Move every button forward in the list
	// The last button becomes the first
	public void forward()
	{
		if (getSize() <= 1)
			return;
		
		String tag = tags.set(getSize() - 1, tags.get(0));
		
		for (int i = 2; i <= getSize(); i++)
			tag = tags.set(getSize() - i, tag);
	}
	
	// Move every button backward in the list
	// The first button becomes the last
	public void backward()
	{
		if (getSize() <= 1)
			return;
		
		String tag = tags.set(0, tags.get(getSize() - 1));
		
		for (int i = 1; i < getSize(); i++)
			tag = tags.set(i, tag);
	}
	
	// Rotate forward through the four states
	public void rotate()
	{
		if (rotation == 0)
			return;
		
		if (rotation < 4)
			rotation++;
		else
			rotation = 1;
	}
	
	// Return a formatted image name
	public String getImage(int i)
	{
		if (rotation == 0 || tags.get(i).contains("arrow") || tags.get(i).contains("rotate"))
			return tags.get(i);
		
		return tags.get(i) + "_r" + rotation;
	}
}