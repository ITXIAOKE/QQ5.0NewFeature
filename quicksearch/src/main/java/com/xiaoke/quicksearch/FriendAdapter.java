package com.xiaoke.quicksearch;

import java.security.PublicKey;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FriendAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Friend> friends;
	
	public FriendAdapter(Context context, ArrayList<Friend> friends) {
		super();
		this.context = context;
		this.friends = friends;
	}

	@Override
	public int getCount() {
		return friends.size();
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
		if(convertView==null){
			convertView = View.inflate(context, R.layout.adapter_friend, null);
		}
		ViewHolder holder = ViewHolder.getHolder(convertView);
		
		//绑定数据
		Friend friend = friends.get(position);
		
		//获取当前的首字母
		String letter = friend.pinyin.charAt(0)+"";
		//获取上一个首字母
		if(position>0){
			String lastLetter = friends.get(position-1).pinyin.charAt(0)+"";
			if(letter.equals(lastLetter)){
				//需要隐藏当前的字母TextView
				holder.tv_letter.setVisibility(View.GONE);
			}else {
				//说明不一样，那么则直接设置字母
				//由于是复用的，所以当需要显示的时候，要重新设置为可见
				holder.tv_letter.setVisibility(View.VISIBLE);
				holder.tv_letter.setText(letter);
			}
		}else {
			//说明当前是第0个条目，则不需要判断，直接设置
			//由于是复用的，所以当需要显示的时候，要重新设置为可见
			holder.tv_letter.setVisibility(View.VISIBLE);
			holder.tv_letter.setText(letter);
		}
		
		holder.tv_name.setText(friend.name);
		
		return convertView;
	}

	
	static class ViewHolder{
		TextView tv_letter,tv_name;
		public ViewHolder (View view){
			tv_letter = (TextView) view.findViewById(R.id.tv_letter);
			tv_name = (TextView) view.findViewById(R.id.tv_name);
		}
		public static ViewHolder getHolder(View view){
			ViewHolder holder = (ViewHolder) view.getTag();
			if(holder == null){
				holder = new ViewHolder(view);
				view.setTag(holder);
			}
			return holder;
		}
	}

}
