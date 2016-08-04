package ubcomputerscience.ubwins.cellularnetworkmonitor;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by Gautam on 7/18/16.
 * MBP111.0138.B16
 * agautam2@buffalo.edu
 * University at Buffalo, The State University of New York.
 * Copyright © 2016 Gautam. All rights reserved.
 */
public class ScheduleIntentReceiver
{
    LocationFinder locationFinder;
    CellularDataRecorder cdr;
    PhoneCallStateRecorder pcsr;
    DBstore dbStore;
    Location location;
    public final String TAG = "[CELNETMON-HNDLRCVR]";
 public void onScheduleIntentReceiver(Context arg0)
 {
     Log.v(TAG, "inside onReceive");
     locationFinder = new LocationFinder(arg0);

     final TelephonyManager telephonyManager = (TelephonyManager) arg0.getSystemService(Context.TELEPHONY_SERVICE);
     cdr = new CellularDataRecorder();
     pcsr = new PhoneCallStateRecorder();

     locationFinder = new LocationFinder(arg0);
     Log.v(TAG, "Calling getLocalTimeStamp and getCellularInfo");
     Long timeStamp = cdr.getLocalTimeStamp();
     String cellularInfo = cdr.getCellularInfo(telephonyManager);
     int dataActivity = cdr.getCurrentDataActivity(telephonyManager);
     int dataState = cdr.getCurrentDataState(telephonyManager);
     int mobileNetworkType = cdr.getMobileNetworkType(telephonyManager);
     String fusedApiLatitude = ForegroundService.FusedApiLatitude;
     String fusedApiLongitude = ForegroundService.FusedApiLongitude;
     String lmLatitude = Double.toString(locationFinder.latitude);
     String lmLongitude = Double.toString(locationFinder.longitude);
     String locationProvider = locationFinder.locationProvider;
     String locationdata[] = {lmLatitude,lmLongitude,fusedApiLatitude,fusedApiLongitude,locationProvider};
     int phoneCallState = pcsr.call_state;
     Log.i(TAG, "onReceive: Location data is before inserting"+locationdata[0] +" "+ locationdata[1]+" "+ locationdata[2]+" "+ locationdata[3]);


     Log.v(TAG, "TIME STAMP: " + timeStamp);
     Log.v(TAG, "CELLULAR INFO: " + cellularInfo);
     Log.v(TAG, "DATA ACTIVITY: " + dataActivity);
     Log.v(TAG, "DATA STATE: " + dataState);
     Log.v(TAG, "MOBILE NETWORK TYPE: " + mobileNetworkType);

     dbStore = new DBstore(arg0);
     dbStore.insertIntoDB(locationdata, timeStamp, cellularInfo, dataActivity, dataState,phoneCallState,mobileNetworkType);
 }

}
