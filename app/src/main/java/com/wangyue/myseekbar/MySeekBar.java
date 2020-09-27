package com.wangyue.myseekbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MySeekBar extends View {

    /**
     * 条形进度条 bar
     */
    private int barWith,barHeight;
    private int barLeft,barTop;
    //刻度
    private int barMax,barMin,barUnit;
    /** 进度条背景 **/
    private boolean isCustomBackground = false;
    //HSV 颜色 选择
    private boolean isHsvColor = true;
    private int[] hsvColors;
    private int hsvColor;
    private int hsvPosition;

    /**
     * 滑块
     */
    private int thumbWith,thumbHeight;
    private int thumbRadius;
    private int thumbPosition;
    private int thumbColor = Color.WHITE;


    public MySeekBar(Context context) {
        super(context);
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public MySeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //进度条
        barWith = (int) (w - h * 0.9 * 2);
        barHeight = (int) (h * 0.8);
        barLeft = (w - barWith) / 2;
        barTop = (h - barHeight) / 2;
        //滑块
        thumbHeight = (int) (h * 0.9);
        thumbWith = thumbHeight;
        thumbRadius = thumbHeight / 2;
        thumbPosition = barLeft;

        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBarBackground(canvas);
        drawThumb(canvas);
        drawText(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下
                break;

            case MotionEvent.ACTION_MOVE:
                //滑动
                thumbPosition = (int) event.getX();
                //滑块的位置不能超过滑块
                if (thumbPosition < barLeft){
                    thumbPosition = barLeft;
                }
                else if (thumbPosition > barLeft + barWith){
                    thumbPosition = barLeft + barWith;
                }
                if (isHsvColor) {
                    processHsvColor();
                }
                progressPositionListener();
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                //松开
                break;
        }
        return true;
    }

    /**
     * 绘制 进度条的背景
     * @param canvas
     */
    private void drawBarBackground(Canvas canvas){
        Paint barBackgroundPaint = new Paint();
        if (isCustomBackground){

        }
        else if (isHsvColor){
            initHsvColor();
            //防锯齿 会损失一定的性能
            barBackgroundPaint.setAntiAlias(true);
            //设置线帽 结合处为圆弧
            barBackgroundPaint.setStrokeCap(Paint.Cap.ROUND);
            /**
             * 线性起点的x坐标
             * 线性起点的y坐标
             * 线性终点的x坐标
             * 线性终点的y坐标
             * 实现渐变效果的颜色的组合
             * 为前面的颜色组合中的各颜色在渐变中占据的位置（比重），如果为空，则表示上述颜色的集合在渐变中均匀出现
             * 为渲染器平铺的模式，一共有三种
             * -CLAMP 边缘拉伸
             * -REPEAT 在水平和垂直两个方向上重复，相邻图像没有间隙
             * -MIRROR 以镜像的方式在水平和垂直两个方向上重复，相邻图像有间隙
             */
            barBackgroundPaint.setShader(
                    new LinearGradient(
                            barLeft,
                            barTop,
                            barLeft + barWith,
                            barTop + barHeight,
                            hsvColors,
                            null, 
                            Shader.TileMode.CLAMP
                    )
            );
            canvas.drawRoundRect(
                    new RectF(barLeft, barTop, barLeft + barWith, barTop + barHeight),
                    barHeight / 2,
                    barHeight / 2,
                    barBackgroundPaint
            );
        }


    }

    //初始化HSV颜色
    private void initHsvColor(){
        barMin = 0;
        barMax = 360;
        barUnit = 1;
        int colorCount = 12;
        int colorItem = 360/colorCount;
        hsvColors = new int[colorCount + 1];
        float[] hsv = {0f,1f,1f};
        for (int i = 0; i < hsvColors.length; i++){
            float h = colorItem * i;
//            if (h == 360){
//                //色调 0 == 360 的颜色
//                h = 359;
//            }
            hsv[0] = h;
            hsvColors[i] = Color.HSVToColor(hsv);
        }
    }

    /**
     * 绘制 滑块
     * @param canvas
     */
    private void drawThumb(Canvas canvas){
        Paint thumbPaint = new Paint();
        thumbPaint.setColor(thumbColor);
        canvas.drawOval(
                new RectF(
                        thumbPosition - thumbRadius,
                        getHeight() / 2 - thumbRadius,
                        thumbPosition + thumbRadius,
                        getHeight() / 2 + thumbRadius
                ),
                thumbPaint
        );
    }

    /**
     * 绘制进度值
     * @param canvas
     */
    private void drawText(Canvas canvas){
        Paint mPaint = new Paint();
        //文字颜色
        mPaint.setColor(Color.WHITE);

        String text = hsvPosition + "°";
        //
        Rect rect = new Rect();
        float textSize = barHeight * 0.6f;
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(text, 0, text.length(), rect);
        //滑块位置
        int thumbLeft = thumbPosition - thumbRadius;
        //滑块和进度条起始位置关系
        int thumbToBar = thumbLeft - barLeft;
        int y = (getHeight() / 2) - rect.centerY();

        int textLeft;
        int textWidth = (int) (textSize * text.length())/2;
        if (textWidth + thumbRadius > thumbToBar){
            textLeft = (int) (thumbLeft + thumbRadius * 2);
        }
        else{
            textLeft = thumbLeft - textWidth;
        }

        //绘制文字
        canvas.drawText(text, textLeft, y, mPaint);
    }


    /**
     * 计算 hsvColor 的颜色
     */
    private void processHsvColor(){
        //计算 HSV模型的色调值
        float position = thumbPosition - barLeft;
        float item = position / barWith;
        hsvPosition = (int) (item * 360);
        hsvColor = Color.HSVToColor(new float[]{hsvPosition,1f,1f});
    }

    //返回 hsvColor
    public int getHsvColor() {
        return hsvColor;
    }

    //设置 hsvColor
    public void setHsvColor(int hsvColor) {
        this.hsvColor = hsvColor;
        if (isHsvColor){
            float[] hsv = new float[3];
            Color.RGBToHSV(
                    Color.red(hsvColor),
                    Color.blue(hsvColor),
                    Color.green(hsvColor),
                    hsv
            );
            thumbPosition = (int) (barWith / 360 * hsv[0] + barLeft);
            processHsvColor();
            progressPositionListener();
            invalidate();
        }
    }

    /**
     * 设置刻度
     */
    public int getBarMax() {
        return barMax;
    }

    public void setBarMax(int barMax) {
        this.barMax = barMax;
    }

    public int getBarMin() {
        return barMin;
    }

    public void setBarMin(int barMin) {
        this.barMin = barMin;
    }

    public int getBarUnit() {
        return barUnit;
    }

    public void setBarUnit(int barUnit) {
        this.barUnit = barUnit;
    }


    /**
     * 监听时间
     */
    public interface OnChangePositionListener{
        void onChangePosition(int position);
    }

    private OnChangePositionListener onChangePositionListener;

    public void setOnChangePositionListener(OnChangePositionListener onChangePositionListener){
        this.onChangePositionListener = onChangePositionListener;
    }
    
    //处理监听
    private void progressPositionListener(){
        if (onChangePositionListener != null){
            float change = thumbPosition - barLeft;
            float unit = change / barWith / barUnit;
            int position = (int) (unit * barMax);
            onChangePositionListener.onChangePosition(position);
        }
    }
    
}
