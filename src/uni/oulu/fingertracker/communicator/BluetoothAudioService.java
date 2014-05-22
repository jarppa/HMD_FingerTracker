/*
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

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.R.raw;
import uni.oulu.fingertracker.model.FingerTrackModel;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothAudioService {
    // Debugging
    private static final String TAG = "BluetoothAudioService";
    private static final boolean D = true;

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private int mState;

    public static final int STATE_NONE = -1;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_PLAYING = 2;
    public static final int STATE_CONNECTING = 3;
    
    private RecordPlayer mPlayer;
    private BluetoothA2dp mProxy;
    
    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BluetoothAudioService(Context context, Handler handler) {
    	
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.A2DP);
        
        mPlayer = new RecordPlayer();
        mPlayer.loadSample(context,FingerTrackModel.DIR_DOWN_KEY,R.raw.alas);
        mPlayer.loadSample(context,FingerTrackModel.DIR_UP_KEY,R.raw.ylos);
        mPlayer.loadSample(context,FingerTrackModel.DIR_RIGHT_KEY,R.raw.oikea);
        mPlayer.loadSample(context,FingerTrackModel.DIR_LEFT_KEY,R.raw.vasen);
        mPlayer.loadSample(context,FingerTrackModel.DIR_RIGHT_DOWN_KEY,R.raw.alaoikea);
        mPlayer.loadSample(context,FingerTrackModel.DIR_RIGHT_UP_KEY,R.raw.ylaoikea);
        mPlayer.loadSample(context,FingerTrackModel.DIR_LEFT_DOWN_KEY,R.raw.alavasen);
        mPlayer.loadSample(context,FingerTrackModel.DIR_LEFT_UP_KEY,R.raw.ylavasen);
        mPlayer.loadSample(context,FingerTrackModel.DIR_GOAL_KEY,R.raw.valmis);
        
        
        if (mAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED)
        	mState = STATE_CONNECTED;
        else
        	mState = STATE_NONE;
        mHandler = handler;
    }

    // Define Service Listener of BluetoothProfile
    private BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (profile == BluetoothProfile.HEADSET) {
        	mProxy = (BluetoothA2dp) proxy;
			setState(STATE_CONNECTED);
        }
    }
    public void onServiceDisconnected(int profile) {
        if (profile == BluetoothProfile.HEADSET) {
        	mProxy = null;
        }
    }
    };
    
    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D) Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(BtAudioCommunicator.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState() {
        return mState;
    }
   
    public void play(String data) {
        mPlayer.playSample(data);
    }
    
    public void stop() {
    	mAdapter.closeProfileProxy(BluetoothProfile.A2DP,mProxy);
    	
    }
}
