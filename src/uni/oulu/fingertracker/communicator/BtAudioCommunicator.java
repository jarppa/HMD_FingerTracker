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

import uni.oulu.fingertracker.activity.DeviceListActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class BtAudioCommunicator implements HmdCommunicator {

    // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;
    
    // Message types sent from the BluetoothAudioService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    
    // Key names received from the BluetoothAudioService Handler
    public static final String DEVICE_NAME = "device_name";
    
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothAudioService mAudioService = null;
	
	private Activity activity_context = null;
	private boolean bConnected = false;
	private String last_data = "NONE";
	
	public BtAudioCommunicator(Activity a) {
		activity_context = a;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	@Override
	public void doStart() {
		if (mAudioService == null) setupAudio();
	}
	
	@Override
	public void doResume() {
		
	}
	
	@Override
	public void doStop() {
		if (mAudioService != null) {
			mAudioService.stop();
			mAudioService = null;
		}
	}

	@Override
	public boolean isConnected() {
		return bConnected;
	}
	
	private void setupAudio() {
        // Initialize the BluetoothAudioService to perform bluetooth connections
		if (mAudioService == null)
			mAudioService = new BluetoothAudioService(activity_context, mHandler);
    }
	
	/*public void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        //mAudioService.connect(device, secure);
    }*/
	
	public void findDevice() {
		Intent serverIntent = null;
		serverIntent = new Intent(activity_context, DeviceListActivity.class);
        activity_context.startActivityForResult(serverIntent, BtAudioCommunicator.REQUEST_CONNECT_DEVICE_INSECURE);
	}
	
	@Override
	public boolean sendData(String data) {
		// Check that we're actually connected before trying anything
		if (mAudioService == null)
			return false;
		
        if (mAudioService.getState() != BluetoothAudioService.STATE_CONNECTED) {
           return false;
        }
        
        if (last_data != data) {
        	new PlayAudioTask().execute(data);
        	last_data = data;
        }
        return true;
	}
	
	
	// The Handler that gets information back from the BluetoothAudioService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothAudioService.STATE_CONNECTED:
                	bConnected = true;
                    break;
                case BluetoothAudioService.STATE_NONE:
                    break;
                }
                break;
            }
        }
    };
    
    
    private class PlayAudioTask extends AsyncTask <String, Void, Void> {

		@Override
		protected Void doInBackground(String... data) {
			//byte[] send = toBytes(data[0]);
	        //mChatService.write(send);
			mAudioService.play(data[0]);
			return null;
		}
		
		protected void onPostExecute() {
	        //mImageView.setImageBitmap(result);
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

	@Override
	public boolean sendData(long data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void doPause() {
		// TODO Auto-generated method stub
		
	}
}