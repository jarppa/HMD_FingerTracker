package uni.oulu.fingertracker.communicator;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.R.raw;
import uni.oulu.fingertracker.model.FingerTrackModel;
import android.content.Context;
import android.os.Handler;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class LocalAudioService {
    
    private RecordPlayer mPlayer;
    
    /**
     * Constructor. Prepares a new BluetoothChat session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public LocalAudioService(Context context, Handler handler) {   
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
        
    }
   
    public void play(String data) {
        mPlayer.playSample(data);
    }
    
    public void stop() {
    	
    }
}
