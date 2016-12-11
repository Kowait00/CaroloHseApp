package com.wacker.carolohseapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by wacke on 11.12.2016.
 */
public class CarTopDownView extends View
{
    private CaroloCarSensorData mCarData = new CaroloCarSensorData();
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

        // set up wheels for car
        Paint wheelPaint = new Paint();
        wheelPaint.setColor(Color.DKGRAY);
        RectF wheelRearLeft = new RectF((float)(picTopLeftX), (float)(picBottomRightY-picHeight*0.3), (float)(picTopLeftX+picWidth/8), (float)(picBottomRightY-picHeight*0.1));
        canvas.drawRect(wheelRearLeft, wheelPaint);
        RectF wheelRearRight = new RectF((float)(picBottomRightX-picWidth/8), (float)(picBottomRightY-picHeight*0.3), (float)(picBottomRightX), (float)(picBottomRightY-picHeight*0.1));
        canvas.drawRect(wheelRearRight, wheelPaint);


        RectF wheelFrontLeft = new RectF((float)(picTopLeftX+picWidth/20), (float)(picTopLeftY+picHeight*0.1), (float)(picTopLeftX+picWidth/6), (float)(picTopLeftY+picHeight*0.28));
        // Draw turing angle of the wheel (rotate canvas around wheel center to draw angled rectangle)
        canvas.save();
        canvas.rotate(mCarData.wheelAngle, wheelFrontLeft.centerX(), wheelFrontLeft.centerY());
        canvas.drawRect(wheelFrontLeft, wheelPaint);
        canvas.restore();

        RectF wheelFrontRight = new RectF((float)(picBottomRightX-picWidth/6), (float)(picTopLeftY+picHeight*0.1), (float)(picBottomRightX-picWidth/20), (float)(picTopLeftY+picHeight*0.28));
        // Draw turing angle of the wheel (rotate canvas around wheel center to draw angled rectangle)
        canvas.save();
        canvas.rotate(mCarData.wheelAngle, wheelFrontRight.centerX(), wheelFrontRight.centerY());
        canvas.drawRect(wheelFrontRight, wheelPaint);
        canvas.restore();

        //Draw the car
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
        if(mCarData.distFrontLeft > 0 && mCarData.distFrontLeft < 30) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 210, 58, false, paint );

        if(mCarData.distFrontRight > 0 && mCarData.distFrontRight < 30) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 330, -58, false, paint );

        // Draw the second layer of arcs for the front distance sensors
        // therefore increase oval size by scaleFactor
        float ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.2;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.distFrontLeft > 0 && mCarData.distFrontLeft < 60) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 210, 58, false, paint );


        if(mCarData.distFrontRight > 0 && mCarData.distFrontRight < 60) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 330, -58, false, paint );

        // Draw the third layer of arcs for the front distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.4;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.distFrontLeft > 0 && mCarData.distFrontLeft < 90) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 210, 58, false, paint );

        if(mCarData.distFrontRight > 0 && mCarData.distFrontRight < 90) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 330, -58, false, paint );

        // Draw compass
        float compassCenterX = picTopLeftX+picWidth/2;
        float compassCenterY = picBottomRightY-picHeight/4;
        float compassRadius = picWidth/5;
        Paint compassPaint = new Paint();
        compassPaint.setColor(Color.DKGRAY);
        compassPaint.setAntiAlias(true);
        canvas.drawCircle(compassCenterX, compassCenterY, compassRadius, compassPaint);
        // Draw N on north direction of compass
        compassPaint.setStyle(Paint.Style.FILL);
        compassPaint.setTextSize(compassRadius/3);
        compassPaint.setTextAlign(Paint.Align.CENTER);
        compassPaint.setColor(Color.WHITE);
        canvas.drawText("N", compassCenterX, compassCenterY-(float)(compassRadius*0.7), compassPaint);
        // Draw pointer triangles of the compass, with correct rotation
        canvas.save();
        canvas.rotate(mCarData.heading, compassCenterX, compassCenterY);
        compassPaint = new Paint();
        compassPaint.setColor(Color.WHITE);
        Path path = new Path();
        path.moveTo(compassCenterX, compassCenterY+compassRadius);
        path.lineTo(compassCenterX+compassRadius/4, compassCenterY);
        path.lineTo(compassCenterX-compassRadius/4, compassCenterY);
        path.lineTo(compassCenterX, compassCenterY+compassRadius);
        path.close();
        canvas.drawPath(path, compassPaint);
        compassPaint.setColor(Color.RED);
        path = new Path();
        path.moveTo(compassCenterX, compassCenterY-compassRadius);
        path.lineTo(compassCenterX+compassRadius/4, compassCenterY);
        path.lineTo(compassCenterX-compassRadius/4, compassCenterY);
        path.lineTo(compassCenterX, compassCenterY-compassRadius);
        path.close();
        canvas.drawPath(path, compassPaint);
        canvas.restore();

    }

    public void updateDisplayedData(CaroloCarSensorData carData)
    {
        mCarData = carData;
        invalidate();
    }


}
