package com.wacker.carolohseapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by wacke on 11.12.2016.
 */
public class CarTopDownView extends View
{
    private Paint mPaint = new Paint();
    private Drawable mImageTopDownCar;
    private float mImageTopDownCarAspectRatio;

    public CarTopDownView(Context context)
    {
        super(context);

        mImageTopDownCar = ContextCompat.getDrawable(context, R.drawable.truck_topdown);
        mImageTopDownCarAspectRatio = (float)mImageTopDownCar.getIntrinsicWidth()/(float)mImageTopDownCar.getIntrinsicHeight();
    }


    @Override
    public void onDraw(Canvas canvas)
    {
        // set up top down car graphic relative to the screen width while maintaining the aspect ratio
        // to keep aspect ratio of graphic: newHeight = newWidth/aspectRatio
        int picWidth = (int)((float)(canvas.getWidth())*(0.4));
        int picHeight = (int)(picWidth/mImageTopDownCarAspectRatio);
        int picTopLeftX = canvas.getWidth()/9;                 // horizontal padding of 1/9 canvas width
        int picTopLeftY = (canvas.getHeight()-picHeight)/2;    // vertically centered
        int picBottomRightX = picTopLeftX + picWidth;
        int picBottomRightY = picTopLeftY + picHeight;
        Rect imageBounds = new Rect(picTopLeftX, picTopLeftY, picBottomRightX, picBottomRightY);
        mImageTopDownCar.setBounds(imageBounds);
        mImageTopDownCar.draw(canvas);


        // set up the corners of an ellipse for displaying distance sensor information
        // in a way that it scales with the size of the vehicle graphic
        float ovalRectTopLeftX = picTopLeftX;
        float ovalRectTopLeftY = picTopLeftY-picWidth/8;
        float ovalRectBottomRightX = picTopLeftX+picWidth;
        float ovalRectBottomRightY = picTopLeftY+picWidth-picWidth/2;

        RectF ovalRect = new RectF(ovalRectTopLeftX, ovalRectTopLeftY, ovalRectBottomRightX, ovalRectBottomRightY);

        // set up a paint to make drawArc only paint the outer arc of the oval
        Paint paint = new Paint();
        paint.setStrokeWidth(picWidth/10);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);

        // Draw the inner most arcs for the front distance sensors
        paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 210, 58, false, paint );

        paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 330, -58, false, paint );

        // Draw the second layer of arcs for the front distance sensors
        // therefore increase oval size by scaleFactor
        float ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.2;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 210, 58, false, paint );

        paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 330, -58, false, paint );

        // Draw the third layer of arcs for the front distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.4;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 210, 58, false, paint );

        paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 330, -58, false, paint );




    }


}
