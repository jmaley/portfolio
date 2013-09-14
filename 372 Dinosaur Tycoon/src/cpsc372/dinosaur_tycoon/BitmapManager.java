// File: BitMapManager.java
// Author: Joe Maley

package cpsc372.dinosaur_tycoon;

import java.util.ArrayList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//Class: BitMapManager
//Description: A 'Flyweight' manager to load and store bitmap objects from the resources 
public class BitmapManager
{	
	// Class: IndexedBitmap
	// Description: An inner-class to bind bitmaps with an index
	private class IndexedBitmap
	{
		private int index;
		private Bitmap bitmap;
		
		public IndexedBitmap(int index, Resources resources)
		{
			this.index = index;
			bitmap = BitmapFactory.decodeResource(resources, index);
		}
		
		public Bitmap getBitmap() { return bitmap; }
		public int getIndex() { return index; }
	}

	private Resources resources;
	private ArrayList<IndexedBitmap> list;
	
	public BitmapManager(Resources resources)
	{
		this.resources = resources;
		list = new ArrayList<IndexedBitmap>();
	}
	
	public Bitmap getBitmap(int index)
	{
		Bitmap bitmap = null;
		
		// Return an existing bitmap
		for (int i = 0; i < list.size(); i++)
			if(list.get(i).getIndex() == index)
				bitmap = list.get(i).getBitmap();
		
		// Otherwise, Create a new bitmap
		if (bitmap == null)
		{
			IndexedBitmap ibmp = new IndexedBitmap(index, resources);
			list.add(ibmp);
			bitmap = ibmp.getBitmap();
		}
		
		return bitmap;
	}
}