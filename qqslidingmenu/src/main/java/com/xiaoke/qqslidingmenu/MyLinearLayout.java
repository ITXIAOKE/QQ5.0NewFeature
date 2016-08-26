package com.xiaoke.qqslidingmenu;

import com.xiaoke.qqslidingmenu.SlideMenu.SlideState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 目的是当前SlideMenu处于打开的时候去拦截并消费掉子控件的触摸事件
 * 
 * @author lxj
 * 
 */
public class MyLinearLayout extends LinearLayout {

	@SuppressLint("NewApi")
	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}

	private SlideMenu slideMenu;

	public void setSlideMenu(SlideMenu slideMenu) {
		this.slideMenu = slideMenu;
	}
	

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 判断SlideMenu是否处于打开的状态，如果处于打开，那么则应该拦截
		if (slideMenu != null && slideMenu.getSlideState() == SlideState.Open) {

			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 判断SlideMenu是否处于打开的状态，如果处于打开，那么则应该消费
		if (slideMenu != null && slideMenu.getSlideState() == SlideState.Open) {
			
			//如果用户按下，则直接关闭SlideMenu
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				slideMenu.close();
			}
			
			return true;//消费掉事件
		}
		return super.onTouchEvent(event);
	}

}
