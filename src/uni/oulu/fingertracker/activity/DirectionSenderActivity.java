package uni.oulu.fingertracker.activity;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.communicator.HmdBtCommunicator;
import uni.oulu.fingertracker.model.FingerTrackModel;
import uni.oulu.fingertracker.ui.DirectionImageButton;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class DirectionSenderActivity extends Activity implements OnClickListener{

	private HmdBtCommunicator mBtComm = null;
	private String devaddress = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        DirectionImageButton b;
        b = (DirectionImageButton)findViewById(R.id.ibConfirm);
        b.setData(FingerTrackModel.DIR_CONFIRM_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibCancel);
        b.setData(FingerTrackModel.DIR_CANCEL_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibDown);
        b.setData(FingerTrackModel.DIR_DOWN_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibDownLeft);
        b.setData(FingerTrackModel.DIR_LEFT_DOWN_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibDownRight);
        b.setData(FingerTrackModel.DIR_RIGHT_DOWN_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibLeft);
        b.setData(FingerTrackModel.DIR_LEFT_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibRight);
        b.setData(FingerTrackModel.DIR_RIGHT_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibNone);
        b.setData(FingerTrackModel.DIR_NONE_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibUp);
        b.setData(FingerTrackModel.DIR_UP_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibUpLeft);
        b.setData(FingerTrackModel.DIR_LEFT_UP_KEY);
        b.setOnClickListener(this);
        b = (DirectionImageButton)findViewById(R.id.ibUpRight);
        b.setData(FingerTrackModel.DIR_RIGHT_UP_KEY);
        b.setOnClickListener(this); 
        
        mBtComm = new HmdBtCommunicator(this, null);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	if (mBtComm != null) {
    		mBtComm.doStart();
    	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (mBtComm != null)
    		mBtComm.doResume();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if (mBtComm != null)
    		mBtComm.doPause();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if (mBtComm != null)
    		mBtComm.doStop();
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
            	mBtComm.findDevice();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case HmdBtCommunicator.REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	devaddress = data.getExtras()
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            	if (mBtComm != null)	
            		mBtComm.connectDevice(data, true);
            }
            break;
        case HmdBtCommunicator.REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	devaddress = data.getExtras()
                        .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
            	if (mBtComm != null)
            		mBtComm.connectDevice(data, false);
            }
            break;
        case HmdBtCommunicator.REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	if (mBtComm != null)
            		mBtComm.doStart();
            }
            break;
        default:
        	break;
        }
    }

	@Override
	public void onClick(View arg0) {
		
		switch (arg0.getId()) {
		case R.id.ibConfirm:
		case R.id.ibCancel:
		case R.id.ibNone:
		case R.id.ibDown:
		case R.id.ibDownLeft:
		case R.id.ibDownRight:
		case R.id.ibLeft:
		case R.id.ibRight:
		case R.id.ibUp:
		case R.id.ibUpLeft:
		case R.id.ibUpRight:
			if (mBtComm != null) {
				mBtComm.sendData( ((DirectionImageButton)arg0).getData() );
			}
			break;
		}
	}
}