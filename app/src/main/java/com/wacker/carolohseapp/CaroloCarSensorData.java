package com.wacker.carolohseapp;

import android.util.Log;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Created by wacke on 27.11.2016.
 */
public class CaroloCarSensorData implements Serializable
{
    float velocity;
    float acceleration;
    float heading;
    float wheelAngle;
    float distFront;
    float distRear;
    float distLeft;
    float distRight;

    public CaroloCarSensorData(byte[] byteMsg)
    {
        if(byteMsg.length != 1024)
        {
            Log.e("CarSensorData", "Input data has unexpected byte count and can't be decoded");
            return;
        }
        //Retrieve the values from the byte stream
        velocity = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 0, 32) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        acceleration = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 32, 64) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        heading = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 64, 96) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        wheelAngle = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 96, 128) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distFront = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 128, 160) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distRear = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 160, 192) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distLeft = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 192, 224) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distRight = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 224, 256) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();

    }



}
