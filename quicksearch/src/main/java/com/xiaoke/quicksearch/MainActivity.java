package com.xiaoke.quicksearch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.xiaoke.quicksearch.QuickIndexView.OnLetterChangeListener;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends Activity {
	QuickIndexView quickIndeView;
	ListView listView;
	TextView tv_current_letter;
	ArrayList<Friend> friends = new ArrayList<Friend>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		quickIndeView = (QuickIndexView) findViewById(R.id.quickIndeView);
		listView = (ListView) findViewById(android.R.id.list);
		tv_current_letter = (TextView) findViewById(R.id.tv_current_letter);
		
		//设置字母改变的监听器
		quickIndeView.setOnLetterChangeListener(new OnLetterChangeListener() {
			@Override
			public void onLetterChange(String letter) {
				//根据触摸字母去集合中查找条目
				for (int i = 0; i < friends.size(); i++) {
					String word = friends.get(i).pinyin.charAt(0)+"";
					if(word.equals(letter)){
						//说明找到了，那么则将i的条目放置到顶端
						listView.setSelection(i);
						//由于只需要第一个，那么找到后立即break
						break;
					}
				}
				
				//在界面中间显示当前触摸的字母
				showCurrentLetter(letter);
			}
		});
		
		//填充数据
		fillData();
		//对数据进行排序
		Collections.sort(friends);
		//设置adapter
		listView.setAdapter(new FriendAdapter(this,friends));
		
//		Log.e("tag", PinYinUtil.getPinYin("胡     辣 汤 "));//HULATANG
//		Log.e("tag", PinYinUtil.getPinYin("胡，。辣 【汤】  "));//HULATANG
//		Log.e("tag", PinYinUtil.getPinYin("AC胡 d辣 汤t "));//ACHUdLATANGt
	}
	
	private Handler handler = new Handler();
	/**
	 * 显示当前字母
	 * @param letter
	 */
	protected void showCurrentLetter(String letter) {
//		tv_current_letter.setVisibility(View.VISIBLE);
		tv_current_letter.setText(letter);
		//通过动画的方式显示
		if(!isRunAnim){
			ViewPropertyAnimator.animate(tv_current_letter).scaleX(1).scaleY(1f)
			.setDuration(400)
			.setListener(new MyListener())
			.start();
		}
		
		//过一会儿要消失,每次执行消失任务的时候，需要先移除之前的任务
		handler.removeCallbacksAndMessages(null);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
//				tv_current_letter.setVisibility(View.GONE);
				//通过动画的方式隐藏
				ViewPropertyAnimator.animate(tv_current_letter).scaleX(0).scaleY(0f)
				.setDuration(400)
				.start();
			}
		}, 2000);
	}

	private void fillData() {
		// 虚拟数据
		friends.add(new Friend("李伟"));
		friends.add(new Friend("张三"));
		friends.add(new Friend("阿三"));
		friends.add(new Friend("阿四"));
		friends.add(new Friend("段誉"));
		friends.add(new Friend("段正淳"));
		friends.add(new Friend("张三丰"));
		friends.add(new Friend("陈坤"));
		friends.add(new Friend("林俊杰1"));
		friends.add(new Friend("陈坤2"));
		friends.add(new Friend("王二a"));
		friends.add(new Friend("林俊杰a"));
		friends.add(new Friend("张四"));
		friends.add(new Friend("林俊杰"));
		friends.add(new Friend("王二"));
		friends.add(new Friend("王二b"));
		friends.add(new Friend("赵四"));
		friends.add(new Friend("杨坤"));
		friends.add(new Friend("赵子龙"));
		friends.add(new Friend("杨坤1"));
		friends.add(new Friend("李伟1"));
		friends.add(new Friend("宋江"));
		friends.add(new Friend("宋江1"));
		friends.add(new Friend("李伟3"));
	}
	private boolean isRunAnim = false;
	class MyListener extends AnimatorListenerAdapter{
		@Override
		public void onAnimationStart(Animator animation) {
			isRunAnim = true;
		}
		@Override
		public void onAnimationEnd(Animator animation) {
			isRunAnim = false;
		}
	}
}
