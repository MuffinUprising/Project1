package com.example.casey.project1;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;


public class MainActivity extends Activity implements ConnectionStateCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private static final String CLIENT_ID = "bd75486680a342fdaaba61c9be3b499e";
    private static final String REDIRECT_URI = "project-one-login://callback";

    protected static final String TAG = "MainActivity";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int SPOTIFY_LOGIN_REQUEST_CODE = 314;

    private Button mSpotifyLogin;
    private GoogleApiClient mGoogleApiClient;
    private Location mDeviceLastLocation;
    private double mDeviceLatitude;
    private double mDeviceLongitude;

    private ResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpotifyLogin = (Button)findViewById(R.id.spotify_authenticator_dialog_button);
        mSpotifyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpotifyLoginDialog();
                //TODO; make it so
            }
        });

        getGoogleAPIClient();
    }

    protected synchronized void getGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(GeocoderConstants.RECEIVER, mResultReceiver);
        intent.putExtra(GeocoderConstants.LOCATION_DATA_EXTRA, mDeviceLastLocation);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mDeviceLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mDeviceLastLocation != null) {
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                return;
            }
        }  else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
            return;
        }
        startIntentService();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPOTIFY_LOGIN_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode,data);
            if(response.getType() == AuthenticationResponse.Type.TOKEN) {
                //TODO: launch WeatherActivity
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getSpotifyLoginDialog() {

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();


        AuthenticationClient.openLoginActivity(this, SPOTIFY_LOGIN_REQUEST_CODE, request);
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
        Toast.makeText(MainActivity.this, "Login Failed. Username/password incorrect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }
}
