package com.google.firebase.samples.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.ListVersionsOptions;
import com.google.firebase.remoteconfig.ListVersionsPage;
import com.google.firebase.remoteconfig.Template;
import com.google.firebase.remoteconfig.Version;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * Retrieve and publish templates for Firebase Remote Config using the REST API.
 */
public class Configure {

  /**
   * Gets current Firebase Remote Config template from server and store it locally.
   *
   * @throws IOException
   */
  private static void getTemplate() throws IOException {
    try {
      Template template = FirebaseRemoteConfig.getInstance().getTemplate();
      JsonParser jsonParser = new JsonParser();
      JsonElement jsonElement = jsonParser.parse(template.toJSON());
      Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
      String jsonStr = gson.toJson(jsonElement);

      File file = new File("config.json");
      PrintWriter printWriter = new PrintWriter(new FileWriter(file));
      printWriter.print(jsonStr);
      printWriter.flush();
      printWriter.close();
      System.out.println("Template retrieved and has been written to config.json");

      // Print ETag
      String etag = template.getETag();
      System.out.println("ETag from server: " + etag);
    } catch (FirebaseRemoteConfigException e) {
      System.out.println(e.getHttpResponse().getContent());
    }
  }
  
  /**
   * Prints the last 5 available Firebase Remote Config template metadata from the server. 
   */
  private static void getVersions() {
  	ListVersionsOptions listVersionsOptions = ListVersionsOptions.builder().setPageSize(5).build();
	try {
      ListVersionsPage page = FirebaseRemoteConfig.getInstance().listVersions(listVersionsOptions);
	  System.out.println("Versions: ");
	  System.out.println(versionsToJSONString(page));
    } catch (FirebaseRemoteConfigException e) {
      System.out.println(e.getHttpResponse().getContent());
    }
  }

  /**
   * Rolls back to an available version of Firebase Remote Config template.
   *
   * @param version The version to roll back to.
   */
  private static void rollback(int version) {
	try {
      Template template = FirebaseRemoteConfig.getInstance().rollback(version);
      System.out.println("Rolled back to: "  + version);
      System.out.println(template.toJSON());
      System.out.println("ETag from server: " + template.getETag());	
	} catch (FirebaseRemoteConfigException e) {
      System.out.println("Error:");
      System.out.println(e.getHttpResponse().getContent());
	}
  }

  /**
   * Publishes local template to Firebase server.
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
	
    String templateStr = readConfig();
	try {
      Template template = Template.fromJSON(templateStr);
      if (etag.equals("*")) {
	    Template publishedTemplate = FirebaseRemoteConfig.getInstance()
	          .forcePublishTemplate(template);
      } else {
	    Template publishedTemplate = FirebaseRemoteConfig.getInstance()
  	          .publishTemplate(template);		
      }
      System.out.println("Template has been published.");
    }
	catch (FirebaseRemoteConfigException e) {
      System.out.println("Error:");
      System.out.println(e.getHttpResponse().getContent());
    }
  }

  /**
   * Reads the Firebase Remote Config template from config.json file.
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
   * Converts the list of versions into a formatted JSON string.
   *
   * @return String representing the list of versions.
   */ 
  private static String versionsToJSONString(ListVersionsPage page) {
  	Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
  	JsonParser jsonParser = new JsonParser();
      
  	JsonArray versionsJsonArray = new JsonArray();
  	for (Version version : page.iterateAll()) {
      versionsJsonArray.add(jsonParser.parse(gson.toJson(version)));
  	}
	  
  	JsonObject jsonObject = new JsonObject();
  	jsonObject.add("versions", versionsJsonArray);	  
  	return gson.toJson(jsonParser.parse(jsonObject.toString()));
  }
  
  public static void initializeWithDefaultCredentials() throws IOException {
    // [START initialize_sdk_with_application_default]
    FirebaseOptions options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build();

    FirebaseApp.initializeApp(options);
    // [END initialize_sdk_with_application_default]
  }

  public static void main(String[] args) throws IOException {
	initializeWithDefaultCredentials();
    if (args.length > 1 && args[0].equals("publish")) {
      publishTemplate(args[1]);
    } else if (args.length == 1 && args[0].equals("get")) {
      getTemplate();
    } else if (args.length == 1 && args[0].equals("versions")) {
      getVersions();
    } else if (args.length > 1 && args[0].equals("rollback")) {
      rollback(Integer.parseInt(args[1]));
    } else {
      System.err.println("Invalid request. Please use one of the following commands:");
      // To get the current template from the server.
      System.err.println("./gradlew run -Paction=get");
      // To publish the template in config.json to the server.
      System.err.println("./gradlew run -Paction=publish -Petag='<LATEST_ETAG>'");
      // To get the available template versions from the server.
      System.err.println("./gradlew run -Paction=versions");
      // To roll back to a particular version.
      System.err.println("./gradlew run -Paction=rollback -Pversion=<TEMPLATE_VERSION_NUMBER>");
    }
  }

}
