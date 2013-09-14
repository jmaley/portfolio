package edu.clemson.cs.cybertiger.test;

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
import edu.clemson.cs.cybertiger.protobuf.CyberTigerBuf.StreamingTestParams;
import edu.clemson.cs.cybertiger.protobuf.CyberTigerBuf.TestResults;

/**
 * Downloads a series of packets and reports the network statistics.
 * @author Adam Hodges
 * @author Joe Maley
 */
public class StreamingTest extends GenericTest {
	private int MAX_ARRAY_SIZE;
	private int chunkSize;
	private int delay;
	private int waitNextPacket;
	
	private int LOST = -1;
	private int ARRIVED = 1;
	
	private double MILDmetric = 0.0;
	private double MBLmetric = 0.0;
	private double avgJitter = 0.0;
	private DatagramSocket udpSocket = null;
	private ByteBuffer socketBuffer;
	private byte[] buffer= null;
	private DatagramPacket messagePacket= null;

	private int packet_arrivals[];
	private double jitter[];
	private double arrivalTime[];

	private int numberLost;
	private int numberReceived;

	private double firstArrivalTime;
	private double lastArrivalTime;

	private int lastRcvdSnum;
	private int sNum;
	private double thisJitter;

	private double F1;
	private double F2;
	private double finalLossRate;
	private int total_pkts;
	private int lost_pkts;
	private int numberLossBlocks;

	private int MBL[];
	private int MILD[];
	private int mbl_counter;
	private int mild_counter;

	private double MILD_numerator;
	private double MILD_denominator;
	private double MBL_numerator;
	private double MBL_denominator;

	private double totalJitter;

	public StreamingTest(MainActivity activity, TestMenu testMenu,
			int testID, int arraySize, int byteSize, int pdelay, int wait) {
		super(activity, testMenu, testID);
		
		MAX_ARRAY_SIZE = arraySize;
		chunkSize = byteSize;
		delay = pdelay;
		waitNextPacket = wait;
	}
	
	@Override
	public void begin() {
		super.begin();
		
		if (!connectToServer()) return;
		
		if (!handShake()) return;
		
		testMenu.setStatusText("Reading packets...");
		if (!collectPackets()) return;

		calculate();
		
		TestResults.Builder resultsBuilder = TestResults.newBuilder();
		resultsBuilder.setMbl(MBLmetric);
		resultsBuilder.setMild(MILDmetric);
		resultsBuilder.setAverageJitter(avgJitter);
		resultsBuilder.setPacketObs(numberReceived);
		resultsBuilder.setBlc(numberLossBlocks);
		resultsBuilder.setLossPercent(finalLossRate);
		resultsBuilder.setDuration(lastArrivalTime-firstArrivalTime);
		resultsBuilder.setMessage("MBL : " + MBLmetric + " MILD : " + MILDmetric + " Loss %: " + finalLossRate *100 + " Jitter: " + avgJitter);
		TestResults testResults = resultsBuilder.build();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			testResults.writeDelimitedTo(baos);
		} catch (IOException e) {
			testMenu.createErrorDialog("Error", "Unable to send results to server.", null);
			return;
		}
		
		socketBuffer = ByteBuffer.wrap(baos.toByteArray());
		
		if (!writeToServer(socketBuffer, "Unable to send results to server.")) return;
		
		testMenu.createResultsDialog("Results", testResults.getMessage(), null);
		
		socketBuffer.clear();
		
		if (udpSocket != null)
			udpSocket.close();
		
		closeConnection();
	}
	
	/**
	 * Downloads a series of packets from the server.
	 * @return true if successful, otherwise false
	 */
	private boolean collectPackets() {	
		StreamingTestParams.Builder builder = StreamingTestParams.newBuilder();
		builder.setChunkSize(chunkSize);
		builder.setDelayMs(delay);
		builder.setArraySize(MAX_ARRAY_SIZE);
		StreamingTestParams streamingParams = builder.build();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			streamingParams.writeDelimitedTo(baos);
		} catch (IOException e) {
			testMenu.createErrorDialog("Error", "Could not collect packets.", null);
			return false;
		}
		
		socketBuffer = ByteBuffer.wrap(baos.toByteArray());

		if (!writeToServer(socketBuffer, "Unable to send test parameters.")) return false;

		byte[] pingMsg=new byte[1];
		
		socketBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		try {
			
			timeout.start("Could not open UDP socket.");
			while (udpSocket == null && running) {
				udpSocket = new DatagramSocket(port);
			}
			if (timeout != null && timeout.stop()) return false;
			
			udpSocket.setReuseAddress(true);
		} catch (SocketException e) {
			testMenu.createErrorDialog("Error", "Could not open UDP socket.", null);
			return false;
		}
		
		DatagramPacket pingPacket = new DatagramPacket(pingMsg, 1);
		pingPacket.setSocketAddress(new InetSocketAddress(cyberTigerAddress, port));
		try {
			udpSocket.send(pingPacket);
		} catch (IOException e2) {
			testMenu.createErrorDialog("Error", "Could not send ping packet.", null);
			return false;
		}
		
		buffer = new byte[chunkSize];
		messagePacket = new DatagramPacket(buffer, chunkSize);
		
		packet_arrivals = new int[MAX_ARRAY_SIZE + 1];
		jitter = new double[MAX_ARRAY_SIZE + 1];
		arrivalTime = new double[MAX_ARRAY_SIZE + 1];
		int i;

		numberLost = 0;
		numberReceived = 0;

		for (i = 0; i < MAX_ARRAY_SIZE + 1; i++) {
			packet_arrivals[i] = -1;
			jitter[i] = 0.0;
			arrivalTime[i] = 0.0;
		}

		firstArrivalTime = -1;
		lastArrivalTime = -1;

		lastRcvdSnum = 0;
		thisJitter = 0.0;

		try {
			udpSocket.setSoTimeout(waitNextPacket);
		} catch (SocketException e1) {
			testMenu.createErrorDialog("Error", "Could set UDP timeout.", null);
			return false;
		}

		while(running) {

			try {
				udpSocket.receive(messagePacket);
			} catch (IOException e) {
				
				if (readFromServer(BUFFER_SIZE, "Unable to read port.") != null)
					return true;
				
				testMenu.createErrorDialog("Error", "Could read UDP packets.", null);
				return false;
			}
				
			double curTime = System.nanoTime() / 1000000; 
			sNum = (int) messagePacket.getData()[3] & 0xff;
			sNum += ((int) messagePacket.getData()[2] & 0xff) * 256;
			sNum += ((int) messagePacket.getData()[1]& 0xff) * 256 * 256;
			sNum += ((int) messagePacket.getData()[0]& 0xff) * 256 * 256 * 256;

			Log.w("mine", "snum:" + sNum);
			
			if(firstArrivalTime == -1) {
				firstArrivalTime = curTime;
				lastArrivalTime = curTime;
			}
			
			numberReceived++;
			if(sNum <= lastRcvdSnum) {
				
				if(packet_arrivals[sNum] == ARRIVED) {
				}
				else{
					packet_arrivals[sNum] = ARRIVED;
					arrivalTime[sNum]= curTime;
					thisJitter=curTime-lastArrivalTime;
					lastArrivalTime = curTime;
					jitter[sNum] = thisJitter;
				}
			}
			else{
				while(true) {
					lastRcvdSnum++;
					
					if(lastRcvdSnum == sNum ) {
						packet_arrivals[lastRcvdSnum]=ARRIVED;
						arrivalTime[lastRcvdSnum] = curTime;
						thisJitter = curTime - lastArrivalTime;
						jitter[lastRcvdSnum] = thisJitter;
						lastArrivalTime= curTime;
						
						break;
					}
					else
					{
						packet_arrivals[lastRcvdSnum] = LOST;
					}
				}
			}
		}
		
		return running;
	}
	
	/**
	 * Calculates network statistics from the download.
	 */
	private void calculate() {
		F1 = 0.0;
		F2 = 0.0;
		finalLossRate = 0.0;
		total_pkts = 0;
		lost_pkts = 0;
		numberLossBlocks = 0;

		MBL = new int[MAX_ARRAY_SIZE+1];
		MILD = new int[MAX_ARRAY_SIZE+1];
		mbl_counter = 0;
		mild_counter = 0;

		MILD_numerator = 0.0;
		MILD_denominator = 0.0;
		MBL_numerator = 0.0;
		MBL_denominator = 0.0;

		if(socketBuffer != null) {
			socketBuffer.clear();
		}
		
		total_pkts = MAX_ARRAY_SIZE;

		int i;
		for(i = 1; i < MAX_ARRAY_SIZE + 1;i++) {
			
			if(packet_arrivals[i] == LOST) {
				numberLost++;
			}
		}

		lost_pkts = numberLost;

		F1 = (double)total_pkts;
		F2 = (double)lost_pkts;
		
		if(total_pkts != 0) {
			finalLossRate = F2/F1;
		}
		
		totalJitter = 0.0;

		int counter = 0;
		for(i = 0; i < MAX_ARRAY_SIZE + 1;i++) {
			
			if (jitter[i] > 0.0) {
				totalJitter = totalJitter + jitter[i];
				counter++;
			}
		}
		
		if (counter > 0) {
			avgJitter = totalJitter / counter;
		}

		for (i = 1; i < MAX_ARRAY_SIZE+1; i++) {
			MBL[i] = 0;
			MILD[i] = 0;
		}

		for (i = 1; i < MAX_ARRAY_SIZE + 1; i++) {
			
			if (packet_arrivals[i] == ARRIVED) {
				mild_counter++;
				
				if (mbl_counter != 0) {
					MBL[mbl_counter] = MBL[mbl_counter] + 1;
					numberLossBlocks++;
				}
				
				mbl_counter = 0;
			} else {
				mbl_counter++;
				if (mild_counter != 0) {
					MILD[mild_counter] = MILD[mild_counter] + 1;
				}
				mild_counter = 0;
			}
		}

		if (mbl_counter != 0) {
			MBL[mbl_counter] = MBL[mbl_counter] + 1;
		}
		
		if (mild_counter != 0) {
			MILD[mild_counter] = MILD[mild_counter] + 1;
		}

		for (i = 1; i < MAX_ARRAY_SIZE+1; i++) {
			MILD_numerator = MILD_numerator + (i * MILD[i]);
			MILD_denominator = MILD_denominator + MILD[i];
			MBL_numerator = MBL_numerator + (i * MBL[i]);
			MBL_denominator = MBL_denominator + MBL[i];
		}

		if (MILD_denominator > 0) {
			MILDmetric = (double) (MILD_numerator) / MILD_denominator;
		}

		if (MBL_denominator > 0) {
			MBLmetric = (double) (MBL_numerator) / MBL_denominator;
		}
	}
}
