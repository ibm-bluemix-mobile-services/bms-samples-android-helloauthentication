package com.ibm.helloauthentication;
/**
 * Copyright 2015 IBM Corp. All Rights Reserved.
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ibm.mobilefirstplatform.clientsdk.android.security.googleauthentication.GoogleAuthenticationManager;

import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;

import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

/**
 * Main Activity implements Response listener for http request call back handling.
 */
public class MainActivity extends Activity implements ResponseListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView buttonText = (TextView) findViewById(R.id.button_text);

        try {
            //initialize SDK with IBM Bluemix application ID and route
			// You can find your backendRoute and backendGUID in the Mobile Options section on top of your Bluemix application dashboard
            //TODO: Please replace <APPLICATION_ROUTE> with a valid ApplicationRoute and <APPLICATION_ID> with a valid ApplicationId
            BMSClient.getInstance().initialize(this, "<APPLICATION_ROUTE>", "<APPLICATION_ID>");
        }
        catch (MalformedURLException mue) {
            this.setStatus("Unable to parse Application Route URL\n Please verify you have entered your Application Route and Id correctly and rebuild the app", false);
            buttonText.setClickable(false);
        }

        // Register this activity as the default auth listener
        Log.i(TAG, "Registering Google Auth Listener");
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

        TextView errorText = (TextView) findViewById(R.id.error_text);
        errorText.setText("Attempting to Connect");

        Log.i(TAG, "Attempting to Connect");

        // Testing the connection to Bluemix by sending a Get request to a protected resource on the Node.js application, using this Activity to handle the response.
        // This Node.js code was provided in the MobileFirst Services Starter boilerplate.
        // The below request uses the IBM Mobile First Core sdk to send the request using the applicationRoute that was provided when initializing the BMSClient earlier.
        new Request(BMSClient.getInstance().getBluemixAppRoute() + "/protected", Request.GET).send(this, this);
    }

    /**
     * Updates text fields in the UI
     * @param messageText String that displays in center text box
     * @param wasSuccessful Boolean that decides appropriate text to display
     */
    private void setStatus(final String messageText, boolean wasSuccessful){
        final TextView errorText = (TextView) findViewById(R.id.error_text);
        final TextView topText = (TextView) findViewById(R.id.top_text);
        final TextView bottomText = (TextView) findViewById(R.id.bottom_text);
        final TextView buttonText = (TextView) findViewById(R.id.button_text);
        final String topStatus = wasSuccessful ? "Yay!" : "Bummer";
        final String bottomStatus = wasSuccessful ? "Connected to MCA protected endpoint" : "Something Went Wrong";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonText.setClickable(true);
                errorText.setText(messageText);
                topText.setText(topStatus);
                bottomText.setText(bottomStatus);
            }
        });
    }

    // Necessary override for Google auth, allows Activity to interact back and forth with Bluemix mobile backend
    @Override
    protected void onActivityResult( int  requestCode,  int  responseCode, Intent intent) {
        GoogleAuthenticationManager.getInstance().onActivityResultCalled(requestCode, responseCode, intent);
    }

    // Implemented for the response listener to handle the success response when Bluemix is pinged
    @Override
    public void onSuccess(Response response) {
        setStatus(BMSClient.getInstance().getBluemixAppRoute() + "/protected", true);
        Log.i(TAG, "You have connected to a protected Bluemix endpoint successfully");
    }

    // Implemented for the response listener to handle failure response when Bluemix is pinged
    @Override
    public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {
        String errorMessage = "";

        if (response != null) {
            if (response.getStatus() == 404) {
                errorMessage += "Application Route not found at:\n" + BMSClient.getInstance().getBluemixAppRoute() + "/protected" +
                        "\nPlease verify your Application Route and rebuild the app.";
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
        Log.e(TAG, "Get request to Bluemix failed: " + errorMessage);
    }

}
