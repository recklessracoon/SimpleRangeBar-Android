package com.example.roman.thesimplerangebar;

/**
 * Created by Roman on 09.09.2017.
 */

public interface SimpleRangeFunctionality {

    void setThumbValues(long leftValue, long rightValue);

    long getRightThumbValue();
    long getLeftThumbValue();

    void setRanges(long min, long max);
    long getMaxRange();
    long getMinRange();

    void setOnSimpleRangeBarChangeListener(SimpleRangeBarOnChangeListener listener);
}
