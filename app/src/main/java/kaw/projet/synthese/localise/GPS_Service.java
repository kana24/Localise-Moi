package kaw.projet.synthese.localise;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Created by Kaw
 */

public class GPS_Service extends Service  {
    @Nullable
    private LocationListener listener;
    private LocationManager locationManager;
    private String date;
private MainActivity main;


    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            date= DateFormat.getDateTimeInstance().format(new Date());
        }
        main=new MainActivity();
        listener = new LocationListener() {
            @Override

            public void onLocationChanged(Location location) {
                main = new MainActivity();
                Intent i = new Intent("location Update");
                Intent i2=new Intent("dateh");
                i.putExtra("Coordinates","{"+"\"lat\"" +":" + "\""+location.getLatitude()+"\""

                        +",\n"+ "\"lon\""+":"+ "\""+location.getLongitude()+"\""+"}");

                i2.putExtra("dateh",date);


                sendBroadcast(i);
                sendBroadcast(i2);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);

        }
    }

}