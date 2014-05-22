package uni.oulu.fingertracker.communicator;

import android.app.Activity;
import android.os.AsyncTask;

public class LocalAudioCommunicator implements HmdCommunicator {

	private LocalAudioService mAudioService = null;
	private Activity activity_context = null;
	private String last_data = "NONE";
	
	public LocalAudioCommunicator(Activity a) {
		activity_context = a;
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
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void doStart() {
		if (mAudioService == null) setupAudio();		
	}

	@Override
	public void doStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doResume() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean sendData(String data) {
		// Check that we're actually connected before trying anything
		if (mAudioService == null)
			return false;       
        
        if (last_data != data) {
        	new PlayAudioTask().execute(data);
        	last_data = data;
        }
        return true;
	}
	
	private void setupAudio() {
        // Initialize the BluetoothAudioService to perform bluetooth connections
        mAudioService = new LocalAudioService(activity_context, null);

        // Initialize the buffer for outgoing messages
        //mOutStringBuffer = new StringBuffer("");
    }
	
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

}
