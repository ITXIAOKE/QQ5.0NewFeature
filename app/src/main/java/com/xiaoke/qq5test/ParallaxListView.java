package com.xiaoke.qq5test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ListView;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class ParallaxListView extends ListView{

	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParallaxListView(Context context) {
		super(context);
	}
	
	private int maxHeight;
	private int originalHeight;
	private ImageView parallaxImage;
	public void setParallaxImage(final ImageView parallaxImage){
		this.parallaxImage = parallaxImage;
		
		//设定最大高度为图片的真实高度
		maxHeight = parallaxImage.getDrawable().getIntrinsicHeight();
		
		//获取最初高度
		parallaxImage.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			/**
			 * 该方法是在完成布局的时候调用
			 */
			@Override
			public void onGlobalLayout() {
				//一般用完立即移除，因为只要有宽高发生变化，那么会重新布局，则会重新引起onGlobalLayout调用
				parallaxImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
				originalHeight = parallaxImage.getHeight();
//				Log.e("tag", "originalHeight:"+originalHeight);
			}
		});
		
	}
	
	/**
	 * 该方法就是在listview滑动到头的时候调用，并且可以在该方法中获取滑动的距离
	 * deltaY: 继续滑动的距离    正值：表示底部到头     负值：顶部到头
	 * maxOverScrollY： 表示到头之后可以继续滑动的最大距离
	 * isTouchEvent: true：表示是手指拖动到头      false：表示是靠惯性滑动到头
	 */
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//		Log.e("tag", "deltaY: "+deltaY   +"  isTouchEvent: "+isTouchEvent);
		//如果是顶部到头，并且是手指拖动到头，才让IMageView的高度增高
		if(deltaY<0 && isTouchEvent){
			//1.计算ImageView的newHeight
			int newHeight = parallaxImage.getHeight() - deltaY/3;
			//2.对newHeight进行限制
			if(newHeight>maxHeight)newHeight = maxHeight;
			//3.将newHeight设置给ImageView
			android.view.ViewGroup.LayoutParams params = parallaxImage.getLayoutParams();
			params.height = newHeight;
			parallaxImage.setLayoutParams(params);
//			parallaxImage.requestLayout();//或者这样写也行
		}
		
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction()==MotionEvent.ACTION_UP){
			//让ImageView的高度缓慢恢复到最初高度即120
			//自定义动画逻辑
			ValueAnimator animator = ValueAnimator.ofInt(parallaxImage.getHeight(),originalHeight);
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					//获取动画当前的值
					int value = (Integer) animator.getAnimatedValue();
					//将动画的值设置给imageVIew的高度
					android.view.ViewGroup.LayoutParams params = parallaxImage.getLayoutParams();
					params.height = value;
					parallaxImage.setLayoutParams(params);
				}
			});
			//设置速度插值器
			animator.setInterpolator(new OvershootInterpolator());
			animator.setDuration(400);
			animator.start();
		}
		return super.onTouchEvent(ev);
	}
	
}
