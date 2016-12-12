package com.wacker.carolohseapp;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private Context context;

    private DummyDataGenerator mDummyDataGenerator;
    private boolean mGeneratingDummyData = false;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = this;

        mDummyDataGenerator = new DummyDataGenerator();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        //Start UDP receiver service
        //Intent intent = new Intent(this, UdpReceiverService.class);
        //startService(intent);


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //Start UDP receiver service
        Intent intent = new Intent(this, UdpReceiverService.class);
        startService(intent);

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        //Stop UDP receiver service
        Intent intent = new Intent(this, UdpReceiverService.class);
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_appInfo) {
            Intent intent = new Intent(this, AppInfoActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_generateTestData) {
            if(mGeneratingDummyData == true)
            {
                mDummyDataGenerator.stop();
                mGeneratingDummyData = false;
            }
            else
            {
                Thread thread = new Thread(mDummyDataGenerator);
                thread.start();
                mGeneratingDummyData = true;
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    Tab1Visualization tab1 = new Tab1Visualization();
                    return tab1;
                case 1:
                    Tab2RawData tab2 = new Tab2RawData();
                    return tab2;
                case 2:
                    Tab3CameraPlot tab3 = new Tab3CameraPlot();
                    return tab3;
            }
            return null;
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position) {
                case 0:
                    return "Übersicht";
                case 1:
                    return "Daten";
                case 2:
                    return "Kamera";
            }
            return null;
        }
    }

    private class DummyDataGenerator implements Runnable
    {
        private boolean stop = false;

        @Override
        public void run()
        {
            stop = false;
            CaroloCarSensorData dummyData = new CaroloCarSensorData();

            while(stop != true)
            {
                // Change dummy data and send new values as local broadcast every second
                dummyData.heading = (dummyData.heading + 30) % 360;
                dummyData.wheelAngle = (dummyData.wheelAngle + 45 + 10) % 90 - 45;
                dummyData.distFrontLeft = (dummyData.distFrontLeft + 15) % 120;
                dummyData.distFrontRight = (dummyData.distFrontRight + 15) % 120;
                dummyData.distRearLeft = (dummyData.distRearLeft + 15) % 120;
                dummyData.distRearRight = (dummyData.distRearRight + 15) % 120;
                dummyData.distLeft = (dummyData.distLeft + 15) % 120;
                dummyData.distRight = (dummyData.distRight + 15) % 120;
                dummyData.velocity = (dummyData.velocity + 5) % 50;
                dummyData.acceleration = (dummyData.acceleration + 2) % 20;

                Intent intent = new Intent(UdpReceiverService.UDPRECV_RESULT);
                intent.putExtra(UdpReceiverService.UDPRECV_MESSAGE, dummyData);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                Log.d("MainActivity", "Generated dummy data");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        public void stop()
        {
            stop = true;
        }
    }
}
