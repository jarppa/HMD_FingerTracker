/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 *
 */

package uni.oulu.fingertracker.model;

import java.util.Hashtable;

import android.content.Context;
import android.view.View;

public class TrackNode extends View{

	//private long mData = 0x0;
	private boolean bChecked = false;
	private String mDirToNext = "NONE";
	private boolean bStart = false;
	private boolean bGoal = false;
	private int id = -1;
	private Hashtable<Integer,String> data_contexts = new Hashtable<Integer,String>();
	
	public TrackNode(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setId(int i) {
		id = i;
	}
	public int getId() {
		return id;
	}
	
	public void setData(String d) {
		//mData = d;
		data_contexts.put(0,d);
	}
	public void setData(String d, int context) {
		//mData = d;
		data_contexts.put(context,d);
	}

	public void clearData() {
		data_contexts.clear();
	}
	
	public String getData() {
		try {
			return data_contexts.get(0);
		}catch(ArrayIndexOutOfBoundsException ae) {
			return "NONE";
		}
	}
	public String getData(int context) {
		if (context < 0)
			return "NONE";
		return data_contexts.get(context);
	}

	public void setChecked(boolean checked) {
		bChecked = checked;
	}
	
	public void setStart(boolean s) {
		bStart = s;
	}
	
	public boolean isStart() {
		return bStart;
	}
	
	public void setGoal(boolean s) {
		bGoal = s;
	}
	public boolean isGoal() {
		return bGoal;
	}
	
	public boolean isChecked() {
		return bChecked;
	}
	
	public void setDirToNext(String d) {
		mDirToNext = d;
	}
	
	public String getDirToNext() {
		return mDirToNext;
	}
}
