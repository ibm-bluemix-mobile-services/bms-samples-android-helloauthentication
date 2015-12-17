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
3.	Click **Finish**.
4. Get information about your app. <br/> After the provisioning process is complete, you will see a a page for your newly provisioned mobile backend. Click the **Mobile Options** link in top right part of a screen to find your *appRoute* and *appGUID*. Keep this screen open in your browser; you will need these parameters in the next steps.

#### Configure the Mobile Client Access service

1.	In the Mobile Client Access dashboard, go to the **Authentication** tab to configure your authentication service.  
2.  Choose your authentication type (this sample has been configured for Google authentication).
3.  Enter the required configuration settings (OAuth 2.0 client ID for Google authentication).

>**Note:** If you have not previously created a Google cloud application, go to the [Google Developers Console](https://console.developers.google.com), create a new project, and follow our docs to [Configure Google Authentication](https://www.ng.bluemix.net/docs/services/mobileaccess/security/google/t_google_config.html). You will need to enter `com.ibm.helloauthentication` as your package name.

### Configuring the front end in the HelloAuthentication sample
1. Using Android Studio, open the `bms-samples-android-helloauthentication` directory where the project was cloned.
2. Run a Gradle sync (usually starts automatically) to import the required `core` and `googleauthentication` SDKs. You can view the **build.gradle** file in the following directory: `helloAuthentication\app`

3. After the Gradle sync is complete, open the `MainActivity.java` file and locate the `try` block within the `onCreate()` function.
4. In the ```BMSClient.getInstance().initialize()``` function, replace ```<APPLICATION_ROUTE>``` and ```<APPLICATION_ID>``` with the application route and ID you were given when creating your application on Bluemix.
```java
		try {
            //initialize SDK with IBM Bluemix application ID and route
            //TODO: Please replace <APPLICATION_ROUTE> with a valid ApplicationRoute and <APPLICATION_ID> with a valid ApplicationId
            BMSClient.getInstance().initialize(this, "<APPLICATION_ROUTE>", "<APPLICATION_ID>");
        }
```


### Running the Android App
Now you can run the Android application in your Android emulator or on a physical device.

When you run the application, you will see a single view application with a **PING BLUEMIX** button. When you click this button, the application tests a connection from the client to a protected resource in the backend Bluemix application. Because this is a protected resource, the authentication process will begin. Log in to the authentication service using valid credentials (Google in this example).  The application will then display if the connection was successful or unsuccessful. In the unsuccessful state, an error is displayed in the application as well as output to Logcat.

>**Note:** Inside the `MainActivity`, a GET request is made to a protected resource in the Node.js runtime on Bluemix. This code has been provided in the MobileFirst Services Starter boilerplate. The Node.js code provided in this boilerplate must be present in order for the sample to work as expected.

### License
This package contains sample code provided in source code form. The samples are licensed under the under the Apache License, Version 2.0 (the "License"). You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 and may also view the license in the license.txt file within this package. Also see the notices.txt file within this package for additional notices.
