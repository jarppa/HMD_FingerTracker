package uni.oulu.fingertracker.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import android.content.Context;

public final class FilePatternStore implements PatternStorer {
	
	private Context mContext;
	private String filename = null;
	
	private Hashtable<String,DirectionPattern> directions;// = new Hashtable<String,DirectionPattern>();
	
	/* TODO: Move these to some other place. */
	public static final long[] DIR_CONFIRM_VAL = {0x80208220,0x0};
	public static final long[] DIR_CANCEL_VAL = {0x0,0x0};
	public static final long[] DIR_START_VAL = {0x80ffffff,0x0};
	public static final long[] DIR_STOP_VAL = {0x0,0x0};
	public static final long[] DIR_RIGHT_VAL = {0x800a0000,0x0};
	public static final long[] DIR_LEFT_VAL = {0x80000802,0x0};
	public static final long[] DIR_UP_VAL = {0x80008200,0x0};
	public static final long[] DIR_DOWN_VAL = {0x80200020,0x0};
	public static final long[] DIR_LEFT_DOWN_VAL = {0x80000022,0x0};
	public static final long[] DIR_RIGHT_DOWN_VAL = {0x80280000,0x0};
	public static final long[] DIR_LEFT_UP_VAL = {0x80000a00,0x0};
	public static final long[] DIR_RIGHT_UP_VAL = {0x80028000,0x0};
	public static final long[] DIR_NONE_VAL = {0x0,0x0};
	
	private static final FilePatternStore SINGLETON = new FilePatternStore();
	
	public static FilePatternStore getInstance() {
		return SINGLETON;
	}
	
	private FilePatternStore() {
	
	}
	
	public String getDefaultStore() {
		return "led_patterns5";
	}
	
	public void open(Context c, String filename) {
		
		if (this.filename != null && !this.filename.equals(filename)) {
			//TODO: Throw exception
			return;
		}
		
		mContext = c;
		this.filename = filename;
		
		if (directions == null) {
			directions = new Hashtable<String,DirectionPattern>();
			directions.put(FingerTrackModel.DIR_CONFIRM_KEY, new DirectionPattern(DIR_CONFIRM_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_CANCEL_KEY, new DirectionPattern(DIR_CANCEL_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_START_KEY, new DirectionPattern(DIR_START_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_RIGHT_KEY, new DirectionPattern(DIR_RIGHT_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_LEFT_KEY, new DirectionPattern(DIR_LEFT_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_UP_KEY, new DirectionPattern(DIR_UP_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_DOWN_KEY, new DirectionPattern(DIR_DOWN_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_LEFT_DOWN_KEY, new DirectionPattern(DIR_LEFT_DOWN_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_RIGHT_DOWN_KEY, new DirectionPattern(DIR_RIGHT_DOWN_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_LEFT_UP_KEY, new DirectionPattern(DIR_LEFT_UP_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_RIGHT_UP_KEY, new DirectionPattern(DIR_RIGHT_UP_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_NONE_KEY, new DirectionPattern(DIR_NONE_VAL, 500,500,1));
			directions.put(FingerTrackModel.DIR_STOP_KEY, new DirectionPattern(DIR_STOP_VAL, 500,500,1));
		}
	}

	
	private void loadPatterns() {
		String line = null;
		FileInputStream fis = null;
		//Hashtable<String,DirectionPattern> ret = new Hashtable<String,DirectionPattern>();
		if (filename == null)
			return;
		
		try {
			File f = new File(mContext.getExternalFilesDir(null), filename);
			
			fis = new FileInputStream(f);
			//fis = openFileInput("led_patterns");
			
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
		
		directions.clear();
		
		while(line != null) {
			String [] s = line.split(" ");
			if (s.length == 2)
				directions.put(s[0],DirectionPattern.compilePattern(s[1]));
			
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
	
	public void savePatterns(Hashtable<String,DirectionPattern> patterns) {
		FileOutputStream fos = null;
		try {
			File f = new File(mContext.getExternalFilesDir(null), filename);
			fos = new FileOutputStream(f);
			//fos = openFileOutput("led_patterns", 0);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
		Set<Entry<String, DirectionPattern>> s = patterns.entrySet();
		Iterator <Entry<String, DirectionPattern>> it = s.iterator();
		while(it.hasNext()) {
			StringBuilder str = new StringBuilder();
			Entry<String, DirectionPattern> e = it.next();
			str.append(e.getKey()).append(' ');
			
			DirectionPattern pat = e.getValue();
			str.append(pat.toString());
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

	@Override
	public DirectionPattern getPattern(String id) {
		loadPatterns();
		if (! directions.containsKey(id))
			return null;
		return directions.get(id);
	}

	@Override
	public void putPattern(String id, DirectionPattern dp) {
		directions.put(id,  dp);
		savePatterns(directions);
	}

	@Override
	public void save() {
		savePatterns(directions);
	}

	@Override
	public void load() {
		loadPatterns();
	}

	@Override
	public Map<String, DirectionPattern> getPatterns() {
		return directions;
	}
}
