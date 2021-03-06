/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pushnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.app.model.MyUtill;
import com.get.wazzon.MainActivity;
import com.get.wazzon.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            String key = "";
            String value = "";
            for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
                key = entry.getKey();
                value = entry.getValue();
                Log.d(TAG, "key, '" + key + "' value '" + value + "'");
            }

            Log.d(TAG, "getTitle" + remoteMessage.getNotification().getTitle());
            /*try {
                JSONObject obj = new JSONObject(remoteMessage.getNotification().getBody().toString());

                obj.get

            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            //This is where you get your click_action
            Log.d(TAG, "Notification Click Action: " + remoteMessage.getNotification().getClickAction());  //commented as not working
            //put code here to navigate based on click_action
            MyUtill.sendNotification(this, remoteMessage.getNotification().getBody(), "WazzOn",  key, value);


        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    /*public void sendNotification(String messageBody, String key, String value) {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", messageBody);
        intent.putExtra(key, value);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 *//* Request code *//*, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_admin)
                .setContentTitle("WazzOn")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());*/
        //   if (messageBody.contains("$$")) {
        /*    String msg = "";
            Intent intent = new Intent(this, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //value = messageBody.split("$$")[1];
            //messageBody = messageBody.split("$$")[0];
            if (messageBody.contains("$$")) {
                Log.i("Notification", " messageBody [0] " + messageBody.split(Pattern.quote("$$"))[0]);
                Log.i("Notification", " messageBody [1] " + messageBody.split(Pattern.quote("$$"))[1]);

                intent.putExtra("eventID", messageBody.split(Pattern.quote("$$"))[1]);
                msg =  messageBody.split(Pattern.quote("$$"))[0];
            }else{
                msg = messageBody;
                intent.putExtra("eventID", value);
            }

            intent.putExtra("title", msg);
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;

            PendingIntent pendingIntent = PendingIntent.getActivity(this, m *//* Request code *//*, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.icon_admin)
            .setContentTitle("WazzOn")
            .setContentText(msg)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(m *//* ID of notification *//*, notificationBuilder.build());*/
        /*  }else{
            //            Toast.makeText(this, "invalid notification format", Toast.LENGTH_SHORT).show();
            }*/
  //  }
}
