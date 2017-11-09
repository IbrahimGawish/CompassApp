package com.gawish.app.compassapp;

import android.app.AlertDialog;
import android.app.Service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gawish-Z510 on 6/4/2015.
 */

public class GPSTracker extends Service implements LocationListener {

    private static String TAG = GPSTracker.class.getName();
    private final Context context;

    boolean isGpsEnabled = false;
    boolean isNetworkEnabled = false;
    boolean isGPSTrackingEnabled = false;
    Location location;
    double longitude;
    double latitude;
    int geoCoderMaxResults = 1;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;//meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60*1 ;//MilliSeconds

    protected LocationManager locationManager;
    private String providerInfo;

    public GPSTracker(Context context) {
        this.context = context;
        getLocation();
    }



    public void getLocation(){
        try{
        locationManager=(LocationManager)context.getSystemService(LOCATION_SERVICE);
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if(isGpsEnabled){
            this.isGPSTrackingEnabled = true;
            Log.d(TAG, "GPS Trackng is enabled!!");
            //Toast.makeText(context,"GPS",Toast.LENGTH_LONG).show();
            providerInfo =LocationManager.GPS_PROVIDER;
        }else if(isNetworkEnabled){
            this.isGPSTrackingEnabled=true;
            Log.d(TAG,"Application uses network to Track location!!");
            providerInfo=LocationManager.NETWORK_PROVIDER;
        }
        if(!providerInfo.isEmpty()) {

            locationManager.requestLocationUpdates(providerInfo, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);


            if (locationManager != null) {
                location=locationManager.getLastKnownLocation(providerInfo);

                updateGPSCoordinates();

            }
        }
    }catch (Exception e){
            Log.e(TAG, "Impossible to connect to LocationManager", e);
        }
    }

    public void updateGPSCoordinates() {
        if(location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //Toast.makeText(context, "toot", Toast.LENGTH_LONG).show();
       }

    }

    public double getLatitude(){
        if(location !=null){
            latitude = location.getLatitude();
        }
        return latitude;
    }
    public double getLongitude(){
        if(location !=null){
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public  boolean getIsGPSTrackingEnabled(){
        return this.isGPSTrackingEnabled;
    }

    public  void stopUsingGPS(){
        if(locationManager !=null){
            locationManager.removeUpdates(GPSTracker.this);

        }
    }

    public void showSettingsAlert(){
        Toast.makeText(context, "toot", Toast.LENGTH_LONG).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.GPSAlertDialogTitle);
        alertDialog.setMessage(R.string.GPSAlertDialogMessage);
        alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public List<Address> getGeocoderAddress(Context context) throws IOException {
        if(location != null){
            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
            List<Address>addresses = geocoder.getFromLocation(latitude,longitude,this.geoCoderMaxResults);

            return  addresses;
        }
        return null;
    }


    public String getAdreessLine(Context context) throws IOException {
        List<Address>addresses= getGeocoderAddress(context);
        if(addresses != null && addresses.size()>0){
            Address address = addresses.get(0);
            String adressLine = address.getAddressLine(0);
            return  adressLine;
        }else {
            return null;
        }

    }


    public String getLocality(Context context) throws IOException {
        List<Address>addresses = getGeocoderAddress(context);
        if(addresses != null && addresses.size()>0){
            Address address = addresses.get(0);
            String locality = address.getLocality();
            return locality;
        }else{
            return null;
        }
    }

    public String getPostalCode(Context context) throws IOException {
        List<Address>addresses= getGeocoderAddress(context);
        if (addresses != null && addresses.size()>0) {
            Address address = addresses.get(0);
            String postalCode = address.getPostalCode();
            return postalCode;

        }else {
            return null;
        }
    }

    public String getCountryName(Context context) throws IOException {
        List<Address>addresses = getGeocoderAddress(context);
        if (addresses!=null && addresses.size()>0) {
            Address address=addresses.get(0);
            String countryName = address.getCountryName();
            return countryName;
        }else{
            return null;
        }

    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}