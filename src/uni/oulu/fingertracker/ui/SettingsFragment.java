package uni.oulu.fingertracker.ui;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.R.xml;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {

	public static final String KEY_DEBUG = "uni.oulu.fingertracker.enable_debug";
	public static final String KEY_BLUETOOTH = "uni.oulu.fingertracker.enable_bluetooth";
	public static final String KEY_LOCAL_AUDIO = "uni.oulu.fingertracker.enable_local_audio";
	public static final String KEY_GRIDSIZE = "uni.oulu.fingertracker.grid_size";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
