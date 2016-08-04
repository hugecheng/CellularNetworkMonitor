/**
 * Created by Gautam on 6/18/16.
 * MBP111.0138.B16
 * agautam2@buffalo.edu
 * University at Buffalo, The State University of New York.
 * Copyright © 2016 Gautam. All rights reserved.
 */

package ubcomputerscience.ubwins.cellularnetworkmonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;


public class DBstore
{
    static final String TAG = "[CELNETMON-DBSTORE]";
    private final Context mContext;


    public DBstore(Context context)
    {
        this.mContext=context;
    }

    public void insertIntoDB(String[] locationdata, String timeStamp, String cellularInfo, String dataActivity, String dataState, String phoneCallState)
    {
        String networkType = "";
        String networkState = "";
        String networkStateVariables[]={"","","",""};
        String networkRSSI = "";
        String networkRSSIVariables[]={"",""};
        ContentValues contentValues = new ContentValues();
        DBHandler dbHandler = new DBHandler(mContext);
        SQLiteDatabase sqLiteDatabase = dbHandler.getWritableDatabase();

        if(!(cellularInfo.equals("")))
        {

            Log.v(TAG, "before split: " + cellularInfo);
            String[] mainsplit = cellularInfo.split(":");
            String[] splitter = mainsplit[0].split("@");
            Log.v(TAG, "splitter of zero: " + splitter[0]);
            Log.v(TAG, "splitter of one: " + splitter[1]);
            networkType = splitter[0];
            String splitter1[] = splitter[1].split("_");
            networkState = splitter1[0];
            networkStateVariables = networkState.split("#");

            Log.v(TAG, "splitter1 of zero: " + splitter1[0]);
            Log.v(TAG, "splitter1 of one: " + splitter1[1]);
            networkRSSI = splitter1[1];
            networkRSSIVariables = networkRSSI.split("#");
        }
        Log.v(TAG,"Trying to push to DB");
        contentValues.put("N_LAT",locationdata[0]);
        contentValues.put("N_LONG",locationdata[1]);
        contentValues.put("F_LAT",locationdata[2]);
        contentValues.put("F_LONG",locationdata[3]);
        contentValues.put("NETWORK_PROVIDER", locationdata[4]);
        contentValues.put("TIMESTAMP",timeStamp);
        contentValues.put("NETWORK_TYPE", networkType);
        contentValues.put("NETWORK_PARAM1", networkStateVariables[0]);
        contentValues.put("NETWORK_PARAM2", networkStateVariables[1]);
        contentValues.put("NETWORK_PARAM3", networkStateVariables[2]);
        contentValues.put("NETWORK_PARAM4", networkStateVariables[3]);
        contentValues.put("DBM", networkRSSIVariables[0]);
        contentValues.put("NETWORK_LEVEL", networkRSSIVariables[1]);
        contentValues.put("DATA_STATE",dataState);
        contentValues.put("DATA_ACTIVITY", dataActivity);
        contentValues.put("CALL_STATE",phoneCallState);

        sqLiteDatabase.insert("cellRecords", null, contentValues);
        sqLiteDatabase.close();
        Log.v(TAG,"Push to DB Successful");
    }

}
