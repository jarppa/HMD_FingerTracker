package uni.oulu.fingertracker.activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.R.id;
import uni.oulu.fingertracker.R.layout;
import uni.oulu.fingertracker.R.menu;
import uni.oulu.fingertracker.communicator.HmdBtCommunicator;
import uni.oulu.fingertracker.model.DirectionPattern;
import uni.oulu.fingertracker.model.FilePatternStore;
import uni.oulu.fingertracker.model.PatternStorer;
import uni.oulu.fingertracker.ui.LedButton;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.TextView;

public class LedConfigActivity extends Activity {
	
    Spinner cyclesSpinner;
    Spinner frequencySpinner;
    Spinner brightnessSpinner;
    Spinner directionSpinner;
    TextView log_textview;
    int blink_time=3;
    double frequency=1;
    String LogString="";

    //private Hashtable<String,DirectionPattern> directions = new Hashtable<String,DirectionPattern>();
    private HmdBtCommunicator mBtCommunicator;
    private FilePatternStore mStore;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ledconfig);
     
        cyclesSpinner =(Spinner)findViewById(R.id.cycles_spinner);
        brightnessSpinner =(Spinner)findViewById(R.id.brightness_spinner);
        frequencySpinner =(Spinner)findViewById(R.id.freq_spinner);
        directionSpinner =(Spinner)findViewById(R.id.dir_spinner);
        
        //log_textview = (TextView)findViewById(R.id.log_textview);
        
        mBtCommunicator = new HmdBtCommunicator(this, null);
        //directions = mBtCommunicator.getDirections();
        mStore = FilePatternStore.getInstance();
        mStore.open(getApplicationContext(), "led_patterns3");
        mStore.load();
        
        directionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				setLeds(mStore.getPattern((String)directionSpinner.getSelectedItem()));
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
       
    }


    @Override
    protected void onStart() {
    	super.onStart();
    	if (mBtCommunicator != null)
    		mBtCommunicator.doStart();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (mBtCommunicator != null)
    		mBtCommunicator.doResume();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mStore.save();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if (mBtCommunicator != null)
    		mBtCommunicator.doStop();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.firstprotocol_main, menu);
        
       return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_connect:
            	mBtCommunicator.findDevice();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goOnClick(View v)
    {
        int [] values = new int[14];
        values[0] = ((LedButton) findViewById(R.id.button1)).getLedState();
        values[1] = ((LedButton) findViewById(R.id.button2)).getLedState();
        values[2] = ((LedButton) findViewById(R.id.button3)).getLedState();
        values[3] = ((LedButton) findViewById(R.id.button4)).getLedState();
        values[4] = ((LedButton) findViewById(R.id.button5)).getLedState();
        values[5] = ((LedButton) findViewById(R.id.button6)).getLedState();
        values[6] = ((LedButton) findViewById(R.id.button7)).getLedState();
        values[7] = ((LedButton) findViewById(R.id.button8)).getLedState();
        values[8] = ((LedButton) findViewById(R.id.button9)).getLedState();
        values[9] = ((LedButton) findViewById(R.id.button10)).getLedState();
        values[10] = ((LedButton) findViewById(R.id.button11)).getLedState();
        values[11] = ((LedButton) findViewById(R.id.button12)).getLedState();
        values[12] = ((LedButton) findViewById(R.id.button13)).getLedState();
        values[13] = ((LedButton) findViewById(R.id.button14)).getLedState();
        
        int brightness = Integer.parseInt(brightnessSpinner.getSelectedItem().toString());
        
        blink_time = Integer.parseInt(cyclesSpinner.getSelectedItem().toString());
        frequency = Double.valueOf(frequencySpinner.getSelectedItem().toString());

        DirectionPattern p = new DirectionPattern(new long [] {
                compileLedValues(values, brightness),0x0 }, (int)((0.5/frequency)*1000),
                (int)((0.5/frequency)*1000), (int)(frequency*blink_time));

        mStore.putPattern((String)directionSpinner.getSelectedItem(), p);

        mBtCommunicator.sendData((String)directionSpinner.getSelectedItem());

    }
    
    private void setLeds(DirectionPattern dp) {
    	
    	if (dp == null) {
    		((LedButton) findViewById(R.id.button1)).setLedState(0);
        	((LedButton) findViewById(R.id.button2)).setLedState(0);
        	((LedButton) findViewById(R.id.button3)).setLedState(0);
        	((LedButton) findViewById(R.id.button4)).setLedState(0);
        	((LedButton) findViewById(R.id.button5)).setLedState(0);
        	((LedButton) findViewById(R.id.button6)).setLedState(0);
        	((LedButton) findViewById(R.id.button7)).setLedState(0);
        	((LedButton) findViewById(R.id.button8)).setLedState(0);
        	((LedButton) findViewById(R.id.button9)).setLedState(0);
        	((LedButton) findViewById(R.id.button10)).setLedState(0);
        	((LedButton) findViewById(R.id.button11)).setLedState(0);
        	((LedButton) findViewById(R.id.button12)).setLedState(0);
        	((LedButton) findViewById(R.id.button13)).setLedState(0);
        	((LedButton) findViewById(R.id.button14)).setLedState(0);
    	}
    	
    	((LedButton) findViewById(R.id.button1)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),0));
    	((LedButton) findViewById(R.id.button2)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),1));
    	((LedButton) findViewById(R.id.button3)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),2));
    	((LedButton) findViewById(R.id.button4)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),3));
    	((LedButton) findViewById(R.id.button5)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),4));
    	((LedButton) findViewById(R.id.button6)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),5));
    	((LedButton) findViewById(R.id.button7)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),6));
    	((LedButton) findViewById(R.id.button8)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),7));
    	((LedButton) findViewById(R.id.button9)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),8));
    	((LedButton) findViewById(R.id.button10)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),9));
    	((LedButton) findViewById(R.id.button11)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),10));
    	((LedButton) findViewById(R.id.button12)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),11));
    	((LedButton) findViewById(R.id.button13)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),12));
    	((LedButton) findViewById(R.id.button14)).setLedState(DirectionPattern.extractLedValue(dp.getData(0),13));
    }

    public void storeOnClick(View v)
    {

    }


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case HmdBtCommunicator.REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	if (mBtCommunicator != null)	
            		mBtCommunicator.connectDevice(data, true);
            }
            break;
        case HmdBtCommunicator.REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	if (mBtCommunicator != null)
            		mBtCommunicator.connectDevice(data, false);
            }
            break;
        case HmdBtCommunicator.REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	if (mBtCommunicator != null)
            		mBtCommunicator.doStart();
            }
            break;
        default:
        	break;
        }
    }	

	private long compileLedValues(int [] values, int bright) {
		long ret = 0x0;
		for (int i=0; i<values.length; i++) {
			ret |= (long) ((values[i]&0x3) << (i*2));
		}
		
		return ret|((bright&0xf) << 28);//0xf0000000;
	}
}
