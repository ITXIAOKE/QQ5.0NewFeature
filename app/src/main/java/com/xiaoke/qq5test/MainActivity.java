package com.xiaoke.qq5test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	ParallaxListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listview = (ParallaxListView) findViewById(R.id.listview);
		
		//去掉蓝色边缘的阴影
		listview.setOverScrollMode(AbsListView.OVER_SCROLL_NEVER);
		
		//添加headerView
		View view = View.inflate(this, R.layout.layout_header, null);
		ImageView parallaxImage = (ImageView) view.findViewById(R.id.parallaxImage);
		listview.setParallaxImage(parallaxImage);
		
		listview.addHeaderView(view);
		
		//填充数据
		listview.setAdapter(new MyAdapter());
	}
	
	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return 30;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(MainActivity.this);
			textView.setText(position+" ");
			textView.setPadding(15, 15, 15, 15);
			textView.setGravity(Gravity.CENTER);
			return textView;
		}
		
	}

}
