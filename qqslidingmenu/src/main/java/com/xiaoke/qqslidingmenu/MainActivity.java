package com.xiaoke.qqslidingmenu;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;
import com.xiaoke.qqslidingmenu.SlideMenu.OnSlideStateChangeListener;

import java.util.Random;

public class MainActivity extends Activity {
	ListView menu_listview,main_listview;
	SlideMenu slideMenu;
	ImageView iv_head;
	MyLinearLayout my_layout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		menu_listview = (ListView) findViewById(R.id.menu_listview);
		main_listview = (ListView) findViewById(R.id.main_listview);
		slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
		
		//填充数据
		main_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.NAMES));
		menu_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.sCheeseStrings){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = (TextView) super.getView(position, convertView, parent);
				textView.setTextColor(Color.WHITE);
				return textView;
			}
		});
		
		//设置滑动状态改变的监听器
		slideMenu.setOnSlideStateChangeListener(new OnSlideStateChangeListener() {
			@Override
			public void onOpen() {
				menu_listview.smoothScrollToPosition(new Random().nextInt(Constant.sCheeseStrings.length));
			}
			@Override
			public void onDraging(float fraction) {
				ViewCompat.setAlpha(iv_head, 1-fraction);
			}
			@Override
			public void onClose() {
				//ViewPropertyAnimator是对ObjectAnimator的简化封装
				ViewPropertyAnimator.animate(iv_head).translationX(25)
									.setInterpolator(new CycleInterpolator(20))
									.setDuration(1000)
									.start();
//				ViewPropertyAnimator.animate(iv_head).rotationYBy(360)
//									.setDuration(500)
//									.start();
				
				//使用下面的方法执行属性动画也行，下面的方法是V4包中提供的
//				ViewCompat.animate(iv_head).translationX(100).setDuration(300).start();
			}
		});
		
		//给MylinearLayout设置SlideMenu
		my_layout.setSlideMenu(slideMenu);
		
		
	}

}
