package com.colin.go4lunch.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.colin.go4lunch.models.FormattedPlace;
import com.colin.go4lunch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class NotifyWorker extends Worker {
    private static final String NOTIFICATION_TAG = "NOTIFICATION TAG";
    private static final int NOTIFICATION_ID = 12;

    private static final String CHANNEL_1_ID = "Channel1";
    private static final String CHANNEL_1_NAME = "Channel 1";

    private String placeName;
    private String placeId;
    private String placeAddress;

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Data data = workerParams.getInputData();
        placeAddress = data.getString("placeAddress");
        placeId = data.getString("placeId");
        placeName = data.getString("placeName");
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isActivated = preferences.getBoolean(getApplicationContext().getString(R.string.notification_parameter), false);
        if (!isActivated)
            return Result.failure();
        sendNotification();
        return Result.success();
    }

    private PendingIntent buildPendingIntent(String id, String name, String address) {
        Context context = getApplicationContext();
        FormattedPlace place = new FormattedPlace();
        place.setId(id);
        place.setName(name);
        place.setAddress(address);
        Intent intent = new Intent(context, RestaurantDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("FormattedPlace", place);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.InboxStyle createNotificationUI(String name, String address, ArrayList<String> names) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getApplicationContext().getResources().getString(R.string.app_name));
        Context context = getApplicationContext();
        inboxStyle.addLine(context.getString(R.string.place_notification) + name);
        inboxStyle.addLine(context.getString(R.string.at_notification)+ address);
        inboxStyle.addLine(context.getString(R.string.with_notification));
        for (int i = 0; i < names.size(); i++) {
            inboxStyle.addLine(names.get(i));
            if (i >= 4)
                break;
        }
        return inboxStyle;
    }

    private NotificationCompat.Builder buildNotification(Context context, PendingIntent pendingIntent, NotificationCompat.InboxStyle inboxStyle) {
        return new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.lunch_notification))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setStyle(inboxStyle);
    }

    private void sendNotification() {
        UserHelper.getUsersInterestedByPlace(placeId).addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<String> names = new ArrayList<>();
            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots)
                names.add(Objects.requireNonNull(documentSnapshot1.toObject(User.class)).getName());
            Context context = getApplicationContext();

            NotificationCompat.InboxStyle inboxStyle = createNotificationUI(placeName, placeAddress, names);
            PendingIntent pendingIntent = buildPendingIntent(placeId, placeName, placeAddress);
            NotificationCompat.Builder builder = buildNotification(context, pendingIntent, inboxStyle);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (manager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, CHANNEL_1_NAME, NotificationManager.IMPORTANCE_HIGH);
                    manager.createNotificationChannel(channel);
                }
                manager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, builder.build());
                updateUserPlace();
            }
        });
    }

    private void updateUserPlace() {
        String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        UserHelper.updateString("selectedPlaceId", "", id);
        UserHelper.updateString("selectedPlaceName", "", id);
    }

}
