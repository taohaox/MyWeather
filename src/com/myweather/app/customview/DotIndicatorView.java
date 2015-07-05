package com.myweather.app.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

/**
 * 自定义圆点指示器
 * @author winGyb
 *
 */
public class DotIndicatorView extends View{
	
	private int sizeR = 15;
	private int color = Color.rgb(222, 222, 222);
	private int count = 5;
	private int selColor = Color.WHITE;
	
	private int position = 1;
	/**
	 * 用来画圆的框
	 */
	private Paint paint1 =  new Paint();
	/**
	 * 用来填充被选中的圆
	 */
	private Paint paint2 = new Paint();
	
	private int width;
	private int height;
	
	public DotIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Point point = new Point();
		window.getDefaultDisplay().getSize(point);
		width = point.x;
		height = point.y;
		System.out.println("屏幕宽:"+width);
		System.out.println("屏幕高:"+height);
	}
	/**
	 * 由于不需要再xml中显示  所以构造方法中接收颜色  圆的大小,圆的个数等参数
	 * @param context
	 * @param sizeR   圆的半径
	 * @param color   画框的颜色
	 * @param count   圆的个数
	 * @param selColor 被选中的圆的颜色
	 */
	public DotIndicatorView(Context context,int sizeR,int color,int count,int selColor){
		this(context,null);
		this.sizeR = sizeR;
		this.color = color;
		this.count = count;
		this.selColor = selColor;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		paint1.setColor(color);
		paint1.setAntiAlias(true);//抗锯齿
		paint1.setAlpha(80);
		paint2.setColor(selColor);
		paint2.setAntiAlias(true);//抗锯齿
		//paint1.setStyle(Style.STROKE);//画笔设置为空心的
		int begin = (width-(sizeR*3*count-sizeR))/2;
		for(int i=0;i<count;i++){
			
			
			if(position-1==i){
				canvas.drawCircle(begin+sizeR+(sizeR*3*i), (height+sizeR)*3/5, sizeR, paint2);
			}else{
				canvas.drawCircle(begin+sizeR+(sizeR*3*i), (height+sizeR)*3/5, sizeR, paint1);
				//System.out.println(begin+sizeR+(sizeR*3*i));
				//System.out.println((height-sizeR)/2);
			}
		}
		
	}
	public int getsizeR() {
		return sizeR;
	}
	public void setSizeR(int sizeR) {
		this.sizeR = sizeR;
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSelColor() {
		return selColor;
	}
	public void setSelColor(int selColor) {
		this.selColor = selColor;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
}
