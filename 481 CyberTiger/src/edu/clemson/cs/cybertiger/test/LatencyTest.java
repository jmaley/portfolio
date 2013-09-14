package edu.clemson.cs.cybertiger.test;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.menu.TestMenu;

/**
 * Pings the server.
 * 
 * @author Adam Hodges
 * @author Joe Maley
 */
public class LatencyTest extends GenericTest {
	
	public LatencyTest(MainActivity activity, TestMenu testMenu, int testID) {
		super(activity, testMenu, testID);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		if (!connectToServer()) return;
		
		if (!handShake()) return;
		
		if (!setNoDelay(true)) return;
		
		if (!ping()) return;
		if (!ping()) return;
		if (!ping()) return;
		
		if (!setNoDelay(false)) return;
		
		if (!parseResults()) return;

		closeConnection();
	}
}
