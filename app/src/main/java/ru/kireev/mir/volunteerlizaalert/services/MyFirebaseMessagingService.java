package ru.kireev.mir.volunteerlizaalert.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import ru.kireev.mir.volunteerlizaalert.AllActiveDeparturesActivity;
import ru.kireev.mir.volunteerlizaalert.R;
import ru.kireev.mir.volunteerlizaalert.pojo.Departure;
import ru.kireev.mir.volunteerlizaalert.viewmodels.DeparturesViewModel;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String messageTopic = data.get("topic");
        if (messageTopic != null) {
            String departureTime = data.get("time") == null ? "" : data.get("time");
            String departurePlace = data.get("place") == null ? "" : data.get("place");
            String coordinator = data.get("coordinator") == null ? "" : data.get("coordinator");
            String inforg = data.get("inforg") == null ? "" : data.get("inforg");
            String inforgPhoneNumber = data.get("inforg_phone") == null ? "" : data.get("inforg_phone");
            String forumTopic = data.get("forum_topic") == null ? "" : data.get("forum_topic");
            DeparturesViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(DeparturesViewModel.class);
            Departure departure = new Departure(
                    messageTopic,
                    departureTime,
                    departurePlace,
                    coordinator,
                    inforg,
                    inforgPhoneNumber,
                    forumTopic
            );
            viewModel.insertDeparture(departure);
            sendNotification(messageTopic);
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, AllActiveDeparturesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_push)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo))
                .setContentTitle(this.getString(R.string.alert))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "ALERT",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, notificationBuilder.build());
    }
}
