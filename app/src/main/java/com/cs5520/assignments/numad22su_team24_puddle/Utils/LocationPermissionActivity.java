package com.cs5520.assignments.numad22su_team24_puddle.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cs5520.assignments.numad22su_team24_puddle.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class LocationPermissionActivity{
    public static final int ERROR_DIALOG_REQUEST = 99;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 88;
    public static final int REQUEST_CODE_FINE_LOCATION = 77;
    public static boolean locationPermissionGranted = false;
    public static DecimalFormat df = new DecimalFormat("###.####");


    public static boolean checkGoogleService(Activity activity){
        Log.d("LocationRequest", "checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);

        if(available == ConnectionResult.SUCCESS){
            Log.d("LocationRequest", "Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d("LocationRequest", "an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(activity, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(activity, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean enableLocationAccess(Activity activity){
        final LocationManager manager = (LocationManager) activity.getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        return true;
    }

    public static void requestPermission(Activity activity, int requestCode) {

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    requestCode);
        }

    }

    public static boolean checkLocationPermission(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            return true;
        }
        else{
            locationPermissionGranted = false;
            return false;
        }

    }

    public static boolean checkMapServices(Activity activity){
        if(LocationPermissionActivity.checkGoogleService(activity)){
            if(LocationPermissionActivity.enableLocationAccess(activity)){
                return true;
            }
        }
        return false;
    }

    public static void getCurrentLocationList(double latitude, double longitude, String google_maps_api_key) throws IOException {
        String googleMapsURL = "https://maps.googleapis.com/maps/api/geocode/xml?latlng=" +
                df.format(latitude) + "," + df.format(longitude) +
                "&key="+ google_maps_api_key;
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        final Handler handler = new Handler();
        Runnable runnable = () -> {
            URL url = null;
            try {
                url = new URL(googleMapsURL);
                InputStream stream = url.openStream();
                List<String> addresses = new ArrayList<>();
                if (stream != null) {
                    InputSource inputXml = new InputSource(stream);
                    NodeList nodes = (NodeList) xpath.evaluate(
                            "//formatted_address",
                            inputXml,
                            XPathConstants.NODESET);

                    for (int i = 0, n = nodes.getLength(); i < n; i++) {
                        String formattedAddress = nodes.item(i).getTextContent();
                        addresses.add(formattedAddress);
                        Log.d("getCurrentLocationList", formattedAddress);
                    }

//                    handler.post(() -> {
//                        // send the addresses object
//                    });

                } else {
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    // For implementing getCurrentLocationList method, write below:
    //    public void testlocationlist(View v) throws IOException {
    //        LocationPermissionActivity.getCurrentLocationList(43.6687,-70.2848, getString(R.string.google_maps_api_key));
    //    }
}
