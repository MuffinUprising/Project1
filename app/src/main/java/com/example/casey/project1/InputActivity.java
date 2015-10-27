package com.example.casey.project1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.ConnectionStateCallback;


public class InputActivity extends Activity implements ConnectionStateCallback {

    private static final String CLIENT_ID = "bd75486680a342fdaaba61c9be3b499e";
    private static final String REDIRECT_URI = "project-one-login://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int SPOTIFY_LOGIN_REQUEST_CODE = 314;

    Button mSpotifyLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpotifyLogin = (Button)findViewById(R.id.spotify_authenticator_dialog_button);
        mSpotifyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpotifyLoginDialog();

            }
        });

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
        Log.d("InputActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("InputActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("InputActivity", "Login failed");
        Toast.makeText(InputActivity.this, "Login Failed. Username/password incorrect", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTemporaryError() {
        Log.d("InputActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("InputActivity", "Received connection message: " + message);
    }
}
