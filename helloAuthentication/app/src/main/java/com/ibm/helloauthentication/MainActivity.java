package com.ibm.helloauthentication;
/**
 * Copyright 2015, 2016 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.security.googleauthentication.GoogleAuthenticationManager;
import com.ibm.mobilefirstplatform.clientsdk.android.security.mca.api.MCAAuthorizationManager;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;

/**
 * Main Activity implements Response listener for call back handling.
 */
public class MainActivity extends Activity implements ResponseListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_GET_ACCOUNTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView buttonText = (TextView) findViewById(R.id.button_text);

        // initialize SDK with IBM Bluemix application ID and REGION, TODO: Update region if not using Bluemix US SOUTH
        BMSClient.getInstance().initialize(this, BMSClient.REGION_US_SOUTH);

        // Runtime Permission handling required for SDK 23+
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            buttonText.setClickable(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSION_REQUEST_GET_ACCOUNTS);
        }

        // Register this activity as the default auth listener
        Log.i(TAG, "Registering Google Auth Listener");

        // Must create MCA auth manager before registering Google auth Manager
        //TODO: Please replace <APP_GUID> with a valid Application GUID from your MCA instance
        MCAAuthorizationManager MCAAuthMan = MCAAuthorizationManager.createInstance(this, "<APP_GUID>");
        BMSClient.getInstance().setAuthorizationManager(MCAAuthMan);
        GoogleAuthenticationManager.getInstance().register(this);
    }

    /**
     * Called when ping bluemix button is pressed.
     * Attempts to access protected Bluemix backend, kicking off the Authentication login prompt.
     * @param view the button pressed.
     */
    public void pingBluemix(View view) {

        TextView buttonText = (TextView) findViewById(R.id.button_text);
        buttonText.setClickable(false);

        TextView responseText = (TextView) findViewById(R.id.response_text);
        responseText.setText(R.string.Connecting);

        Log.i(TAG, "Attempting to Connect");

        // Testing the connection to Bluemix by attempting to obtain an authorization header, using this Activity to handle the response.
        BMSClient.getInstance().getAuthorizationManager().obtainAuthorization(this, this);
        // As an alternative, you can send a Get request using the Bluemix Mobile Services Core sdk to send the request to a protected resource on the Node.js application to kick off Authentication. See example below:
        // new Request(<YOUR_BLUEMIX_APP_ROUTE> + "/protected", Request.GET).send(this, this); TODO: Replace <YOUR_BLUEMIX_APP_ROUTE> with your appRoute on Bluemix
        // The Node.js code was provided in the MobileFirst Services Starter boilerplate.
    }

    /**
     * Called when logout button is pressed.
     * Logs out user from MCA.
     * @param view the button pressed.
     */
    public void logout(View view) {
        Log.i(TAG, "Logging out");
        TextView responseText = (TextView) findViewById(R.id.response_text);
        responseText.setText(R.string.Logout);
        
        // Logs user out from Google, null parameter can be replaced with a response listener like the one implemented by this activity
        GoogleAuthenticationManager.getInstance().logout(getApplicationContext(), null);
    }

    // Implemented for the response listener to handle the success response when a protected resource is accessed on Bluemix
    @Override
    public void onSuccess(Response response) {
        setStatus(MCAAuthorizationManager.getInstance().getUserIdentity().toString(), true);
        Log.i(TAG, "You have successfully authenticated against your Bluemix MCA instance: " + response.getResponseText());
    }

    // Implemented for the response listener to handle failure response when a protected resource is accessed on Bluemix
    @Override
    public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
        String errorMessage = "";
	
	// Be sure to check for null pointers, any of the above paramters may be null depending on the failure.
        if (response != null) {
            if (response.getStatus() == 404 || response.getStatus() == 500) {
                errorMessage += "MCA application not found on Bluemix" +
                        "\nPlease verify your MCA Application GUID and rebuild the app.";
            } else if(response.getStatus() == 400) {
                errorMessage += "If you just corrected your APP_GUID, it may be right, but the authorization has been revoked for this application. Try uninstalling this app before running again.";
            } else {
                errorMessage += response.toString() + "\n";
            }
        }

        if (throwable != null) {
            if (throwable.getClass().equals(UnknownHostException.class)) {
                errorMessage = "Unable to access Bluemix host!\nPlease verify internet connectivity and try again.";
            } else {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                errorMessage += "THROWN" + sw.toString() + "\n";
            }
        }

        if (errorMessage.isEmpty())
            errorMessage = "Request Failed With Unknown Error.";

        setStatus(errorMessage, false);
        Log.e(TAG, "Request to Bluemix failed: " + errorMessage);
    }

    // Necessary override for Google auth, allows Activity to interact back and forth with Bluemix mobile backend
    @Override
    protected void onActivityResult( int  requestCode,  int  responseCode, Intent intent) {
        GoogleAuthenticationManager.getInstance().onActivityResultCalled(requestCode, responseCode, intent);
    }

    // Necessary override for Runtime Permission Handling required for SDK 23+
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_GET_ACCOUNTS: {
                TextView buttonText = (TextView) findViewById(R.id.button_text);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buttonText.setClickable(true);

                } else {
                    setStatus("Unable to authorize without full permissions. \nPlease retry with permissions enabled.", false);
                    buttonText.setClickable(false);
                }
            }
        }
    }

    /**
     * Updates text fields in the UI
     * @param messageText String that displays in center text box
     * @param wasSuccessful Boolean that decides appropriate text to display
     */
    private void setStatus(final String messageText, boolean wasSuccessful){
        final TextView responseText = (TextView) findViewById(R.id.response_text);
        final TextView topText = (TextView) findViewById(R.id.top_text);
        final TextView bottomText = (TextView) findViewById(R.id.bottom_text);
        final TextView buttonText = (TextView) findViewById(R.id.button_text);
        final String topStatus = wasSuccessful ? "Yay!" : "Bummer";
        final String bottomStatus = wasSuccessful ? "Connected to MCA protected endpoint" : "Something Went Wrong";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonText.setClickable(true);
                responseText.setText(messageText);
                topText.setText(topStatus);
                bottomText.setText(bottomStatus);
            }
        });
    }
}
