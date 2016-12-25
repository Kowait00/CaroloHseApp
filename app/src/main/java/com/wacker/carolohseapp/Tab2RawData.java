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

/**
 * Created by wacke on 27.11.2016.
 */
public class Tab2RawData extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab2_rawdata, container, false);

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
        TextView textView = (TextView) getView().findViewById(R.id.rawdata_velocity_value);
        textView.setText(String.valueOf(carData.movementV));
    }

}
