/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 * 
 * Mostly adapted from Android API sample's BluetoothChat:
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uni.oulu.fingertracker.communicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import uni.oulu.fingertracker.activity.DeviceListActivity;
import uni.oulu.fingertracker.model.DirectionPattern;
import uni.oulu.fingertracker.model.FilePatternStore;
import uni.oulu.fingertracker.model.FingerTrackModel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HmdBtCommunicator implements HmdCommunicator {

	/*public static final long[] DIR_GOAL_VAL = {0x80208220,0x0};
	public static final long[] DIR_START_VAL = {0x80ffffff,0x0};
	public static final long[] DIR_RIGHT_VAL = {0x800a0000,0x0};
	public static final long[] DIR_LEFT_VAL = {0x80000802,0x0};
	public static final long[] DIR_UP_VAL = {0x80008200,0x0};
	public static final long[] DIR_DOWN_VAL = {0x80200020,0x0};
	public static final long[] DIR_LEFT_DOWN_VAL = {0x80000022,0x0};
	public static final long[] DIR_RIGHT_DOWN_VAL = {0x80280000,0x0};
	public static final long[] DIR_LEFT_UP_VAL = {0x80000a00,0x0};
	public static final long[] DIR_RIGHT_UP_VAL = {0x80028000,0x0};
	public static final long[] DIR_NONE_VAL = {0x0};*/
	
    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
    
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    public static final int SEND_NEXT_DATA = 6;
    
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothChatService mChatService = null;
	
	//private FileInputStream fis = null;
	//private FileOutputStream fos = null;
	
	private Activity activity_context = null;
	private boolean bConnected = false;
	private long last_data = 0x0;
	private String last_string_data = null;
	
	private BluetoothDevice last_device = null;
	
	private FilePatternStore mStore = FilePatternStore.getInstance();
	
	public HmdBtCommunicator(Activity a, Hashtable<String, DirectionPattern> dirs) {
		activity_context = a;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mStore = FilePatternStore.getInstance();
		mStore.open(activity_context, "led_patterns4");
	}
	
	@Override
	public void doStart() {

		// If BT is not on, request that it be enabled.
	    // setupChat() will then be called during onActivityResult
	    if (!mBluetoothAdapter.isEnabled()) {
	        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        activity_context.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	    // Otherwise, setup the chat session
	    } else {
	        if (mChatService == null) setupChat();
	    }
	    
	}
	
	@Override
	public void doResume() {
		
		mStore.load();//loadPatterns();
		if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
              if (last_device != null && bConnected)
            	  mChatService.connect(last_device, false); //FIXME: Always insecure here!
            }
        }
	}
	
	@Override
	public void doStop() {
		if (mChatService != null) mChatService.stop();
	}

	@Override
	public boolean isConnected() {
		return bConnected;
	}
	
	private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(activity_context, mHandler);
    }
	
	public void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        Log.d("BTBLOP", "DEVICE:"+device.toString());
        if (device != null) {
        	mChatService.connect(device, secure);
        	last_device = device;
        }
    }
	
	public void findDevice() {
		Intent serverIntent = null;
		serverIntent = new Intent(activity_context, DeviceListActivity.class);
        activity_context.startActivityForResult(serverIntent, HmdBtCommunicator.REQUEST_CONNECT_DEVICE_INSECURE);
	}
	
	@Override
	public boolean sendData(long data) {
		// Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
           return false;
        }

        // Get the message bytes and tell the BluetoothChatService to write
        //byte[] send = toBytes(data);
        //mChatService.write(send);
        if (last_data != data) {
        	new SendDataTask().execute(data);
        	last_data = data;
        }
        return true;
	}
	
	
	// The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            	case MESSAGE_STATE_CHANGE:
            		switch (msg.arg1) {
            			case BluetoothChatService.STATE_CONNECTED:
            				bConnected = true;
            				break;
            			case BluetoothChatService.STATE_CONNECTING:                   
            				break;
            			case BluetoothChatService.STATE_LISTEN:
            			case BluetoothChatService.STATE_NONE:
            				bConnected = false;
            				break;
            		}
            		break;
            	case MESSAGE_WRITE:
            		//byte[] writeBuf = (byte[]) msg.obj;
            		// construct a string from the buffer
            		//String writeMessage = new String(writeBuf);
            		//mConversationArrayAdapter.add("Me:  " + writeMessage);
            		break;
            	case MESSAGE_READ:
            		//byte[] readBuf = (byte[]) msg.obj;
            		// construct a string from the valid bytes in the buffer
            		//String readMessage = new String(readBuf, 0, msg.arg1);
            		//mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
            		break;
            	case MESSAGE_DEVICE_NAME:
            		// save the connected device's name
            		//mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
            		//Toast.makeText(getApplicationContext(), "Connected to "
            		//               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
            		break;
            	case SEND_NEXT_DATA:
            		Long d = (Long)msg.obj;
            		new SendDataTask().execute(d);
            		break;
            	}
        	}
    };
    
    private class SendDataTask extends AsyncTask <Long, Void, Void> {

		@Override
		protected Void doInBackground(Long... data) {
			byte[] send = toBytes(data[0]);
	        mChatService.write(send);
			return null;
		}
	}

	@Override
	public boolean sendData(int data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendData(byte data) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected byte[] toBytes(long d) {

		byte [] ret = new byte[4];
			
		ret[0] = (byte) (d  & 0xff);
		ret[1] = (byte) ((d >>> 8) & 0xff);
		ret[2] = (byte) ((d >>> 16) & 0xff);
		ret[3] = (byte) ((d >>> 24) & 0xff);
		return ret;
	 }

	@Override
	public boolean sendData(String data) {
		if (mChatService == null)
			return false;
		
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
	           return false;
	    }
		
		/*if (data == last_string_data)
			return true;*/
		
		mHandler.removeMessages(SEND_NEXT_DATA);
		
		//DirectionPattern d = directions.get(data);
		DirectionPattern d = mStore.getPattern(data);
		if (d == null)
			return false;
		
		long time=0;
		if (d.getRepeats() != 0	) {
			for (int i=0; i<d.getRepeats();i++){
				for (int j=0; j<d.getDataCount();j++){
					Message m = mHandler.obtainMessage(SEND_NEXT_DATA, d.getData(j));
					if (j==0 && i==0)
						mHandler.sendMessage(m);
					else if ( (j & 0x1)==1) {
						time += (d.getOnDelay());
						mHandler.sendMessageDelayed(m, time);	
					}			
					else{
						time += (d.getOffDelay());
						mHandler.sendMessageDelayed(m, time);
					}
				}
			}
		}
		else {
			Message m = mHandler.obtainMessage(SEND_NEXT_DATA, d.getData(0));
			mHandler.sendMessage(m);
		}
		last_string_data = data;
		
	    /*if (last_data != d) {
	        new SendDataTask().execute(d);
	        last_data = d;
	    }*/
	    return true;
	}
	
	/*private void loadPatterns() {
		String line = null;
		
		try {
			File f = new File(activity_context.getExternalFilesDir(null), "led_patterns2");
			fis = new FileInputStream(f);
			//fis = activity_context.openFileInput("led_patterns");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(fis));
		
		try {
			line = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(line != null) {
			String [] s = line.split(" ");
			if (s.length == 2)
				directions.put(s[0],compilePattern(s[1]));
			
			try {
				line = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		try {
			in.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private DirectionPattern compilePattern(String t) {
		String [] c = t.split(":");
		int size = c.length;
		
		if (size < 4)
			return null;
		
		DirectionPattern ret = null;

		String [] sv = c[0].split(",");
		long [] v = new long[sv.length+1];
		v[v.length-1] = 0x0;
		for(int j=0;j<sv.length;j++) {
			v[j] = Long.valueOf(sv[j]);
		}
		
		ret= new DirectionPattern(v, Integer.valueOf(c[1]), Integer.valueOf(c[2]), Integer.valueOf(c[3]));
		return ret;
	}*/
}
