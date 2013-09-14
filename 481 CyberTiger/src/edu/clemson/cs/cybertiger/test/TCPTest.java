package edu.clemson.cs.cybertiger.test;

import java.io.IOException;
import java.nio.ByteBuffer;

import android.util.Log;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.menu.TestMenu;

/**
 * Reads and receives TCP packets.
 * @author Adam Hodges
 * @author Joe Maley
 */
public class TCPTest extends GenericTest {

	public TCPTest(MainActivity activity, TestMenu testMenu, int testID) {
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
		
		/*
		 * This test sends packets back over a fixed length of time,
		 * which needs to be received from the server.
		 */
		ByteBuffer lengthBuffer = readFromServer(2, "Unable to read length.");
		if (lengthBuffer == null) return;
		
		lengthBuffer.rewind();
		
		double testLength = lengthBuffer.getShort();
		testLength = testLength * 1000000000;
		
		/*
		 * We also need to read a chunk size from the server.
		 */
		ByteBuffer chunkBuffer = readFromServer(4, "Unable to read chunk size.");
		if (chunkBuffer == null) return;

		chunkBuffer.rewind();
		
		int chunkSize = chunkBuffer.getInt();

		int bytesRead = chunkSize;
		ByteBuffer testBuffer = ByteBuffer.allocate(chunkSize);
		
		testMenu.setStatusText("Reading Packets...");
		
		timeout.start("Unable to read TCP packets.");
		while (running) {
			
			/*
			 * Read until we receive the end message 0x01.
			 */
			if (bytesRead > 0 && testBuffer.get(bytesRead-1) == (byte)0x01) {
				break;
			}
			
			testBuffer.rewind();
			
			try {
				bytesRead = client.read(testBuffer);
			} catch (IOException e) {
				if (running) testMenu.createErrorDialog("Error", "Unable to read TCP packets.", null);
				return;
			}
		}
		if (timeout != null && timeout.stop()) return;
	
		testMenu.setStatusText("Sending Packets...");
		
		double startTime = System.nanoTime();
		
		/*
		 * Send for the length of time specified from the server.
		 */
		while((System.nanoTime() - startTime) < testLength && running) {
			
			if (!writeToServer(testBuffer, "Unable to send TCP packets.")) return;

			testBuffer.rewind();
		}
		
		byte[] endMsg = new byte[1];
		endMsg[0] = (byte)0x01;
		ByteBuffer endBuffer = ByteBuffer.wrap(endMsg);
		
		if (!writeToServer(endBuffer, "Unable to send TCP end message.")) return;
		
		if (!parseResults()) return;
		
		closeConnection();
	}
}
