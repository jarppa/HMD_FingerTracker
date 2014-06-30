package uni.oulu.fingertracker.activity;

import java.util.List;
import java.util.Map;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.model.TrackStore;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TrackPickerActivity extends ListActivity {

	public static final int REQUEST_PICK_TRACK = 0x734873;
	
	private TrackStore store;
	private SimpleAdapter mAdapter = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_picker);
        
        Button butt = (Button)findViewById(R.id.button_clear);
        butt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				store.removeAll();
				setList();
			}
        	
        });
        
        store = new TrackStore(this);
        setList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		store.reloadTracks();
		setList();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		store.saveTracks();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		CharSequence sid = ((TextView)v).getText();
		setResult(Activity.RESULT_OK,new Intent().putExtra("uni.oulu.fingertracker.track", sid));
		finish();
	}
	
	private void setList() {
		
		List<Map<String, String>> traks = store.getTrackIdMap();
		
        //List <Map<String, TrackCoord[]>> list = new ArrayList<Map<String, TrackCoord[]>>();
        //list.add(traks);
        
		mAdapter = new SimpleAdapter(this, traks, android.R.layout.simple_list_item_1, 
        		new String[] {"id"}, new int[] {android.R.id.text1});
		
        setListAdapter(mAdapter);
	}
	
}
