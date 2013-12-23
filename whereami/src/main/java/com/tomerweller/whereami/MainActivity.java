package com.tomerweller.whereami;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private TextView mTextOut;
    private Geocoder mGeoCoder;
    private LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGeoCoder = new Geocoder(this, new Locale("iw_IL"));
        mTextOut = (TextView) findViewById(R.id.text_out);

         /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        Location location = mLocationClient.getLastLocation();
        if (location==null){
            Toast.makeText(this, "No last known location", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //TODO: move geocoding to async task
            List<Address> addresses =
                    mGeoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size()>0){
                mTextOut.setText(addresses.get(0).getAddressLine(0));
            } else {
                mTextOut.setText("No geocoder results");
            }
        } catch (IOException e) {
            Toast.makeText(this, "No geocoder", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Connection lost.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try {
            // Start an Activity that tries to resolve the error
            connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode){
                    case RESULT_OK:
                        mLocationClient.connect();
                        break;
                    default:
                        Toast.makeText(this, "OOps", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
