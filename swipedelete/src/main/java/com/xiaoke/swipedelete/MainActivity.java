package com.xiaoke.swipedelete;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoke.swipedelete.SwipeLayout.OnSwipeLayoutClickListner;

public class MainActivity extends ActionBarActivity {
	ListView listView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.listView);
		
		//1.填充数据
		listView.setAdapter(new MyAdapter());
		//2.给listview设置item点击
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.e("tag", "onItemClick: "+position);
			}
		});
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = View.inflate(MainActivity.this, R.layout.adapter_list, null);
			}
			
			ViewHolder holder = ViewHolder.getHolder(convertView);
			//绑定数据
			holder.bindData(position);
			
			holder.swipeLayout.setOnSwipeLayoutClickListener(new OnSwipeLayoutClickListner() {
				@Override
				public void onClick() {
					Toast.makeText(MainActivity.this, "item - "+position, 0).show();
				}
			});
			
			return convertView;
		}
	}
	
	static class ViewHolder{
		TextView tv_name;
		SwipeLayout swipeLayout;
		public ViewHolder(View view){
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			swipeLayout = (SwipeLayout) view.findViewById(R.id.swipeLayout);
		}
		/**
		 * 绑定数据
		 * @param position
		 */
		public void bindData(int position) {
			tv_name.setText("牛肉面 - " + position);
		}
		
		public static ViewHolder getHolder(View view){
			ViewHolder holder = (ViewHolder) view.getTag();
			if(holder==null){
				holder = new ViewHolder(view);
				view.setTag(holder);
			}
			return holder;
		}
		
		
		
	}

}
