package uni.oulu.fingertracker.communicator;

import android.content.Context;
import android.os.Handler;

public class UsbCommunicator extends USBControl implements HmdCommunicator{

	public UsbCommunicator(Context c, Handler ui) {
    	super(c, ui);
	}

	@Override
    public void onReceive(byte[] msg) {

	}
	
	@Override
	public void onNotify(String msg) {
		//console(msg);
	}

	@Override
	public void onConnected() {
		//usb.enable();
	}

	@Override
	public void onDisconnected() {
		//usb.pause();
		//finish();
	}
	
	protected byte[] toBytes(long d) {

		byte [] ret = new byte[4];
			
		ret[0] = (byte) (d  & 0xff);
		ret[1] = (byte) ((d >>> 8) & 0xff);
		ret[2] = (byte) ((d >>> 16) & 0xff);
		ret[3] = (byte) ((d >>> 24) & 0xff);
		return ret;
	 }

	long last_data = 0x0;
	
	@Override
	public boolean sendData(long data) {
		if (data != last_data) {
			send(toBytes(data));
			last_data=data;
			return true;
		}
		return false;
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
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void doStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doStop() {
		destroyReceiver();
	}

	@Override
	public void doResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean sendData(String data) {
		// TODO Auto-generated method stub
		return false;
	}
}

