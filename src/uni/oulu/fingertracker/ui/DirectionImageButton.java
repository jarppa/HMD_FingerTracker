package uni.oulu.fingertracker.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class DirectionImageButton extends ImageButton {

	private String mDirData;
	
	public DirectionImageButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public DirectionImageButton(Context context, AttributeSet attrs) {
		super(context,attrs);
	}

	public DirectionImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
	}
	
	public void setData(String d) {
		mDirData = d;
	}
	public String getData() {
		return mDirData;
	}
}
