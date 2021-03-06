package com.fsl.mcu.kinetislist;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.widget.HorizontalScrollView;


/** This is my horizontal scroll derived from HorizontalScrollView which may have a buddy to scroll together */
public class MyHscroll extends HorizontalScrollView {
	private static final String TAG = "HorizontalScrollView()";
	private HorizontalScrollView myBuddy=null;

	public MyHscroll(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (myBuddy != null) {
			if (l != oldl)
				myBuddy.scrollTo(l, 0);
		}
	}

	protected void setBuddy(HorizontalScrollView buddy) {
		myBuddy = buddy;
	}
}
