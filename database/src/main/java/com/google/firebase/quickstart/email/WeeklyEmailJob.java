/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.quickstart.email;

import com.google.firebase.database.*;
import com.google.firebase.quickstart.model.Post;
import com.google.firebase.quickstart.model.User;
import org.knowm.sundial.Job;
import org.knowm.sundial.annotations.CronTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;

import java.util.List;
import java.util.Map;

/**
 * Cron job to send weekly emails (Sundays at 2:30pm)
 */
@CronTrigger(cron = "0 30 14 ? * SUN *")
public class WeeklyEmailJob extends Job {

    public void doRun() throws JobInterruptException {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        // Top 5 Posts in the database, ordered by stars
        // [START top_posts_query]
        Query topPostsQuery = ref.child("posts").orderByChild("starCount").limitToLast(5);
        // [END top_posts_query]

        // All Users
        final DatabaseReference allUsersRef = ref.child("users");

        topPostsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(final DataSnapshot topPostsSnapshot) {
                allUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot allUsersSnapshot) {
                        // Get users and posts as lists
                        Map<String,User> users = allUsersSnapshot.getValue(new GenericTypeIndicator<Map<String, User>>() {});
                        List<Post> posts = topPostsSnapshot.getValue(new GenericTypeIndicator<List<Post>>() {});

                        // Send email to all users about the top 5 posts
                        MyEmailer.sendWeeklyEmail(users, posts);
                    }

                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("WeeklyEmailJob: could not get all users");
                        System.out.println("WeeklyEmailJob: " + databaseError.getMessage());
                    }
                });
            }

            public void onCancelled(DatabaseError databaseError) {
                System.out.println("WeeklyEmailJob: could not get top posts");
                System.out.println("WeeklyEmailJob: " + databaseError.getMessage());
            }
        });
    }

}
