package uni.oulu.fingertracker.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.util.Date;

import android.content.Context;
import android.util.Log;

public class TrackLogger {

	static final String BEGIN = "LOG START\n";
	static final String END = "LOG END\n";
	
	//BufferedOutputStream os = null;
	private OutputStream os = null;
	private File file = null;
	private Context context;
	private String filename;
	
	public TrackLogger(Context context, String filename) {
		try {
			this.context = context;
			this.filename = filename;
			file = new File(context.getExternalFilesDir(null), "Tracklog_"+filename);
			Log.d("LOGGER", file.getAbsolutePath());
			os = new FileOutputStream(file);
			//os = new BufferedOutputStream(fos);
			//os = context.openFileOutput(filename, Context.MODE_APPEND);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void log(String what) {
		if (os == null)
			return;
		Date now = new Date();
		String str = String.valueOf(now.getTime())+":"+what+"\n";
		try {
			os.write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void log(String key, String value) {
		if (os == null)
			return;
		Date now = new Date();
		String str = String.valueOf(now.getTime())+":"+key+" ["+value+"]\n";
		try {
			os.write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void log(String key, String [] values) {
		if (os == null)
			return;
		Date now = new Date();
		String str = String.valueOf(now.getTime())+":"+key+" [";
		for (String s : values) {
			str+="\""+s+"\",";
		}
		str+="]"+"\n";
		try {
			os.write(str.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() {
		if (os == null)
			return;
		
		Date now = new Date();
		try {
			os.write((String.valueOf(now.getTime())+":"+BEGIN).getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if(os == null)
			return;
		
		Date now = new Date();
		try {
			os.write((String.valueOf(now.getTime())+":"+END).getBytes());
			//os.close();
			//os=null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			os = null;
		}
	}
	public String getLogs() {
		//String [] files = context.getExternalFilesDir(null).list();
		StringBuilder sb = new StringBuilder();
		byte [] cb;
		FileInputStream is = null;
		
		File [] files = context.getExternalFilesDir(null).listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().contains("Tracklog_") && !pathname.getName().equals(filename))
					return true;
				return false;
			}
			
		});
		
		for (File f : files) {
			cb = new byte[(int)f.length()];
			sb.append(f.getName()).append("\n\n");
			try {
				is = new FileInputStream(f.getAbsoluteFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				continue;
			}
			try {
				is.read(cb);
				is.close();
				sb.append(new String(cb,"UTF-8"));
			} catch (IOException e) {
				//smör smör
				e.printStackTrace();
			}
			sb.append("\nEOF\n");
		}
		
		return sb.toString();
	}
	
	public void clearLogs() {
		//String [] files = context.getExternalFilesDir(null).list();
		File [] files = context.getExternalFilesDir(null).listFiles(new FileFilter(){

			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				if (pathname.getName().contains("Tracklog_"))
					return true;
				return false;
			}
			
		});
		
		for (File f : files) {
			f.delete();
		}
		
	}
}
