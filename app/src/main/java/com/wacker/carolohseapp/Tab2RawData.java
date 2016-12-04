package com.wacker.carolohseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by wacke on 27.11.2016.
 */
public class Tab2RawData extends Fragment
{
    BroadcastReceiver broadcastReceiver = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab2_rawdata, container, false);

        //Set up a broadcast receiver and update the ui whenever a new message is retreived from the UdpReceiver
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                CaroloCarSensorData carData = (CaroloCarSensorData) intent.getSerializableExtra(UdpReceiverService.UDPRECV_MESSAGE);
                updateUI(carData);
            }
        };

        return rootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(UdpReceiverService.UDPRECV_RESULT));
    }

    @Override
    public void onPause()
    {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    protected void updateUI(CaroloCarSensorData carData)
    {
        TextView textView = (TextView) getView().findViewById(R.id.rawdata_velocity_value);
        textView.setText(String.valueOf(carData.velocity));
    }

}
