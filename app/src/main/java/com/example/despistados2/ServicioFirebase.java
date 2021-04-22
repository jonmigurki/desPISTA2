package com.example.despistados2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {

    public ServicioFirebase(){}

    public void onMessageReceived(RemoteMessage remoteMessage){
        if(remoteMessage.getData().size() > 0){
            Log.d("HOLAA HAS RECIBIDO ALGO", "AVAV");

            String m = remoteMessage.getData().get("message");

            NotificationManager nm = (NotificationManager) getSystemService(Menu.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "firebase");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel elCanal = new NotificationChannel("firebase", "notifirebase",
                        NotificationManager.IMPORTANCE_DEFAULT);

                nm.createNotificationChannel(elCanal);

                elCanal.setDescription("NOTIFICACION FIREBASE");
                elCanal.enableLights(true);
                elCanal.setLightColor(Color.RED);
                elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                elCanal.enableVibration(true);
            }

            elBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("desPISTA2")
                    .setContentText(m)
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setAutoCancel(true);


            nm.notify(12345, elBuilder.build());

        }

        if(remoteMessage.getNotification() != null){

            Log.d("ESTOY AQUI", "ESTOY AQUI");
            String m = remoteMessage.getData().get("body");

            Intent i = new Intent(this, Menu.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pi = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setContentTitle("FIREBASE")
                    .setContentText(m)
                    .setContentIntent(pi);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.notify(0,builder.build());

        }
    }

}
