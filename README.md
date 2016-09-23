# Android helloAuthentication application for Bluemix Mobile Services
---
The helloAuthentication sample contains an Android project that you can use to learn more about the Mobile Client Access service.  

Use the following steps to configure the helloAuthentication sample for Android:

1. [Download the helloAuthentication sample](#downloading-the-helloauthentication-sample)
2. [Configure the mobile back end for your helloAuthentication application](#configuring-the-mobile-back-end-for-your-helloauthentication-application)
3. [Configure the front end in the helloAuthentication sample](#configuring-the-front-end-in-the-helloauthentication-sample)
4. [Run the Android app](#running-the-android-app)

### Before you begin
Before you start, make sure you have:

- A [Bluemix](http://bluemix.net) account.

### Downloading the helloAuthentication sample
Clone the sample from Github with the following command:

``git clone https://github.com/ibm-bluemix-mobile-services/bms-samples-android-helloauthentication``

### Configuring the mobile backend for your HelloAuthentication application
Before you can run the helloAuthentication application, you must set up an app on Bluemix.  The following procedure shows you how to create a MobileFirst Services Starter application. A Node.js runtime environment is created so that you can provide server-side functions, such as resource URIs and static files. The Cloudant® NoSQL DB, IBM Push Notifications, and Mobile Client Access services are then added to the app.

#### Creating a mobile backend in the  Bluemix dashboard

1.	In the **Boilerplates** section of the Bluemix catalog, click **MobileFirst Services Starter**.
2.	Enter a name and host for your mobile backend and click **Create**.
3.	Once complete, navigate to your Mobile Client Access dashboard and select the **Manage** tab.
4. Get information about your app. Click the **Mobile Options** button in top right part of the page to find your *appGUID*. Save this value, it will be needed later.

#### Configure the Mobile Client Access service

1.	In the Mobile Client Access dashboard, from the **Manage** tab,configure your authentication service.  
2.  Click the **Configure** button within the **Google** box (this sample has been configured for Google authentication).
3.  Under **Configure for Mobile** Enter the required configuration settings (OAuth 2.0 client ID for Google authentication) under *Client ID for Android*.

>**Note:** If you have not previously created a Google cloud application, go to the [Google Developers Console](https://console.developers.google.com), create a new project, and follow our docs to [Configure Google Authentication](https://www.bluemix.net/docs/services/mobileaccess/google-auth-android.html). Stop at **Configuring Mobile Client Access client SDK for Android**. You will need to enter `com.ibm.helloauthentication` as your package name.

### Configuring the front end in the HelloAuthentication sample
1. Using Android Studio, open the `bms-samples-android-helloauthentication` directory where the project was cloned.
2. Run a Gradle sync (usually starts automatically) to import the required `core` and `googleauthentication` SDKs. You can view the **build.gradle** file in the following directory: `helloAuthentication\app`

3. After the Gradle sync is complete, open the `MainActivity.java` file and add the corresponding App Guid that you saved earlier to your MCAAuthorizationManager.createInstance function.
4. In the ```onCreate``` function, replace ```<APPN_GUID>``` with the App GUID you saved after creating your application on Bluemix.
```java
	// initialize SDK with IBM Bluemix application ID and REGION, TODO: Update region if not using Bluemix US SOUTH
        BMSClient.getInstance().initialize(this, BMSClient.REGION_US_SOUTH);
        .
        .
        .
	// Must create MCA auth manager before registering Google auth Manager
        //TODO: Please replace <APP_GUID> with a valid Application GUID from your MCA instance
        MCAAuthorizationManager MCAAuthMan = MCAAuthorizationManager.createInstance(this, "<APP_GUID>");
        BMSClient.getInstance().setAuthorizationManager(MCAAuthMan);
        GoogleAuthenticationManager.getInstance().register(this);
```

> **Note**: If your Bluemix app is **not** hosted in US_SOUTH, be sure to update the region parameter appropriately: BMSClient.REGION_SYDNEY or BMSClient.REGION_UK.    

### Running the Android App
Now you can run the Android application in your Android emulator or on a physical device.

When you run the application, you will see a single view application with a **PING BLUEMIX** button. When you click this button, the application attempts to obtain a valid authorization header. This causes the authentication process to begin. Log in to the authentication service using valid credentials (Google in this example).  The application will then display if the authentication was successful or unsuccessful. In the unsuccessful state, an error is displayed in the application as well as output to Logcat.

>**Note:** Inside the `MainActivity`, a GET request is commented out to a protected resource in the Node.js runtime on Bluemix. This code has been provided in the MobileFirst Services Starter boilerplate. You can test out that functionality if you'd like by adding in your application route of the boiler plate for ```<YOUR_BLUEMIX_APP_ROUTE>```. This also kicks off authentication if you don't want to use the ```obtain.authorization``` api.

### License
This package contains sample code provided in source code form. The samples are licensed under the under the Apache License, Version 2.0 (the "License"). You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 and may also view the license in the license.txt file within this package. Also see the notices.txt file within this package for additional notices.
