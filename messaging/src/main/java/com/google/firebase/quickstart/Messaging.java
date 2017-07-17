package com.google.firebase.quickstart;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Firebase Cloud Messaging (FCM) can be used to send messages to clients on iOS, Android and Web.
 *
 * This sample uses FCM to send two types of messages to clients that are subscribed to the `news`
 * topic. One type of message is a simple notification message (display message). The other is
 * a notification message (display notification) with platform specific customizations, for example,
 * a badge is added to messages that are sent to iOS devices.
 */
public class Messaging {

  private static final String PROJECT_ID = "<YOUR-PROJECT-ID>";
  private static final String BASE_URL = "https://fcm.googleapis.com";
  private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";

  private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
  private static final String[] SCOPES = { MESSAGING_SCOPE };

  private static final String TITLE = "FCM Notification";
  private static final String BODY = "Notification from FCM";
  public static final String MESSAGE_KEY = "message";

  /**
   * Retrieve a valid access token that can be use to authorize requests to the FCM REST
   * API.
   *
   * @return Access token.
   * @throws IOException
   */
  // [START retrieve_access_token]
  private static String getAccessToken() throws IOException {
    GoogleCredential googleCredential = GoogleCredential
        .fromStream(new FileInputStream("service-account.json"))
        .createScoped(Arrays.asList(SCOPES));
    googleCredential.refreshToken();
    return googleCredential.getAccessToken();
  }
  // [END retrieve_access_token]

  /**
   * Create HttpURLConnection that can be used for both retrieving and publishing.
   *
   * @return Base HttpURLConnection.
   * @throws IOException
   */
  private static HttpURLConnection getConnection() throws IOException {
    // [START use_access_token]
    URL url = new URL(BASE_URL + FCM_SEND_ENDPOINT);
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
    httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
    return httpURLConnection;
    // [END use_access_token]
  }

  /**
   * Send request to FCM message using HTTP.
   *
   * @param fcmMessage Body of the HTTP request.
   * @throws IOException
   */
  private static void sendMessage(JsonObject fcmMessage) throws IOException {
    HttpURLConnection connection = getConnection();
    connection.setDoOutput(true);
    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
    outputStream.writeBytes(fcmMessage.toString());
    outputStream.flush();
    outputStream.close();

    int responseCode = connection.getResponseCode();
    if (responseCode == 200) {
      String response = inputstreamToString(connection.getInputStream());
      System.out.println("Message sent to Firebase for delivery, response:");
      System.out.println(response);
    } else {
      System.out.println("Unable to send message to Firebase:");
      String response = inputstreamToString(connection.getErrorStream());
      System.out.println(response);
    }
  }

  /**
   * Send a message that uses the common FCM fields to send a notification message to all
   * platforms. Also platform specific overrides are used to customize how the message is
   * received on Android and iOS.
   *
   * @throws IOException
   */
  private static void sendOverrideMessage() throws IOException {
    JsonObject overrideMessage = buildOverrideMessage();
    System.out.println("FCM request body for override message:");
    prettyPrint(overrideMessage);
    sendMessage(overrideMessage);
  }

  /**
   * Build the body of an FCM request. This body defines the common notification object
   * as well as platform specific customizations using the android and apns objects.
   *
   * @return JSON representation of the FCM request body.
   */
  private static JsonObject buildOverrideMessage() {
    JsonObject jNotificationMessage = buildNotificationMessage();

    JsonObject messagePayload = jNotificationMessage.get(MESSAGE_KEY).getAsJsonObject();
    messagePayload.add("android", buildAndroidOverridePayload());

    JsonObject apnsPayload = new JsonObject();
    apnsPayload.add("headers", buildApnsHeadersOverridePayload());
    apnsPayload.add("payload", buildApsOverridePayload());

    messagePayload.add("apns", apnsPayload);

    jNotificationMessage.add(MESSAGE_KEY, messagePayload);

    return jNotificationMessage;
  }

  /**
   * Build the android payload that will customize how a message is received on Android.
   *
   * @return android payload of an FCM request.
   */
  private static JsonObject buildAndroidOverridePayload() {
    JsonObject androidNotification = new JsonObject();
    androidNotification.addProperty("click_action", "android.intent.action.MAIN");

    JsonObject androidNotificationPayload = new JsonObject();
    androidNotificationPayload.add("notification", androidNotification);

    return androidNotificationPayload;
  }

  /**
   * Build the apns payload that will customize how a message is received on iOS.
   *
   * @return apns payload of an FCM request.
   */
  private static JsonObject buildApnsHeadersOverridePayload() {
    JsonObject apnsHeaders = new JsonObject();
    apnsHeaders.addProperty("apns-priority", "10");

    return apnsHeaders;
  }

  /**
   * Build aps payload that will add a badge field to the message being sent to
   * iOS devices.
   *
   * @return JSON object with aps payload defined.
   */
  private static JsonObject buildApsOverridePayload() {
    JsonObject badgePayload = new JsonObject();
    badgePayload.addProperty("badge", 1);

    JsonObject apsPayload = new JsonObject();
    apsPayload.add("aps", badgePayload);

    return apsPayload;
  }

  /**
   * Send notification message to FCM for delivery to registered devices.
   *
   * @throws IOException
   */
  public static void sendCommonMessage() throws IOException {
    JsonObject notificationMessage = buildNotificationMessage();
    System.out.println("FCM request body for message using common notification object:");
    prettyPrint(notificationMessage);
    sendMessage(notificationMessage);
  }

  /**
   * Construct the body of a notification message request.
   *
   * @return JSON of notification message.
   */
  private static JsonObject buildNotificationMessage() {
    JsonObject jNotification = new JsonObject();
    jNotification.addProperty("title", TITLE);
    jNotification.addProperty("body", BODY);

    JsonObject jMessage = new JsonObject();
    jMessage.add("notification", jNotification);
    jMessage.addProperty("topic", "news");

    JsonObject jFcm = new JsonObject();
    jFcm.add(MESSAGE_KEY, jMessage);

    return jFcm;
  }

  /**
   * Read contents of InputStream into String.
   *
   * @param inputStream InputStream to read.
   * @return String containing contents of InputStream.
   * @throws IOException
   */
  private static String inputstreamToString(InputStream inputStream) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    Scanner scanner = new Scanner(inputStream);
    while (scanner.hasNext()) {
      stringBuilder.append(scanner.nextLine());
    }
    return stringBuilder.toString();
  }

  /**
   * Pretty print a JsonObject.
   *
   * @param jsonObject JsonObject to pretty print.
   */
  private static void prettyPrint(JsonObject jsonObject) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    System.out.println(gson.toJson(jsonObject) + "\n");
  }

  public static void main(String[] args) throws IOException {
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
