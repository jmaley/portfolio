package edu.clemson.cs.cybertiger.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.util.Log;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.PreferenceManager;
import edu.clemson.cs.cybertiger.menu.TestMenu;
import edu.clemson.cs.cybertiger.protobuf.CyberTigerBuf.UDPBandwidthDownResults;
import edu.clemson.cs.cybertiger.protobuf.CyberTigerBuf.UDPBandwidthInit;

/**
 * Receives and sends UDP packets.
 * @author Adam Hodges
 * @author Joe Maley
 */
public class UDPTest extends GenericTest {

	public UDPTest(MainActivity activity, TestMenu testMenu, int testID) {
		super(activity, testMenu, testID);
	}
	
	@Override
	public void begin() {
		super.begin();
		
		if (!connectToServer()) return;
		
		if (!handShake()) return;

		DatagramSocket udpSocket = null;
		
		/*
		 * Open a UDP socket, although we read the stop signal from the client TCP socket.
		 */
		try {
			timeout.start("Could not open UDP socket.");
			while (udpSocket == null && running) {
				udpSocket = new DatagramSocket(port);
			}
			if (timeout != null && timeout.stop()) return;
		} catch (SocketException e) {
			testMenu.createErrorDialog("Error", "Could not open UDP socket.", null);
			return;
		}
		
		testMenu.setStatusText("Pinging...");
		
		byte[] pingMsg = new byte[1];
		DatagramPacket pingPacket = new DatagramPacket(pingMsg, 1);
		pingPacket.setSocketAddress(new InetSocketAddress(cyberTigerAddress, port));

		try {
			udpSocket.send(pingPacket);
			if (!running) return;
		
			udpSocket.receive(pingPacket);
			udpSocket.send(pingPacket);	
			if (!running) return;
	
			udpSocket.receive(pingPacket);
			udpSocket.send(pingPacket);
			if (!running) return;
			
			udpSocket.receive(pingPacket);
			udpSocket.send(pingPacket);
			if (!running) return;
			
			udpSocket.receive(pingPacket);
			udpSocket.send(pingPacket);
			if (!running) return;
			
		} catch (IOException e) {
			testMenu.createErrorDialog("Error", "Unable to transfer ping packet.", null);
			return;
		}
		
		testMenu.setStatusText("Reading config...");
		ByteBuffer socketBuffer = readFromServer(BUFFER_SIZE, "Unable to read test parameters.");
		if (socketBuffer == null) return;
		
		socketBuffer.flip();
		
		byte[] initBuf = new byte[socketBuffer.remaining()];
		socketBuffer.get(initBuf);
		ByteArrayInputStream initStream = new ByteArrayInputStream(initBuf);
		UDPBandwidthInit udpBandwidthInit;
		
		try {
			udpBandwidthInit = UDPBandwidthInit.parseDelimitedFrom(initStream);
		} catch (IOException e) {
			testMenu.createErrorDialog("Error", "Unable to set test specifications.", null);
			return;
		}
		
		socketBuffer.clear();
		
		int chunkSize = udpBandwidthInit.getUdpChunkSize();
		double testLength = udpBandwidthInit.getTestLengthSec();
		testLength = testLength * 1000000000;
		int messageSize = udpBandwidthInit.getUdpMessageSize();
		int udpDelay = udpBandwidthInit.getUdpDelayMs();
		int totalBytesRead = 0;

		byte[] buffer = new byte[messageSize];
		DatagramPacket messagePacket = new DatagramPacket(buffer, messageSize);
		
		double startTime = System.nanoTime();
		
		testMenu.setStatusText("Reading packets...");
		timeout.start("Unable to read UDP packets.");
		while((System.nanoTime() - startTime) < testLength && running) {
			
			try {
				udpSocket.receive(messagePacket);
			} catch (IOException e) {
				if (running) testMenu.createErrorDialog("Error", "Unable to receive UDP packets.", null);
				return;
			}
			
			totalBytesRead += messagePacket.getLength();
		}
		if (timeout != null && timeout.stop()) return;
		
		double downloadLength = (System.nanoTime() - startTime) / 1000000;
		
		UDPBandwidthDownResults udpBandwidthDownResults;
		UDPBandwidthDownResults.Builder builder = UDPBandwidthDownResults.newBuilder();
		builder.setBytesReceived(totalBytesRead);
		builder.setTimeElapsedMs(downloadLength);
		udpBandwidthDownResults = builder.build();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			udpBandwidthDownResults.writeDelimitedTo(baos);
		} catch (IOException e1) {
			testMenu.createErrorDialog("Error", "Unable to create signal.", null);
			return;
		}
	
		socketBuffer = ByteBuffer.wrap(baos.toByteArray());

		if (!writeToServer(socketBuffer, "Unable to signal server.")) return;
		
		socketBuffer.clear();
		socketBuffer = ByteBuffer.allocate(1);
		
		testMenu.setStatusText("Sending packets...");

		timeout.start("Unable to send UDP packets.");
		while (running) {
			
			try {
				Thread.sleep(udpDelay);
			} catch (InterruptedException e) {
				testMenu.createErrorDialog("Error", "Unable to send datagram.", null);
				return;
			}

			for(int i = 0; i < chunkSize; i += messageSize) {
				
				try {
					udpSocket.send(messagePacket);
				} catch (IOException e) {
					testMenu.createErrorDialog("Error", "Unable to send datagram.", null);
					return;
				}
				
				if((i + messageSize) > chunkSize) {
					try {
						udpSocket.send(messagePacket);
					} catch (IOException e) {
						testMenu.createErrorDialog("Error", "Unable to send datagram.", null);
						return;
					}
				}
			}

			try {
			
				if(client.read(socketBuffer) > 0) {
					break;
				}
			} catch (IOException e) {
				testMenu.createErrorDialog("Error", "Unable to send datagram.", null);
				return;
			}					
		}
		if (timeout != null && timeout.stop()) return;
		
		socketBuffer.clear();
		
		if (!parseResults()) return;
		
		if (udpSocket != null)
			udpSocket.close();
		
		closeConnection();
	}
}
