package uni.oulu.fingertracker.communicator;

import java.util.Hashtable;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class RecordPlayer {

	private SoundPool mPool;

	// samples
	//private int mSampleID = -1;
	private Hashtable<String,Integer> sounds = new Hashtable<String,Integer>();
	
	public RecordPlayer() {
		mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
	}

	public void loadSample(Context context, String id, int resid) {
		sounds.put(id,mPool.load(context, resid, 1));
	}

	private void unloadSound(int soundID) {
		mPool.unload(soundID);
		sounds.remove(soundID);
	}

	public void playSample(String id) {
		Integer soundid = sounds.get(id);
		if (soundid != null)
			playSound(soundid);
	}

	private void playSound(int soundID) {
		if (soundID == 0)
			return;
		mPool.play(soundID, 1, 1, 0, 0, 1.0f);
	}

	public void close() {
		mPool.release();
		sounds.clear();
		mPool = null;
	}
}