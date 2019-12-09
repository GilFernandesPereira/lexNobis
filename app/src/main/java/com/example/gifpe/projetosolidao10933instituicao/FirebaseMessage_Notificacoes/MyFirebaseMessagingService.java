package com.example.gifpe.projetosolidao10933instituicao.FirebaseMessage_Notificacoes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.gifpe.projetosolidao10933instituicao.Fragments.AddParticipanteFragment;
import com.example.gifpe.projetosolidao10933instituicao.Fragments.EventosFragment;
import com.example.gifpe.projetosolidao10933instituicao.Fragments.MenuFragment;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.example.gifpe.projetosolidao10933instituicao.Views.Menu;
import com.example.gifpe.projetosolidao10933instituicao.Views.MenuPrincipal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingServce";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String notificationTitle = null, notificationBody = null;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            notificationTitle = remoteMessage.getNotification().getTitle();
            notificationBody = remoteMessage.getNotification().getBody();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        sendNotification(notificationTitle, notificationBody);
    }


    private void sendNotification(String notificationTitle, String notificationBody) {
        Intent intent = new Intent(this, FragmentManager.class);//Antes estava MenuPrincipal.Class
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.drawable.logo_final) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setSound(defaultSoundUri);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

//    private static final String TAG = "MyFirebaseMsgService";
//    private static final int BROADCAST_NOTIFICATION_ID = 1;
//
//    @Override
//    public void onDeletedMessages() {
//        super.onDeletedMessages();
//    }
//
//    /**
//     * Called when message is received.
//     *
//     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
//     */
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        String notificationBody = "";
//        String notificationTitle = "";
//        String notificationData = "";
//        String notificationUserID="";
//        try{
//            notificationData = remoteMessage.getData().toString();
//            notificationTitle = remoteMessage.getNotification().getTitle();
//            notificationBody = remoteMessage.getNotification().getBody();
//            notificationUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
//        }catch (NullPointerException e){
//            Log.e(TAG, "onMessageReceived: NullPointerException: " + e.getMessage() );
//        }
//        Log.d(TAG, "onMessageReceived: data: " + notificationData);
//        Log.d(TAG, "onMessageReceived: notification body: " + notificationBody);
//        Log.d(TAG, "onMessageReceived: notification title: " + notificationTitle);
//        Log.d(TAG, "onMessageReceived: notification UserID: " + notificationUserID);
//
//
//        String dataType = remoteMessage.getData().get("data_type");
//        if(dataType.equals("direct_message")){
//            Log.d(TAG, "onMessageReceived: new incoming message.");
//            String title = remoteMessage.getData().get("Pedido de missão");
//            String message = remoteMessage.getData().get("Tens um novo pedido de missão!");
//            String messageId = remoteMessage.getData().get("message_id");
//            sendMessageNotification(title, message, messageId);
//        }
//    }
//
//    /**
//     * Build a push notification for a chat message
//     * @param title
//     * @param message
//     */
//    private void sendMessageNotification(String title, String message, String messageId){
//        Log.d(TAG, "sendChatmessageNotification: building a chatmessage notification");
//
//        //get the notification id
//        int notificationId = buildNotificationId(messageId);
//
//        // Instantiate a Builder object.
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
//                "Teste");
//        // Creates an Intent for the Activity
//        Intent pendingIntent = new Intent(this, EventosFragment.class);
//        // Sets the Activity to start in a new, empty task
//        pendingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        // Creates the PendingIntent
//        PendingIntent notifyPendingIntent =
//                PendingIntent.getActivity(
//                        this,
//                        0,
//                        pendingIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        //add properties to the builder
//        builder.setSmallIcon(R.drawable.teamwork)
//                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
//                        R.drawable.teamwork))
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//                .setContentTitle(title)
//                .setAutoCancel(true)
//                //.setSubText(message)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setOnlyAlertOnce(true);
//
//        builder.setContentIntent(notifyPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        mNotificationManager.notify(notificationId, builder.build());
//
//    }
//
//
//    private int buildNotificationId(String id){
//        Log.d(TAG, "buildNotificationId: building a notification id.");
//
//        int notificationId = 0;
//        for(int i = 0; i < 9; i++){
//            notificationId = notificationId + id.charAt(0);
//        }
//        Log.d(TAG, "buildNotificationId: id: " + id);
//        Log.d(TAG, "buildNotificationId: notification id:" + notificationId);
//        return notificationId;
//    }

}

