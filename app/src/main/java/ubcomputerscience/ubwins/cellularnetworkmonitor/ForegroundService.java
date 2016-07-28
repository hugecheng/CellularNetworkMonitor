package ubcomputerscience.ubwins.cellularnetworkmonitor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class ForegroundService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener
{
    private static final String LOG_TAG = "ForegroundService";

    ScheduleIntentReceiver scheduleIntentReceiver;
    //Handler h = null;
    Scheduler scheduler;
    private GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    public static String FusedApiLatitude;
    public static String FusedApiLongitude;
    LocationFinder locationFinder;
    PowerManager.WakeLock wakeLock;


    @Override
    public void onCreate()
    {
        super.onCreate();
        buildGoogleApiClient();
        scheduleIntentReceiver = new ScheduleIntentReceiver();
        scheduler = new Scheduler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (intent.getAction().equals("startforeground"))
        {
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction("mainAction");
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("CellularNetworkMonitor is running")
                    .setSmallIcon(R.mipmap.m)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
            startForeground(101,
                    notification);

            /*ACQUIRING WAKELOCK*/
            PowerManager mgr = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
            wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakeLock");
            wakeLock.acquire();
            Log.v(LOG_TAG, "Acquired WakeLock");

            mGoogleApiClient.connect();
            //finished connecting API Client
            locationFinder = new LocationFinder(getApplicationContext());
            //calling getLocation() from Location provider
            locationFinder.getLocation();

            /*TESTING HANDLER*/
            /*
            h = new Handler();
            h.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.v(LOG_TAG,"testing handler");
                    scheduleIntentReceiver.onHandlerReceiver(getApplicationContext());
                    h.postDelayed(this, 1000);
                }
            }, 500);

            Toast.makeText(getApplicationContext(), "Handler Set", Toast.LENGTH_SHORT).show();
            */

            /*TESTING SCHEDULER SERVICE*/

            /*CALL TO SCHEDULER METHOD*/
            scheduler.beepForAnHour(getApplicationContext());
            Log.v(LOG_TAG, "SCHEDULER SET TO BEEP FOR 60");
            Toast.makeText(getApplicationContext(), "Scheduler Set for 60 minutes", Toast.LENGTH_SHORT).show();



        } else if (intent.getAction().equals("stopforeground"))
        {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");

            /*STOP HANDLER*/
            /*
            if(h!=null) {
                h.removeCallbacksAndMessages(null);
            }*/

            /*CANCEL SCHEDULER AND RELEASE WAKELOCK*/
            wakeLock.release();
            Log.v(LOG_TAG, "Releasing WakeLock");
            scheduler.stopScheduler();
            Log.v(LOG_TAG, "Beeping Service Stoppped");

            /*to disconnect google api client*/
            if(mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
            }
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location){
        Log.i(LOG_TAG,"Location data has changed");
        FusedApiLatitude = Double.toString(location.getLatitude());
        FusedApiLongitude = Double.toString(location.getLongitude());
        Log.i(LOG_TAG,"apiLat is : "+FusedApiLatitude);
        Log.i(LOG_TAG,"apiLong  is : "+FusedApiLongitude);

    }

    @Override
    public void onConnectionSuspended(int i){
        Log.i(LOG_TAG,"Google Api client has been suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.i(LOG_TAG,"Google Api client connection has failed");
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
}