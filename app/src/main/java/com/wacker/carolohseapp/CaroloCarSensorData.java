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
    // Odometry data
    double poseX = 0;                   // x position from start point (m)
    double poseY = 0;                   // y position from start point (m)
    double posePsi = 0;                 // angle in relation to start point (deg)
    double movementS = 0;               // total distance covered since start
    double movementV = 0;               // current velocity (m/s)
    double movementA = 0;               // current acceleration (m/s^2)
    double rotationPsi_K = 0;           // current angle hor? (degree)
    double rotationPsi_Ko = 0;          // current angle vert? (degree)
    double rotationYaw_K = 0;           // current angle hor? speed (deg/s)
    double rotationYaw_Ko = 0;          // current angle vert? speed (deg/s)

    // Environment data
    double environmentUS_Front = 0;     // ultrasonic sensor distance front (m)
    double environmentUS_Rear = 0;      // ultrasonic sensor distance rear (m)
    double environmentIR_Side_Front = 0;// infrared sensor distance side front (m)
    double environmentIR_Side_Rear = 0; // infrared sensor distance side rear (m)
    double environmentIR_Front_Left = 0;// infrared sensor distance front left (m)
    double valideSideDisLeft = 0;       // detected lane shape left (polynom of 2nd degree)
    double valideSideDisRight = 0;       // detected lane shape left (polynom of 2nd degree)


    public CaroloCarSensorData()
    {

    }

    public CaroloCarSensorData(byte[] byteMsg)
    {
        //if(byteMsg.length != 1024)
        //{
        //    Log.e("CarSensorData", "Input data has unexpected byte count and can't be decoded");
        //    return;
        //}
        //Retrieve the values from the byte stream

        poseX = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 0, 8) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        poseY = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 8, 16 ) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        posePsi = Math.toDegrees(ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 16, 25) ).order(ByteOrder.LITTLE_ENDIAN).getDouble());
        movementS = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 24, 32) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        movementV = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 32, 40) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        movementA = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 40, 48) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        rotationPsi_K = Math.toDegrees(ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 48, 56) ).order(ByteOrder.LITTLE_ENDIAN).getDouble());
        rotationPsi_Ko = Math.toDegrees(ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 56, 64) ).order(ByteOrder.LITTLE_ENDIAN).getDouble());
        rotationYaw_K = Math.toDegrees(ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 64, 72) ).order(ByteOrder.LITTLE_ENDIAN).getDouble());
        rotationYaw_Ko = Math.toDegrees(ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 72, 80) ).order(ByteOrder.LITTLE_ENDIAN).getDouble());
        environmentUS_Front = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 80, 88) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        environmentUS_Rear = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 88, 96) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        environmentIR_Side_Front = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 96, 104) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        environmentIR_Side_Rear = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 104 , 112) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        environmentIR_Front_Left = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 112, 120) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        valideSideDisLeft = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 120, 128) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        valideSideDisRight = ByteBuffer.wrap( Arrays.copyOfRange(byteMsg, 128, 136) ).order(ByteOrder.LITTLE_ENDIAN).getDouble();

    }



}
