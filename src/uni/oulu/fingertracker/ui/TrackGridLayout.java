/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 *
 */

package uni.oulu.fingertracker.ui;

import uni.oulu.fingertracker.model.FingerTrackModel;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

public class TrackGridLayout extends GridLayout {
	
	private FingerTrackModel mModel;
	
	public TrackGridLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public TrackGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TrackGridLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onInterceptTouchEvent (MotionEvent ev) {
		int x, y;
		Rect mHitRect = new Rect();
		switch (ev.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	        	break;
	        case MotionEvent.ACTION_UP:
	        	mModel.fingerRaised();
	        	break;
	        	
	        case MotionEvent.ACTION_MOVE:
	        	x = Math.round(ev.getX());
	            y = Math.round(ev.getY());
	            //Log.d("BLOP", String.valueOf(x)+" "+String.valueOf(y));
	        	for (int i = 0; i < getChildCount(); i++) {
	                View child = getChildAt(i);
	                child.getHitRect(mHitRect);
	               
	                if (mHitRect.contains(x, y)) {
	                    child.dispatchTouchEvent(ev);
	                    //Log.d("BLOP", mHitRect.toShortString());
	                    break;
	                }
	            }
	        	break;
	        default:
	            return false;
	    }
		//Make sure to still call through to the superclass, so that
		//the ViewGroup still functions normally (e.g. scrolling)
		return super.onTouchEvent(ev);   
	}
	
	public void setModel(FingerTrackModel m) {
		mModel = m;
	}
}
