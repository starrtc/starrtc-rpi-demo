package com.starrtc.iot_car.demo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HandleView extends View {

    private Paint mPaintForCircle;
    private HandleReaction mHandleReaction;
    private float[] touchPosition ;
    int radiusOuter =1;
    int radiusInner = 1;

    public HandleView(Context context) {
        this(context, null);
    }

    public HandleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Compare to: {@link android.view.View#getDefaultSize(int, int)}
     * If mode is AT_MOST, return the child size instead of the parent size
     * (unless it is too big).
     */
    private static int getDefaultSize2(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    public void setHandleReaction(final HandleReaction handleReaction) {
        mHandleReaction = handleReaction;
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE: {
                        touchPosition[0] = motionEvent.getX();
                        touchPosition[1] = motionEvent.getY();
                        if(touchPosition[0]<radiusInner){
                            touchPosition[0]=radiusInner;
                        }else if(touchPosition[0]>getWidth()-radiusInner){
                            touchPosition[0]=getWidth()-radiusInner;
                        }
                        if(touchPosition[1]<radiusInner){
                            touchPosition[1]=radiusInner;
                        }else if(touchPosition[1]>getHeight()-radiusInner){
                            touchPosition[1]=getHeight()-radiusInner;
                        }
                        HandleView.this.invalidate();
                        handleReaction.report((touchPosition[0]-radiusInner)/(getWidth()-radiusInner*2),(touchPosition[1]-radiusInner)/(getHeight()-radiusInner*2));
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        touchPosition[0] = getWidth()/2;
                        touchPosition[1] = getHeight()/2;
                        HandleView.this.invalidate();
                        handleReaction.report(0.5f,0.5f);
                        return true;
                    }
                }
                return true;
            }
        });
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize2(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize2(getSuggestedMinimumHeight(), heightMeasureSpec));
        int childWidthSize = getMeasuredWidth();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(null == touchPosition){
            touchPosition = new float[2];
            touchPosition[0] = getWidth()/2;
            touchPosition[1] = getHeight()/2;
        }

        canvas.drawColor(Color.TRANSPARENT);


        float cx = getWidth() / 2;
        float cy = getHeight() / 2;
        radiusOuter = getWidth() / 2;
        radiusInner = getWidth() / 5;

        if (null == mPaintForCircle) {
            mPaintForCircle = new Paint();
        }

        mPaintForCircle.setAntiAlias(true);
        mPaintForCircle.setStyle(Paint.Style.FILL);

        mPaintForCircle.setColor(Color.argb(0x7f, 0xcc, 0xcc, 0xcc));
        canvas.drawCircle(cx, cy, radiusOuter, mPaintForCircle);
//        canvas.drawRect(0,0,getWidth(),getHeight(), mPaintForCircle);

        if (touchPosition[0] == cx && touchPosition[1] == cy) {
            mPaintForCircle.setColor(Color.argb(0xff, 0x11, 0x11, 0x11));
            canvas.drawCircle(cx, cy, radiusInner, mPaintForCircle);
            canvas.save();
            return;
        }
        //圆球不超出操控区
//        mPaintForCircle.setColor(Color.argb(0xff, 0x11, 0x11, 0x11));
//        double ratio = (radiusOuter - radiusInner) / Math.sqrt(
//                Math.pow(touchPosition[0] - cx - getLeft(), 2) + Math.pow(touchPosition[1] - cy - getTop(),2));
//        float cx2 = (float) (ratio * (touchPosition[0] - cx - getLeft()) + cx);
//        float cy2 = (float) (ratio * (touchPosition[1] - cy - getTop()) + cy);
//        canvas.drawCircle(cx2, cy2, radiusInner, mPaintForCircle);
        mPaintForCircle.setColor(Color.argb(0xff, 0x11, 0x11, 0x11));
        canvas.drawCircle(touchPosition[0], touchPosition[1], radiusInner, mPaintForCircle);
        canvas.save();
    }

    public interface HandleReaction {
        void report(float h,float v);
    }
}