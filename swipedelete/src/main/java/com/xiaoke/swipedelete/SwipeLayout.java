package com.xiaoke.swipedelete;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

public class SwipeLayout extends FrameLayout{

	private View contentView;
	private View deleteView;
	private int contentWidth;
	private int contentHeight;
	private int deleteWidth;
	private int deleteHeight;
	
	private ViewDragHelper viewDragHelper;
	private int touchSlop;//
 
	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwipeLayout(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		viewDragHelper = ViewDragHelper.create(this, callback);
		touchSlop = ViewConfiguration.getTouchSlop();
	}
	
	public enum SwipeState{
		Open,Close
	}
	private SwipeState mState = SwipeState.Close;//默认的状态是关闭的
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//作简单的异常处理
		if(getChildCount()!=2){
			throw new IllegalArgumentException("SwipeLayout only can have 2 children!");
		}
		contentView = getChildAt(0);
		deleteView = getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		contentWidth = contentView.getMeasuredWidth();
		contentHeight = contentView.getMeasuredHeight();
		deleteWidth = deleteView.getMeasuredWidth();
		deleteHeight = deleteView.getMeasuredHeight();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
		contentView.layout(0, 0, contentWidth, contentHeight);
		deleteView.layout(contentWidth,0,contentWidth+deleteWidth, deleteHeight);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		
		//判断当前是否满足可以滑动的条件
		if(!SwipeLayoutManager.getInstance().isCanSwipe(this)){
			//说明不满足，应该首先去关闭已经打开的
			SwipeLayoutManager.getInstance().closeSwipeLayout();
			
			//去拦截事件，交给onTouchEvent处理
			result = true;
		}
		
		return result;
	}
	
	private float downX,downY;
	private long downTime;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!SwipeLayoutManager.getInstance().isCanSwipe(this)){
			//应该不让SwipeLayout滑动
			//此时应该请求ListVIew不要去拦截
			requestDisallowInterceptTouchEvent(true);
			return true;
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			downTime = System.currentTimeMillis();
			break;
		case MotionEvent.ACTION_MOVE:
			//1.获取移动的坐标
			float moveX = event.getX();
			float moveY = event.getY();
			//2.计算移动的距离
			float deltaX = moveX - downX;
			float deltaY = moveY - downY;
			//3.判断滑动的方向是否是偏向于水平的
			if(Math.abs(deltaX)>Math.abs(deltaY)){
				//此时应该请求ListVIew不要去拦截
				requestDisallowInterceptTouchEvent(true);
			}
			
			break;
		case MotionEvent.ACTION_UP:
			//1.计算按下抬起的距离
			float xOffset = event.getX()-downX;
			float yOffset = event.getY()-downY;
			float distance = (float) Math.sqrt(xOffset*xOffset + yOffset*yOffset);
			//2.计算按下抬起的时间
			long duration = System.currentTimeMillis() - downTime;
			if(duration<400 && distance<touchSlop){
				//认为是点击事件
				if(listner!=null){
					listner.onClick();
				}
			}
			break;
		}
		viewDragHelper.processTouchEvent(event);
		return true;
	}
	
	private Callback callback = new Callback() {
		@Override
		public boolean tryCaptureView(View child, int arg1) {
			return child==contentView || child==deleteView;
		}
		@Override
		public int getViewHorizontalDragRange(View child) {
			return deleteWidth;
		}
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			//限制contentVIew的两边
			if(child==contentView){
				if(left>0)left = 0;//限制left最大值
				if(left<-deleteWidth)left = -deleteWidth;//限制left最小值
			}else if (child==deleteView) {
				if(left>contentWidth)left = contentWidth;//限制left最大值
				if(left<(contentWidth-deleteWidth)){
					left = contentWidth-deleteWidth;//限制left最小值
				}
			}
			return left;
		}

		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			//如果当前移动的是contentView，那么让deleteView执行伴随移动
			if(changedView==contentView){
				int newLeft = deleteView.getLeft()+dx;
				deleteView.layout(newLeft,0,newLeft+deleteWidth, deleteView.getBottom());
			}else if (changedView==deleteView) {
				//如果当前移动的是deleteView，那么让contentView执行伴随移动
				int newLeft = contentView.getLeft()+dx;
				contentView.layout(newLeft,0,newLeft+contentWidth, contentView.getBottom());
			}
			
			//处理打开与关闭的逻辑
			if(contentView.getLeft()==-deleteWidth && mState!=SwipeState.Open){
				//说明打开了
				mState = SwipeState.Open;
				
				//需要让SwipeLayoutManager去记录
				SwipeLayoutManager.getInstance().setOpenSwipeLayout(SwipeLayout.this);
			}else if (contentView.getLeft()==0 && mState!=SwipeState.Close) {
				//说明关闭了
				mState = SwipeState.Close;
				
				//需要让SwipeLayoutManager去清除掉所记录的
				SwipeLayoutManager.getInstance().clearSwipeLayout();
			}
		}
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(contentView.getLeft()<-deleteWidth/2){
				//应该打开
				open();
			}else {
				//应该关闭
				close();
			}
		}
	};
	
	/**
	 * 打开
	 */
	public void open() {
		viewDragHelper.smoothSlideViewTo(contentView,-deleteWidth,0);
		//刷新整个VIew
		ViewCompat.postInvalidateOnAnimation(this);
	}
	
	/**
	 * 关闭
	 */
	public void close() {
		viewDragHelper.smoothSlideViewTo(contentView,0,0);
		//刷新整个VIew
		ViewCompat.postInvalidateOnAnimation(this);
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(viewDragHelper.continueSettling(true)){
			//刷新整个VIew
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	
	private OnSwipeLayoutClickListner listner;
	public void setOnSwipeLayoutClickListener(OnSwipeLayoutClickListner listner){
		this.listner = listner;
	}
	public interface OnSwipeLayoutClickListner{
		void onClick();
	}
	
}
