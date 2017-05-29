package com.wacker.carolohseapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by wacke on 11.12.2016.
 */
public class CarTopDownView extends View
{
    private Context mContext;
    private CaroloCarSensorData mCarData = new CaroloCarSensorData();
    private Paint mPaint = new Paint();
    private Drawable mImageTopDownCar;
    private float mImageTopDownCarAspectRatio;
    private int mTireTreadTextureId = 1;
    private Drawable mTireTreadTexture;

    public CarTopDownView(Context context)
    {
        super(context);
        mContext = context;

        mImageTopDownCar = ContextCompat.getDrawable(context, R.drawable.truck_topdown);
        mImageTopDownCarAspectRatio = (float)mImageTopDownCar.getIntrinsicWidth()/(float)mImageTopDownCar.getIntrinsicHeight();
        mTireTreadTextureId = 1;
        mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_01);
        simulateWheelSpin.run();

    }


    @Override
    public void onDraw(Canvas canvas)
    {
        // Take height of the android navigation bar into account, when present
        int canvasHeight = 0;
        Resources resources = mContext.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) canvasHeight = canvas.getHeight() - resources.getDimensionPixelSize(resourceId);
        else canvasHeight = canvas.getHeight();

        // set up top down car graphic relative to the screen width while maintaining the aspect ratio
        // to keep aspect ratio of graphic: newHeight = newWidth/aspectRatio
        int picWidth = (int)((float)(canvas.getWidth())*(0.4));
        int picHeight = (int)(picWidth/mImageTopDownCarAspectRatio);
        int picTopLeftX = canvas.getWidth()/9;                 // horizontal padding of 1/9 canvas width
        int picTopLeftY = (canvasHeight-picHeight)/2;    // vertically centered
        int picBottomRightX = picTopLeftX + picWidth;
        int picBottomRightY = picTopLeftY + picHeight;
        Rect imageBounds = new Rect(picTopLeftX, picTopLeftY, picBottomRightX, picBottomRightY);
        mImageTopDownCar.setBounds(imageBounds);


        // set up wheels for car
        Rect tireTextureBoundaries = new Rect();
        Paint wheelPaint = new Paint();
        wheelPaint.setColor(Color.DKGRAY);
        RectF wheelRearLeft = new RectF((float)(picTopLeftX), (float)(picBottomRightY-picHeight*0.3), (float)(picTopLeftX+picWidth/8), (float)(picBottomRightY-picHeight*0.1));
        canvas.drawRect(wheelRearLeft, wheelPaint);
        wheelRearLeft.round(tireTextureBoundaries);
        mTireTreadTexture.setBounds(tireTextureBoundaries);
        mTireTreadTexture.draw(canvas);

        RectF wheelRearRight = new RectF((float)(picBottomRightX-picWidth/8), (float)(picBottomRightY-picHeight*0.3), (float)(picBottomRightX), (float)(picBottomRightY-picHeight*0.1));
        canvas.drawRect(wheelRearRight, wheelPaint);
        wheelRearRight.round(tireTextureBoundaries);
        mTireTreadTexture.setBounds(tireTextureBoundaries);
        mTireTreadTexture.draw(canvas);

        RectF wheelFrontLeft = new RectF((float)(picTopLeftX+picWidth/20), (float)(picTopLeftY+picHeight*0.1), (float)(picTopLeftX+picWidth/6), (float)(picTopLeftY+picHeight*0.28));
        // Draw turing angle of the wheel (rotate canvas around wheel center to draw angled rectangle)
        canvas.save();
        canvas.rotate((float)mCarData.rotationYaw_K, wheelFrontLeft.centerX(), wheelFrontLeft.centerY());
        canvas.drawRect(wheelFrontLeft, wheelPaint);
        wheelFrontLeft.round(tireTextureBoundaries);
        mTireTreadTexture.setBounds(tireTextureBoundaries);
        mTireTreadTexture.draw(canvas);
        canvas.restore();

        RectF wheelFrontRight = new RectF((float)(picBottomRightX-picWidth/6), (float)(picTopLeftY+picHeight*0.1), (float)(picBottomRightX-picWidth/20), (float)(picTopLeftY+picHeight*0.28));
        // Draw turing angle of the wheel (rotate canvas around wheel center to draw angled rectangle)
        canvas.save();
        canvas.rotate((float)mCarData.rotationYaw_K, wheelFrontRight.centerX(), wheelFrontRight.centerY());
        canvas.drawRect(wheelFrontRight, wheelPaint);
        wheelFrontRight.round(tireTextureBoundaries);
        mTireTreadTexture.setBounds(tireTextureBoundaries);
        mTireTreadTexture.draw(canvas);
        canvas.restore();

        //Draw the car
        mImageTopDownCar.draw(canvas);

        Paint labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(canvas.getWidth()/25);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        // Distance sensors front
        // set up the corners of an ellipse for displaying distance sensor information
        // in a way that it scales with the size of the vehicle graphic
        float ovalRectTopLeftX = picTopLeftX;
        float ovalRectTopLeftY = picTopLeftY-picWidth/6;
        float ovalRectBottomRightX = picTopLeftX+picWidth;
        float ovalRectBottomRightY = picTopLeftY+picWidth/2;

        RectF ovalRect = new RectF(ovalRectTopLeftX, ovalRectTopLeftY, ovalRectBottomRightX, ovalRectBottomRightY);

        // set up a paint to make drawArc only paint the outer arc of the oval
        Paint paint = new Paint();
        paint.setStrokeWidth(picWidth/10);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);

        // Draw the inner most arcs for the front distance sensors
        if(mCarData.environmentUS_Front > 0 && mCarData.environmentUS_Front < 1) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 220, 100, false, paint );

        // Draw the second layer of arcs for the front distance sensors
        // therefore increase oval size by scaleFactor
        float ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.2;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.environmentUS_Front > 0 && mCarData.environmentUS_Front < 2) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 220, 100, false, paint );

        // Draw the third layer of arcs for the front distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.4;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.environmentUS_Front > 0 && mCarData.environmentUS_Front < 3) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 220, 100, false, paint );

        // Draw labelling with actual dist value
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.14;
        canvas.drawText( String.format("%.2f", (mCarData.environmentUS_Front) ) + " m", ovalRectTopLeftX+(ovalRectBottomRightX-ovalRectTopLeftX)/2, ovalRectTopLeftY-ovalScaling, labelPaint);

        // Distance sensors rear
        // set up the corners of an ellipse for displaying distance sensor information
        // in a way that it scales with the size of the vehicle graphic
        ovalRectTopLeftX = picTopLeftX;
        ovalRectTopLeftY = picBottomRightY-picWidth/2;
        ovalRectBottomRightX = picBottomRightX;
        ovalRectBottomRightY = picBottomRightY+picWidth/6;

        ovalRect = new RectF(ovalRectTopLeftX, ovalRectTopLeftY, ovalRectBottomRightX, ovalRectBottomRightY);

        // set up a paint to make drawArc only paint the outer arc of the oval
        paint = new Paint();
        paint.setStrokeWidth(picWidth/10);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);

        // Draw the inner most arcs for the rear distance sensors
        if(mCarData.environmentUS_Rear > 0 && mCarData.environmentUS_Rear < 1) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 45, 90, false, paint );

        // Draw the second layer of arcs for the rear distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.2;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.environmentUS_Rear > 0 && mCarData.environmentUS_Rear < 2) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 45, 90, false, paint );

        // Draw the third layer of arcs for the rear distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.4;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.environmentUS_Rear > 0 && mCarData.environmentUS_Rear < 3) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 45, 90, false, paint );

        // Draw labelling with actual dist value
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.24;
        canvas.drawText( String.format("%.2f", (mCarData.environmentUS_Rear) ) + " m", ovalRectTopLeftX+(ovalRectBottomRightX-ovalRectTopLeftX)/2, ovalRectBottomRightY+ovalScaling, labelPaint);


        // Distance sensors side front
        // set up the corners of an ellipse for displaying distance sensor information
        // in a way that it scales with the size of the vehicle graphic
        ovalRectTopLeftX = picBottomRightX-picWidth/2;
        ovalRectTopLeftY = picTopLeftY-picWidth/10;
        ovalRectBottomRightX = picBottomRightX-picWidth/20;
        ovalRectBottomRightY = picTopLeftY+4*picWidth/10;

        ovalRect = new RectF(ovalRectTopLeftX, ovalRectTopLeftY, ovalRectBottomRightX, ovalRectBottomRightY);

        // set up a paint to make drawArc only paint the outer arc of the oval
        paint = new Paint();
        paint.setStrokeWidth(picWidth/15);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);

        // Draw the inner most arcs for the side front distance sensors
        if(mCarData.environmentIR_Side_Front > 0 && mCarData.environmentIR_Side_Front < 0.1) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 345, 10, false, paint );

        // Draw the second layer of arcs for the side front distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.2;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.environmentIR_Side_Front > 0 && mCarData.environmentIR_Side_Front < 0.2) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 345, 10, false, paint );

        // Draw the third layer of arcs for the side front distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.4;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.environmentIR_Side_Front > 0 && mCarData.environmentIR_Side_Front < 0.3) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 345, 10, false, paint );

        // Draw labelling with actual dist value
        labelPaint.setTextAlign(Paint.Align.LEFT);
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.52;
        canvas.save();
        canvas.rotate(-6, (ovalRectTopLeftX+ovalRectBottomRightX)/2, (ovalRectTopLeftY+ovalRectBottomRightY)/2);
        canvas.drawText( String.format("%.2f", (mCarData.environmentIR_Side_Front) ) + " m", ovalRectBottomRightX+ovalScaling, ovalRectTopLeftY+(ovalRectBottomRightY-ovalRectTopLeftY)/2, labelPaint);
        canvas.restore();


        // Distance sensors side rear
        // set up the corners of an ellipse for displaying distance sensor information
        // in a way that it scales with the size of the vehicle graphic
        ovalRectTopLeftX = picBottomRightX-picWidth/2;
        ovalRectTopLeftY = picBottomRightY-4*picWidth/10;
        ovalRectBottomRightX = picBottomRightX-picWidth/20;
        ovalRectBottomRightY = picBottomRightY+picWidth/10;

        ovalRect = new RectF(ovalRectTopLeftX, ovalRectTopLeftY, ovalRectBottomRightX, ovalRectBottomRightY);

        // set up a paint to make drawArc only paint the outer arc of the oval
        paint = new Paint();
        paint.setStrokeWidth(picWidth/15);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStyle(Paint.Style.STROKE);

        // Draw the inner most arcs for the side rear distance sensors
        if(mCarData.environmentIR_Side_Rear > 0 && mCarData.environmentIR_Side_Rear < 0.1) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 5, 10, false, paint );

        // Draw the second layer of arcs for the side rear distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.2;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.environmentIR_Side_Rear > 0 && mCarData.environmentIR_Side_Rear < 0.2) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 5, 10, false, paint );

        // Draw the third layer of arcs for the side rear distance sensors
        // therefore increase oval size by scaleFactor
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.4;
        ovalRect.set(ovalRectTopLeftX-ovalScaling, ovalRectTopLeftY-ovalScaling, ovalRectBottomRightX+ovalScaling, ovalRectBottomRightY+ovalScaling);

        if(mCarData.environmentIR_Side_Rear > 0 && mCarData.environmentIR_Side_Rear< 0.3) paint.setColor(Color.RED);
        else paint.setColor(Color.GREEN);
        canvas.drawArc(ovalRect, 5, 10, false, paint );

        // Draw labelling with actual dist value
        labelPaint.setTextAlign(Paint.Align.LEFT);
        ovalScaling = (ovalRectBottomRightY-ovalRectTopLeftY)*(float)0.52;
        canvas.save();
        canvas.rotate(8, (ovalRectTopLeftX+ovalRectBottomRightX)/2, (ovalRectTopLeftY+ovalRectBottomRightY)/2);
        canvas.drawText( String.format("%.2f", (mCarData.environmentIR_Side_Rear) ) + " m", ovalRectBottomRightX+ovalScaling, ovalRectTopLeftY+3*(ovalRectBottomRightY-ovalRectTopLeftY)/5, labelPaint);
        canvas.restore();


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
        canvas.drawText("start", compassCenterX, compassCenterY-(float)(compassRadius*0.7), compassPaint);
        canvas.drawText("dir", compassCenterX, compassCenterY-(float)(compassRadius*0.4), compassPaint);
        // Draw pointer triangles of the compass, with correct rotation
        canvas.save();
        canvas.rotate((float)mCarData.posePsi, compassCenterX, compassCenterY);
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
        compassPaint.setColor(Color.DKGRAY);
        canvas.drawCircle(compassCenterX, compassCenterY, compassRadius/15, compassPaint);

        // draw wheel speed labelling
        Paint linePaint = new Paint();
        linePaint.setColor(Color.argb(255, 0, 100, 215));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeWidth(canvas.getWidth()/75);
        path = new Path();
        path.moveTo(wheelRearRight.centerX()+wheelRearRight.width()/2, wheelRearRight.centerY());
        path.lineTo(wheelRearRight.centerX()+canvas.getWidth()/6, wheelRearRight.centerY()-wheelRearRight.height()/2);
        path.lineTo(canvas.getWidth()-canvas.getWidth()/20, wheelRearRight.centerY()-wheelRearRight.height()/2);
        canvas.drawPath(path, linePaint);
        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(canvas.getWidth()/25);
        labelPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText( String.format("%.2f", (mCarData.movementV * 3.6) ) + " km/h", canvas.getWidth()-canvas.getWidth()/20, wheelRearRight.centerY()-wheelRearRight.height()/4, labelPaint);

        // draw turning angle labelling
        path = new Path();
        path.moveTo(wheelFrontRight.centerX()+wheelFrontRight.width()/2, wheelFrontRight.centerY());
        path.lineTo(wheelFrontRight.centerX()+canvas.getWidth()/6, wheelFrontRight.centerY()-wheelFrontRight.height()/2);
        path.lineTo(canvas.getWidth()-canvas.getWidth()/20, wheelFrontRight.centerY()-wheelFrontRight.height()/2);
        canvas.drawPath(path, linePaint);
        canvas.drawText( mCarData.rotationYaw_K + " °/s", canvas.getWidth()-canvas.getWidth()/20, wheelFrontRight.centerY()-wheelFrontRight.height()/4, labelPaint);


    }

    // Runnable that sets a new wheel texture to simulate spinning wheels and triggers a redraw of the window (invalidate)
    // reschedules itself to be called again after a certain amount of millis depending on the car's speed
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable simulateWheelSpin = new Runnable(){
        public void run(){

            // Select new wheel tread texture to simulate spinning tire
            // switching to the next texture in sequence every 1/30s accounts for roughly 0.023m/s (with a wheel circumference of ca 0.2m)
            //int cycleNoTextures = (int) Math.ceil(mCarData.movementV / 0.023);
            int cycleNoTextures = (int) Math.ceil( mCarData.movementV / (1/3.6) );   // increase perceived tire spin rate with each 1km/h (1m/s / 3.6)
            if(cycleNoTextures > 6) cycleNoTextures = 6;                            // up to number of textures/2 (for greater values wheels would appear
                                                                                    // to slow down again due to looping through the same textures (wagon wheel effect) )

            mTireTreadTextureId = (mTireTreadTextureId + cycleNoTextures) % 11; // switch between the 11 different textures

            switch(mTireTreadTextureId)
            {
                case 0: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_01); break;
                case 1: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_02); break;
                case 2: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_03); break;
                case 3: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_04); break;
                case 4: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_05); break;
                case 5: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_06); break;
                case 6: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_07); break;
                case 7: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_08); break;
                case 8: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_09); break;
                case 9: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_10); break;
                case 10: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_11); break;
                default: mTireTreadTexture = ContextCompat.getDrawable(mContext, R.drawable.tire_tread_01);
            }

            invalidate(); //will trigger the onDraw
            handler.postDelayed(this,33); // re-schedules this simulateWheelSpin() method for re-execution in 1/30s (30FPS)
        }
    };

    public void updateDisplayedData(CaroloCarSensorData carData)
    {
        mCarData = carData;
        //invalidate();
    }


}
