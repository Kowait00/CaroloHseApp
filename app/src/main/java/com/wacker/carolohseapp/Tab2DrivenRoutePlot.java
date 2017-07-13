package com.wacker.carolohseapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.ui.HorizontalPositioning;
import com.androidplot.ui.Size;
import com.androidplot.ui.SizeMode;
import com.androidplot.ui.VerticalPositioning;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wacke on 27.11.2016.
 */
public class Tab2DrivenRoutePlot extends Fragment
{
    private XYPlot plot;
    private List<Double> xPosVals = new ArrayList<Double>();
    private List<Double> yPosVals = new ArrayList<Double>();
    private XYSeries posSeries;
    long lastDataUpdated = 0;
    long lastPlotUpdated = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.tab2_drivenrouteplot, container, false);

        // Register local broadcast receiver to receive broadcast of current vehicle data
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UdpReceiverService.UDPRECV_RESULT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bcReceiver, intentFilter);

        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        plot = (XYPlot) getView().findViewById(R.id.plot);
        plot.setDomainBoundaries(-10, 10, BoundaryMode.GROW);
        plot.setRangeBoundaries(-10, 10, BoundaryMode.GROW);
        plot.getLayoutManager().remove(plot.getLegend());
        plot.getGraph().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraph().getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getBorderPaint().setColor(Color.TRANSPARENT);
        plot.getGraph().setSize(new Size(0, SizeMode.FILL, 0, SizeMode.FILL));
        plot.getGraph().position(0, HorizontalPositioning.ABSOLUTE_FROM_LEFT, 0, VerticalPositioning.ABSOLUTE_FROM_TOP);
        posSeries = new SimpleXYSeries(xPosVals, yPosVals, "car position");
        plot.addSeries(posSeries, new LineAndPointFormatter(Color.RED, Color.GREEN, null, null));
    }

    @Override
    public void onStop()
    {
        // unregister broadcast receiver
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(bcReceiver);
        super.onStop();
    }

    private final BroadcastReceiver bcReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equalsIgnoreCase(UdpReceiverService.UDPRECV_RESULT) )
            {
                CaroloCarSensorData carData = (CaroloCarSensorData) intent.getSerializableExtra(UdpReceiverService.UDPRECV_MESSAGE);
                Log.d("Tab3", "Broadcast received");
                updatePlot(carData);
            }
        }
    };

    private void updatePlot(CaroloCarSensorData carData)
    {
        if(carData.poseX == 0 && carData.poseY == 0 && carData.movementV == 0)
        {
            xPosVals.clear();
            yPosVals.clear();
        }
        // only keep track of data points with delta t greater than 0.25s to reduce amount of data points with barely any change
        if(System.currentTimeMillis() - lastDataUpdated >= 100)
        {
            xPosVals.add(carData.poseX);
            yPosVals.add(carData.poseY);
            lastDataUpdated = System.currentTimeMillis();
        }

        // improve performance by capping max. plot refresh rate and max. number of data points
        if(xPosVals.size() > 2000) reducePosPlotResolution();
        if(System.currentTimeMillis() - lastPlotUpdated > 500)
        {
            plot.clear();
            posSeries = new SimpleXYSeries(xPosVals, yPosVals, "car position");
            plot.addSeries(posSeries, new LineAndPointFormatter(Color.RED, Color.GREEN, null, null));
            plot.redraw();
            lastPlotUpdated = System.currentTimeMillis();
        }
    }

    private void reducePosPlotResolution()
    {
        // halves resolution of datapoints for first 3/4 of the data
        for(int i = (int) (xPosVals.size() * 0.75); i > 0; i--)
        {
            if(i % 2 == 0)
            {
                xPosVals.remove(i);
                yPosVals.remove(i);
            }
        }
    }

}
