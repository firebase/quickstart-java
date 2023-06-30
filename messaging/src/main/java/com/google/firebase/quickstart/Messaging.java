package com.google.firebase.quickstart;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

/**
 * Firebase Cloud Messaging (FCM) can be used to send messages to clients on iOS, Android and Web.
 *
 * This sample uses FCM to send two types of messages to clients that are subscribed to the `news`
 * topic. One type of message is a simple notification message (display message). The other is
 * a notification message (display notification) with platform specific customizations, for example,
 * a badge is added to messages that are sent to iOS devices.
 */
public class Messaging {

  private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
  private static final String[] SCOPES = { MESSAGING_SCOPE };

  private static final String TITLE = "FCM Notification";
  private static final String BODY = "Notification from FCM";

  /**
   * Retrieves a valid access token that can be used to authorize requests to the FCM REST
   * API.
   * This method is not used in the rest of the class: the main method in this class uses 
   * the default credential in sending a FCM message. However, this method is used to 
   * demonstrate how to generate an OAuth2 access token using the service account 
   * credential downloaded from Firebase Console. The access token can be attached to your
   * HTTP request to FCM. 
   *
   * @return Access token.
   * @throws IOException
   */
  // [START retrieve_access_token]
  private static String getAccessToken() throws IOException {
    GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(new FileInputStream("android-gcm-test-519bd-4b2b39c38ceb.json"))
            // .fromStream(new FileInputStream("service-account.json"))
            .createScoped(Arrays.asList(SCOPES));
    googleCredentials.refresh();
    return googleCredentials.getAccessToken().getTokenValue();
  }
  // [END retrieve_access_token]

  /**
   * Sends request message to FCM using HTTP.
   *
   * @param message The message of the send request.
   * @throws FirebaseMessagingException
   */
  private static void sendMessage(Message message) throws FirebaseMessagingException {
    try {
      String response = FirebaseMessaging.getInstance().send(message);
      System.out.println("Message sent to Firebase for delivery, response:");
      System.out.println(response);
    } catch (FirebaseMessagingException e) {
      System.out.println("Unable to send message to Firebase, error code:");
      System.out.println(e.getMessagingErrorCode());
    }
  }
  
  /**
   * Sends a message that uses the common FCM fields to send a notification message to all
   * platforms. Also platform specific overrides are used to customize how the message is
   * received on Android and iOS.
   *
   * @throws FirebaseMessagingException
   */
  private static void sendOverrideMessage() throws FirebaseMessagingException {
    Message overrideMessage = buildOverrideMessage();
    System.out.println("FCM request body for override message:");
    prettyPrint(overrideMessage);
    sendMessage(overrideMessage);
  }

  /**
   * Builds the body of an FCM request. This body defines the common notification object
   * as well as platform specific customizations using the android and apns objects.
   *
   * @return Message representation of the FCM request body.
   */
  private static Message buildOverrideMessage() {
    Message message = buildNotificationMessage()
	    .setAndroidConfig(buildAndroidOverridePayload())
		.setApnsConfig(buildApnsOverridePayload())		
        .build();

    return message;
  }
  
  /**
   * Builds the android config that will customize how a message is received on Android.
   *
   * @return android config of an FCM request.
   */  
  private static AndroidConfig buildAndroidOverridePayload() {
    AndroidNotification androidNotification = AndroidNotification.builder()
	    .setClickAction("android.intent.action.MAIN")
		.build();
		
	AndroidConfig androidConfig = AndroidConfig.builder()
	    .setNotification(androidNotification)
		.build();
	  
    return androidConfig;
  }
  
  /**
   * Builds the apns config that will customize how a message is received on iOS.
   *
   * @return apns config of an FCM request.
   */
  private static ApnsConfig buildApnsOverridePayload() {
    Aps aps = Aps.builder()
	    .setBadge(1)
		.build();
  
    ApnsConfig apnsConfig = ApnsConfig.builder()
	    .putHeader("apns-priority", "10")
		.setAps(aps)
		.build();
		
	return apnsConfig;
  }
  
  /**
   * Sends notification message to FCM for delivery to registered devices.
   *
   * @throws FirebaseMessagingException
   */
  public static void sendCommonMessage() throws FirebaseMessagingException {
    Message notificationMessage = buildNotificationMessage().build();
    System.out.println("FCM request body for message using common notification object:");
    prettyPrint(notificationMessage);
    sendMessage(notificationMessage);
  }

  /**
   * Constructs the body of a notification message request.
   *
   * @return the builder object of the notification message.
   */
  private static Message.Builder buildNotificationMessage() {
    Notification notification = Notification.builder()
	    .setTitle(TITLE)
		.setBody(BODY)
        .build();
		
    Message.Builder message = Message.builder()
	    .setNotification(notification)
		.setTopic("news");
	
	return message;
  }

  /**
   * Pretty-prints an object.
   *
   * @param object object to pretty print.
   */
  private static void prettyPrint(Object object) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(object) + "\n");
  }
  
  /**
   * Initializes the enviroment with Firebase default credentials.
   */
  public static void initializeWithDefaultCredentials() throws IOException {
    // [START initialize_sdk_with_application_default]
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build();

    FirebaseApp.initializeApp(options);
    // [END initialize_sdk_with_application_default]
  }

  public static void main(String[] args) throws IOException, FirebaseMessagingException {
    initializeWithDefaultCredentials();
    if (args.length == 1 && args[0].equals("common-message")) {
      sendCommonMessage();
    } else if (args.length == 1 && args[0].equals("override-message")) {
	  sendOverrideMessage();
    } else {
      System.err.println("Invalid command. Please use one of the following commands:");
      // To send a simple notification message that is sent to all platforms using the common
      // fields.
      System.err.println("./gradlew run -Pmessage=common-message");
      // To send a simple notification message to all platforms using the common fields as well as
      // applying platform specific overrides.
      System.err.println("./gradlew run -Pmessage=override-message");
    }
  }

}
