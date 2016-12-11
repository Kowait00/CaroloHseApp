package com.wacker.carolohseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by wacke on 27.11.2016.
 */
public class Tab1Visualization extends Fragment
{
    CarTopDownView mCarTopDownView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab1_visualization, container, false);

        //Set the car top down visuals view into the relative layout of the fragment
        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.visualizationLayout);
        mCarTopDownView = new CarTopDownView(getActivity());
        relativeLayout.addView(mCarTopDownView);

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
                Log.d("Tab1", "Broadcast received");
                CaroloCarSensorData carData = (CaroloCarSensorData) intent.getSerializableExtra(UdpReceiverService.UDPRECV_MESSAGE);
                mCarTopDownView.updateDisplayedData(carData);
            }
        }
    };
}
