Firebase Remote Config REST API Java Quickstart
===============================================

The Firebase Remote Config Java quickstart app demonstrates fetching and
updating the Firebase Remote Config template.

Introduction
------------

This is a simple example of using the Firebase Remote Config REST API to update
the Remote Config template being used by clients apps.

Getting started
---------------

1. [Add Firebase to your Android Project](https://firebase.google.com/docs/android/setup).
2. Create a service account as described in [Adding Firebase to your Server](https://firebase.google.com/docs/admin/setup) and download the JSON file.
  - Copy the private key JSON file to this folder and rename it to `service-account.json`.
3. Change the `PROJECT_ID` variable in `Messenger.java` to your project ID.

Run
---

- From the `config` directory run `./gradlew build run -Pfetch` to fetch the template.
- Store the returned template in a file.
- Update the template.
- From the `config` directory run `./gradlew build run -Ppush` to update the template.
- Confirm in the console that the template has been updated.

Best practices
--------------

This section provides some additional information about how the Remote Config
REST API should be used when fetching and updating templates.

### Etags ###

Every fetch of the Remote Config template contains an Etag. This Etag is a
unique identifier of the current template on the server. When submitting updates
to the template you must submit your last fetched Etag to ensure that your
updates are consistent.

In the event that you want to completely overwrite the server's template use
an Etag of "\*". Use this with caution since this operation cannot be undone.

Support
-------

- [Stack Overflow](https://stackoverflow.com/questions/tagged/firebase-cloud-messaging)
- [Firebase Support](https://firebase.google.com/support/)
