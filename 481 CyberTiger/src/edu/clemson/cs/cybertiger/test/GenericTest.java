package edu.clemson.cs.cybertiger.test;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.PreferenceManager;
import edu.clemson.cs.cybertiger.menu.DiagnosticsMenu;
import edu.clemson.cs.cybertiger.menu.TestMenu;
import edu.clemson.cs.cybertiger.menu.TestMenu.Speed;
import edu.clemson.cs.cybertiger.protobuf.CyberTigerBuf.ClientDetails;
import edu.clemson.cs.cybertiger.protobuf.CyberTigerBuf.ServerAck;
import edu.clemson.cs.cybertiger.protobuf.CyberTigerBuf.TestResults;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Provides a template for test classes by implementing
 * standard networking functions and collecting diagnostic information.
 * @author Adam Hodges
 * @author Joe Maley
 */
public abstract class GenericTest extends AsyncTask<Void, Void, Void> {
	protected static final String cyberTigerAddress = "cybertiger.clemson.edu";
	protected static final int cyberTigerPort = 6202;
	
	protected static final int BUFFER_SIZE = 5000;
	protected static final int TIMEOUT_S = 20;
	protected static final int TIMEOUT_S_GPS = 10;
	
	protected MainActivity activity = null;
	protected TestMenu testMenu = null;
	protected int testID = 0;
	
	protected String macAddress = "";
	protected String make = "";
	protected String model = "";
	protected String platform = "";
	protected int version = 0;
	protected String linkType = "";
    protected String networkOperator = "";
    protected double linkSpeed = 0;
    protected int signalStrength = 0;
    protected String SSID = "";
    protected String BSSID = "";
    
    protected double latitude = 0;
    protected double longitude = 0;
    protected double altitude = 0;
    protected double accuracy = 0;
    protected int gpsMethod = 0;
    
    protected boolean running = false;
    
    private LocationManager locationManager = null;
    private GPSListener gpsListener = null;
    
    private TelephonyManager telephonyManager = null;
    private SignalStrengthListener signalStrengthListener = null;
    
    protected SocketChannel client = null;
	protected ClientDetails clientDetails = null;
	protected SocketChannelTimeout timeout = null;
	protected int port = 0;
	
	private Semaphore gpsMutex = null;
	private Semaphore signalMutex = null;
    
	/**
	 * Listens for a change in GPS. Releases the gpsMutex and closes listener when received.
	 * @author Joe Maley
	 */
    private class GPSListener implements LocationListener {
    	
    	@Override
		public void onLocationChanged(Location location) {
    		latitude = location.getLatitude();
    		longitude = location.getLongitude();
    		altitude = location.getAltitude();
    		accuracy = location.getAccuracy();
    		
    		if (locationManager != null) {
    			locationManager.removeUpdates(this);
    		}
    		
    		gpsMutex.release();
    	}

		@Override
		public void onProviderDisabled(String provider) { }

		@Override
		public void onProviderEnabled(String provider) { }

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) { }
    }
	
    /**
	 * Listens for a change in signal. Releases the signalMutex and closes listener when received.
	 * @author Adam Hodges
	 * @author Joe Maley
	 */
	private class SignalStrengthListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signal) {
			
			switch(telephonyManager.getNetworkType()) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				signalStrength = signal.getCdmaDbm();
				linkSpeed = .153;
				linkType = "1xRTT";
				break;
			case TelephonyManager.NETWORK_TYPE_CDMA:
				signalStrength = signal.getCdmaDbm();
				linkSpeed=.064;
				linkType="CDMA";
				break;
			case TelephonyManager.NETWORK_TYPE_EDGE:
				signalStrength = (signal.getGsmSignalStrength()*2) - 113;
				linkSpeed = .2368;
				linkType = "EDGE";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				signalStrength = signal.getEvdoDbm();
				linkSpeed = 2.457;
				linkType = "EVDO Rev 0";
				break;
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				signalStrength = signal.getEvdoDbm();
				linkSpeed = 3.1;
				linkType = "EVDO Rev A";
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				signalStrength = (signal.getGsmSignalStrength()*2) - 113;
				linkSpeed = .0576;
				linkType = "GPRS";
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				signalStrength = (signal.getGsmSignalStrength()*2) - 113;
				linkSpeed = 13.98;
				linkType = "HSDPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSPA:
				signalStrength = (signal.getGsmSignalStrength()*2) - 113;
				linkSpeed = 13.98;
				linkType = "HSPA";
				break;
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				signalStrength = (signal.getGsmSignalStrength()*2) - 113;
				linkSpeed = 13.98;
				linkType = "HSUPA";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				signalStrength = (signal.getGsmSignalStrength()*2) - 113;
				linkSpeed = 42;
				linkType = "UMTS/HSPA+";
				break;
			case 13:
				linkSpeed = 100;
				linkType = "LTE";
				Method[] methods = android.telephony.SignalStrength.class.getMethods();
				
				for (Method mthd : methods) {
					
					if (mthd.getName().equals("getLteRssi") || mthd.getName().equals("getLteSignalStrength") || mthd.getName().equals("getLteRsrp")) {
							
						try {
							signalStrength = (Integer) mthd.invoke(signal, new Object[]{});
							break;
						} catch (Exception e) {
							testMenu.createErrorDialog("Error", "Unable to fetch LTE signal strength.", null);
							break;
						}
					}
				}
				
				break;
			case 14:
				signalStrength = signal.getEvdoDbm();
				linkSpeed = 3.1;
				linkType = "eHRPD";
				break;
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				signalStrength = -1;
				linkSpeed = -1;
				linkType = "Unknown";
				break;	
			default:
				signalStrength = -1;
				linkSpeed = -1;
				linkType = "Not Available";
				break;
			}
			
			if (telephonyManager != null) {
				telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
    		}
			
			signalMutex.release();
		}
	}
	
	/**
	 * Starts a timer, and will stop test if expires.
	 * @author Joe Maley
	 */
	protected class SocketChannelTimeout extends Timer {
		private long time;
		private TimerTask timerTask;
		private boolean stopped;
		    
		public SocketChannelTimeout(long time) {
			this.time = time * 1000;
			this.timerTask = null;
			this.stopped = false;
		}
			
		public void start(final String s) {
				
			timerTask = new TimerTask() {
					
				@Override
				public void run() {
					stopped = true;
					testMenu.createErrorDialog("Timeout Error", s, null);
				}
			};
				
			this.schedule(timerTask, time);
		}
			
		public boolean stop() {
			if (timerTask != null) {
				timerTask.cancel();
			}
			
			return stopped;
		}
	}
    
    public GenericTest(final MainActivity activity, final TestMenu testMenu, final int testID) {
    	this.activity = activity;
    	this.testMenu = testMenu;
    	this.testID = testID;
    	
    	this.gpsMutex = new Semaphore(0);
    	this.signalMutex = new Semaphore(0);
    }
	
    @Override
    protected Void doInBackground(Void... params) {
		
    	if (running) {
			return null;
		}

		running = true;
		
		/* Set initial UI display */
		testMenu.setRotationSpeed(Speed.SLOW);
		testMenu.setStatusText("Fetching GPS Signal");
		testMenu.setBeginClickable(false);
		
		/* The connectivity manager is used to test if we have enabled Wifi and/or Mobile */
		ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		int preferredNetwork = PreferenceManager.getInstance().getPreferredNetwork(activity);
		
		NetworkInfo networkInfo = null;
		if (preferredNetwork == 1) { // mobile
			networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		} else { // wifi
			networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		
		/* If the preferred network is not connected, halt and display an error */
		if (networkInfo == null || !networkInfo.isConnected()) {
			testMenu.createErrorDialog("Unable To Connect",
					"Please enable " + (preferredNetwork == 0 ? "Wifi." : "Mobile Network."),
					null);
	    	return null;
	    }

	    WifiManager wifiManager = ((WifiManager) activity.getSystemService(Context.WIFI_SERVICE));

	    /* Collect device information */
	    make = Build.MANUFACTURER;    
	    model = Build.MODEL;
	    platform = "Android";
	    version = Build.VERSION.SDK_INT;

	    /* Wifi Network*/
	    if (preferredNetwork == 0) {
	    	macAddress = wifiManager.getConnectionInfo().getMacAddress();
		    macAddress = macAddress == null ? "" : macAddress;

	    	linkType = "Wifi";
	    	linkSpeed = wifiManager.getConnectionInfo().getLinkSpeed();
	    	
	    	List<ScanResult> results = wifiManager.getScanResults();
	    	
	    	for (ScanResult result : results) {
	    		
	      		if (result.BSSID.compareTo(wifiManager.getConnectionInfo().getBSSID()) == 0) {
	      			signalStrength = result.level;
	        		SSID = result.SSID;
	        		BSSID = result.BSSID;
	        		signalMutex.release();
	        		break;
	      		}
	    	}
	    	
	    /* Mobile Network */
	    } else {
	    	wifiManager.setWifiEnabled(true);

			while(!wifiManager.isWifiEnabled()) {
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			macAddress = wifiManager.getConnectionInfo().getMacAddress();
			wifiManager.setWifiEnabled(false);
	    	
	    	telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

	    	/* We must run listeners on the UI thread */
			activity.runOnUiThread(new Runnable() {
				
	    		public void run() {
	    			signalStrengthListener = new SignalStrengthListener();
	    			telephonyManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	    			
	    			networkOperator = telephonyManager.getNetworkOperatorName();
	    		}
	    	});
	    }
	      
	    /* LocationManager is used to collect GPS information */
	    locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
	    gpsListener = new GPSListener();
	    int preferredGPS = PreferenceManager.getInstance().getPreferredGPS(activity);
	    
	    if (preferredGPS == 0 && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) { // Mobile GPS
	    	gpsMethod = 1;
	    	
	    	/* We must run listeners on the UI thread */
	    	activity.runOnUiThread(new Runnable() {
	    		
	    		public void run() {
	    			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 250, 0, gpsListener);
	    		}
	    	});
	    } else if (preferredGPS == 1 && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) { // Network GPS
	    	gpsMethod = 2;
	    	
	    	/* We must run listeners on the UI thread */
	    	activity.runOnUiThread(new Runnable() {
	    		
	    		public void run() {
	    			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 250, 0, gpsListener);
	    		}
	    	});
	    } else {
	    	testMenu.createErrorDialog("Unable To Locate",
	    			"Please enable " + (preferredGPS == 0 ? "Mobile GPS." : "Network GPS"),
	    			null);
	    	return null;
	    }
	    
	    timeout = new SocketChannelTimeout(TIMEOUT_S_GPS);
	    timeout.start("Unable to fetch location.");
	    
	    if (running) {

			try {
				gpsMutex.acquire();
				if(timeout != null && timeout.stop()) return null;
				
				timeout.start("Unable to fetch signal strength.");
				testMenu.setStatusText("Fetching Signal Str");
				signalMutex.acquire();
				if(timeout != null && timeout.stop()) return null;
			} catch (InterruptedException e) {
				testMenu.createErrorDialog("Error", "Semaphore logic error.", null);
		    	return null;
			}
			
			if (!running) {
				return null;
			}

			begin();
		}
	    
	    return null;
    }
	
    /**
     * Called by child classes
     */
	protected void begin() {
    	buildClientDetails();
		
		testMenu.setRotationSpeed(Speed.FAST);
	    testMenu.setStatusText("Connecting");
	    
	    timeout = new SocketChannelTimeout(TIMEOUT_S);
    }
	
	/**
	 * Creates the initial message full of diagnostic information.
	 */
	private void buildClientDetails() {
		ClientDetails.Builder builder = ClientDetails.newBuilder();
		builder.setMac(macAddress);
		builder.setMake(make);
		builder.setModel(model);
		builder.setPlatform(platform);
		builder.setVersion("" + version);
		builder.setLongitude(longitude);
		builder.setLatitude(latitude);
		builder.setAltitude(altitude);
		builder.setGpsAccuracy(accuracy);
		builder.setGeolocationMethod(gpsMethod);
		builder.setNetworkType(linkType);
		builder.setNetworkOperator(networkOperator);
		builder.setMaxBitrate(linkSpeed);
		builder.setSignalDbm(signalStrength);
		builder.setBssid(BSSID);
		builder.setSsid(SSID);
		builder.setTestRequested(testID);
		clientDetails = builder.build();
	}
	
	/**
	 * Attempts to connect to the server
	 * @return true on connection, false otherwise
	 */
	protected boolean connectToServer() {
		try {
			client = SocketChannel.open();
			client.socket().setReuseAddress(true);
			
			/* Blocking is set to false to allow for timeouts */
			client.configureBlocking(false);
			InetSocketAddress inetSocketAddress = new InetSocketAddress(cyberTigerAddress, cyberTigerPort);
			
			if (inetSocketAddress.isUnresolved()) {
				testMenu.createErrorDialog("Error", "Address cannot be resolved.", null);
				return false;
			}
			
			timeout.start("Server cannot be reached.");
			if (!client.connect(inetSocketAddress) && running) {
				while(!client.finishConnect() && running);
			}
			if (timeout != null && timeout.stop()) return false;
			
		} catch (IOException e) {
			testMenu.createErrorDialog("Error", "Server cannot be reached.", null);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Attempts to read the port and ack messages from the server
	 * @return true on success, otherwise false
	 */
	protected boolean handShake() {
		testMenu.setStatusText("Handshaking...");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		try {
			clientDetails.writeDelimitedTo(byteArrayOutputStream);
		} catch (IOException e1) {
			testMenu.createErrorDialog("Error", "Unable to format handshake.", null);
			return false;
		}
			
		ByteBuffer socketBuffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
		if (!writeToServer(socketBuffer, "Unable to initialize.")) return false;
			
		socketBuffer = readFromServer(BUFFER_SIZE, "Unable to read port.");
		if (socketBuffer == null) return false;

		socketBuffer.flip();
		byte[] ackBuf = new byte[socketBuffer.remaining()];
		socketBuffer.get(ackBuf);
		ByteArrayInputStream ackStream = new ByteArrayInputStream(ackBuf);
		
		ServerAck serverAck;
		try {
			serverAck = ServerAck.parseDelimitedFrom(ackStream);
		} catch (IOException e1) {
			testMenu.createErrorDialog("Error", "Server Acknowledgement failed.", null);
			return false;
		}

		port = serverAck.getPort();
		
		if (port == 0) {
			testMenu.createErrorDialog("Error", serverAck.getMessage(), null);
			return false;
		}

		socketBuffer.clear();
		
		return true;
	}
	
	/**
	 * Retrieves results and prints them to a dialog box.
	 * @return true on success, false otherwise
	 */
	protected boolean parseResults() {
		ByteBuffer socketBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		
		socketBuffer = readFromServer(BUFFER_SIZE, "Unable to read test results.");
		if (socketBuffer == null) return false;
		
		socketBuffer.flip();
		byte[] resultsBuf = new byte[socketBuffer.remaining()];
		
		socketBuffer.get(resultsBuf);
		ByteArrayInputStream resultsStream = new ByteArrayInputStream(resultsBuf);
		
		TestResults testResults = null;
		try {
			testResults = TestResults.parseDelimitedFrom(resultsStream);
			testMenu.createResultsDialog("Results", testResults.getMessage(), null);
		} catch (IOException e) {
			testMenu.createErrorDialog("Error", "Unable to parse results", null);
			return false;
		}
		
		socketBuffer.clear();
		
		return true;
	}
	
	/**
	 * Writes a byteBuffer to the server.
	 * @param byteBuffer Contents to write to the server
	 * @param s Message to print if failed or times out
	 * @return true on success, false otherwise
	 */
	protected boolean writeToServer(ByteBuffer byteBuffer, String s) {
		timeout.start(s);
		while(byteBuffer.hasRemaining() && running) {
			try {
				client.write(byteBuffer);
			} catch (IOException e) {
				e.printStackTrace();
				if (running) testMenu.createErrorDialog("Error", s, null);
				return false;
			}
		}
		if (timeout != null && timeout.stop()) return false;
		
		return running ? true : false;
	}
	
	/**
	 * Reads a byteBuffer from the server.
	 * @param bufferSize Location to store content
	 * @param s Message to print if failed or times out
	 * @return true on success, false otherwise
	 */
	protected ByteBuffer readFromServer(int bufferSize, String s) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
		int bytesRead = 0;
		timeout.start(s);
		while (bytesRead == 0 && running) {
			try {
				bytesRead = client.read(byteBuffer);
			} catch (IOException e) {
				if (running) testMenu.createErrorDialog("Error", s, null);
				return null;
			}
		}
		if (timeout != null && timeout.stop()) return null;

		return running ? byteBuffer : null;
	}
	
	/**
	 * Pings a server
	 * @return true on success, false otherwise
	 */
	protected boolean ping() {
		testMenu.setStatusText("Pinging...");
		
		ByteBuffer latencyBuffer = readFromServer(1, "Unable to read ping.");
		if (latencyBuffer == null) return false;
		
		latencyBuffer.rewind();
		if (!writeToServer(latencyBuffer, "Unable to write ping.")) return false;
		latencyBuffer.clear();
		
		return true;
	}
	
	/**
	 * Sets the the noDelay attribute on the client socket
	 * @param noDelay attribute boolean
	 * @return true on success, ...
	 */
	protected boolean setNoDelay(boolean noDelay) {
		
		try {
			client.socket().setTcpNoDelay(noDelay);
		} catch (SocketException e1) {
			testMenu.createErrorDialog("Error", "Delay Error.", null);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Attempts to close a connection.
	 */
	protected void closeConnection() {
		try {
			if (client != null)
				client.close();
		} catch (IOException e) {
			System.err.println("ERROR: Unable to close connection.");
			return;
		}
	}
	
	/**
	 * Cancels the test via user
	 * @param activity
	 * @param testMenu
	 */
	public void cancel() {
		
		if (running) {
			
			testMenu.createConfirmDialog("Cancel test?", "Confirm", new DialogInterface.OnClickListener() {
	    		
				public void onClick(DialogInterface dialog, int id) {
					stopTest();
				}
			}, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		} else {
			activity.setMenu(new DiagnosticsMenu(activity));
		}
	}
	
	/**
	 * Halts the test.
	 */
	public void stopTest() {
		running = false;
		
		if (locationManager != null && gpsListener != null) {
			locationManager.removeUpdates(gpsListener); 
		}
		
		if (telephonyManager != null && signalStrengthListener != null) {
			telephonyManager.listen(signalStrengthListener, PhoneStateListener.LISTEN_NONE);
		}
		
		closeConnection();
		
		if (timeout != null) {
			timeout.stop();
		}
		
		if (gpsMutex != null) {
			
			if (gpsMutex.availablePermits() == 0) {
				gpsMutex.release();
			}
		}
		
		if (signalMutex != null) {
			
			if (signalMutex.availablePermits() == 0) {
				signalMutex.release();
			}
		}
	}
	
	@Override
	protected void onPostExecute (Void result) {
		testMenu.setBeginClickable(true);
		testMenu.setRotationSpeed(Speed.OFF);
		testMenu.setStatusText("Ready to Begin");
		running = false;
	}
}
