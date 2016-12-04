package com.wacker.carolohseapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by wacke on 27.11.2016.
 */
public class UdpReceiverService extends Service
{
    static final public String UDPRECV_RESULT = "com.wacker.carolohseapp.UdpReceiverService.MESAGE_PROCESSED";
    static final public String UDPRECV_MESSAGE = "com.wacker.carolohseapp.UdpReceiverService.UDP_MESSAGE";

    boolean currentlyReceiving = false;
    PackageReceiver packRec = null;
    LocalBroadcastManager broadcastManager = null;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class UdpReceiverBinder extends Binder
    {
        UdpReceiverService getService() {
            return UdpReceiverService.this;
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //return super.onStartCommand(intent, flags, startId);
        Toast.makeText(UdpReceiverService.this, "UDP Receiver Service started", Toast.LENGTH_LONG).show();

        //AsyncTask zum Empfangen der UDP Multicast Packets starten
        if(!currentlyReceiving)
        {
            packRec = new PackageReceiver();
            packRec.execute(8625);
            currentlyReceiving = true;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        //super.onDestroy();
        if(currentlyReceiving)
        {
            packRec.cancelNow();
            currentlyReceiving = false;
        }
        Toast.makeText(UdpReceiverService.this, "UDP Receiver Service stopped", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    //Inner class for an AsyncTask which handles receiving of UDP packages
    public class PackageReceiver extends AsyncTask<Integer, String, String>
    {
        boolean cancellationPending = false;

        @Override
        protected String doInBackground(Integer... ports)
        {
            Log.d("UdpRecv", "Starting to receive in background");
            byte[] bytes = "error".getBytes();
            int multicastPort = ports[0];
            byte[] messageBuffer = new byte[1024];
            InetAddress multicastAddress = null;
            MulticastSocket multicastSocket = null;

            cancellationPending = false;

            /* Turn off multicast filter */
            WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiManager.MulticastLock multicastLock = wifiMan.createMulticastLock("receivePackagesLock");

            try {
                //Set up a socket to receive UDP multicasts
                multicastAddress = InetAddress.getByName("224.0.0.251");
                DatagramPacket packet = new DatagramPacket(messageBuffer, messageBuffer.length, multicastAddress, multicastPort);
                multicastSocket = new MulticastSocket(multicastPort);
                multicastSocket.joinGroup(multicastAddress);

                //Listen for UDP messages and publish retrieved data as a local broadcast
                while (!cancellationPending) {
                    Log.d("UdpRecv", "Waiting for UDP Messsage");
                    multicastSocket.receive(packet);
                    bytes = packet.getData();
                    publishRetrievedData(bytes);
                    Log.d("Info", "Reveived message: " + UdpMessageDecoder.bytesToHexString(bytes));
                    publishProgress(UdpMessageDecoder.bytesToHexString(bytes));
                }

            } catch (UnknownHostException e) {
                Log.e("UdpRecv", "Couldn't parse the multicast address");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Wenn multicastlock released wird immer exception geworfen, dass under-released wäre, einfach handlen
            try {
                multicastLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            multicastSocket.close();
            return UdpMessageDecoder.bytesToHexString(bytes);

        }

        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);
            //Toast.makeText(UdpReceiverService.this, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled(String s)
        {
            cancellationPending = true;
            super.onCancelled(s);
        }

        protected void cancelNow()
        {
            cancellationPending = true;
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            Toast.makeText(UdpReceiverService.this, "Stopped Receiving", Toast.LENGTH_SHORT).show();
        }

        /**
         * Retrieves the car's sensor data from the byte message
         * and publishes it via local broadcast (so the UI fragments can listen for updates)
         * @param byteMsg   Carolo Car sensor data as byte array
         */
        protected void publishRetrievedData(byte[] byteMsg)
        {
            if (byteMsg != null)
            {
                Intent intent = new Intent(UDPRECV_RESULT);
                //Intent intent = new Intent().setAction(UDPRECV_RESULT)
                CaroloCarSensorData receivedData = new CaroloCarSensorData(byteMsg);
                intent.putExtra(UDPRECV_MESSAGE, receivedData);
                broadcastManager.sendBroadcast(intent);
                Log.d("UdpRecv", "Published retrieved car data via local broadcast");
            }
        }


    }


}
