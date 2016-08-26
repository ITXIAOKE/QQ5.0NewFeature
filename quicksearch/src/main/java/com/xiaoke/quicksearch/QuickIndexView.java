package com.xiaoke.quicksearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class QuickIndexView extends View{
	private String[] indexArr = { "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z" };
	private Paint paint;
	private final int DEFAULT_COLOR = Color.WHITE;//默认颜色
	private final int PRESSED_COLOR = Color.BLACK;//按下颜色
	public QuickIndexView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public QuickIndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public QuickIndexView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);//开启抗锯齿
		paint.setColor(DEFAULT_COLOR);
		paint.setTextSize(16);
		//设置文字绘制的起点
		paint.setTextAlign(Align.CENTER);//设置文字绘制起点是文字底边中心
	}
	private int width;
	private float cellHeight;//格子高度
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getMeasuredWidth();
		cellHeight = getMeasuredHeight()*1f/indexArr.length;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		float x = width/2;//宽度的一般
		//遍历数组，绘制26个字母
		for (int i = 0; i < indexArr.length; i++) {
			String text = indexArr[i];
			//y:格子高度一半 + 文字高度一半  + i*格子高度
			float y = cellHeight/2 + getTextHeight(text)/2 + i*cellHeight;
			
			//判断当前正在绘制的是否和当前触摸的index一致
			paint.setColor(i==index?PRESSED_COLOR:DEFAULT_COLOR);
			
			canvas.drawText(text, x, y, paint);
		}
	}
	
	private int index = -1;//注意：初始化值不能是0，因为0是有效的值
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			//如果上一个字母索引和当前的字母索引不一致才打印
			if(index!=(int) (event.getY()/cellHeight)){
				index = (int) (event.getY()/cellHeight);
				//进行简单的安全性的检查
				if(index>=0 && index<indexArr.length){
					String letter = indexArr[index];
					//回调监听器的方法
					if(listener!=null){
						listener.onLetterChange(letter);
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			//抬起的时候需要重置index
			index = -1;
			break;
		}
		//引起重绘
		invalidate();
		return true;
	}
	
	/**
	 * 获取文字高度
	 * @param text
	 * @return
	 */
	private int getTextHeight(String text){
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0,text.length(), bounds);//只有执行完，bounds就有值了
		return bounds.height();
	}
	
	private OnLetterChangeListener listener;
	public void setOnLetterChangeListener(OnLetterChangeListener listener){
		this.listener = listener;
	}
	public interface OnLetterChangeListener{
		void onLetterChange(String letter);
	}
	
	
}
