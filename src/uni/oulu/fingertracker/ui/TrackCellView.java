/*
 * Author: Jari Tervonen <jjtervonen@gmail.com> 2013-
 *
 */
package uni.oulu.fingertracker.ui;

import uni.oulu.fingertracker.R;
import uni.oulu.fingertracker.R.drawable;
import uni.oulu.fingertracker.model.FingerTrackModel;
import uni.oulu.fingertracker.model.TrackNode;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class TrackCellView extends TrackNode {
	private FingerTrackModel mModel;
	private Paint p = new Paint();
	private RectF mDrawRect;
	
	public TrackCellView(Context context, FingerTrackModel m) {
		super(context);
		mModel = m;
		setBackgroundColor(Color.BLACK);
		setClickable(true);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			if (mModel.isCreating()) {
				if (mModel.nodeInTrack(this))
					mModel.removeNodeFromTrack(this);
				else
					mModel.addTrackNode(this);
				invalidate();
				
			}
			return true;
			
		case MotionEvent.ACTION_MOVE:
			if (mModel.isCreating()) {
				break;
			}
			int x = Math.round(event.getX());
            int y = Math.round(event.getY());
            //Log.d("BLOP", String.valueOf(x)+" "+String.valueOf(y));
        	
            Rect mHitRect = new Rect();
            getHitRect(mHitRect);
               
            if (!mHitRect.contains(x, y)) {
            	return false;
            }
			//Log.d("blio", "itsmee:"+this.toString());
		    mModel.nodeVisited(this);
		    
		    invalidate();
			return true;
			
		default:
			break;
		}
		
		/*if (event.getAction() != MotionEvent.ACTION_DOWN &&
				event.getAction() != MotionEvent.ACTION_MOVE)
			return false;
		
		if (mModel.isCreating() && event.getAction() == MotionEvent.ACTION_DOWN) {
			if (mModel.nodeInTrack(this))
				mModel.removeTrackNode(this);
			else
				mModel.addTrackNode(this);
		}
		else if (mModel.isTracking()) {
			if(event.getAction() == MotionEvent.ACTION_MOVE || 
					event.getAction() == MotionEvent.ACTION_DOWN) {
				mModel.nodeVisited(this);
			}
		}*/
		invalidate();
		return false;
	}

	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//Log.d("blop", String.valueOf(View.MeasureSpec.getSize(widthMeasureSpec)));
		//Log.d("blop", String.valueOf(View.MeasureSpec.getSize(heightMeasureSpec)));
		
		//TODO: Make things properly!
		int tot_h = View.MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec)/mModel.getCols(), tot_h/mModel.getRows());
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mDrawRect = new RectF(0,0,getWidth()-2, getHeight()-2);
	}
	
	@Override
	protected void onDraw(Canvas c) {
		//setBackgroundColor(Color.BLUE);
		super.onDraw(c);
		
		p.setStyle(Style.STROKE);
		p.setColor(Color.GRAY);
		c.drawRoundRect(mDrawRect, 6.0f, 6.0f, p);
		p.setStyle(Style.FILL);
		
		if (mModel.isTracking()) {
			Resources res = getResources();
			String d;
	
			setBackgroundColor(Color.BLACK);
			
			if (mModel.getDebug()) {
				d = getData(mModel.getDirContextIndex());
					
				if (d == FingerTrackModel.DIR_DOWN_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.down));
				else if (d == FingerTrackModel.DIR_UP_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.up));
				else if (d == FingerTrackModel.DIR_RIGHT_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.right));
				else if (d == FingerTrackModel.DIR_LEFT_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.left));
				else if (d == FingerTrackModel.DIR_RIGHT_DOWN_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.down_right));
				else if (d == FingerTrackModel.DIR_LEFT_DOWN_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.down_left));
				else if (d == FingerTrackModel.DIR_RIGHT_UP_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.up_right));
				else if (d == FingerTrackModel.DIR_LEFT_UP_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.up_left));
				else if (d == FingerTrackModel.DIR_CONFIRM_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.confirmation));
				else if (d == FingerTrackModel.DIR_START_KEY)
					setBackgroundDrawable(res.getDrawable(R.drawable.start));
			}
			else {
				if (isChecked()) {
					p.setColor(Color.GREEN);
					float x = getWidth()/2.0f;
					float y = getHeight()/2.0f;
					float radius = (float) (getHeight() < getWidth() ? getHeight() : getWidth())/2.0f;
					c.drawCircle( x, y, radius, p);
				}
				else
					setBackgroundColor(Color.BLACK);
			}
			
			/*if (isStart())
				setBackgroundColor(Color.MAGENTA);
			
			if (isVisited())
				setBackgroundColor(Color.GREEN);*/
			
		}
		else if (mModel.isCreating()) {
			if (mModel.nodeInTrack(this)) {
				p.setColor(Color.RED);
				float x = getWidth()/2.0f;
				float y = getHeight()/2.0f;
				float radius = (float) (getHeight() < getWidth() ? getHeight() : getWidth())/2.0f;
				c.drawCircle( x, y, radius, p);
			}else {
				setBackgroundColor(Color.BLACK);
			}
		}
		//super.onDraw(c);
	}
}
