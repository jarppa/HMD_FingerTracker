/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 *
 */

package uni.oulu.fingertracker.activity;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

import uni.oulu.fingertracker.communicator.BtAudioCommunicator;
import uni.oulu.fingertracker.communicator.HmdBtCommunicator;
import uni.oulu.fingertracker.communicator.LocalAudioCommunicator;
import uni.oulu.fingertracker.communicator.UsbCommunicator;
import uni.oulu.fingertracker.model.FingerTrackModel;
import uni.oulu.fingertracker.model.FingerTrackStateListener;
import uni.oulu.fingertracker.model.TrackCoord;
import uni.oulu.fingertracker.model.TrackNode;
import uni.oulu.fingertracker.model.TrackStore;
import uni.oulu.fingertracker.ui.SettingsFragment;
import uni.oulu.fingertracker.ui.TrackCellView;
import uni.oulu.fingertracker.ui.TrackGridLayout;

import uni.oulu.fingertracker.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class TrackerActivity extends Activity implements FingerTrackStateListener, OnSharedPreferenceChangeListener {

    private FingerTrackModel mModel = null;
    private HmdBtCommunicator mBtCommunicator;
    private UsbCommunicator mAccCommunicator;
    private Handler mUIHandler = new Handler();
    private Context mContext;
    
	private BtAudioCommunicator mBtAudioCommunicator;
	
	private SharedPreferences mSharedPrefs;
	private SharedPreferences mLedPrefs;
	private LocalAudioCommunicator mLocalAudioCommunicator;
	
	private View decor;
	
	private MenuItem resetItem;
	private MenuItem connectItem;
	private MenuItem saveItem;
	private MenuItem loadItem;
	private MenuItem doneItem;
	private MenuItem settingsItem;
	
	private Menu mOptionsMenu;
	
	private ActionBar mActionBar;
	private Runnable mHider;
	
    private void fillCells() {
    	TrackGridLayout gridLayout = (TrackGridLayout)findViewById(R.id.GridLayout1);
    	gridLayout.setModel(mModel);
    	gridLayout.setColumnCount(mModel.getCols());
    	gridLayout.setRowCount(mModel.getRows());
    	for (int i=0;i<(mModel.getRows()*mModel.getCols());i++) {
    		TrackCellView v = new TrackCellView(getBaseContext(), mModel);
    		mModel.addNode(v);
    		gridLayout.addView(v);
    	}
    	
    }
    private void emptyCells() {
    	TrackGridLayout gridLayout = (TrackGridLayout)findViewById(R.id.GridLayout1);
    	gridLayout.removeAllViews();    	
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        decor = getWindow().getDecorView();
        
        mContext = this;
        
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        setContentView(R.layout.activity_tracker);       
        
        mActionBar = getActionBar();
        mHider = new Hider();
        
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mLedPrefs = getSharedPreferences("uni.oulu.fingertracker.LEDPREFS", 0);
        mLedPrefs.registerOnSharedPreferenceChangeListener(this);
        
        /*Button butt = (Button)findViewById(R.id.button_go);
        butt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mModel.setTrackReady();
				mModel.startTracking();
				invalidateAll();
			}
        	
        });
        butt = (Button)findViewById(R.id.button_clear);
        butt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (mModel.isCreating())
					mModel.clearTrack();
				else if (mModel.isTracking())
					mModel.startCreating();
				
				invalidateAll();
			}
        	
        });
        butt = (Button)findViewById(R.id.button_connect);
        butt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mBtCommunicator != null)
					mBtCommunicator.findDevice();
			}
        	
        });
        butt = (Button)findViewById(R.id.button_tracks);
        butt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(mContext,TrackPickerActivity.class), TrackPickerActivity.REQUEST_PICK_TRACK);
			}
        	
        });
        butt = (Button)findViewById(R.id.button_save);
        butt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				TrackStore s = new TrackStore(mContext);
				s.putTrack(mModel.getTrack(), String.valueOf(new Date().getTime()));
				s.saveTracks();
			}
        	
        });
        
        butt = (Button)findViewById(R.id.button_settings);
        butt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(mContext,SettingsActivity.class);
				startActivity(i);
			}
        	
        });*/
 
        String s = mSharedPrefs.getString(SettingsFragment.KEY_GRIDSIZE, "15,19");
        int c = Integer.valueOf(s.split(",")[0]);
        int r = Integer.valueOf(s.split(",")[1]);
        
        mModel = new FingerTrackModel(this,c,r);
        fillCells();
        
        mModel.setDebug(mSharedPrefs.getBoolean(SettingsFragment.KEY_DEBUG, false));
        
        mModel.attachStateListener(this);
        
        if (mSharedPrefs.getBoolean(SettingsFragment.KEY_BLUETOOTH, false)) {
        	mBtCommunicator = new HmdBtCommunicator(this, null);
        	mModel.addCommunicator(mBtCommunicator);
        	mBtAudioCommunicator = new BtAudioCommunicator(this);
            mModel.addCommunicator(mBtAudioCommunicator);
        }
        
        if (mSharedPrefs.getBoolean(SettingsFragment.KEY_LOCAL_AUDIO, false)) {
        	mLocalAudioCommunicator = new LocalAudioCommunicator(this);
        	mModel.addCommunicator(mLocalAudioCommunicator);
        }
        
        mAccCommunicator = new UsbCommunicator(this,mUIHandler); 
        mModel.addCommunicator(mAccCommunicator);
       
        //hideUi();
    }
    
    
    @Override
    protected void onStart() {
    	if (mBtCommunicator != null)
    		mBtCommunicator.doStart();
    	if (mAccCommunicator != null)
    		mAccCommunicator.doStart();
    	if (mBtAudioCommunicator != null)
    		mBtAudioCommunicator.doStart();
    	if (mLocalAudioCommunicator != null)
    		mLocalAudioCommunicator.doStart();
    	super.onStart();
    }
    
    @Override
    protected void onDestroy() {
    	if (mBtCommunicator != null)
    		mBtCommunicator.doStop();
    	if (mAccCommunicator != null)
    		mAccCommunicator.doStop();
    	if (mBtAudioCommunicator != null)
    		mBtAudioCommunicator.doStop();
    	if (mLocalAudioCommunicator != null)
    		mLocalAudioCommunicator.doStop();
    	
    	mLedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    	super.onDestroy();
    }
    
    @Override
    protected void onResume() {
    	if (mBtCommunicator != null)
    		mBtCommunicator.doResume();
    	if (mAccCommunicator != null)
    		mAccCommunicator.doResume();
    	if (mBtAudioCommunicator != null)
    		mBtAudioCommunicator.doResume();
    	if (mLocalAudioCommunicator != null)
    		mLocalAudioCommunicator.doResume();
    	//hideUi();
    	super.onResume();
    }
    
    @Override
    public void onUserInteraction() {
    	//showUi();
    	//mUIHandler.removeCallbacks(mHider);
    	//mUIHandler.postDelayed(mHider, 1000);
    }
    
    private void hideUi() {
    	mActionBar.hide();
    	int currentApiVersion = android.os.Build.VERSION.SDK_INT;
    	if (currentApiVersion >= 19) {
    		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    				| View.SYSTEM_UI_FLAG_FULLSCREEN
    				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    		);
    	}else{
    		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    				| View.SYSTEM_UI_FLAG_FULLSCREEN
    		);
    	}
    }
    
    private void showUi() {
    	mActionBar.show();
    	int currentApiVersion = android.os.Build.VERSION.SDK_INT;
    	if (currentApiVersion >= 19) {
    		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    		);
    	}else{
    		decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    		);
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	mOptionsMenu = menu;
       getMenuInflater().inflate(R.menu.tracker_main, menu);
       loadItem = menu.findItem(R.id.action_load);
       saveItem = menu.findItem(R.id.action_save);
       resetItem = menu.findItem(R.id.action_reset);
       doneItem = menu.findItem(R.id.action_done);
       settingsItem = menu.findItem(R.id.action_settings);
       connectItem = menu.findItem(R.id.action_connect);
       return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.action_done:
        		mModel.setTrackReady();
				//mModel.startTracking();
				invalidateAll();
				return true;
			
            case R.id.action_connect:
            	if (mBtCommunicator != null)
					mBtCommunicator.findDevice();
                return true;
                
            case R.id.action_load:
            	startActivityForResult(new Intent(mContext,TrackPickerActivity.class), TrackPickerActivity.REQUEST_PICK_TRACK);
            	return true;
            	
            case R.id.action_save:
            	TrackStore s = new TrackStore(mContext);
				s.putTrack(mModel.getTrack(), String.valueOf(new Date().getTime()));
				s.saveTracks();
				return true;
				
            case R.id.action_reset:
            	if (mModel.isCreating())
					mModel.clearTrack();
				else if (mModel.isTracking())
					mModel.startCreating();
				
				invalidateAll();
                return true;
                
            case R.id.action_settings:
            	Intent i = new Intent(mContext,SettingsActivity.class);
				startActivity(i);
				return true;
				
            case R.id.action_sendlogs:
            	File [] fs = mModel.getLogs();
            	if (fs != null && fs.length > 0) {
            		ArrayList<Uri> uris = new ArrayList<Uri>();
            		for (int j=0; j<fs.length;j++) {
            			uris.add(Uri.parse("file://"+fs[j]));
            		}
            		Intent intent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
            		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                	intent.setType("message/rfc822");
                	//i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
                	intent.putExtra(Intent.EXTRA_SUBJECT, "HMD Finger tracker logs");
                	intent.putExtra(Intent.EXTRA_TEXT   , "This email was sent using HMD Finger Tracker application.");
            		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            		try {
                	    startActivity(Intent.createChooser(intent, "Send mail..."));
                	} catch (android.content.ActivityNotFoundException ex) {
                	    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                	}
            	}
            	else
            		Toast.makeText(this, "There aren't any logs to send.", Toast.LENGTH_SHORT).show();
            	
            	return true;
            
            case R.id.action_deletelogs:
            	mModel.clearLogs();
            	return true;
            	
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
	@Override
	public void OnFingerTrackStateChanged(int state) {
		//Button butt;
		
		switch (state) {
		case FingerTrackModel.STATE_CREATING:
			if (mOptionsMenu != null)
				loadItem.setEnabled(true);
			break;
			
		case FingerTrackModel.STATE_DONE:
			//mModel.startCreating();
			if (mOptionsMenu != null) {
				doneItem.setEnabled(false);
				loadItem.setEnabled(true);
			}
			break;
			
		case FingerTrackModel.STATE_TRACKING:
			if (mOptionsMenu != null) {
				doneItem.setEnabled(false);
				loadItem.setEnabled(false);
				saveItem.setEnabled(false);
			}
			break;
			
		default:
			break;
			
		}
		invalidateAll();
	}

	@Override
	public void OnFingerTrackChanged(int what) {
		switch(what) {
		case FingerTrackModel.TRACK_CLEARED:
			doneItem.setEnabled(false);
			saveItem.setEnabled(false);
			break;
		
		case FingerTrackModel.TRACK_NODE_ADDED:
		case FingerTrackModel.TRACK_SET:
		case FingerTrackModel.TRACK_NODE_REMOVED:
			if (mModel.trackValid()) {
				doneItem.setEnabled(true);
				saveItem.setEnabled(true);
				
			} else {
				doneItem.setEnabled(false);
				saveItem.setEnabled(false);
				
			}
			loadItem.setEnabled(true);
			invalidateAll();
			break;
		default:
			break;
		}
	}
  
	private void invalidateAll() {
		TrackNode [][] nods = mModel.getNodes();
		int i,j;
		for (i=0;i<mModel.getRows();i++) {
			for (j=0;j<mModel.getCols();j++) {
				nods[i][j].invalidate();
			}
		}
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
        case TrackPickerActivity.REQUEST_PICK_TRACK:
        	if (resultCode == Activity.RESULT_OK) {
        		// Track Picked
        		Bundle b = data.getExtras();
        		String id = b.getCharSequence("uni.oulu.fingertracker.track").toString();
        		TrackStore s = new TrackStore(this);
        		TrackCoord [] c = s.getTrack(id);
        		mModel.restart(c);
        	}else {
        		
        	}
        	break;
        }
        
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		// TODO Auto-generated method stub
		if (key.equals(SettingsFragment.KEY_DEBUG)) {
            mModel.setDebug(prefs.getBoolean(SettingsFragment.KEY_DEBUG, false));
        }
		else if(key.equals(SettingsFragment.KEY_BLUETOOTH)) {
			if (prefs.getBoolean(SettingsFragment.KEY_BLUETOOTH, false)){
				if (mBtCommunicator == null){
					mBtCommunicator = new HmdBtCommunicator(this, null);
					mModel.addCommunicator(mBtCommunicator);
					mBtCommunicator.doStart();
				}
				if (mBtAudioCommunicator == null){
					mBtAudioCommunicator = new BtAudioCommunicator(this);
					mModel.addCommunicator(mBtAudioCommunicator);
					mBtAudioCommunicator.doStart();
				}
			}
			else {
				if (mBtCommunicator != null){
					mBtCommunicator.doStop();
					mModel.removeCommunicator(mBtCommunicator);
					mBtCommunicator = null;
				}
				if (mBtAudioCommunicator != null){
					mBtAudioCommunicator.doStop();
					mModel.removeCommunicator(mBtAudioCommunicator);
					mBtAudioCommunicator = null;
				}
			}
		}
		else if(key.equals(SettingsFragment.KEY_GRIDSIZE)) {
			String s = mSharedPrefs.getString(SettingsFragment.KEY_GRIDSIZE, "11,9");
	        int c = Integer.valueOf(s.split(",")[0]);
	        int r = Integer.valueOf(s.split(",")[1]);
	        mModel.finish();
	        emptyCells();
	        mModel = new FingerTrackModel(this,c,r);
	        fillCells();
		}
		else if (key_in(key,FingerTrackModel.DIRECTION_KEYS)) {
			if (mBtCommunicator == null)
				return;
			//String val = mLedPrefs.getString(key, "");
			//Hashtable<String,DirectionPattern> d = mBtCommunicator.getDirections();
			//d.put(key, new DirectionPattern( new long[] {val}, 0, 0, 0, 1));
			//mBtCommunicator.setDirections(val);
			
		}
	}
	
	private boolean key_in(String  k, String [] keys) {
		for (String ke : keys) {
			if (ke.equals(k))
				return true;
		}
		return false;
	}
	
	private class Hider implements Runnable {

		@Override
		public void run() {
			hideUi();
		}
		
	}
}
	
