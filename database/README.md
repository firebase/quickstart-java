Firebase Java Realtime Database Quickstart
==========================================

The Java Firebase Database quickstart demonstrates how to connect to and use the Firebase Realtime Database using Java through a simple social blogging app. It will interoperate with the Web, iOS and Android database quickstarts.

This server will:
 - Update the star counts for all posts.
 - Send notifications when a post has been stared.
 - Run weekly job listing the top 5 posts.

Introduction
------------

- [Read more about Firebase Database](https://firebase.google.com/docs/database/)

Getting Started
---------------

- Create your project on the [Firebase Console](https://console.firebase.google.com).
- Create a service account as described in [Adding Firebase to your Server](https://firebase.google.com/docs/admin/setup) and download the JSON file.
  - Copy the json file to this folder and rename it to `service-account.json`.
- Change the `DATATBASE_URL` variable in `Database.java` to be the URL of your Firebase Database.


Run
--------------
- From the `database` directory run `./gradlew build run` to start run the quickstart.
- Configure and run one of the Database quickstarts for [Web](https://github.com/firebase/quickstart-js/tree/master/database),
  [iOS](https://github.com/firebase/quickstart-ios/tree/master/database) or
  [Android](https://github.com/firebase/quickstart-android/tree/master/database).
  Then use one of these apps to publish new posts: you should see console output when one of your posts have
  received a new star and the starred counter should be kept up to date by the app.

Support
-------

https://firebase.google.com/support/

License
-------

© Google, 2016. Licensed under an [Apache-2](../LICENSE) license.
