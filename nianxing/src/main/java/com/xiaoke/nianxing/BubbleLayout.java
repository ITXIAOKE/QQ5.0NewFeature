package com.xiaoke.nianxing;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 目的是将ImageVIew摆放到合适的位置
 * @author lxj
 *
 */
public class BubbleLayout extends FrameLayout{

	public BubbleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BubbleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BubbleLayout(Context context) {
		super(context);
	}
	
	private float x,y;
	public void setBubblePosition(float x,float y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		View child = getChildAt(0);
		int l = (int) (x-child.getMeasuredWidth()/2);
		int t = (int) (y-child.getMeasuredHeight()/2);
		child.layout(l, t, l+child.getMeasuredWidth(),t+child.getMeasuredHeight());
	}
	
}
