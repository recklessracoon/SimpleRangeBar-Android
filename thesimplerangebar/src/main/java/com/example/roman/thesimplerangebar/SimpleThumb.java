package com.example.roman.thesimplerangebar;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Roman on 09.09.2017.
 */

public class SimpleThumb {

    public static float RADIUS_STANDARD = 25;
    public static int COLOR_STANDARD = Color.BLACK;
    public static int COLOR_SECOND_STANDARD = Color.GREEN;

    public float cx, cy, radius;
    public Paint mainPaint, secondaryPaint;

    public SimpleThumb(){
        Paint paint = new Paint();
        paint.setColor(COLOR_STANDARD);

        Paint paint2 = new Paint();
        paint.setColor(COLOR_SECOND_STANDARD);

        init(0,0, RADIUS_STANDARD, paint, paint2);
    }

    private void init(float cx, float cy, float radius, Paint paint, Paint secondaryPaint){
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.mainPaint = paint;
        this.secondaryPaint = secondaryPaint;
    }

    public boolean contains(float x, float y){
        // only an approximation with 4 rectangles, if the point is in either, this circle "contains" the point

        if((x >= (cx - radius) && x <= cx) && (y >= (cy - radius) && y <= cy))
            return true;

        if((x <= (cx + radius) && x >= cx) && (y <= (cy + radius) && y >= cy))
            return true;

        if((x >= (cx - radius) && x <= cx) && (y <= (cy + radius) && y >= cy))
            return true;

        if((x <= (cx + radius) && x >= cx) && (y >= (cy - radius) && y <= cy))
            return true;

        return false;
    }

    public SimpleThumb withMainPaint(Paint mainPaint){
        this.mainPaint = mainPaint;
        return this;
    }

    public SimpleThumb withSecondaryPaint(Paint secondaryPaint){
        this.secondaryPaint = secondaryPaint;
        return this;
    }

    public SimpleThumb withX(float cx){
        this.cx = cx;
        return this;
    }

    public SimpleThumb withY(float cy){
        this.cy = cy;
        return this;
    }

    public SimpleThumb withRadius(float r){
        this.radius = r;
        return this;
    }
}
