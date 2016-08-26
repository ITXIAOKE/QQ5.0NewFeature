package com.xiaoke.nianxing;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GooView extends View{

	private Paint paint;
	public GooView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GooView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GooView(Context context) {
		super(context);
		init();
	}
	private WindowManager windowManager;
	private void init(){
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);//开启抗锯齿
		paint.setColor(Color.RED);
		
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	}
	
	private PointF dragCenter = new PointF(200, 100);
	private float dragRadius = 15;//drag圆的半径
	private PointF gooCenter = new PointF(200, 100);
	private float gooRadius = 15;//goo圆的半径
	private PointF[] dragPointFs = {new PointF(100, 85),new PointF(100, 115)};
	private PointF[] gooPointFs = {new PointF(200, 85),new PointF(200, 115)};
	private PointF controlPointF = new PointF(150, 100);
	
	private double lineK;//斜率
	
	@Override
	protected void onDraw(Canvas canvas) {
		//让画布往上移动一个状态栏的高度
		canvas.translate(0, -Utils.getStatusBarHeight(getResources()));
		
		//动态计算goo圆的半径
		gooRadius = getGooRadius();
		
		//手指移动过程中去动态计算贝塞尔曲线的4个点和控制点
		float yOffset = dragCenter.y-gooCenter.y;
		float xOffset = dragCenter.x-gooCenter.x;
		if(xOffset!=0){
			//计算出斜率
			lineK = yOffset/xOffset;
		}
		//a.动态计算dragPointFs
		dragPointFs = GeometryUtil.getIntersectionPoints(dragCenter,dragRadius, lineK);
		//b.动态计算gooPointFs
		gooPointFs = GeometryUtil.getIntersectionPoints(gooCenter, gooRadius, lineK);
		//c.动态计算控制点
		controlPointF = GeometryUtil.getPointByPercent(dragCenter, gooCenter,0.618f);
		
		
		//1.绘制2个圆
		//绘制drag圆
		canvas.drawCircle(dragCenter.x,dragCenter.y, dragRadius, paint);
		if(!isOutOfRange){
			//绘制goo圆
			canvas.drawCircle(gooCenter.x,gooCenter.y, gooRadius, paint);
			//2.使用贝塞尔曲线绘制2圆连接部分
			Path path = new Path();
			path.moveTo(gooPointFs[0].x,gooPointFs[0].y);//移到起点
			path.quadTo(controlPointF.x, controlPointF.y,dragPointFs[0].x,dragPointFs[0].y);
			path.lineTo(dragPointFs[1].x,dragPointFs[1].y);//以直线的方式连到某个点
			path.quadTo(controlPointF.x, controlPointF.y, gooPointFs[1].x,gooPointFs[1].y);
			path.close();//终点连线到起点，默认会自动闭合
			//绘制路径
			canvas.drawPath(path, paint);
		}
			
		
		//绘制拖拽范围的圈圈
		paint.setStyle(Style.STROKE);//空心圆
		canvas.drawCircle(gooCenter.x, gooCenter.y, maxDistance, paint);
		paint.setStyle(Style.FILL);//实心圆
	}
	
	private float maxDistance = 80;//2圆圆心最大距离
	/**
	 * 根据2圆圆心距离去计算goo圆半径
	 * @return
	 */
	private float getGooRadius(){
		float distance = GeometryUtil.getDistanceBetween2Points(dragCenter, gooCenter);
		//得到百分比
		float fraction = distance/maxDistance;
		//根据百分比计算半径
		return GeometryUtil.evaluateValue(fraction, 15, 3);
	}
	private boolean isOutOfRange = false;
	float distance;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			//改变drag圆圆心坐标
			dragCenter.set(event.getRawX(),event.getRawY());
			
			//1.判断拖拽过程中2圆圆心距离是否超出最大值，如果超出最大值，则不去绘制2圆连接部分
			distance = GeometryUtil.getDistanceBetween2Points(dragCenter, gooCenter);
			isOutOfRange = distance>maxDistance;
			break;
		case MotionEvent.ACTION_UP:
			distance = GeometryUtil.getDistanceBetween2Points(dragCenter, gooCenter);
			isOutOfRange = distance>maxDistance;
			if(isOutOfRange){
				//超出范围，则播放爆炸动画
				playBoomAnim(dragCenter);
				//让darg圆和goo圆重叠
				dragCenter.set(gooCenter);
			}else {
				//则回弹到goo圆圆心位置
				final PointF startPointF = new PointF(dragCenter.x,dragCenter.y);
				ValueAnimator animator = ValueAnimator.ofFloat(1f);
				animator.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animator) {
						float fraction = animator.getAnimatedFraction();
						//在动画执行过程中动态更改drag圆圆心位置
						dragCenter.set(GeometryUtil.getPointByPercent(startPointF, gooCenter, fraction));
						//需要重绘才能生效
						invalidate();
					}
				});
				animator.setInterpolator(new OvershootInterpolator(4));
				animator.setDuration(400);
				animator.start();
			}
			break;
		}
		//重绘
		invalidate();
		return true;
	}

	/**
	 * 播放爆炸动画
	 * @param dragCenter2
	 */
	private void playBoomAnim(PointF point) {
		LayoutParams params = new LayoutParams();
		//将windowManager添加的window设置为透明
		params.format = PixelFormat.TRANSPARENT;
		
		//创建真布局
		final BubbleLayout frameLayout = new BubbleLayout(getContext());
		//创建ImageView
		ImageView imageView = getImageView();
		//将IMageView加到真布局中
		//设置位置
		frameLayout.setBubblePosition(point.x, point.y-Utils.getStatusBarHeight(getResources()));
		frameLayout.addView(imageView);
		
		//播放动画
		AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
		drawable.start();
		
		windowManager.addView(frameLayout, params);
		
		//播放完毕将FrameLayout移除
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				windowManager.removeView(frameLayout);
			}
		}, 601);
	}

	private ImageView getImageView() {
		ImageView imageView = new ImageView(getContext());
		imageView.setLayoutParams(new FrameLayout.LayoutParams(68, 68));
		imageView.setBackgroundResource(R.drawable.boom_anim);//设置帧动画
		return imageView;
	}
	
}
