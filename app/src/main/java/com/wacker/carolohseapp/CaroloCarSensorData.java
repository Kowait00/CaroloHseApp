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
    float velocity = 0;
    float acceleration = 0;
    float heading = 0;
    float wheelAngle = 0;
    float distFrontLeft = 0;
    float distFrontRight = 0;
    float distRearLeft = 0;
    float distRearRight = 0;
    float distLeft = 0;
    float distRight = 0;

    public CaroloCarSensorData()
    {

    }

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
        distFrontLeft = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 128, 160) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distFrontRight = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 160, 192) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distRearLeft = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 192, 224) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distRearRight = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 224, 256) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distLeft = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 256, 288) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        distRight = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 288, 320) ).order(ByteOrder.LITTLE_ENDIAN).getFloat();

    }



}
