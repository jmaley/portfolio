// File: ParkManager.java
// Author: Joe Maley, Jacob Harrelson

package cpsc372.dinosaur_tycoon;

import java.util.Random;

import android.widget.TextView;

//Class: ParkManager
//Description: Provides logic for money-handling
public class ParkManager
{
	private EntityManager entityManager;
	private GamePlayActivity game;
	
	private long delta;
	
	private double parkValue;

	private int ticketPrice  = 5;
	private int visitorCount = 0;
	private int playerMoney  = 1000;
	
	public double getParkValue()    { return parkValue; }
	public int    getTicketPrice()  { return ticketPrice; }
	public int    getVisitorCount() { return visitorCount; }
	public int    getPlayerMoney()  { return playerMoney; }
	
	public ParkManager(GamePlayActivity game, EntityManager entityManager)
	{
		this.game = game;
		this.entityManager = entityManager;
	}
	
	// Update the ticket price and player money on the UI
	public void updateDisplay()
	{	
		// Clamp the player money on to the UI
		if (playerMoney > 999999)
			playerMoney = 999999;
		
		TextView tv = (TextView) game.findViewById(R.id.Money);
		tv.setText("$" + playerMoney);
		
		tv = (TextView) game.findViewById(R.id.Ticket);
		tv.setText("Ticket: $" + ticketPrice);
		
		// Have the button colors reflect the new change in money
		game.getUIManager().updateUIColors(playerMoney);
	}
	
	// Clamps the ticket price between 0 and 99
	public void setTicketPrice(int price) 
	{
		if (price < 0)
			ticketPrice = 0;
		else if (price > 99)
			ticketPrice = 99;
		else
			ticketPrice = price;
		
		updateDisplay();
	}
	
	// Refunds half of the entity value
	public void deleteEntity(String image)
	{
		int entityCount = entityManager.getEntityCountByImage(image);
		int entityValue = entityManager.getEntityValue(image);
		
		playerMoney += entityManager.getEntityValue(image) / 2;
		
		// Add entity value back to park value
		if (entityCount < 2)
			parkValue -= entityValue;
		else if (entityCount <= 4)
			parkValue -= (int) (entityValue * .75);
		else if (entityCount <= 7)
			parkValue -= (int) (entityValue * .50);
		else
			parkValue -= (int) (entityValue * .25);
	}
	
	// Checks if the player can afford a given entity
	public boolean canAfford(String image)
	{
		int entityValue = entityManager.getEntityValue(image);
		
		return playerMoney >= entityValue;
	}
	
	// Attempts to purchase an entity
	// Returns true if purchased, false otherwise
	public boolean purchaseEntity(String image)
	{
		int entityValue = entityManager.getEntityValue(image);
		int entityCount = entityManager.getEntityCountByImage(image);
		
		if (canAfford(image))
		{
			playerMoney -= entityValue;
			
			updateDisplay();
			
			if (image.contains("fence"))
				return true;	// don't add fence value to overall park value
			
			if (entityCount < 2)
				parkValue += entityValue;
			else if (entityCount <= 4)
				parkValue += (int) (entityValue * .75);
			else if (entityCount <= 7)
				parkValue += (int) (entityValue * .50);
			else
				parkValue += (int) (entityValue * .25);
			
			return true;
		}
		
		return false;
	}
	
	public void updatePark(long delta)
	{	
		this.delta += delta;
		int numZeros = 0;
		int num = (int)parkValue;
		
		// Called 15 every seconds
		if (this.delta > 15000)
		{
			this.delta = 0;

			while (num > 10)
			{
				num = num / 10;
				numZeros++;
			}
			Random random = new Random(System.currentTimeMillis());
			
			double ratio = (ticketPrice * Math.pow(10, numZeros)) / parkValue;
			
			// calculate park visitors based on ticketprice / parkvalue ratio
			if (parkValue < 10000)
			{
				if (ratio >= .45 && ratio <= .55) // Best ratio
					visitorCount = random.nextInt(50) + 80;
				else if ((ratio < .25) || (ratio > .85)) // Ticket way too low or high
					visitorCount = random.nextInt(1) + 5;
				else if (ratio < .45)	// Ticket price too low
					visitorCount = random.nextInt(10) + 20;
				else if (ratio > .55)  // Ticket price too high
					visitorCount = random.nextInt(5) + 10;
			}
			else if (parkValue < 50000)
			{
				if (ratio >= .45 && ratio <= .55)
					visitorCount = random.nextInt(80) + 130;
				else if ((ratio < .25) || (ratio > .85))
					visitorCount = random.nextInt(5) + 10;
				else if (ratio < .45)
					visitorCount = random.nextInt(30) + 70;
				else if (ratio > .55)
					visitorCount = random.nextInt(10) + 20;
			}
			else
			{
				if (ratio >= .45 && ratio <= .55)
					visitorCount = random.nextInt(110) + 200;
				else if (ratio < .25)
					visitorCount = random.nextInt(15) + 30;
				else if (ratio < .45)
					visitorCount = random.nextInt(40) + 80;
				else if (ratio > .85)
					visitorCount = random.nextInt(15) + 20;
				else if (ratio > .55)
					visitorCount = random.nextInt(20) + 40;
			}
			
			playerMoney += visitorCount * ticketPrice + 10;
		}
		
		updateDisplay();
	}	
}
