Firebase Cloud Messaging Java Quickstart
========================================

The Firebase Cloud Messaging Java quickstart app demonstrates sending
notification-messages to a topic. All clients subscribed to the topic
will receive the message.

Introduction
------------

This is a simple example of using Firebase Cloud Messaging REST API to send
the same message to different platforms. To learn more about how you can use
Firebase Cloud Messaging REST API in your app, see [About Cloud Messaging Server](https://firebase.google.com/docs/cloud-messaging/server/).

Getting started
---------------

1. [Add Firebase to your Android Project](https://firebase.google.com/docs/android/setup).
2. Create a service account as described in [Adding Firebase to your Server](https://firebase.google.com/docs/admin/setup) and download the JSON file.
  - Copy the json file to this folder and rename it to `service-account.json`.
3. Change the `PROJECT_ID` variable in `Messenger.java` to your project ID.

Run
---
- From the `messaging` directory run `./gradlew build run` to run the quickstart.
- Any client devices that you have subscribed to the news topic should receive
  a notification.

Best practices
--------------
This section provides some additional information about how the FCM payloads can
be used to target different platforms.

### Common payloads ###

In many cases you may want to send the same message to multiple platforms. If
this is a notification-message (display notification) then you can use the
common payloads. These are payloads that are automatically translated to their
platform equivalent payloads.

Support
-------

- [Stack Overflow](https://stackoverflow.com/questions/tagged/firebase-cloud-messaging)
- [Firebase Support](https://firebase.google.com/support/)

License
-------

Copyright 2016 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.