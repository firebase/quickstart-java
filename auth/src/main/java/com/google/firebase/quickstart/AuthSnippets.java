package com.google.firebase.quickstart;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.google.firebase.auth.UserRecord.UpdateRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Auth snippets for documentation.
 *
 * See:
 * https://firebase.google.com/docs/auth/admin
 */
public class AuthSnippets {

  public static void getUserById(String uid) throws InterruptedException, ExecutionException {
    // [START get_user_by_id]
    UserRecord userRecord = FirebaseAuth.getInstance().getUserAsync(uid).get();
    // See the UserRecord reference doc for the contents of userRecord.
    System.out.println("Successfully fetched user data: " + userRecord.getUid());
    // [END get_user_by_id]
  }

  public static void getUserByEmail(String email) throws InterruptedException, ExecutionException {
    // [START get_user_by_email]
    UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmailAsync(email).get();
    // See the UserRecord reference doc for the contents of userRecord.
    System.out.println("Successfully fetched user data: " + userRecord.getEmail());
    // [END get_user_by_email]
  }

  public static void getUserByPhoneNumber(
      String phoneNumber) throws InterruptedException, ExecutionException {
    // [START get_user_by_phone]
    UserRecord userRecord = FirebaseAuth.getInstance().getUserByPhoneNumberAsync(phoneNumber).get();
    // See the UserRecord reference doc for the contents of userRecord.
    System.out.println("Successfully fetched user data: " + userRecord.getPhoneNumber());
    // [END get_user_by_phone]
  }

  public static void createUser() throws InterruptedException, ExecutionException {
    // [START create_user]
    CreateRequest request = new CreateRequest()
        .setEmail("user@example.com")
        .setEmailVerified(false)
        .setPassword("secretPassword")
        .setPhoneNumber("+11234567890")
        .setDisplayName("John Doe")
        .setPhotoUrl("http://www.example.com/12345678/photo.png")
        .setDisabled(false);

    UserRecord userRecord = FirebaseAuth.getInstance().createUserAsync(request).get();
    System.out.println("Successfully created new user: " + userRecord.getUid());
    // [END create_user]
  }

  public static void createUserWithUid() throws InterruptedException, ExecutionException {
    // [START create_user_with_uid]
    CreateRequest request = new CreateRequest()
        .setUid("some-uid")
        .setEmail("user@example.com")
        .setPhoneNumber("+11234567890");

    UserRecord userRecord = FirebaseAuth.getInstance().createUserAsync(request).get();
    System.out.println("Successfully created new user: " + userRecord.getUid());
    // [END create_user_with_uid]
  }

  public static void updateUser(String uid) throws InterruptedException, ExecutionException {
    // [START update_user]
    UpdateRequest request = new UpdateRequest(uid)
        .setEmail("user@example.com")
        .setPhoneNumber("+11234567890")
        .setEmailVerified(true)
        .setPassword("newPassword")
        .setDisplayName("Jane Doe")
        .setPhotoUrl("http://www.example.com/12345678/photo.png")
        .setDisabled(true);

    UserRecord userRecord = FirebaseAuth.getInstance().updateUserAsync(request).get();
    System.out.println("Successfully updated user: " + userRecord.getUid());
    // [END update_user]
  }

  public static void deleteUser(String uid) throws InterruptedException, ExecutionException {
    // [START delete_user]
    FirebaseAuth.getInstance().deleteUserAsync(uid).get();
    System.out.println("Successfully deleted user.");
    // [END delete_user]
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    System.out.println("Hello, AuthSnippets!");

    // Initialize Firebase
    try {
      // [START initialize]
      FileInputStream serviceAccount = new FileInputStream("service-account.json");
      FirebaseOptions options = new FirebaseOptions.Builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .build();
      FirebaseApp.initializeApp(options);
      // [END initialize]
    } catch (IOException e) {
      System.out.println("ERROR: invalid service account credentials. See README.");
      System.out.println(e.getMessage());

      System.exit(1);
    }

    // Smoke test
    createUserWithUid();
    getUserById("some-uid");
    getUserByEmail("user@example.com");
    getUserByPhoneNumber("+11234567890");
    updateUser("some-uid");
    deleteUser("some-uid");
    System.out.println("Done!");
  }

}
