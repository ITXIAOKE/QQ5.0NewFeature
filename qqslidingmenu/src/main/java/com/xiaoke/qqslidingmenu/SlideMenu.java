package com.xiaoke.qqslidingmenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;

public class SlideMenu extends FrameLayout{

	private View menuView;
	private View mainView;
	private int menuWidth;
	private int menuHeight;
	private int mainWidth;
	private int mainHeight;
	private ViewDragHelper viewDragHelper;
	protected int dragRange;
	
	private FloatEvaluator floatEvaluator;//浮点计算器

	public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		viewDragHelper = ViewDragHelper.create(this, callback);
		floatEvaluator = new FloatEvaluator();
	}
	/**
	 * 定义状态常量
	 * @author lxj
	 *
	 */
	public enum SlideState{
		Open,Close
	}
	private SlideState mState = SlideState.Close;//默认是关闭状态
	
	/**
	 * 实现测量自己和子View,由于onMeasure的实现比较麻烦(同时大都是都是固定写法)，所以我们没有必要自己
	 * 去自定义onMeasure的实现，那么一般会选择继承系统已有的布局如FrameLayout，目的是为了让FrameLayout
	 * 帮助我们实现onMeasure
	 */
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		
//		View menuView = getChildAt(0);
//		View mainView = getChildAt(1);
//		
//		//使用View提供好的测量方法
//		measureChild(menuView, widthMeasureSpec, heightMeasureSpec);
//		measureChild(mainView, widthMeasureSpec, heightMeasureSpec);
//	}
	
	/**
	 * 当完成xml的布局填充后执行，准确的来说应该是当前SlideMenu在xml中的结束标签的时候执行,
	 * 所以说该方法执行的时候就知道了当前有几个子View了，一般用来初始化子View的引用
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		menuView = getChildAt(0);
		mainView = getChildAt(1);
	}
	
	/**
	 * 该方法在onMeasure之后执行，所以在该方法中是可以获取到子View的宽高的
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		menuWidth = menuView.getMeasuredWidth();
		menuHeight = menuView.getMeasuredHeight();
		mainWidth = mainView.getMeasuredWidth();
		mainHeight = mainView.getMeasuredHeight();
		
		//设定dragRange的值
		dragRange = (int) (mainWidth*0.6);
	}
	
	/**
	 * 重写onLayout，自己来实现对子View的摆放
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
//		Log.e("tag", "onLayout");
		//super的实现是FrameLayout的默认实现，会让子View叠加
//		super.onLayout(changed, left, top, right, bottom);
		//摆放菜单和主界面
//		menuView.layout(-menuWidth, 0, 0, menuHeight);
//		mainView.layout(0, 0,mainWidth,mainHeight);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//让ViewDragHelper帮助我们判断是否应该拦截
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//将触摸事件交给ViewDragHelper处理
		viewDragHelper.processTouchEvent(event);
		return true;
	}
	
	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		/**
		 * 判断是否捕获当前child的触摸事件
		 * child：表示当前手指触摸的子View
		 * pointerId: 触摸索引的id,没什么用
		 * return返回值： true：表示捕获，   false：不捕获
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==mainView || child==menuView;
		}
		/**
		 * 当View被捕获的时候，一般用来初始化一些操作
		 * capturedChild：表示当前被捕获的View
		 */
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
		}
		/**
		 * 看起来好像是用来限制view水平方向拖拽范围的，然而并不是这样，目前该方法最好不要返回0，
		 * 它内部会判断是否大于0，如果大于0才能进行水平方向的移动，但是还有一个用处：用在计算View
		 * 抬起的时候平滑移动的动画时间上
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			return dragRange;
		}
		/**
		 * 控制child在水平方向的移动
		 * child: 表示当前触摸的子View
		 * left: 表示ViewDragHelper认为child的left将要变成的值：left=child.getLeft()+dx
		 * dx : 表示本次手指移动的距离
		 * return返回值： 表示我们真正想让child的left变成的值
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			//限制mainView
			if(child==mainView){
				//限制left的最大值
				if(left>dragRange){
					left = dragRange;
				}
				//限制left的最小值
				if(left<0){
					left = 0;
				}
			}
//			else if (child==menuView) {
//				//限制菜单不要动
//				left = 0;
//			}
			return left;
		}
		/**
		 * 控制child在垂直方向的移动
		 * child: 表示当前触摸的子View
		 * top: 表示ViewDragHelper认为child的top将要变成的值：top=child.getTop()+dy
		 * dy : 表示本次手指移动的距离
		 * return返回值： 表示我们真正想让child的top变成的值
		 */
		public int clampViewPositionVertical(View child, int top, int dy) {
			return 0;
		}
		/**
		 * 当View的position被改变的回调,一般在该方法中实现伴随移动
		 * changedView： 当前改变位置的view
		 * left: 改变后的left
		 * dx: 水平方向移动的距离
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
//			Log.e("tag", "left: "+left +"   dx: "+dx);
			//如果当前改变的menuView，那么让mainView进行伴随移动
			if(changedView==menuView){
				//手动通过layout的方式限制menuView的位置
				menuView.layout(0,0,menuWidth, menuHeight);
				
				int newLeft = mainView.getLeft()+dx;
				//对newLeft进行范围限制
				if(newLeft>dragRange)newLeft = dragRange;
				if(newLeft<0)newLeft = 0;
				mainView.layout(newLeft,mainView.getTop(),newLeft+mainWidth, 
						mainView.getBottom());
			}
			
			//1.计算mainView滑动的百分比
			float fraction = mainView.getLeft()*1.0f/dragRange;
			//2.根据滑动的百分比执行一些列的伴随动画
			executeAnim(fraction);
			
			//3.执行状态更改的逻辑，和监听器方法的回调
			if(mainView.getLeft()==dragRange && mState!=SlideState.Open){
				//打开
				mState = SlideState.Open;
				if(listener!=null){
					listener.onOpen();
				}
			}else if (mainView.getLeft()==0 && mState!=SlideState.Close) {
				//关闭
				mState = SlideState.Close;
				if(listener!=null){
					listener.onClose();
				}
			}
			//回调拖拽中的方法
			if(listener!=null){
				listener.onDraging(fraction);
			}
			
		}
		/**
		 * 手指抬起的时候执行
		 * xvel:表示x方向滑动的速度
		 * yvel：表示y方向滑动的速度
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			//拿mainView的left作为参考来判断
			if(mainView.getLeft()>dragRange/2){
				//应该打开
				open();
			}else {
				//应该关闭
				close();
			}
			
			if(xvel>150){
				open();
			}
//			Log.e("tag", "xvel: "+xvel);
		}
	};
	/**
	 * 执行伴随动画
	 * @param fraction
	 */
	protected void executeAnim(float fraction) {
		//fraction: 0-1
		//1.让mainView进行缩放
		//start		            end
		//10  -> 20 ->  60  -> 	110
		//value = start + (end-start)*fraction
		
		//1->0.8f
		float scaleValue = floatEvaluator.evaluate(fraction,1f, 0.8f);
		ViewCompat.setScaleX(mainView, scaleValue);
		ViewCompat.setScaleY(mainView, scaleValue);
		//测试旋转效果
//		float rotate = 0f + (180-0)*fraction;
//		ViewCompat.setRotation(mainView, rotate);
//		ViewCompat.setRotationX(mainView, rotate);
//		ViewCompat.setRotationY(mainView, rotate);
		
		//2.让menuView进行缩放，平移
		ViewCompat.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));
		ViewCompat.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));
		
		ViewCompat.setTranslationX(menuView, floatEvaluator.evaluate(fraction,-menuWidth/2,0));
		ViewCompat.setAlpha(menuView, floatEvaluator.evaluate(fraction,0.3f, 1f));
		
		//3.给SlideMenu的背景添加阴影遮罩
		if(getBackground()!=null){
			int color = (Integer) ColorUtil.evaluateColor(fraction,Color.BLACK,Color.TRANSPARENT);
 			getBackground().setColorFilter(color,Mode.SRC_OVER);
		}
		
		
	}
	/**
	 * 打开菜单的方法
	 */
	public void open() {
		//用法跟Scroller一样
		viewDragHelper.smoothSlideViewTo(mainView, dragRange,mainView.getTop());
		//也需要一个刷新,刷新整个View
		ViewCompat.postInvalidateOnAnimation(this);
		
		//Scroller的写法
//		Scroller scroller = new Scroller(getContext());
//		scroller.startScroll(startX, startY, dx, dy);
//		invalidate();
	}
	

	/**
	 * 关闭菜单的方法
	 */
	public void close() {
		//用法跟Scroller一样
		viewDragHelper.smoothSlideViewTo(mainView, 0,mainView.getTop());
		//也需要一个刷新,刷新整个View
		ViewCompat.postInvalidateOnAnimation(this);
	}
	
	/**
	 * 重写computeScroll方法，因为刷新会调用computeScroll
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if(viewDragHelper.continueSettling(true)){
			//如果动画没有结束，那么则继续刷新
			ViewCompat.postInvalidateOnAnimation(this);
		}
		//Scroller的写法
//		Scroller scroller = new Scroller(getContext());
//		if(scroller.computeScrollOffset()){
//			scrollTo(scroller.getCurrX(), scroller.getCurrY());
//			invalidate();
//		}
	}
	
	private OnSlideStateChangeListener listener;
	public void setOnSlideStateChangeListener(OnSlideStateChangeListener listener){
		this.listener = listener;
	}
	public interface OnSlideStateChangeListener{
		/**
		 * 打开的回调
		 */
		void onOpen();
		/**
		 * 关闭的回调
		 */
		void onClose();
		/**
		 * 拖拽中的回调
		 */
		void onDraging(float fraction);
	}
	/**
	 * 返回当前的滑动状态
	 * @return
	 */
	public SlideState getSlideState() {
		return mState;
	}
	
	
}
