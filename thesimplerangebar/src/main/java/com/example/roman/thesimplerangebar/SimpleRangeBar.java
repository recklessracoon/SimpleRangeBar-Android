package com.example.roman.thesimplerangebar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Roman on 08.09.2017.
 */

public class SimpleRangeBar extends View implements SimpleRangeFunctionality, SimpleRangeVisuals{

    private static String DEBUG_TAG = "GESTURE";

    private static float STANDARD_THICKNESS = 25;
    private static float STANDARD_PADDING = 50;

    private static int STANDARD_RANGEBAR_COLOR = Color.GRAY;
    private static int STANDARD_RANGE_COLOR = Color.GREEN;

    private static boolean STANDARD_ROUND_CORNERS = false;

    private boolean roundCorners;

    private int actualThickness;
    private int actualPadding;

    //background rect repesenting the rangebar
    private Rect actualBarRect;
    private Paint actualBarPaint;

    //rect representing the range between the thumbs
    private Rect rangeRect;
    private Paint rangePaint;

    private SimpleThumb thumbLeft, thumbRight; //objects representing both thumbs

    //needed for on touch events
    private boolean leftIsDragged, rightIsDragged, eitherDragged;
    private float dragDirection;

    private SimpleRangeBarOnChangeListener listener; //listener for this view
    private SimpleRatioRepresenter representer; //helper class to calculate pixel position -> value and vice versa

    public SimpleRangeBar(Context context) {
        super(context);
        init(null);
    }

    public SimpleRangeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SimpleRangeBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet){

        representer = new SimpleRatioRepresenter();

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.SimpleRangeBar,
                0, 0);

        actualThickness = (int)a.getDimension(R.styleable.SimpleRangeBar_thickness, STANDARD_THICKNESS);
        actualPadding = (int)a.getDimension(R.styleable.SimpleRangeBar_padding, STANDARD_PADDING);

        actualBarRect = new Rect();
        actualBarPaint = new Paint();
        actualBarPaint.setColor(a.getColor(R.styleable.SimpleRangeBar_colorRangeBar, STANDARD_RANGEBAR_COLOR));

        rangeRect = new Rect();
        rangePaint = new Paint();
        rangePaint.setColor(a.getColor(R.styleable.SimpleRangeBar_colorRange, STANDARD_RANGE_COLOR));

        Paint thumbMain = new Paint();
        thumbMain.setColor(a.getColor(R.styleable.SimpleRangeBar_colorThumb, SimpleThumb.COLOR_STANDARD));

        Paint thumbSecond = new Paint();
        thumbSecond.setColor(a.getColor(R.styleable.SimpleRangeBar_colorThumbPressed, SimpleThumb.COLOR_SECOND_STANDARD));

        thumbLeft = new SimpleThumb();
        thumbLeft.
                withX(actualPadding).
                withMainPaint(thumbMain).
                withSecondaryPaint(thumbSecond).
                withRadius(a.getDimension(R.styleable.SimpleRangeBar_radiusThumb, SimpleThumb.RADIUS_STANDARD));

        thumbRight = new SimpleThumb();
        thumbRight.
                withX(actualPadding).
                withMainPaint(thumbMain).
                withSecondaryPaint(thumbSecond).
                withRadius(a.getDimension(R.styleable.SimpleRangeBar_radiusThumb, SimpleThumb.RADIUS_STANDARD));

        int min = a.getInteger(R.styleable.SimpleRangeBar_minValue, SimpleRatioRepresenter.STANDARD_MIN_VALUE);
        int max = a.getInteger(R.styleable.SimpleRangeBar_maxValue, SimpleRatioRepresenter.STANDARD_MAX_VALUE);

        representer.
                withLeftThumb(thumbLeft).
                withRightThumb(thumbRight).
                withRangeBarBounds(actualBarRect).
                withMinValue(min).
                withMaxValue(max);

        roundCorners = a.getBoolean(R.styleable.SimpleRangeBar_roundCorners, STANDARD_ROUND_CORNERS);

        a.recycle();
    }

    private void dragMotionOnLeftThumbStarted(){
        leftIsDragged = true;
    }

    private void dragMotionOnLeftThumbFinished(){
        leftIsDragged = false;
        invalidate();
    }

    private void dragMotionOnRightThumbStarted(){
        rightIsDragged = true;
    }

    private void dragMotionOnRightThumbFinished(){
        rightIsDragged = false;
        invalidate();
    }

    private void dragMotionOnEitherThumbStarted(float oldX){
        eitherDragged = true;
        dragDirection = oldX;
    }

    private void determineDragMotion(float newX){
        dragMotionOnEitherThumbFinished();

        if(dragDirection < newX)
            dragMotionOnRightThumbStarted();
        else
            dragMotionOnLeftThumbStarted();
    }

    private void dragMotionOnEitherThumbFinished(){
        eitherDragged = false;
    }

    private void moveLeftThumb(float cx){
        if(cx <= thumbRight.cx)
            thumbLeft.cx = cx;
        else // handle overlap of thumbs by fixating both thumbs on one position
            thumbLeft.cx = thumbRight.cx;

        if(cx < actualBarRect.left) // restrict cx of leftthumb to most left actualbar point
            thumbLeft.cx = actualBarRect.left;

        if(listener != null)
            listener.leftThumbValueChanged((long)representer.getLeftThumbValue());

        invalidate();
    }

    private void moveRightThumb(float cx){
        if(cx >= thumbLeft.cx)
            thumbRight.cx = cx;
        else
            thumbRight.cx = thumbLeft.cx;

        if(cx > actualBarRect.right)
            thumbRight.cx = actualBarRect.right;

        if(listener != null)
            listener.rightThumbValueChanged((long)representer.getRightThumbValue());

        invalidate();
    }

    private synchronized void moveLeftThumbFromOtherThread(float cx){

        if(cx <= thumbRight.cx)
            thumbLeft.cx = cx;
        else
            thumbLeft.cx = thumbRight.cx;// there might be 2 threads, each for one thumb, if left finishes first one thumb will not move

        if(cx < actualBarRect.left)
            thumbLeft.cx = actualBarRect.left;

        Activity a = (Activity) getContext();
        if(listener != null) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.leftThumbValueChanged((long) representer.getLeftThumbValue());
                }
            });
        }

        postInvalidate();
    }

    private synchronized void moveRightThumbFromOtherThread(float cx){

        if(cx >= thumbLeft.cx)
            thumbRight.cx = cx;
        else
            thumbRight.cx = thumbLeft.cx; // there might be 2 threads, each for one thumb, if left finishes first one thumb will not move

        if(cx > actualBarRect.right)
            thumbRight.cx = actualBarRect.right;

        Activity a = (Activity) getContext();
        if(listener != null) {
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.rightThumbValueChanged((long) representer.getRightThumbValue());
                }
            });
        }

        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        Rect canvasRect = canvas.getClipBounds();

        actualBarRect.left = actualPadding; // padding to view here
        actualBarRect.top = canvasRect.centerY() - actualThickness /2; // position on y axis here
        actualBarRect.right = canvasRect.right - actualPadding;
        actualBarRect.bottom = actualBarRect.top + actualThickness; // bar thickness here

        representer.boundsChanged();

        rangeRect.left = (int)thumbLeft.cx;
        rangeRect.top = canvasRect.centerY() - actualThickness /2;
        rangeRect.right = (int)thumbRight.cx; // ends on the point of the left thumb
        rangeRect.bottom = rangeRect.top + actualThickness;

        long center = actualBarRect.top + (actualBarRect.height() / 2);

        thumbLeft.
                withY(center);

        thumbRight.
                withY(center);

        canvas.drawRect(actualBarRect, actualBarPaint); // draw rect representing the range of the bar
        if(roundCorners) { // round up actualBarRect corners with circles
            canvas.drawCircle(actualBarRect.left, center, actualThickness/2, actualBarPaint);
            canvas.drawCircle(actualBarRect.right, center, actualThickness/2, actualBarPaint);
        }
        canvas.drawRect(rangeRect, rangePaint); // draw rect representing the range enclosed by the two thumbs

        // reminder of conditional color of the thumb
        // draw circles representing the thumbs
        canvas.drawCircle(thumbLeft.cx, thumbLeft.cy, thumbLeft.radius, (leftIsDragged)? thumbLeft.secondaryPaint : thumbLeft.mainPaint);    canvas.drawCircle(thumbRight.cx, thumbRight.cy, thumbRight.radius, (rightIsDragged)? thumbRight.secondaryPaint : thumbRight.mainPaint);
        canvas.drawCircle(thumbRight.cx, thumbRight.cy, thumbRight.radius, (rightIsDragged)? thumbRight.secondaryPaint : thumbRight.mainPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):

                boolean leftContains = thumbLeft.contains(event.getX(), event.getY());
                boolean rightContains = thumbRight.contains(event.getX(), event.getY());

                if(leftContains && rightContains){ // thumbs overlap
                    dragMotionOnEitherThumbStarted(event.getX());
                    return true;
                }

                if(rightContains) {
                    dragMotionOnRightThumbStarted();
                } else if(leftContains) {
                    dragMotionOnLeftThumbStarted();
                }

                return true;
            case (MotionEvent.ACTION_MOVE):

                if(rightIsDragged /*&& actualBarRect.contains((int)event.getX(), (int)thumbLeft.cy)*/){
                    // only move thumb if it is being dragged, and if its inside the bounds of the rangebar rectangle
                    moveRightThumb(event.getX());
                } else if(leftIsDragged /*&& actualBarRect.contains((int)event.getX(), (int)thumbRight.cy)*/){
                    moveLeftThumb(event.getX());
                } else if(eitherDragged){
                    // determine which direction the drag goes to choose the correct thumb
                    determineDragMotion(event.getX());
                }

                return true;
            case (MotionEvent.ACTION_UP):

                if(rightIsDragged) {
                    dragMotionOnRightThumbFinished();
                } else if(leftIsDragged) {
                    dragMotionOnLeftThumbFinished();
                }

                if(eitherDragged){
                    dragMotionOnEitherThumbFinished();
                }

                return true;
            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "Action was CANCEL"+"["+event.getX()+", "+event.getY()+"]");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                        "of current screen element"+"["+event.getX()+", "+event.getY()+"]");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    @Override
    public void setOnSimpleRangeBarChangeListener(SimpleRangeBarOnChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void setThumbValues(long leftValue, long rightValue) {
        if(representer.boundsValid()) {
            moveRightThumb(representer.xPositionFromValue(rightValue));
            moveLeftThumb(representer.xPositionFromValue(leftValue));
        } else {
            enqueueThumbMovement(leftValue, rightValue);
        }
    }

    private void enqueueThumbMovement(final long leftValue, final long rightValue){
        new Thread(new Runnable() {
            @Override
            public void run() {
                float left = representer.xPositionFromValueAsync(leftValue);
                float right = representer.xPositionFromValueAsync(rightValue);
                moveRightThumbFromOtherThread(right);
                moveLeftThumbFromOtherThread(left);
            }
        }).start();
    }

    @Override
    public long getRightThumbValue() {
        return (long) representer.getRightThumbValue();
    }

    @Override
    public long getLeftThumbValue() {
        return (long) representer.getLeftThumbValue();
    }

    @Override
    public void setRanges(long min, long max) {
        representer.
                withMinValue(min).
                withMaxValue(max);

        invalidate();
    }

    @Override
    public long getMaxRange() {
        return representer.getMaxValue();
    }

    @Override
    public long getMinRange() {
        return representer.getMinValue();
    }

    @Override
    public void setThumbColor(int color) {
        Paint p = new Paint();
        p.setColor(color);
        thumbLeft.withMainPaint(p);
        thumbRight.withMainPaint(p);
        invalidate();
    }

    @Override
    public void setThumbColorPressed(int color) {
        Paint p = new Paint();
        p.setColor(color);
        thumbLeft.withSecondaryPaint(p);
        thumbRight.withSecondaryPaint(p);
        invalidate();
    }

    @Override
    public void setRangeBarColor(int color) {
        actualBarPaint.setColor(color);
        invalidate();
    }

    @Override
    public void setRangeColor(int color) {
        rangePaint.setColor(color);
        invalidate();
    }

    @Override
    public void setRoundCorners(boolean round) {
        roundCorners = round;
        invalidate();
    }

    @Override
    public void setThumbRadius(float radius) {
        thumbLeft.withRadius(radius);
        thumbRight.withRadius(radius);
        invalidate();
    }

    @Override
    public void setPadding(float padding) {
        actualPadding = (int)padding;
        invalidate();
    }

    @Override
    public void setThickness(float padding) {
        actualThickness = (int)padding;
        invalidate();
    }

}
