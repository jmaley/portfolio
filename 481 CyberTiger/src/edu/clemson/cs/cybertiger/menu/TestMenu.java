package edu.clemson.cs.cybertiger.menu;

import edu.clemson.cs.cybertiger.MainActivity;
import edu.clemson.cs.cybertiger.R;
import edu.clemson.cs.cybertiger.test.GenericTest;
import edu.clemson.cs.cybertiger.test.LatencyTest;
import edu.clemson.cs.cybertiger.test.StreamingTest;
import edu.clemson.cs.cybertiger.test.TCPTest;
import edu.clemson.cs.cybertiger.test.UDPTest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This menu provides handling for the test XML menu.
 * @author Joe Maley
 */
public class TestMenu extends GenericMenu {
	
	public enum TestID {
		LATENCY(7),
		TCP(1),
		UDP(2),
		STREAMING(3);
		
		int id;
		
		TestID(int id) {
			this.id = id;
		}
		
		public int toInt() {
			return id;
		}
	}
	
	public enum Speed {
		OFF, SLOW, FAST;
	}
	
	private GenericTest test;
	
	/* Ideally, we want one AlertDialog to exist that we can mutate,
	 * However, there is a bug that does not allow us to mutate the 'buttons'.
	 * Instead, we create two alert dialog boxes that have different button
	 * types. 
	 */
	private AlertDialog alertDialogMessage;
	private AlertDialog alertDialogQuestion;

	public TestMenu(final MainActivity activity, final TestID testID) {
		super(activity, R.layout.activity_test_menu, R.id.relativeLayoutTestMenuActivity);
		
		alertDialogMessage = new AlertDialog.Builder(activity).create();
		alertDialogQuestion = new AlertDialog.Builder(activity).create();
		
		switch (testID) {
		case LATENCY:
			setTitle("Latency");
			break;
		case TCP:
			setTitle("TCP Bandwidth");
			break;
		case UDP:
			setTitle("UDP Bandwidth");
			break;
		case STREAMING:
			setTitle("Streaming");
			break;
		}

		Button button = (Button) activity.findViewById(R.id.buttonCancel);
		
		button.setOnTouchListener(new View.OnTouchListener() {	

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
					onBackButton();
				}
				
				return false;
			}
		});
			
		final TestMenu testMenu = this;
		
		button = (Button) activity.findViewById(R.id.buttonBegin);
		
		button.setOnTouchListener(new View.OnTouchListener() {	

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (event.getAction() == MotionEvent.ACTION_UP) {
					
					switch (testID) {
					case LATENCY:
						test = new LatencyTest(activity, testMenu, testID.toInt());
						break;
					case TCP:
						test = new TCPTest(activity, testMenu, testID.toInt());
						break;
					case UDP:
						test = new UDPTest(activity, testMenu, testID.toInt());
						break;
					case STREAMING:
						test = new StreamingTest(activity, testMenu, testID.toInt(), 5000, 1400, 14, 2000);
						break;
					default:
						return false;
					}

					test.execute();
				}
				
				return false;
			}
		});
	}
	
	@Override
	public void onBackButton() {
		
		if (test != null) {
			test.cancel();
		} else {
			activity.setMenu(new DiagnosticsMenu(activity));
		}
	}
	
	/**
	 * Changes the contents of the displayed title.
	 * @param s String to set as title
	 */
	private void setTitle(String s) {
		TextView textView = (TextView) activity.findViewById(R.id.textViewLogo);
		textView.setText(s);
	}
	
	/**
	 * Changes the contents of the displayed status text.
	 * @param s String to set as status text
	 */
	public void setStatusText(final String s) {
		final TextView textViewStatus = (TextView) activity.findViewById(R.id.textViewStatus);
		
		
		if (textViewStatus == null)
			return;
		
		activity.runOnUiThread(new Runnable() {
			
    		public void run() {
    			textViewStatus.setText(s);
    		}
    	});
	}
	
	/**
	 * Changes the speed on the rotating progress bar.
	 * @param Speed the state of the progress bar
	 */
	public void setRotationSpeed(final Speed speed) {
		
		activity.runOnUiThread(new Runnable() {
			
    		public void run() {
    			ProgressBar progressBar1 = (ProgressBar) activity.findViewById(R.id.progressBar1);
    			ProgressBar progressBar2 = (ProgressBar) activity.findViewById(R.id.progressBar2);
    			ProgressBar progressBar3 = (ProgressBar) activity.findViewById(R.id.progressBar3);
    			
    			if (progressBar1 == null || progressBar2 == null || progressBar3 == null)
    				return;

    			switch (speed) {
    			case OFF:
    				progressBar1.setVisibility(View.VISIBLE);
    				progressBar2.setVisibility(View.INVISIBLE);
    				progressBar3.setVisibility(View.INVISIBLE);
    				break;
    			case SLOW:
    				progressBar1.setVisibility(View.INVISIBLE);
    				progressBar2.setVisibility(View.VISIBLE);
    				progressBar3.setVisibility(View.INVISIBLE);
    				break;
    			case FAST:
    				progressBar1.setVisibility(View.INVISIBLE);
    				progressBar2.setVisibility(View.INVISIBLE);
    				progressBar3.setVisibility(View.VISIBLE);
    				break;
    			}
    		}
    	});
	}
	
	/**
	 * Determines whether or not the 'begin' button may be clicked.
	 * @param clickable True if clickable, otherwise false
	 */
	public void setBeginClickable(final boolean clickable) {
		
		final Button button = (Button) activity.findViewById(R.id.buttonBegin);
		
		if (button == null)
			return;
		
		activity.runOnUiThread(new Runnable() {
			
    		public void run() {
    			button.setEnabled(clickable);
    			
    			if (clickable) {
    				button.setTextColor(Color.WHITE);
    			} else {
    				button.setTextColor(Color.GRAY);
    			}
    		}
    	});
	}
	
	/**
	 * Mutates the alertDialogMessage.
	 */
	private void createAlertDialogMessage(String title, String message, int resID, OnClickListener listener) {
		alertDialogQuestion.hide();
		alertDialogMessage.setTitle(title);
    	alertDialogMessage.setMessage(message);
		alertDialogMessage.setCancelable(false);
		alertDialogMessage.setIcon(resID);
		alertDialogMessage.setButton(DialogInterface.BUTTON_POSITIVE, "OK", listener);
		alertDialogMessage.show();
	}
	
	/**
	 * Mutates the alertDialogQuestion.
	 */
	private void createAlertDialogQuestion(String title, String message, int resID, OnClickListener listenerYes, OnClickListener listenerNo) {
		alertDialogMessage.hide();
		alertDialogQuestion.setTitle(title);
    	alertDialogQuestion.setMessage(message);
		alertDialogQuestion.setCancelable(false);
		alertDialogQuestion.setIcon(resID);
		alertDialogQuestion.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", listenerYes);
		alertDialogQuestion.setButton(DialogInterface.BUTTON_NEGATIVE, "No", listenerNo);
		alertDialogQuestion.show();
	}
	
	/**
	 * Creates an AlertDialog to display the test results.
	 * @param title Title of the AlertDialog
	 * @param message Message inside the AlertDialog
	 * @param listener Listener when the 'OK' button is clicked
	 */
	public void createResultsDialog(final String title, final String message, final OnClickListener listener) {
		
		activity.runOnUiThread(new Runnable() {
			
    		public void run() {
    			createAlertDialogMessage(title, message, R.drawable.ic_launcher, listener);
    		}
		});
	}
	
	/**
	 * Creates an AlertDialog to display an error. Also stops the test.
	 * @param title Title of the AlertDialog
	 * @param message Message inside the AlertDialog
	 * @param listener Listener when the 'OK' button is clicked
	 */
	public void createErrorDialog(final String title, final String message, final OnClickListener listener) {
		
		activity.runOnUiThread(new Runnable() {
			
    		public void run() {
    			createAlertDialogMessage(title, message, R.drawable.ic_error, listener);
    		}
		});
		
		test.stopTest();
	}
	
	/**
	 * Creates an AlertDialog to display an a question.
	 * @param title Title of the AlertDialog
	 * @param message Message inside the AlertDialog
	 * @param listenerYes Listener when the 'Yes' button is clicked
	 * @param listenerNo Listener when the 'No' button is clicked
	 */
	public void createConfirmDialog(final String title, final String message, final OnClickListener listenerYes, final OnClickListener listenerNo) {
		
		activity.runOnUiThread(new Runnable() {
			
    		public void run() {
    			createAlertDialogQuestion(title, message, R.drawable.ic_launcher, listenerYes, listenerNo);
	    	}
		});
	}
}
