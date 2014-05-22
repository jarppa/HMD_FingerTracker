package uni.oulu.fingertracker.activity;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.R.id;
import uni.oulu.fingertracker.R.layout;
import uni.oulu.fingertracker.ui.SettingsFragment;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SettingsActivity extends Activity implements OnItemClickListener{

	String[] numbers_text = new String[] { "Set LED patterns"}; 
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        
        getFragmentManager().beginTransaction().replace(R.id.settings_frame, new SettingsFragment()).commit();
        
        ListView lv = (ListView)findViewById(R.id.extra_settings);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(  
				this, android.R.layout.simple_list_item_1,  
				numbers_text);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg2) {
		case 0:
			Intent i = new Intent();
			//i.setComponent(new ComponentName("uni.oulu.firstprotocol", "uni.oulu.firstprotocol.FirstprotocolMainActivity"));
			i.setClass(getApplicationContext(), LedConfigActivity.class);
			startActivity(i);
			break;
		
		default:
			break;
		}
	}
}