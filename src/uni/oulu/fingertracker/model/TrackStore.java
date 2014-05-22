package uni.oulu.fingertracker.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;

public class TrackStore {

	private Map <String, TrackCoord[]> tracks = new HashMap<String, TrackCoord[]>();
	private Context mContext;
	private FileInputStream fis = null;
	private FileOutputStream fos = null;
	
	public TrackStore(Context c) {
		mContext = c;
		loadTracks();
	}
	
	public TrackCoord[] getTrack(String id) {
		return tracks.get(id);
	}
	
	public String[] getTrackIds() {
		String [] ret = new String[tracks.size()];
		tracks.keySet().toArray(ret);
		return ret;
	}
	public List<Map<String,String>> getTrackIdMap() {
		List<Map<String, String>> ret = new ArrayList<Map<String,String>>();

		for (String s : tracks.keySet()) {
			HashMap<String, String> entry = new HashMap<String, String>();
			entry.put("id", s);

			ret.add(entry);
		}
		
		return ret;
	}
	
	public TrackCoord[][] getTracks() {
		return (TrackCoord[][]) tracks.values().toArray();
	}
	
	public Map<String, TrackCoord[]> getTracksMap() {
		return tracks;
	}
	
	public void putTrack(TrackCoord[] t, String id) {
		tracks.put(id, t);
	}
	
	public void removeTrack(String id) {
		if(tracks.containsKey(id)) {
			tracks.remove(id);
			loadTracks();
		}
	}
	
	public void removeAll() {
		tracks.clear();
		saveTracks();
		reloadTracks();
	}
	
	public void reloadTracks() {
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (fos != null) {
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		loadTracks();
	}
	
	private void loadTracks() {
		String line = null;
		
		try {
			fis = mContext.openFileInput("saved_tracks");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(fis));
		
		try {
			line = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tracks.clear();
		
		while(line != null) {
			String [] s = line.split(" ");
			if (s.length == 2)
				tracks.put(s[0],compileTrack(s[1]));
			
			try {
				line = in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		try {
			in.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveTracks() {
		
		try {
			fos = mContext.openFileOutput("saved_tracks", 0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
		Set<Entry<String, TrackCoord[]>> s = tracks.entrySet();
		Iterator <Entry<String, TrackCoord[]>> it = s.iterator();
		while(it.hasNext()) {
			StringBuilder str = new StringBuilder();
			Entry<String, TrackCoord[]> e = it.next();
			str.append(e.getKey()).append(' ');
			TrackCoord[] vals = e.getValue();
			for (int i=0; i<vals.length; i++) {
				str.append(vals[i].x).append(',').append(vals[i].y).append(':');
			}
			str.append("\n");
			
			try {
				out.write(str.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		try {
			out.flush();
			out.close();
			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private TrackCoord[] compileTrack(String t) {
		String [] c = t.split(":");
		int size = c.length;
		
		TrackCoord [] ret = new TrackCoord[size];
		
		for (int i=0; i<size; i++) {
			ret[i]= new TrackCoord(Integer.valueOf(c[i].split(",")[0]), Integer.valueOf(c[i].split(",")[1]));
		}
		return ret;
	}
}
