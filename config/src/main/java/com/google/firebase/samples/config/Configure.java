package com.google.firebase.samples.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Retrieve and publish templates for Firebase Remote Config using the REST API.
 */
public class Configure {

  private final static String PROJECT_ID = "<YOUR-PROJECT-ID>";
  private final static String BASE_URL = "https://firebaseremoteconfig.googleapis.com";
  private final static String REMOTE_CONFIG_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/remoteConfig";
  private final static String REMOTE_CONFIG_SCOPE = "https://www.googleapis.com/auth/firebase.remoteconfig";

  /**
   * Retrieve a valid access token that can be use to authorize requests to the Remote Config REST
   * API.
   *
   * @return Access token.
   * @throws IOException
   */
  private static String getAccessToken() throws IOException {
    GoogleCredential googleCredential = GoogleCredential
        .fromStream(new FileInputStream("service-account.json"))
        .createScoped(Collections.singleton(REMOTE_CONFIG_SCOPE));
    googleCredential.refreshToken();
    return googleCredential.getAccessToken();
  }

  /**
   * Get current Firebase Remote Config template from server and store it locally.
   *
   * @throws IOException
   */
  private static void getTemplate() throws IOException {
    HttpURLConnection httpURLConnection = getCommonConnection();
    httpURLConnection.setRequestMethod("GET");
    httpURLConnection.setRequestProperty("Accept-Encoding", "gzip");

    int code = httpURLConnection.getResponseCode();
    if (code == 200) {
      InputStream inputStream = new GZIPInputStream(httpURLConnection.getInputStream());
      String response = inputstreamToString(inputStream);

      JsonParser jsonParser = new JsonParser();
      JsonElement jsonElement = jsonParser.parse(response);

      Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
      String jsonStr = gson.toJson(jsonElement);

      File file = new File("config.json");
      PrintWriter printWriter = new PrintWriter(new FileWriter(file));
      printWriter.print(jsonStr);
      printWriter.flush();
      printWriter.close();

      System.out.println("Template retrieved and has been written to config.json");

      // Print ETag
      String etag = httpURLConnection.getHeaderField("ETag");
      System.out.println("ETag from server: " + etag);
    } else {
      System.out.println(inputstreamToString(httpURLConnection.getErrorStream()));
    }

  }

  /**
   * Publish local template to Firebase server.
   *
   * @throws IOException
   */
  private static void publishTemplate(String etag) throws IOException {
    if (etag.equals("*")) {
      Scanner scanner = new Scanner(System.in);
      System.out.println("Are you sure you would like to force replace the template? Yes (y), No (n)");
      String answer = scanner.nextLine();
      if (!answer.equalsIgnoreCase("y")) {
        System.out.println("Publish canceled.");
        return;
      }
    }

    System.out.println("Publishing template...");
    HttpURLConnection httpURLConnection = getCommonConnection();
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setRequestMethod("PUT");
    httpURLConnection.setRequestProperty("If-Match", etag);
    httpURLConnection.setRequestProperty("Content-Encoding", "gzip");

    String configStr = readConfig();

    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(httpURLConnection.getOutputStream());
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipOutputStream);
    outputStreamWriter.write(configStr);
    outputStreamWriter.flush();
    outputStreamWriter.close();

    int code = httpURLConnection.getResponseCode();
    if (code == 200) {
      System.out.println("Template has been published.");
    } else {
      System.out.println(inputstreamToString(httpURLConnection.getErrorStream()));
    }

  }

  /**
   * Read the Firebase Remote Config template from config.json file.
   *
   * @return String with contents of config.json file.
   * @throws FileNotFoundException
   */
  private static String readConfig() throws FileNotFoundException {
    File file = new File("config.json");
    Scanner scanner = new Scanner(file);

    StringBuilder stringBuilder = new StringBuilder();
    while (scanner.hasNext()) {
      stringBuilder.append(scanner.nextLine());
    }
    return stringBuilder.toString();
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
   * Create HttpURLConnection that can be used for both retrieving and publishing.
   *
   * @return Base HttpURLConnection.
   * @throws IOException
   */
  private static HttpURLConnection getCommonConnection() throws IOException {
    URL url = new URL(BASE_URL + REMOTE_CONFIG_ENDPOINT);
    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
    httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
    return httpURLConnection;
  }

  public static void main(String[] args) throws IOException {
    if (args.length > 1 && args[0].equals("publish")) {
      publishTemplate(args[1]);
    } else if (args.length == 1 && args[0].equals("get")){
      getTemplate();
    } else {
      System.err.println("Invalid request. Please use one of the following commands:");
      // To get the current template from the server.
      System.err.println("./gradlew run -Paction=get");
      // To publish the template in config.json to the server.
      System.err.println("./gradlew run -Paction=publish -Petag='LATEST_ETAG'");
    }
  }

}