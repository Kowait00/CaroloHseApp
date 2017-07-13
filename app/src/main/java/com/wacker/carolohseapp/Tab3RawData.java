package com.wacker.carolohseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by wacke on 27.11.2016.
 */
public class Tab3RawData extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab3_rawdata, container, false);

        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        // Register local broadcast receiver to receive broadcast of current vehicle data
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UdpReceiverService.UDPRECV_RESULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bcReceiver, intentFilter);
    }

    @Override
    public void onStop()
    {
        // unregister broadcast receiver
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(bcReceiver);
        super.onStop();
    }

    private final BroadcastReceiver bcReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equalsIgnoreCase(UdpReceiverService.UDPRECV_RESULT) )
            {
                Log.d("Tab2", "Broadcast received");
                CaroloCarSensorData carData = (CaroloCarSensorData) intent.getSerializableExtra(UdpReceiverService.UDPRECV_MESSAGE);
                updateUI(carData);
            }
        }
    };

    private void updateUI(CaroloCarSensorData carData)
    {
        TextView textview = (TextView) getView().findViewById(R.id.rawdata_movementV_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.movementV));
        textview = (TextView) getView().findViewById(R.id.rawdata_movementA_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.movementA));
        textview = (TextView) getView().findViewById(R.id.rawdata_movementS_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.movementS));
        textview = (TextView) getView().findViewById(R.id.rawdata_poseX_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.poseX));
        textview = (TextView) getView().findViewById(R.id.rawdata_poseY_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.poseY));
        textview = (TextView) getView().findViewById(R.id.rawdata_posePsi_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.posePsi));
        textview = (TextView) getView().findViewById(R.id.rawdata_rotationPsi_K_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.rotationPsi_K));
        textview = (TextView) getView().findViewById(R.id.rawdata_rotationPsi_Ko_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.rotationPsi_Ko));
        textview = (TextView) getView().findViewById(R.id.rawdata_rotationYaw_K_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.rotationYaw_K));
        textview = (TextView) getView().findViewById(R.id.rawdata_rotationYaw_Ko_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.rotationYaw_Ko));
        textview = (TextView) getView().findViewById(R.id.rawdata_environmentUS_Front_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.environmentUS_Front));
        textview = (TextView) getView().findViewById(R.id.rawdata_environmentUS_Rear_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.environmentUS_Rear));
        textview = (TextView) getView().findViewById(R.id.rawdata_environmentIR_Side_Front_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.environmentIR_Side_Front));
        textview = (TextView) getView().findViewById(R.id.rawdata_environmentIR_Side_Rear_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.environmentIR_Side_Rear));
        textview = (TextView) getView().findViewById(R.id.rawdata_environmentIR_Front_Left_value);
        textview.setText(String.format(Locale.GERMAN, "%.2f", carData.environmentIR_Front_Left));

    }

}
