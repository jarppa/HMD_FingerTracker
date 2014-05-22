package uni.oulu.fingertracker.ui;

import uni.oulu.fingertracker.model.DirectionPattern;
import uni.oulu.fingertracker.model.FingerTrackModel;
import uni.oulu.fingertracker.model.PatternStorer;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DirectionListFragment extends ListFragment{

	String[] numbers_text = new String[] { FingerTrackModel.DIR_UP_KEY, FingerTrackModel.DIR_DOWN_KEY, 
										FingerTrackModel.DIR_LEFT_KEY, FingerTrackModel.DIR_RIGHT_KEY,  
										FingerTrackModel.DIR_RIGHT_UP_KEY, FingerTrackModel.DIR_RIGHT_DOWN_KEY,
										FingerTrackModel.DIR_LEFT_UP_KEY, FingerTrackModel.DIR_LEFT_DOWN_KEY,
										FingerTrackModel.DIR_GOAL_KEY, FingerTrackModel.DIR_NONE_KEY}; 
	
	@Override  
	public void onListItemClick(ListView lv, View v, int position, long id) {  
		//new CustomToast(getActivity(), numbers_digits[(int) id]);
		FragmentManager fm = getFragmentManager();  
		  
		//if (fm.findFragmentById(android.R.id.content) == null) {  
		Activity  parent = getActivity();
		DirectionPattern dp = null;
		if (parent instanceof PatternStorer) {
			dp = ((PatternStorer)parent).getPattern(numbers_text[position]);
		}
		LedSelectFragment l = new LedSelectFragment();
		Bundle b = new Bundle();
		b.putSerializable("pattern", dp);
		b.putString("key", numbers_text[position]);
		l.setArguments(b);
		fm.beginTransaction().addToBackStack(null).replace(android.R.id.content, l).commit();  
		//}
	}
	 
	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(  
				inflater.getContext(), android.R.layout.simple_list_item_1,  
				numbers_text);  
		setListAdapter(adapter);  
	    return super.onCreateView(inflater, container, savedInstanceState);  
	} 
}
