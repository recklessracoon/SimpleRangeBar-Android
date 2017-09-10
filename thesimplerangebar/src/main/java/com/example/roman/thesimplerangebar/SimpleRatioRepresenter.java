package com.example.roman.thesimplerangebar;

import android.graphics.Rect;
import android.util.Log;

/**
 * Created by Roman on 09.09.2017.
 */

public class SimpleRatioRepresenter {

    public static int STANDARD_MIN_VALUE = 0;
    public static int STANDARD_MAX_VALUE = 100;

    private SimpleThumb leftThumb, rightThumb;
    private Rect rangeBarBounds;

    private long minValue, maxValue;
    private long leftThumbValue, rightThumbValue;

    long valDelta;
    float pxDelta;

    public SimpleRatioRepresenter(){
        minValue = STANDARD_MIN_VALUE;
        maxValue = STANDARD_MAX_VALUE;
        minMaxChanged();
    }

    public SimpleRatioRepresenter withLeftThumb(SimpleThumb leftThumb){
        this.leftThumb = leftThumb;
        return this;
    }

    public SimpleRatioRepresenter withRightThumb(SimpleThumb rightThumb){
        this.rightThumb = rightThumb;
        return this;
    }

    public synchronized SimpleRatioRepresenter withRangeBarBounds(Rect rangeBarBounds){
        this.rangeBarBounds = rangeBarBounds;
        boundsChanged();
        return this;
    }

    public SimpleRatioRepresenter withMaxValue(long maxValue){
        this.maxValue = maxValue;
        minMaxChanged();
        return this;
    }

    public SimpleRatioRepresenter withMinValue(long minValue){
        this.minValue = minValue;
        minMaxChanged();
        return this;
    }

    public long getMinValue(){
        return minValue;
    }

    public long getMaxValue(){
        return maxValue;
    }

    public void minMaxChanged(){
        valDelta = maxValue-minValue;
    }

    public synchronized float getLeftThumbValue(){
        return getValue(leftThumb);
    }

    public synchronized float getRightThumbValue(){
        return getValue(rightThumb);
    }

    public synchronized float xPositionFromValue(long value){
        long inputDelta = value-minValue;
        return (pxDelta/valDelta)*inputDelta+rangeBarBounds.left;
    }

    public synchronized float xPositionFromValueAsync(long value){ //only call from non ui thread
        while(!boundsValid()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        float res = xPositionFromValue(value);
        Log.d("ASYNC","calculated xposition async as: "+res);
        return res;
    }

    public synchronized void boundsChanged(){
        pxDelta = rangeBarBounds.right-rangeBarBounds.left;
        notifyAll();
    }

    public synchronized boolean boundsValid(){
        return pxDelta != 0;
    }

    private float getValue(SimpleThumb thumb){
        float inputDelta = thumb.cx - rangeBarBounds.left;
        return inputDelta*(valDelta/pxDelta)+minValue;
    }

}
