package uni.oulu.fingertracker.ui;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.R.id;
import uni.oulu.fingertracker.R.layout;
import uni.oulu.fingertracker.model.DirectionPattern;
import uni.oulu.fingertracker.model.LedValueListener;
import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

public class LedSelectFragment extends Fragment {

	private View mRoot;
	private String mKey;
    int blink_time=3;
    double frequency=1;
	NumberPicker cyclesSpinner;
    NumberPicker frequencySpinner;
    NumberPicker brightnessSpinner;
    
	public LedSelectFragment() {
	}

	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);
		
	    mRoot = inflater.inflate(R.layout.activity_pattern, container, false);
	    //mRoot.findViewById(R.id.confirm_pattern).setOnClickListener(this);
	    
	    //NumberPicker on = ((NumberPicker)mRoot.findViewById(R.id.ontime_picker));
	    //NumberPicker off = ((NumberPicker)mRoot.findViewById(R.id.offtime_picker));
	    //NumberPicker repeat = ((NumberPicker)mRoot.findViewById(R.id.repeat_picker));
	    cyclesSpinner =(NumberPicker)mRoot.findViewById(R.id.cycles_spinner);
        //brightnessSpinner =(Spinner)mRoot.findViewById(R.id.brightness_spinner);
        //frequencySpinner =(Spinner)mRoot.findViewById(R.id.freq_spinner);
        brightnessSpinner =(NumberPicker)mRoot.findViewById(R.id.brightness_spinner);
        frequencySpinner =(NumberPicker)mRoot.findViewById(R.id.freq_spinner);
        
        //log_textview = (TextView)findViewById(R.id.log_textview);
        //int blink_time=3;
        //double frequency=1;
        
	    /*off.setMinValue(0);
	    off.setMaxValue(10);
	    on.setMinValue(0);
	    on.setMaxValue(10);
	    repeat.setMinValue(0);
	    repeat.setMaxValue(10);*/
	    
	    Bundle bundle = this.getArguments();
	    mKey = bundle.getString("key", "");
	    DirectionPattern d = (DirectionPattern)bundle.getSerializable("pattern");
	    if (d != null) {
	    	/*on.setValue(d.getOnDelay()/1000);
	    	off.setValue(d.getOffDelay()/1000);
	    	repeat.setValue(d.getRepeats());*/
	    	//(int)((0.5/frequency)*1000
	    	frequencySpinner.setValue(d.getOnDelay()/2000);
	    	cyclesSpinner.setValue(d.getRepeats());
	    	brightnessSpinner.setValue(extractBrightnessValue(d.getData(0)));
	    	
	    	((LedButton) mRoot.findViewById(R.id.button1)).setLedState(extractLedValue(d.getData(0),0));
	    	((LedButton) mRoot.findViewById(R.id.button2)).setLedState(extractLedValue(d.getData(0),1));
	    	((LedButton) mRoot.findViewById(R.id.button3)).setLedState(extractLedValue(d.getData(0),2));
	    	((LedButton) mRoot.findViewById(R.id.button4)).setLedState(extractLedValue(d.getData(0),3));
	    	((LedButton) mRoot.findViewById(R.id.button5)).setLedState(extractLedValue(d.getData(0),4));
	    	((LedButton) mRoot.findViewById(R.id.button6)).setLedState(extractLedValue(d.getData(0),5));
	    	((LedButton) mRoot.findViewById(R.id.button7)).setLedState(extractLedValue(d.getData(0),6));
	    	((LedButton) mRoot.findViewById(R.id.button8)).setLedState(extractLedValue(d.getData(0),7));
	    	((LedButton) mRoot.findViewById(R.id.button9)).setLedState(extractLedValue(d.getData(0),8));
	    	((LedButton) mRoot.findViewById(R.id.button10)).setLedState(extractLedValue(d.getData(0),9));
	    	((LedButton) mRoot.findViewById(R.id.button11)).setLedState(extractLedValue(d.getData(0),10));
	    	((LedButton) mRoot.findViewById(R.id.button12)).setLedState(extractLedValue(d.getData(0),11));
	    	((LedButton) mRoot.findViewById(R.id.button13)).setLedState(extractLedValue(d.getData(0),12));
	    	((LedButton) mRoot.findViewById(R.id.button14)).setLedState(extractLedValue(d.getData(0),13));
	    }
	    
	    return mRoot;
	}


	public void goOnClick(View v) {
		int [] values = new int[14];
		values[0] = ((LedButton) mRoot.findViewById(R.id.button1)).getLedState();
		values[1] = ((LedButton) mRoot.findViewById(R.id.button2)).getLedState();
		values[2] = ((LedButton) mRoot.findViewById(R.id.button3)).getLedState();
		values[3] = ((LedButton) mRoot.findViewById(R.id.button4)).getLedState();
		values[4] = ((LedButton) mRoot.findViewById(R.id.button5)).getLedState();
		values[5] = ((LedButton) mRoot.findViewById(R.id.button6)).getLedState();
		values[6] = ((LedButton) mRoot.findViewById(R.id.button7)).getLedState();
		values[7] = ((LedButton) mRoot.findViewById(R.id.button8)).getLedState();
		values[8] = ((LedButton) mRoot.findViewById(R.id.button9)).getLedState();
		values[9] = ((LedButton) mRoot.findViewById(R.id.button10)).getLedState();
		values[10] = ((LedButton) mRoot.findViewById(R.id.button11)).getLedState();
		values[11] = ((LedButton) mRoot.findViewById(R.id.button12)).getLedState();
		values[12] = ((LedButton) mRoot.findViewById(R.id.button13)).getLedState();
		values[13] = ((LedButton) mRoot.findViewById(R.id.button14)).getLedState();

		/*int on = ((NumberPicker) mRoot.findViewById(R.id.ontime_picker)).getValue();
		int off = ((NumberPicker) mRoot.findViewById(R.id.offtime_picker)).getValue();
		int repeat = ((NumberPicker) mRoot.findViewById(R.id.repeat_picker)).getValue();*/
		
		int brightness = brightnessSpinner.getValue();
        
        blink_time = cyclesSpinner.getValue();
        //delay = Integer.parseInt(delaySpinner.getSelectedItem().toString());
        frequency = (double)frequencySpinner.getValue();
		
		//DirectionPattern p = new DirectionPattern(new long [] {compileLedValues(values)}, on*1000, off*1000, repeat);
        DirectionPattern p = new DirectionPattern(new long [] {
                compileLedValues(values, brightness),0x0 }, (int)((0.5/frequency)*1000),
                (int)((0.5/frequency)*1000), (int)(frequency*blink_time));
        
		Activity owner = getActivity();
		if (owner instanceof LedValueListener) {
			((LedValueListener) owner).LedValueChaged(mKey, p);
		}
		FragmentManager fm = getFragmentManager();
		fm.popBackStack();
	}
	
	private long compileLedValues(int [] values, int bright) {
		long ret = 0x0;
		for (int i=0; i<values.length; i++) {
			ret |= (long) ((values[i]&0x3) << (i*2));
		}
		
		return ret|((bright&0xf) << 28);//0xf0000000;
	}
	
	private int extractLedValue(long value, int index) {
		int ret = 0x0;
		long bv = 0x3;
		long mask = (0x0 | (bv<<index*2));
		ret = (int)((value & mask)>>>index*2);
		return ret;
	}
	private int extractBrightnessValue(long value) {
		int ret = 0;
		long bv = 0xf0000000;
		ret = (int)( (value & bv)>>>28);
		return ret;
	}
}
