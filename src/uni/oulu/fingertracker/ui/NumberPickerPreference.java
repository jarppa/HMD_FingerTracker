package uni.oulu.fingertracker.ui;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.R.id;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
 
public class NumberPickerPreference extends DialogPreference {
 
NumberPicker picker1;
NumberPicker picker2;
Integer initialValue1;
Integer initialValue2;

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		picker1 = (NumberPicker)view.findViewById(R.id.pref_num_picker1);
		picker2= (NumberPicker)view.findViewById(R.id.pref_num_picker2);
		// TODO this should be an XML parameter:
		picker1.setMaxValue(50);
		picker2.setMaxValue(50);
		picker1.setMinValue(2);
		picker2.setMinValue(2);
		if (initialValue1 != null ) picker1.setValue(initialValue1);
		if (initialValue2 != null ) picker2.setValue(initialValue2);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		if ( which == DialogInterface.BUTTON_POSITIVE ) {
			picker1.clearFocus();
			initialValue1 = picker1.getValue();
			picker2.clearFocus();
			initialValue2 = picker2.getValue();	
			//persistInt( initialValue1 );
			persistString(new String(String.valueOf(initialValue1)+","+String.valueOf(initialValue2)));
			//persistInt( initialValue2 );
			callChangeListener( new String(String.valueOf(initialValue1)+","+String.valueOf(initialValue2)) );
		}
	}
	
	@Override
	protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
		String def;
		if (defaultValue instanceof String) {
			def = (String)defaultValue;
		}
		else
			def = "2,2";
		
		initialValue1 = Integer.parseInt(def.split(",")[0]);
		initialValue2 = Integer.parseInt(def.split(",")[1]);

	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getString(index);
	}
}
